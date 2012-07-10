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

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;


/** <p>The <code>persistence manager</code> (or <code>PM</code>
 * for short) is responsible for reading objects from the
 * database or storing them into the database.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface PM {
  /** <p>Initializes the PM. Called from the
   * {@link org.apache.ws.jaxme.impl.JAXBContextImpl} upon initialization.</p>
   * @param pManager The manager being queried for configuration details.
   */
  public void init(JMManager pManager) throws JAXBException;

  /** <p>Returns the manager being queried for configuration details.</p>
   */
  public JMManager getManager();

  /** <p>Reads documents matching the given query. For any document
   * matching, the Observer's notify method is executed with the
   * matching document as an argument.</p>
   *
   * @param pObserver This Observer is notified for any matching document.
   *   The document is added as an argument.
   * @param pQuery The query to perform.
   */
  public void select(Observer pObserver, String pQuery) throws JAXBException;

  /** <p>Reads documents matching the given query. For any document
   * matching, the Observer's notify method is executed with the
   * matching document as an argument.</p>
   * <p>The query may contain placeholders. If it does, you have
   * to supply an instance of {@link PMParams} with the placeholder
   * values. Example:
   * <pre>
   *   manager.select("Name = ? and Id = ?",
   *                  new PMParams().addString("Someone").addInt(4));
   * </pre></p>
   *
   * @param pObserver This Observer is notified for any matching document.
   *   The document is added as an argument.
   * @param pQuery The query to perform. May contain placeholders.
   * @param pPlaceHolderArgs An array of objects or null, if the
   *   query doesn't contain any placeholders.
   */
  public void select(Observer pObserver, String pQuery,
                      PMParams pPlaceHolderArgs) throws JAXBException;

  /** <p>Returns an iterator to all documents matching the given query.</p>
   *
   * @param pQuery The query to perform.
   */
  public java.util.Iterator select(String pQuery) throws JAXBException;

  /** <p>Returns an iterator to all documents matching the given query.
   * The query may contain placeholders. If it does, you have
   * to supply an instance of {@link PMParams} with the placeholder
   * values. Example:
   * <pre>
   *   manager.select("Name = ? and Id = ?",
   *                  new PMParams().addString("Someone").addInt(4));
   * </pre></p>
   *
   * @param pQuery The query to perform. May contain placeholders.
   * @param pPlaceHolderArgs An array of objects or null, if the
   *   query doesn't contain any placeholders.
   */
  public java.util.Iterator select(String pQuery,
                                    PMParams pPlaceHolderArgs)
    throws JAXBException;

  /** <p>Inserts the given document into the database.</p>
   */
  public void insert(Element element) throws JAXBException;

  /** <p>Updates the given document in the database.</p>
   */
  public void update(Element element) throws JAXBException;

  /** <p>Deletes the given document from the database.</p>
   */
  public void delete(Element element) throws JAXBException;

  /** <p>Creates a new, empty element.</p>
   */
  public Object create() throws JAXBException;
}
