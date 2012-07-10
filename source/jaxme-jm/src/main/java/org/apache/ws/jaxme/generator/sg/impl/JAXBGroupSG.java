/*
 * Copyright 2003,2004  The Apache Software Foundation
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

import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.GroupSGChain;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.ParticleSGChain;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBGroupSG extends JAXBSGItem implements GroupSGChain {
	private ParticleSG[] particles;
	private final boolean isGlobal, isAll, isSequence, isChoice;
	private final Context classContext;
	private final XsQName qName;
	
	/** <p>Creates a new, global group.</p>
	 */
	protected JAXBGroupSG(SGFactory pFactory, SchemaSG pSchema, XSGroup pGroup) throws SAXException {
		super(pFactory, pSchema, pGroup);
		isGlobal = true;
		qName = pGroup.getName();
		isAll = pGroup.isAll();
		isSequence = pGroup.isSequence();
		isChoice = pGroup.isChoice();
		classContext = new GlobalContext(pGroup.getName(), pGroup, null, "Group", pSchema);
	}
	
	/** <p>Creates a new, local group.</p>
	 */
	protected JAXBGroupSG(SGFactory pFactory, SchemaSG pSchema, XSGroup pGroup, Context pContext)
	throws SAXException {
		super(pFactory, pSchema, pGroup);
		isGlobal = pGroup.isGlobal();
		qName = isGlobal ? pGroup.getName() : null;
		isAll = pGroup.isAll();
		isSequence = pGroup.isSequence();
		isChoice = pGroup.isChoice();
		classContext = pContext;
	}
	
	public Object newParticleSG(GroupSG pController, XSParticle pParticle) throws SAXException {
		return new JAXBParticleSG(pController.getFactory(), pParticle, classContext);
	}
	
	public Context getClassContext(GroupSG pController) { return classContext; }
	
	public SGFactory getFactory(GroupSG pController) { return getFactory(); }
	public SchemaSG getSchema(GroupSG pController) { return getSchema(); }
	public Locator getLocator(GroupSG pController) { return getLocator(); }
	public ParticleSG[] getParticles(GroupSG pController) throws SAXException {
		if (particles == null) {
			XSParticle[] xsParticles = ((XSGroup) getXSObject()).getParticles();
			particles = new ParticleSG[xsParticles.length];
			for (int i = 0;  i < xsParticles.length;  i++) {
				ParticleSGChain chain = (ParticleSGChain) pController.newParticleSG(xsParticles[i]);
				ParticleSG particle = new ParticleSGImpl(chain);
				particle.init();
				particles[i] = particle;
			}
		}
		return particles;
	}
	
	public void init(GroupSG pController) throws SAXException {
	}
	
	public boolean isAll(GroupSG pController) { return isAll; }
	public boolean isGlobal(GroupSG pController) { return isGlobal; }
	public boolean isChoice(GroupSG pController) { return isChoice; }
	public boolean isSequence(GroupSG pController) { return isSequence; }
	
	public XsQName getName(GroupSG pController) {
		if (qName == null) {
			throw new IllegalStateException("Attempt to obtain a local groups name.");
		}
		return qName;
	}
}
