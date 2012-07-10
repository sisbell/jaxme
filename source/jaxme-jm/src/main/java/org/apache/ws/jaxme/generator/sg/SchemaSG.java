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

import java.util.Map;

import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.SGItem;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/** <p>Interface of a source generator for the whole schema.</p>
 * <p>
 * Specifies the source representing a complete schema.
 * The actual generation of the concrete source files
 * is delegated to the <code>JavaSourceFactory</code>
 * available by calling {@link #getJavaSourceFactory}.
 * Various portions of the source artifacts are made
 * available through property accessors.
 * </p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SchemaSG extends SGItem {
  /** <p>Returns the schemas global types.</p>
   */
  public TypeSG[] getTypes() throws SAXException;

  /** <p>Returns the schemas global type with the given name or
   * null, if no such type exists.</p>
   */
  public TypeSG getType(XsQName pName) throws SAXException;

  /** <p>Returns the schemas model groups.</p>
   */
  public GroupSG[] getGroups() throws SAXException;

  /** <p>Returns the schemas global group with the given name or
   * null, if no such type exists.</p>
   */
  public GroupSG getGroup(XsQName pName) throws SAXException;

  /** <p>Returns the schemas global attributes and elements.</p>
   */
  public ObjectSG[] getObjects() throws SAXException;

  /** <p>Returns the schemas global element with the given name or
   * null, if no such element exists.</p>
   */
  public ObjectSG getElement(XsQName pName) throws SAXException;

  /** <p>Returns the schemas global elements.</p>
   */
  public ObjectSG[] getElements() throws SAXException;

  /** <p>Returns the schemas collection type, as specified by the
   * JAXB globalBindings tag.</p>
   */
  public String getCollectionType();

  /** <p>Returns the schemas {@link org.apache.ws.jaxme.js.JavaSourceFactory}.</p>
   */
  public JavaSourceFactory getJavaSourceFactory();

  /** <p>Performs the source generation.</p>
   */
  public void generate() throws SAXException;

  /** <p>Returns whether the schema prefers model group binding style or not.
   * Defaults to false.</p>
   */
  public boolean isBindingStyleModelGroup();

  /** <p>Returns whether the value "choiceContentProperty" is enabled.
   * Defaults to false.</p>
   */
  public boolean isChoiceContentProperty();

  /** <p>Returns whether the property "enableFailFastCheck" is enabled.
   * Defaults to false.</p>
   */
  public boolean isFailFastCheckEnabled();

  /** <p>Returns whether the property "enableJavaConventions" is enabled.
   * Defaults to true.</p>
   */
  public boolean isJavaNamingConventionsEnabled();

  /** <p>Returns whether the property "isFixedAttributeConstantProperty" is
   * enabled. Defaults to false.</p>
   */
  public boolean isFixedAttributeConstantProperty();

  /** <p>Returns whether the property "generateIsSetMethod" is enabled.
   * Defaults to false.</p>
   */
  public boolean isGeneratingIsSetMethod();

  /** <p>Returns whether the property "underscoreBinding" has the value
   * "asWordSeparator". Defaults to true.</p>
   */
  public boolean isUnderscoreWordSeparator();

  /** <p>Returns the globally configured instances of {@link JAXBJavaType}.</p>
   */
  public JAXBJavaType[] getJAXBJavaTypes();

  /** <p>Returns the globally configured value for <code>typesafeenumbase</code>.</p>
   */
  public XsQName[] getTypesafeEnumBase();

    /** <p>Creates the "jaxb.properties" file.</p>
     */
  	public void generateJaxbProperties() throws SAXException;

    /** Generates the "Configuration.xml" files.
     */
    public void generateConfigFiles() throws SAXException;
    /** Called by {@link #generateConfigFiles()} to generate the
     * config files.
     * @return A map of config files. The map keys are the package
     * names. The map values are instances of {@link org.w3c.dom.Document}
     * with the config file contents.
     */
    public Map getConfigFiles() throws SAXException;
    /** Called by {@link #generateConfigFiles()} for actually
     * creating the config files.
     * @param pPackageName A package name, which has been returned
     * by {@link #getConfigFiles()} as a map key.
     * @param pDoc A document, which has been returned by
     * {@link #getConfigFiles()} as the package names value.
     */
    public void generateConfigFile(String pPackageName, Document pDoc) throws SAXException;
    /** Called for customizing an elements manager.
     */
    public void createConfigFile(ObjectSG pElement, Element pManager);
    /** Called for customizing a types manager.
     */
	public void createConfigFile(TypeSG pType, Element pManager);
}
