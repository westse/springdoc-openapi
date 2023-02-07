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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("class-hierarchy")
public class Controller {
	@PostMapping("my-base")
	public Response abstractParent(@RequestBody MyBase payload) {
		return null;
	}

}

class Response {
	MyBase myBase;
	YourBase yourBase;

	public MyBase getMyBase() {
		return myBase;
	}

	public void setMyBase(MyBase myBase) {
		this.myBase = myBase;
	}

	public YourBase getYourBase() {
		return yourBase;
	}

	public void setYourBase(YourBase yourBase) {
		this.yourBase = yourBase;
	}
}
