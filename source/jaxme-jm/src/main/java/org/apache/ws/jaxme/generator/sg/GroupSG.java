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
package org.apache.ws.jaxme.generator.sg;

import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.SAXException;


/** <p>Interface of a source generator for model groups.</p>
 */
public interface GroupSG extends SGItem {
	/** <p>Returns whether the group is global.</p>
	 */
	public boolean isGlobal();

    /** If the group is global: Returns the groups name.
     * @throws IllegalStateException The group isn't global.
     * @see #isGlobal()
     */
    public XsQName getName();

	/** <p>Returns whether the groups compositor is "all".</p>
	 */
	public boolean isAll();
	
	/** <p>Returns whether the groups compositor is "choice".</p>
	 */
	public boolean isChoice();
	
	/** <p>Returns whether the groups compositor is "sequence".</p>
	 */
	public boolean isSequence();
	
	/** <p>Returns the groups {@link Context}.</p>
	 */
	public Context getClassContext() throws SAXException;
	
	/** <p>Returns the groups particles.</p>
	 */
	public ParticleSG[] getParticles() throws SAXException;
	
	/** <p>Creates a new instance of
	 * {@link org.apache.ws.jaxme.generator.sg.ParticleSGChain} generating the
	 * given particle.</p>
	 * <p><em>Implementation note</em>: The type
	 * {@link org.apache.ws.jaxme.generator.sg.ParticleSGChain}
	 * must not be exposed in the interface, because the interface
	 * class is used to generate this type. In other words, this
	 * interface must be compilable without the
	 * {@link org.apache.ws.jaxme.generator.sg.ParticleSGChain}
	 * interface.</p>
	 */
	public Object newParticleSG(XSParticle pParticle) throws SAXException;
}
