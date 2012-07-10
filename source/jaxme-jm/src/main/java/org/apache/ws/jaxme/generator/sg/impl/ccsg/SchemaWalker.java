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

import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.xml.sax.SAXException;


/** A schema walker iterates over a schemas components.
 * For any component, it invokes the respective method
 * of its
 * {@link org.apache.ws.jaxme.generator.sg.impl.ccsg.SchemaVisitor}.
 */
public class SchemaWalker {
	private final SchemaVisitor visitor;

	/** Creates a new instance, which invokes the given
	 * visitors methods.
	 */
	public SchemaWalker(SchemaVisitor pVisitor) {
		visitor = pVisitor;
	}

	protected void walk(ObjectSG pElement) throws SAXException {
		visitor.visit(pElement);
		TypeSG type = pElement.getTypeSG();
		if (!type.isGlobalType()) {
			walk(type);
		}
	}

	protected void walk(GroupSG pGroup) throws SAXException {
		visitor.visit(pGroup);
		ParticleSG[] particles = pGroup.getParticles();
		for (int i = 0;  i < particles.length;  i++) {
			walk(particles[i]);
		}
	}

	protected void walk(ParticleSG pParticle) throws SAXException {
		visitor.visit(pParticle);
		if (pParticle.isElement()) {
			ObjectSG oSG = pParticle.getObjectSG();
			if (!oSG.isGlobal()) {
				walk(oSG);
			}
		} else if (pParticle.isGroup()) {
			GroupSG group = pParticle.getGroupSG();
			if (!group.isGlobal()) {
				walk(group);
			}
		} else if (pParticle.isWildcard()) {
			throw new IllegalStateException("TODO: Implement support for wildcards");
		} else {
			throw new IllegalStateException("Unknown particle type");
		}
	}

	protected void walk(TypeSG pType) throws SAXException {
		visitor.visit(pType);
		if (pType.isComplex()) {
			ComplexTypeSG ctSG = pType.getComplexTypeSG();
			if (!ctSG.hasSimpleContent()) {
				ComplexContentSG ccSG = ctSG.getComplexContentSG();
				if (!ccSG.isEmpty()) {
					ParticleSG particle = ccSG.getRootParticle();
					walk(particle);
				}
			}
		}
	}

	/** Iterates over the given schemas components.
	 */
	public void walk(SchemaSG pSchema) throws SAXException {
		TypeSG[] types = pSchema.getTypes();
		for (int i = 0;  i < types.length;  i++) {
			walk(types[i]);
		}
		ObjectSG[] elements = pSchema.getElements();
		for (int i = 0;  i < elements.length;  i++) {
			walk(elements[i]);
		}
		GroupSG[] groups = pSchema.getGroups();
		for (int i = 0;  i < groups.length;  i++) {
			walk(groups[i]);
		}
	}
}
