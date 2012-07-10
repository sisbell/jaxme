/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.jaxme.impl;

import java.util.regex.Pattern;

/** Implementation of {@link REHandler}, using Java API.
 */
public class JavaUtilREHandler implements REHandler {
	public Matcher getMatcher(final String pPattern) {
		final Pattern p = Pattern.compile(pPattern);
		return new Matcher(){
			public String getPattern() { return pPattern; }
			public boolean matches(String pValue) {
				return p.matcher(pValue).matches();
			}
		};
	}
}
