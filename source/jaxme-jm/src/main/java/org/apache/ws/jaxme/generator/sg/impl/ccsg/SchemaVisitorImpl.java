/*
 * Copyright 2005  The Apache Software Foundation
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
package org.apache.ws.jaxme.generator.sg.impl.ccsg;

import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.xml.sax.SAXException;


/** Default implementation of a
 * {@link org.apache.ws.jaxme.generator.sg.impl.ccsg.SchemaVisitor}.
 */
public class SchemaVisitorImpl implements SchemaVisitor {
	public void visit(TypeSG pType) throws SAXException {}
	public void visit(ObjectSG pElement) throws SAXException {}
	public void visit(GroupSG pGroup) throws SAXException {}
	public void visit(ParticleSG pParticle) throws SAXException {}
}
