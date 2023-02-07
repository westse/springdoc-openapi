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

package test.org.springdoc.api.v30.app201;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
		@Type(YourSub1WithListOfYourBase.class),
		@Type(YourSub2WithYourBase.class),
		@Type(YourSub2WithMyBase.class),
		@Type(YourSub3Simple.class),
})
public abstract class YourBase {
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

class YourSub3Simple extends YourBase {
	private String yourSub1Param_simple;

	public String getYourSub1Param_simple() {
		return yourSub1Param_simple;
	}

	public void setYourSub1Param_simple(String yourSub1Param_simple) {
		this.yourSub1Param_simple = yourSub1Param_simple;
	}
}

class YourSub2WithYourBase extends YourBase {
	private YourBase yourBase;

	public YourBase getYourBase() {
		return yourBase;
	}

	public void setYourBase(YourBase yourBase) {
		this.yourBase = yourBase;
	}
}

class YourSub2WithMyBase extends YourBase {
	private MyBase myBase;
	private MySub3Simple mySub3Simple;

	public MyBase getMyBase() {
		return myBase;
	}

	public void setMyBase(MyBase myBase) {
		this.myBase = myBase;
	}

	public MySub3Simple getMySub3Simple() {
		return mySub3Simple;
	}

	public void setMySub3Simple(MySub3Simple mySub3Simple) {
		this.mySub3Simple = mySub3Simple;
	}
}

class YourSub1WithListOfYourBase extends YourBase {
	private List<YourBase> yourBaseList;
	private YourBaseHolder yourBaseHolder;

	public List<YourBase> getYourBaseList() {
		return yourBaseList;
	}

	public void setYourBaseList(List<YourBase> yourBaseList) {
		this.yourBaseList = yourBaseList;
	}

	public YourBaseHolder getYourBaseHolder() {
		return yourBaseHolder;
	}

	public void setYourBaseHolder(YourBaseHolder yourBaseHolder) {
		this.yourBaseHolder = yourBaseHolder;
	}
}

class YourBaseHolder {
	public YourBase yourBase;

	public YourBase getYourBase() {
		return yourBase;
	}

	public void setYourBase(YourBase yourBase) {
		this.yourBase = yourBase;
	}
}
