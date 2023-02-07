/*
 *
 *  *
 *  *  *
 *  *  *  * Copyright 2019-2022 the original author or authors.
 *  *  *  *
 *  *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *  * you may not use this file except in compliance with the License.
 *  *  *  * You may obtain a copy of the License at
 *  *  *  *
 *  *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *  *
 *  *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *  * See the License for the specific language governing permissions and
 *  *  *  * limitations under the License.
 *  *  *
 *  *
 *
 */

package org.springdoc.core.converters;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.RefUtils;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.providers.ObjectMapperProvider;

/**
 * The type Polymorphic model converter.
 * @author bnasslahsen
 */
public class PolymorphicModelConverter implements ModelConverter {

	/**
	 * The Spring doc object mapper.
	 */
	private final ObjectMapperProvider springDocObjectMapper;

	private final AtomicInteger recursionCounter = new AtomicInteger();

	private final Map<String, JavaType> nameToType = new LinkedHashMap<>();
	private final Map<String, ComposedSchema> allComposed = new TreeMap<>();


	/**
	 * Instantiates a new Polymorphic model converter.
	 *
	 * @param springDocObjectMapper the spring doc object mapper
	 */
	public PolymorphicModelConverter(ObjectMapperProvider springDocObjectMapper) {
		this.springDocObjectMapper = springDocObjectMapper;
	}

	private static Schema<?> getResolvedSchema(JavaType javaType, Schema<?> resolvedSchema) {
		if (resolvedSchema instanceof ObjectSchema && resolvedSchema.getProperties() != null) {
			if (resolvedSchema.getProperties().containsKey(javaType.getRawClass().getName()))
				resolvedSchema = resolvedSchema.getProperties().get(javaType.getRawClass().getName());
			else if (resolvedSchema.getProperties().containsKey(javaType.getRawClass().getSimpleName()))
				resolvedSchema = resolvedSchema.getProperties().get(javaType.getRawClass().getSimpleName());
		}
		return resolvedSchema;
	}

	private <T> T withCount(Supplier<T> supplier) {
		recursionCounter.incrementAndGet();
		try {
			return supplier.get();
		}
		finally {
			recursionCounter.decrementAndGet();
		}
	}

	@Override
	public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
		JavaType javaType = springDocObjectMapper.jsonMapper().constructType(type.getType());
		if (javaType != null) {
			if (chain.hasNext()) {
				Schema schema = withCount(() -> chain.next().resolve(type, context, chain));
				for (Map.Entry<String, Schema> e : context.getDefinedModels().entrySet()) {
					if (e.getValue() instanceof ComposedSchema && ((ComposedSchema)e.getValue()).getAllOf() != null) {
						allComposed.put(e.getKey(), (ComposedSchema) e.getValue());
					}
				}

				if (schema == null) {
					return schema;
				}
				schema = getResolvedSchema(javaType, schema);
				if (schema.getName() != null) {
					nameToType.put(schema.getName(), javaType);
					if (schema instanceof ComposedSchema && schema.getAllOf() != null) {
						allComposed.put(schema.getName(), (ComposedSchema) schema);
					}
				}
				if (recursionCounter.get() > 0) {
					return schema;
				}

				// Replace any non-composed defined models with their most-recent composed versions
				allComposed.forEach((k, v) -> context.defineModel(k, v));

				// This is the original non-recursive schema, so apply polymorphic fixups
				if (schema.get$ref() != null) {
					schema = composePolymorphicSchema(schema);
				}
				findAndReplacePolymorphicSchemas(schema);

				// Also fixup references in previously defined models
				for (Schema prevDefinedSchema: context.getDefinedModels().values()) {
					findAndReplacePolymorphicSchemas(prevDefinedSchema);
				}

				nameToType.clear();
				allComposed.clear();
				return schema;
			}
		}
		return null;
	}

	private void findAndReplacePolymorphicSchemas(Schema schema) {
		// Schema is not itself a ref but may have properties that are, so convert those.
		findAndReplacePolymorphicProps(schema);
		findAndReplacePolymorphicAllOf(schema);
		findAndReplacePolymorphicItems(schema);
	}

	private Schema findAndReplacePolymorphicProps(Schema<?> schema) {
		if (schema.getProperties() != null) {
			Map<String, Schema> newProps = schema.getProperties().entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, entry -> {
						Schema propSchema = entry.getValue().get$ref() != null
								? composePolymorphicSchema(entry.getValue())
								: entry.getValue();
						findAndReplacePolymorphicItems(propSchema);
						return propSchema;
					}));
			schema.setProperties(newProps);
		}
		return schema;
	}

	private Schema findAndReplacePolymorphicAllOf(Schema<?> schema) {
		if (schema.getAllOf() != null) {
			for (Object element : schema.getAllOf()) {
				if (element instanceof ObjectSchema) {
					findAndReplacePolymorphicProps((ObjectSchema)element);
					// TODO: needed?
					findAndReplacePolymorphicItems((ObjectSchema)element);
				}
			}
		}
		return schema;
	}

	private Schema findAndReplacePolymorphicItems(Schema<?> schema) {
		if (schema.getItems() != null && schema.getItems().get$ref() != null && schema instanceof ArraySchema) {
			schema.setItems(composePolymorphicSchema(schema.getItems()));
		}
		else if (schema.getItems() != null && schema.getItems().get$ref() != null) {
			System.out.println("@@@ PMC.composePolymorphicItems. items.class=" + schema.getItems().getClass());
		}

		return schema;
	}

	/**
	 * Compose polymorphic schema schema.
	 *
	 * @param schema the schema
	 * @return the schema
	 */
	private Schema composePolymorphicSchema(Schema schema) {
		String ref = schema.get$ref();
		List<Schema> composedSchemas = allComposed.values().stream()
				.filter(s -> s.getAllOf() != null)
				.filter(s -> s.getAllOf().stream().anyMatch(s2 -> ref.equals(s2.get$ref())))
				.map(s -> new Schema().$ref(AnnotationsUtils.COMPONENTS_REF + s.getName()))
				.collect(Collectors.toList());
		if (composedSchemas.isEmpty()) {
			return schema;
		}

		ComposedSchema result = new ComposedSchema();
		if (isConcreteClass(ref)) result.addOneOfItem(schema);
		composedSchemas.forEach(result::addOneOfItem);
		return result;
	}

	/**
	 * Is concrete class boolean.
	 *
	 * @param ref the ref
	 * @return the boolean
	 */
	private boolean isConcreteClass(String ref) {
		String name = (String) RefUtils.extractSimpleName(ref).getLeft();
		JavaType javaType = nameToType.get(name);
		if (javaType == null) {
			return false;
		}
		Class<?> clazz = javaType.getRawClass();
		return !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface();
	}
}
