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
package org.apache.ws.jaxme.generator.sg.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexContentSGChain;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.types.XSAnyType;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/** Default implementation of
 * {@link org.apache.ws.jaxme.generator.sg.ComplexContentSG}.
 */
public class JAXBComplexContentTypeSG implements ComplexContentSGChain {
    private static final Logger log = LoggerAccess.getLogger(JAXBComplexContentTypeSG.class);

    private static class Particle {
        private final GroupSG[] stack;
        private final ParticleSG particle;
        Particle(GroupSG[] pStack, ParticleSG pParticle) {
            stack = pStack;
            particle = pParticle;
        }
        ParticleSG getParticleSG() { return particle; }
        Object[] getStack() { return stack; }
        GroupSG getClosestCommonAnchestor(Particle pParticle) {
            int len = Math.min(stack.length, pParticle.stack.length);
            GroupSG anchestor = stack[0];
            for (int i = 1;  i < len;  i++) {
                if (stack[i] != pParticle.stack[i]) {
                    break;
                }
            }
            return anchestor;
        }
    }

    private final boolean isEmpty, isMixed;
    private final ParticleSG rootParticle;

    private boolean isInitialized;
    private final Locator locator;
    private final List elementParticles = new ArrayList();
    private Particle[] elementParticleArray;
    private final List mixedContentParticles = new ArrayList();
    private final List groupParticlesWithMultiplicityGreaterOne = new ArrayList();
    private final List wildcardParticles = new ArrayList();
    private final List stack = new ArrayList();

	protected JAXBComplexContentTypeSG(ComplexTypeSG pComplexTypeSG, XSType pType) throws SAXException {
		if (pType == XSAnyType.getInstance()) {
			throw new SAXException("The type xs:anyType is not supported.");
		}
		locator = pType.getLocator();
		if (pType.getComplexType().isEmpty()) {
			isEmpty = true;
			isMixed = false;
			rootParticle = null;
		} else {
			XSParticle particle = pType.getComplexType().getParticle();
			if (particle == null) {
				throw new NullPointerException("Missing group particle for type = " + pType.getName());
			}
			if (particle.isGroup()) {
				rootParticle = newParticleSG(pComplexTypeSG.getTypeSG().getFactory(), particle, pComplexTypeSG.getClassContext());
			} else {
				throw new IllegalStateException("Expected internal group");
			}
			isEmpty = false;
			isMixed = pType.getComplexType().isMixed();
		}
	}

	protected ParticleSG newParticleSG(SGFactory pFactory, XSParticle pParticle, Context pContext) throws SAXException {
		JAXBParticleSG chain = new JAXBParticleSG(pFactory, pParticle, pContext);
		ParticleSGImpl result = new ParticleSGImpl(chain);
		result.init();
		return result;
	}

    public Locator getLocator(ComplexContentSG pController) { return locator; }

	public void init(ComplexContentSG pController) throws SAXException {
    }

    private void initialize(ComplexContentSG pController) throws SAXException {
        if (!isInitialized  &&  !isEmpty) {
            isInitialized = true;
            verify(pController);
        }
    }

    private Particle[] getLocalElementParticles(ComplexContentSG pController)
            throws SAXException {
        initialize(pController);
        return elementParticleArray;
    }

	public ParticleSG[] getElementParticles(ComplexContentSG pController)
			throws SAXException {
		Particle[] particles = getLocalElementParticles(pController);
		if (particles == null) {
			return new ParticleSG[0];
		}
		ParticleSG[] result = new ParticleSG[particles.length];
		for (int i = 0;  i  < result.length;  i++) {
			result[i] = particles[i].getParticleSG();
		}
		return result;
	}

	public ParticleSG getRootParticle(ComplexContentSG pController) {
        return rootParticle;
	}
	
	public boolean isEmpty(ComplexContentSG pController) {
		return isEmpty;
	}
	
	public boolean isMixed(ComplexContentSG pController) {
		return isMixed;
	}

    
    private void findParticles(GroupSG[] pStack, ParticleSG pParticle) throws SAXException {
        if (pParticle.isGroup()) {
            if (pParticle.isMultiple()) {
                groupParticlesWithMultiplicityGreaterOne.add(new Particle(pStack, pParticle));
            } else if (isMixed) {
				mixedContentParticles.add(new Particle(pStack, pParticle));
			} else {
                findParticles(pParticle.getGroupSG());
            }
        } else if (pParticle.isWildcard()) {
            wildcardParticles.add(new Particle(pStack, pParticle));
        } else if (pParticle.isElement()) {
            Particle p = new Particle(pStack, pParticle);
            elementParticles.add(p);
        } else {
            throw new SAXParseException("Invalid particle type", pParticle.getLocator());
        }
    }

    private void findParticles(GroupSG pGroup) throws SAXException {
        stack.add(pGroup);
        ParticleSG[] particles = pGroup.getParticles();
        GroupSG[] groups = (GroupSG[]) stack.toArray(new GroupSG[stack.size()]);
        for (int i = 0;  i < particles.length;  i++) {
            findParticles(groups, particles[i]);
        }
        stack.remove(stack.size()-1);
    }

    /** Verifies the contents of a complex type with complex content, according
     * to the JAXB 1.0 specification, 5.9.7.
     */
    private void verify(ComplexContentSG pController) throws SAXException {
        findParticles(new GroupSG[0], pController.getRootParticle());

        /* 5.9.7, 1) If {content type} is mixed, bind the entire content model
         * to a general content property with the content-property name "content".
         * See Section 5.9.4, "Bind mixed content" for more details.
         */
        if (pController.isMixed()) {
			// Make sure, that all groups have the "general content property"
        }
        /* 5.9.7, 2) If (1) a particle has {max occurs} > 1 and (2) its term
         * is a model group, then that particle and its descendants are mapped
         * to one general content property that represents them. See Section
         * 5.9.6, "Bind a repeating occurrence model group" for details.
         */
        if (groupParticlesWithMultiplicityGreaterOne.size() > 0) {
            Particle particle = (Particle) groupParticlesWithMultiplicityGreaterOne.get(0);
            throw new SAXParseException("Model groups with maxOccurs > 1 are not yet supported.",
                                        particle.getParticleSG().getLocator());
        }
        /* 5.9.7, 3) Process all remaining particles 1) whose {term} are
         * wildcard particles and (2) that did not belong to a repeating
         * occurrence model group bound in step 2. If there is only one
         * wildcard, bind it as specified in Section 5.9.5, "Bind wildcard
         * schema component". If there is more than one, then fallback to
         * representing the entire content model as a single general
         * content property.
         */
        if (wildcardParticles.size() > 0) {
            Particle particle = (Particle) wildcardParticles.get(0);
            throw new SAXParseException("Wildcards are unsupported",
                                        particle.getParticleSG().getLocator());
        }
        /* 5.9.7, 4) Process all particles (1) whose {term} are element
         * declarations and (2) that do not belong to a repeating occurrence
         * model group bound in step 2
         * 
         * First we say a particle has a label L, if it refers to an element
         * declaration whose {name} is L. Then, for all the possible pair of
         * particles P and Q in this set, ensure the following constraints are
         * met:
         * a) If P and Q have the same label, then they must refer to the same
         * element declaration.
         * b) If P and Q refer to the same element reference, then its closest
         * common ancestor particle may not have sequence as its {term}.
         * 
         * If either of the above constraints are violated, then the binding
         * compiler must report a property naming collision, that can be
         * corrected via customization.
         */
        elementParticleArray = (Particle[]) elementParticles.toArray(new Particle[elementParticles.size()]);
        for (int i = 0;  i < elementParticleArray.length;  i++) {
            Particle pParticle = elementParticleArray[i];
            ParticleSG p = pParticle.getParticleSG();
            String name = p.getPropertySG().getXMLFieldName();
            for (int j = i+1;  j < elementParticles.size();  j++) {
                Particle qParticle = elementParticleArray[j];
                ParticleSG q = qParticle.getParticleSG();
                if (name.equals(q.getPropertySG().getXMLFieldName())) {
                    ObjectSG pObject = p.getObjectSG();
                    ObjectSG qObject = q.getObjectSG();
                    if (!pObject.isGlobal()  ||  !qObject.isGlobal()  ||
                        pObject != qObject) {
                        throw new SAXParseException("Multiple element particles named " + name
                                                    + ", which aren't references to "
                                                    + " a common global element, are present in a common"
                                                    + " complex type. (JAXB 1.0, 5.9.7.4.a) Use jaxb:property/@name"
                                                    + " for customization.", pController.getLocator());
                    }
                    GroupSG group = pParticle.getClosestCommonAnchestor(pParticle);
                    if (group.isSequence()) {
                        throw new SAXParseException("Multiple element particles named " + name
                                                    + " are contained in a common sequence."
                                                    + " (JAXB 1.0, 5.9.7.4.b) Use jaxb:property/@name"
                                                    + " for customization.", pController.getLocator());
                    }
                }
            }
        }
    }
}
