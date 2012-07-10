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

import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** Interface of a particle.
 */
public interface ParticleSG {
    /** <p>Initializes the ParticleSG.</p>
     */
    public void init() throws SAXException;

    /** <p>Returns the particles locator.</p>
     */
    public Locator getLocator();
    
    /** <p>Returns whether the pariticle is a group. If so, you may use the
     * {@link #getGroupSG()} method.</p>
     * @see #getGroupSG()
     */
    public boolean isGroup();
    
    /** <p>Returns whether the particle is an element. If so, you may use the
     * {@link #getObjectSG()} method.</p>
     * @see #getObjectSG()
     */
    public boolean isElement();
    
    /** <p>Returns whether the particle is a wildcard. If so, you may use the
     * {@link #getObjectSG()} method.</p>
     * @see #getObjectSG()
     */
    public boolean isWildcard();
    
    /** <p>If the particle is a group, returns the particles {@link GroupSG}.</p>
     * @throws IllegalStateException The particle is no group.
     * @see #isGroup()
     */
    public GroupSG getGroupSG();
    
    /** <p>If the particle is an element or wildcard, returns the particles {@link ObjectSG}.</p>
     * @throws IllegalStateException The particle is neither an element nor a wildcard
     * @see #isElement()
     * @see #isWildcard()
     */
    public ObjectSG getObjectSG();
    
    /** <p>Returns the particles minOccurs value.</p>
     * @see #getMaxOccurs()
     */
    public int getMinOccurs();
    
    /** <p>Returns the particles maxOccurs value; -1 indicated "unbounded".</p>
     * @see #isMultiple()
     * @see #getMinOccurs()
     */
    public int getMaxOccurs();
    
    /** <p>Returns whether the particles multiplicity is 2 or greater.</p>
     * @see #getMaxOccurs()
     */
    public boolean isMultiple();
    
    /** <p>Creates a new instance of {@link org.apache.ws.jaxme.generator.sg.PropertySGChain}.</p>
     */
    public Object newPropertySGChain() throws SAXException;

    /** <p>Returns an instance of {@link org.apache.ws.jaxme.generator.sg.PropertySG}.</p>
     */
    public PropertySG getPropertySG() throws SAXException;
    
    /** <p>Invokes the given {@link SGlet} for all non null values.</p>
     */
    public void forAllNonNullValues(JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException;
}
