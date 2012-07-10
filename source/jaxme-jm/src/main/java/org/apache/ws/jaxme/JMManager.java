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
package org.apache.ws.jaxme;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.impl.JAXBContextImpl;
import org.apache.ws.jaxme.impl.JMSAXDriver;
import org.apache.ws.jaxme.impl.JMSAXDriverController;
import org.apache.ws.jaxme.impl.JMSAXElementParser;
import org.xml.sax.SAXException;


/** <p>A JMManager controls the object factory (aka JAXBContext)
 * for a given document type. The document type is both identified
 * by its QName and its interface, which is extending JMElement.</p>
 */
public interface JMManager {
  /** <p>Returns a property value, which is used to configure
   * the manager. The property value is set in the configuration
   * file.</p>
   *
   * @param pName The property name
   * @return pValue The property value; null, if the property is not
   *   set.
   */
  public String getProperty(String pName);

  /** <p>Returns the {@link org.apache.ws.jaxme.impl.JAXBContextImpl},
   * that created this manager.</p>
   */
  public JAXBContextImpl getFactory();

  /** <p>Returns the QName of the document type that this
   * Manager controls.</p>
   */
  public QName getQName();

  /** Returns the interface matching the document type.
   * Usually, this is a  a subinterface of
   * {@link JMElement}.
   * However, for support of POJO's, we should not depend
   * on this.
   */
  public Class getElementInterface();

  /** Returns an instance of the element class. Same as
   * {@link #getElementS()}, except that it throws a
   * different exception.
   */
  public Object getElementJ() throws JAXBException;

  /** Returns an instance of the element class. Same as
   * {@link #getElementJ()}, except that it throws a
   * different exception.
   */
  public Object getElementS() throws SAXException;

  /** Returns the document types handler class.
   */
  public Class getHandlerClass();

  /** Returns an instance of the document types handler class.
   */
  public JMSAXElementParser getHandler() throws SAXException;

  /** Returns the document types driver class.
   */
  public Class getDriverClass();

  /** Returns an instance of the document types driver class.
   */
  public JMSAXDriver getDriver() throws SAXException;

  /** Returns the document types driver controller class.
   */
  public Class getDriverControllerClass();

  /** Returns an instance of the document types driver controller class.
   */
  public JMSAXDriverController getDriverController() throws SAXException;

  /** <p>Returns the persistency class. The persistency class
   * must be able to store documents in a database, update,
   * delete or retrieve them.</p>
   */
  public Class getPmClass();
}
