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
		@Type(MySub1WithListOfMyBase.class),
		@Type(MySub2WithMyBase.class),
		@Type(MySub2WithYourBase.class),
		@Type(MySub3Simple.class),
})
public abstract class MyBase {
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

class MySub3Simple extends MyBase {
	private String mySub1Param_simple;

	public String getMySub1Param_simple() {
		return mySub1Param_simple;
	}

	public void setMySub1Param_simple(String mySub1Param_simple) {
		this.mySub1Param_simple = mySub1Param_simple;
	}
}

class MySub2WithMyBase extends MyBase {
	private MyBase myBase;

	public MyBase getMyBase() {
		return myBase;
	}

	public void setMyBase(MyBase myBase) {
		this.myBase = myBase;
	}
}

class MySub2WithYourBase extends MyBase {
	private YourBase yourBase;
	private YourSub3Simple yourSub3Simple;

	public YourBase getYourBase() {
		return yourBase;
	}

	public void setYourBase(YourBase yourBase) {
		this.yourBase = yourBase;
	}

	public YourSub3Simple getYourSub3Simple() {
		return yourSub3Simple;
	}

	public void setYourSub3Simple(YourSub3Simple yourSub3Simple) {
		this.yourSub3Simple = yourSub3Simple;
	}
}

class MySub1WithListOfMyBase extends MyBase {
	private List<MyBase> myBaseList;
	private MyBaseHolder myBaseHolder;

	public List<MyBase> getMyBaseList() {
		return myBaseList;
	}

	public void setMyBaseList(List<MyBase> myBaseList) {
		this.myBaseList = myBaseList;
	}

	public MyBaseHolder getMyBaseHolder() {
		return myBaseHolder;
	}

	public void setMyBaseHolder(MyBaseHolder myBaseHolder) {
		this.myBaseHolder = myBaseHolder;
	}
}

class MyBaseHolder {
	public MyBase myBase;

	public MyBase getMyBase() {
		return myBase;
	}

	public void setMyBase(MyBase myBase) {
		this.myBase = myBase;
	}
}
