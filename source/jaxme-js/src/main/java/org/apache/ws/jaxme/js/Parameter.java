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
package org.apache.ws.jaxme.js;

import java.io.IOException;

/** <p>A parameter, as used by {@link org.apache.ws.jaxme.js.JavaMethod},
 * or {@link org.apache.ws.jaxme.js.JavaConstructor}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class Parameter implements DirectAccessible, IndentedObject {
	private final JavaQName type;
	private final String name;
	private boolean isNullable = true;
	private boolean isFinal;
	
	public Parameter(JavaQName pType, String pName) {
		type = pType;
		name = pName;
	}
	public JavaQName getType() { return type; }
	public String getName() { return name; }
	public String toString() { return type.toString() + " " + name; }
	public void write(IndentationEngine pEngine, IndentationTarget pTarget)
	throws IOException {
		pEngine.write(pTarget, getName());
	}
	public boolean isNullable() { return isNullable; }
	public void setNullable(boolean pNullable) { isNullable = pNullable; }
	/** Sets, whether the parameter is final.
	 */
	public void setFinal(boolean pFinal) {
		isFinal = pFinal;
	}
	/** Returns, whether the parameter is final.
	 */
	public boolean isFinal() {
		return isFinal;
	}
}
