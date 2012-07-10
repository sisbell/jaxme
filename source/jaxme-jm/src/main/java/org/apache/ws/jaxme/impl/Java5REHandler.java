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

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;


/** Default implementation of {@link REHandler}, using
 * the builtin Xerces from Java 5.
 */
public class Java5REHandler implements REHandler {
	public Matcher getMatcher(final String pPattern) {
		final RegularExpression re = new RegularExpression(pPattern, "X");
		return new Matcher(){
			public String getPattern() { return pPattern; }
			public boolean matches(String pValue) {
				return re.matches(pValue);
			}
		};
	}
}
