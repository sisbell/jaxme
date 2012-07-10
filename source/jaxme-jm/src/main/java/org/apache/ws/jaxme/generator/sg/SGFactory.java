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

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.xs.XSAny;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.Facet;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.xml.sax.SAXException;


/** <p>Interface of a factory for SG classes.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SGFactory {
  /** <p>Returns the {@link org.apache.ws.jaxme.generator.Generator}.</p>
   */
  public Generator getGenerator();

  /** <p>Initializes the {@link SGFactory}.</p>
   */
  public void init();

  /** <p>Creates a new instance of {@link org.apache.ws.jaxme.xs.xml.XsObjectFactory},
   * used by the parser.</p>
   */
  public XsObjectFactory newXsObjectFactory() throws SAXException;

  /** <p>Creates a new instance of {@link org.apache.ws.jaxme.xs.XSObjectFactory},
   * used by the parser.</p>
   */
  public XSObjectFactory newXSObjectFactory() throws SAXException;

  /** <p>Creates a new instance of {@link SchemaSG} generating the
   * given schema.</p>
   */
  public Object newSchemaSG(XSSchema pSchema) throws SAXException;
  /** <p>Returns an instance of {@link SchemaSG} generating the
   * given schema.</p>
   */
  public SchemaSG getSchemaSG(XSSchema pSchema) throws SAXException;
  /** <p>Returns the factorys instance of {@link SchemaSG}.</p>
   * @throws IllegalStateException The factory has not yet created an
   *    instance of SchemaSG. In other words, the methods
   *    {@link #newSchemaSG(XSSchema)} and {@link #getSchemaSG(XSSchema)}
   *    have not yet been invoked.
   */
  public SchemaSG getSchemaSG();

  /** <p>Creates a new instance of {@link ObjectSG} generating the
   * given element.</p>
   */
  public Object newObjectSG(XSElement pElement) throws SAXException;

  /** <p>Returns an instance of {@link ObjectSG} generating the
   * given element.</p>
   */
  public ObjectSG getObjectSG(XSElement pElement) throws SAXException;

  /** <p>Creates a new instance of {@link ObjectSG} generating the
   * given element in the given context.</p>
   */
  public Object newObjectSG(XSElement pElement, Context pContext) throws SAXException;

  /** <p>Returns an instance of {@link ObjectSG} generating the given
   * element in the given context.</p>
   */
  public ObjectSG getObjectSG(XSElement pElement, Context pContext) throws SAXException;

  /** <p>Creates a new instance of {@link ObjectSG} generating the given
   * wildcard in the given context.</p>
   */
  public Object newObjectSG(XSAny any);

  /** <p>Returns an instance of {@link ObjectSG} generating the given
   * wildcard in the given context.</p>
   */
  public ObjectSG getObjectSG(XSAny pWildcard, Context pContext) throws SAXException;

  /** <p>Creates a new instance of {@link GroupSG} generating the
   * given schema.</p>
   */
  public GroupSG newGroupSG(XSGroup pGroup) throws SAXException;

  /** <p>Returns an instance of {@link GroupSG} generating the
   * given schema.</p>
   */
  public GroupSG getGroupSG(XSGroup pGroup) throws SAXException;

  /** <p>Creates a new instance of {@link GroupSG}, which is embedded
   * into the given {@link Context}.</p>
   */
  public GroupSG newGroupSG(XSGroup pGroup, Context pClassContext) throws SAXException;

  /** <p>Returns an instance of {@link GroupSG}, which is embedded
   * into the given {@link Context}.</p>
   */
  public GroupSG getGroupSG(XSGroup pGroup, Context pClassContext) throws SAXException;

  /** <p>Creates a new, global instance of
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain}
   * generating the given type.</p>
   * <p><em>Implementation note</em>: The type
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain}
   * must not be exposed in the interface, because the interface
   * class is used to generate this type. In other words, this
   * interface must be compilable without the
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain}
   * interface.</p>
   */
  public Object newTypeSG(XSType pType, JAXBProperty.BaseType pBaseType) throws SAXException;

  /** <p>Returns a global instance of {@link TypeSG} generating
   * the given type.</p>
   */
  public TypeSG getTypeSG(XSType pType, JAXBProperty.BaseType pBaseType) throws SAXException;

  /** <p>Creates a new, local instance of
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain},
   * generating the given type within the given {@link Context}.
   * <p><em>Implementation note</em>: The type
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain}
   * must not be exposed in the interface, because the interface
   * class is used to generate this type. In other words, this
   * interface must be compilable without the
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain}
   * interface.</p>
   */
  public Object newTypeSG(XSType pType, Context pClassContext, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException;

  /** <p>Returns a local instance of {@link TypeSG} generating
   * the given type within the given {@link Context}.</p>
   */
  public TypeSG getTypeSG(XSType pType, Context pClassContext, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException;

  /** <p>Creates a new, local instance of
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain},
   * as if it were a global type with the given name.</p>
   * <p><em>Implementation note</em>: The type
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain}
   * must not be exposed in the interface, because the interface
   * class is used to generate this type. In other words, this
   * interface must be compilable without the
   * {@link org.apache.ws.jaxme.generator.sg.TypeSGChain}
   * interface.</p>
   */
  public Object newTypeSG(XSType pType, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException;

  /** <p>Returns a new instance of {@link TypeSG} generating
   * the given type, as if it were a global type with the
   * given name.</p>
   */
  public TypeSG getTypeSG(XSType pType, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException;

  /** <p>Creates a new instance of {@link XSParser}.</p>
   */
  public XSParser newXSParser() throws SAXException;

  /** <p>Creates a new enumeration facet.</p>
   */
  public Facet newFacet(XSType pType, XSEnumeration[] pEnumerations) throws SAXException;

  /** <p>Returns an array of all groups created by the factory.</p>
   */
  public GroupSG[] getGroups();

  /** <p>Returns an array of all types created by the factory.</p>
   */
  public TypeSG[] getTypes();

  /** <p>Returns an array of all objects created by the factory.</p>
   */
  public ObjectSG[] getObjects();
}
