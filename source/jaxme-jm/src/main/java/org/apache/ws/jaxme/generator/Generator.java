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
package org.apache.ws.jaxme.generator;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** <p>The Generator is a frontend for working with the
 * SchemaReaders, SourceWriters and whatever else.</p>
 * <p>
 * <strong>Usage:</strong> Generates java source definitions
 * from various inputs:
 * </p>
 * <ul>
 *  <li>{@link #generate(java.net.URL)}</li> 
 *  <li>{@link #generate(java.io.File)}</li> 
 *  <li>{@link #generate(org.xml.sax.InputSource)}</li>
 * </ul>
 * <p>
 * The inputs are interpreted into schema
 * definitions by the {@link SchemaReader} set by calling
 * {@link #setSchemaReader}. 
 * </p>
 * <p>
 * Other properties give fine grained
 * control over various aspects of the file generation:
 * </p>
 * <ul>
 *   <li>{@link #setForcingOverwrite}</li>
 *   <li>{@link #setSettingReadOnly}</li>
 *   <li>{@link #setTargetDirectory}</li>
 * </ul>
 * <p> 
 * and the processing of the schema:
 * </p>
 * <ul>
 *   <li>{@link #setValidating}</li>
 *   </li>{@link #setEntityResolver})</li>
 * </ul>
 * </p>
 * <p><em>Implementation note</em>: If you update this interface, you
 * should consider updating the following files and classes as
 * well:
 * <ul>
 *   <li>{@link Main}</li>
 *   <li>{@link XJCTask}</li>
 *   <li>docs/Reference.html</li>
 * </ul></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: Generator.java 358956 2005-12-25 00:43:38Z jochen $
 */
public interface Generator extends PropertySource {
  /** <p>Sets the SchemaReader to use.</p>
   */
  public void setSchemaReader(SchemaReader pSchemaReader);

  /** <p>Returns the SchemaReader being used.</p>
   */
  public SchemaReader getSchemaReader();

  /** Sets the directory where Java source files are being
   * created.
   */
  public void setTargetDirectory(File pDirectory);

  /** Returns the directory where Java source files are being
   * created.
   */
  public File getTargetDirectory();

  /** Sets the directory where resource files are being
   * created. By default, this is the Java source directory.
   */
  public void setResourceTargetDirectory(File pDirectory);

  /** Sets the directory where resource files are being
   * created. By default, this is the Java source directory.
   */
  public File getResourceTargetDirectory();

  /** <p>Sets whether the generator is using a validating XML
   * schema parser. Defaults to false.</p>
   */
  public void setValidating(boolean pValidating);

  /** <p>Returns whether the generator is using a validating XML
   * schema parser. Defaults to false.</p>
   */
  public boolean isValidating();

  /** <p>Returns whether the generator is forcing an overwrite of files.</p>
   */
  public boolean isForcingOverwrite();

  /** <p>Sets whether the generator is forcing an overwrite of files.</p>
   */
  public void setForcingOverwrite(boolean pIsForcingOverwrite);

  /** <p>Returns whether the generator will create files in read-only mode.</p>
   */
  public boolean isSettingReadOnly();

  /** <p>Sets whether the generator will create files in read-only mode.</p>
   */
  public void setSettingReadOnly(boolean pIsSettingReadOnly);

  /** <p>
    * Generates java source from the given <code>File</code>.
    * </p>
    * @param pFile reads the schema (from which the source is to be generated)
    * from the given file.
    * @return <code>SchemaSG</code> describing the source generated
    */
  public SchemaSG generate(File pFile) throws Exception;

  /** <p>
    * Generates java source from the given <code>URL</code>.
    * </p>
    * @param pURL reads the schema (from which the source is to be generated)
    * from the given URL.
    * @return <code>SchemaSG</code> describing the source generated
    */
  public SchemaSG generate(URL pURL) throws Exception;

  /** <p>
    * Generates java source from the given <code>URL</code>.
    * </p>
    * @param pSource reads the schema (from which the source is to be generated)
    * from the SAX Input Source. The System ID should be (if possible) set.
    * @return <code>SchemaSG</code> describing the source generated
    */
  public SchemaSG generate(InputSource pSource) throws Exception;

  /** <p>Returns a key for getting and setting custom data.</p>
   */
  public String getKey();

  /** <p>Sets the {@link EntityResolver} being used to import external
   * schemata.</p>
   */
  public void setEntityResolver(EntityResolver pEntityResolver);

  /** <p>Returns the {@link EntityResolver} being used to import external
   * schemata.</p>
   */
  public EntityResolver getEntityResolver();

    /** Sets the external schema bindings.
     */
  	public void addBindings(InputSource pSource)
  			throws ParserConfigurationException, SAXException, IOException;

  	/** Returns the external schema bindings, if any, or null.
     */
    public Document[] getBindings();
}
