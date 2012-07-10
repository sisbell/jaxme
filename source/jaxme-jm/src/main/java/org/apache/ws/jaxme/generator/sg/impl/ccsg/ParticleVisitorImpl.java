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

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.xml.sax.SAXException;


/** Default implementation of a
 * {@link org.apache.ws.jaxme.generator.sg.impl.ccsg.ParticleVisitor},
 * with methods doing nothing.
 */
public class ParticleVisitorImpl implements ParticleVisitor {
	public void emptyType(ComplexTypeSG type) throws SAXException {
	}

	public void simpleContent(ComplexTypeSG type) throws SAXException {
	}

	public void startSequence(GroupSG group) throws SAXException {
	}

	public void endSequence(GroupSG group) {
	}

	public void startChoice(GroupSG group) {
	}

	public void endChoice(GroupSG group) {
	}

	public void startAll(GroupSG group) {
	}

	public void endAll(GroupSG group) {
	}

	public void startComplexContent(ComplexTypeSG type) throws SAXException {
	}

	public void endComplexContent(ComplexTypeSG type) throws SAXException {
	}

	public void simpleElementParticle(GroupSG pGroup, ParticleSG pParticle) throws SAXException {
	}

	public void complexElementParticle(GroupSG pGroup, ParticleSG particle) throws SAXException {
	}

	public void wildcardParticle(ParticleSG particle) {
	}
}
