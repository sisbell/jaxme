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
package org.apache.ws.jaxme.sqls;

import java.util.Iterator;

/** <p>A common base interface for {@link Index} and {@link ForeignKey}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface ColumnSet {
   /** <p>Returns the table on which the foreign key is defined.</p>
   */
  public Table getTable();

   /** <p>Returns the column sets columns. In the case of an {@link Index},
    * these are the columns that have been added via {@link Index#addColumn(Column)}.
    * In the case of a {@link ForeignKey}, these are the local columns of
    * column links.</p>
   */
  public Iterator getColumns();
}
