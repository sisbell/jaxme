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
package org.apache.ws.jaxme.generator.types;

import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.SAXException;


/** Implementation of {@link org.apache.ws.jaxme.generator.sg.SimpleTypeSG}
 * for <code>xs:id</code>.
 */
public class IDSG extends StringSG {
	/** Creates a new instance of IDSG.
	 */
	public IDSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
		super(pFactory, pSchema, pType);
	}

	public boolean isXsId(SimpleTypeSG pController) { return true; }
}
