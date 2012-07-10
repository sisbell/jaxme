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


/** A <code>ParticleVisitor</code> iterates over a groups
 * particles.
 */
public interface ParticleVisitor {
	/** Invoked for an empty type.
	 * @throws SAXException The visitor failed.
	 */
	void emptyType(ComplexTypeSG type) throws SAXException;
	/** Invoked for a complex type with simple content.
	 * @throws SAXException The visitor failed.
	 */
	void simpleContent(ComplexTypeSG type) throws SAXException;
	/** Invoked to begin a sequence.
	 * @throws SAXException The visitor failed.
	 */
	void startSequence(GroupSG group) throws SAXException;
	/** Invoked to end a sequence.
	 * @throws SAXException The visitor failed.
	 */
	void endSequence(GroupSG group) throws SAXException;
	/** Invoked to start a choice group.
	 * @throws SAXException The visitor failed.
	 */
	void startChoice(GroupSG group) throws SAXException;
	/** Invoked to end a choice group.
	 * @throws SAXException The visitor failed.
	 */
	void endChoice(GroupSG group) throws SAXException;
	/** Invoked to start an all group.
	 * @throws SAXException The visitor failed.
	 */
	void startAll(GroupSG group) throws SAXException;
	/** Invoked to end an all group.
	 * @throws SAXException The visitor failed.
	 */
	void endAll(GroupSG group) throws SAXException;
	/** Invoked to start a complex content types
	 * content.
	 * @throws SAXException The visitor failed.
	 */
	void startComplexContent(ComplexTypeSG type) throws SAXException;
	/** Invoked to end a complex content types content.
	 * @throws SAXException The visitor failed.
	 */
	void endComplexContent(ComplexTypeSG type) throws SAXException;
	/** Invoked to process an element with simple type.
	 * @throws SAXException The visitor failed.
	 */
	void simpleElementParticle(GroupSG pGroup,ParticleSG particle) throws SAXException;
	/** Invoked to process an element with complex type.
	 * @throws SAXException The visitor failed.
	 */
	void complexElementParticle(GroupSG pGroup, ParticleSG particle) throws SAXException;
	/** Invoked to process a wildcard particle.
	 * @throws SAXException The visitor failed.
	 */
	void wildcardParticle(ParticleSG particle) throws SAXException;
}
