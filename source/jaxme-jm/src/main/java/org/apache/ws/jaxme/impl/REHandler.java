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

/** An instance of RE is able to evaluate, whether
 * a given regular expression is matched by a
 * certain string.<br>
 * JaxMe doesn't actually implement RE handling. It supports
 * a set of implementations, basically Xerces and Java 5.
 * Upon startup, an attempt is made to load an RE implementation.
 */
public interface REHandler {
	/** An RE matcher is a thread safe, compiled regular expression.
	 * Its method "matches" is used to determine, whether a given
	 * string matches the pattern.
	 */
	public interface Matcher {
		/** Returns the regular expressions pattern.
		 */
		String getPattern();
		/** Returns, whether the given string is matched
		 * by the regular expression.
		 */
		boolean matches(String pValue);
	}

	/** Creates a thread safe RE matcher.
	 */
	Matcher getMatcher(String pPattern);
}
