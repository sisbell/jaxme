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

import java.util.Collection;

import org.apache.ws.jaxme.sqls.SQLGenerator;


/** <p>Interface of an SQL generator for DB2 databases.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface DB2SQLGenerator extends SQLGenerator {
  /** <p>Returns an SQL statement creating the given BufferPool.</p>
   */
  public Collection getCreate(BufferPool pBufferPool);

  /** <p>Returns an SQL statement dropping the given BufferPool.</p>
   */
  public Collection getDrop(BufferPool pBufferPool);

  /** <p>Returns an SQL statement creating the given TableSpace.</p>
   */
  public Collection getCreate(TableSpace pTableSpace);

  /** <p>Returns an SQL statement dropping the given TableSpace.</p>
   */
  public Collection getDrop(TableSpace pTableSpace);

  /** <p>Sets whether the generated <code>CREATE TABLE</code> statements
   * will contain table space references or not. By default they will.</p>
   */
  public void setCreatingTableSpaceReferences(boolean pCreatingTableSpaceReferences);

  /** <p>Returns whether the generated <code>CREATE TABLE</code> statements
   * will contain table space references or not. By default they will.</p>
   */
  public boolean isCreatingTableSpaceReferences();
}
