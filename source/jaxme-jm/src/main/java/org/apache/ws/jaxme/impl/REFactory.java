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


/** The REFactory is used for creating instances of
 * {@link REHandler.Matcher}. It provides access to a single
 * instance of {@link REHandler}.
 */
public class REFactory {
	private static final REHandler reHandler;

	static {
		REHandler reh = null;
		String p = System.getProperty(REFactory.class.getName() + ".Implementation");
		if (p != null) {
			try {
				Class c = Class.forName(p);
				reh = (REHandler) c.newInstance();
			} catch (Throwable t) {
			}
		}
		if (p == null) {
			p = REFactory.class.getName();
			p = p.substring(0, p.lastIndexOf('.')+1);
			try {
				reh = (REHandler) Class.forName(p + "JavaUtilREHandler").newInstance();
			} catch (Throwable t0) {
				try {
					reh = (REHandler) Class.forName(p + "Java5REHandler").newInstance();
				} catch (Throwable t1) {
					try {
						reh = (REHandler) Class.forName(p + "XercesREHandler").newInstance();
					} catch (Throwable t2) {
						reh = new DummyREHandler();
					}
				}
			}
		}
		reHandler = reh;
	}

	/** Returns the singleton {@link REHandler}. The instance is
	 * created as follows:
	 * <ol>
	 *   <li>If the system property
	 *     <code>org.apache.ws.jaxme.impl.REFactory.Implementation</code>
	 *     is set, and contains the name of a valid REHandler class,
	 *     then that class is instantiated.</li>
	 *   <li>An instance of {@link Java5REHandler} is created,
	 *     if possible. This is the case, when running under
	 *     Java 5.</li>
	 *   <li>Otherwise, an instance of {@link XercesREHandler}
	 *     is created, if possible. This is the case, when
	 *     Xerces-J is available.</li>
	 *   <li>Otherwise, an instance of {@link DummyREHandler}
	 *     is created. This is an REHandler, which matches
	 *     always. In other words, patterns are ignored.</li>
	 * </ol>
	 */
	public static REHandler getREHandler() { return reHandler; }

	/** Dummy implementation of an {@link REHandler},
	 * which accepts any string as matching for any pattern.
	 * Used as a fallback, if no other implementations are
	 * available.
	 */
	public static class DummyREHandler implements REHandler {
		public Matcher getMatcher(final String pPattern) {
			return new REHandler.Matcher() {
				public String getPattern() { return pPattern; }
				public boolean matches(String pValue) { return true; }
			};
		}
	}
}
