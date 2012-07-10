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
package org.apache.ws.jaxme.sqls.db2;

import java.util.Iterator;

import org.apache.ws.jaxme.sqls.SQLFactory;


/** <p>Interface of an SQL factory for DB2 databases.</p>
*
* @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
*/
public interface DB2SQLFactory extends SQLFactory {
  /** <p>Creates a new {@link TableSpace} with the given name.</p>
   */
  public TableSpace newTableSpace(String pName, TableSpace.Type pType);

  /** <p>Creates a new {@link TableSpace} with the given name.</p>
   */
  public TableSpace newTableSpace(TableSpace.Name pName, TableSpace.Type pType);

  /** <p>Returns the tablespace with the given name or null, if no such
   * tablespace exists.</p>
   */
  public TableSpace getTableSpace(TableSpace.Name pName);

  /** <p>Returns the tablespace with the given name or null, if no such
   * tablespace exists.</p>
   */
  public TableSpace getTableSpace(String pName);

  /** <p>Returns a list of all tablespaces.
   * This Iterator does <em>not</em> include the predefined table spaces
   * <code>SYSCATSPACE</code>, <code>TEMPSPACE1</code>, or
   * <code>USERSPACE1</code>. These table spaces are accessible via
   * {@link #getTableSpace(TableSpace.Name)} only.</p>
   */
  public Iterator getTableSpaces();
}
