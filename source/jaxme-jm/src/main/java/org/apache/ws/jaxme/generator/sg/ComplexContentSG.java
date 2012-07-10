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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.apache.ws.jaxme.generator.sg.ParticleSG;


/** Interface of a complex type with a complex content model.
 */
public interface ComplexContentSG {
    /** <p>Returns the items location in the schema; useful for
     * error messages.</p>
     */
    public Locator getLocator();

    /** <p>Initializes the item.</p>
     */
    public void init() throws SAXException;
	/** Returns, whether the types content model is empty.
	 */
	public boolean isEmpty();
	
	/** Returns, whether the types content model is mixed.
	 */
	public boolean isMixed();
	
	/** Returns the complex types root particle.
	 */
	public ParticleSG getRootParticle();

	/** Returns a list of all element particles, which are
	 * being generated as properties of the type.
	 */
	public ParticleSG[] getElementParticles() throws SAXException;
}
