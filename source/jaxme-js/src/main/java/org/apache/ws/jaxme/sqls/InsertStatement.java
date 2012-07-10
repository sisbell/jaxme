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


/** <p>Interface of an INSERT statement.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface InsertStatement extends SetStatement {
      /** <p>Adds a subselect; the columns returned by the subselect
       * will be inserted. This is used for bulk inserts like
       * <code>INSERT INTO foo (col1, col2, col2) (SELECT * FROM ...)</code>.</p>
       * @param pStatement The statement being performed to create
       *   the rows being inserted.
       */
      public void setSubSelect(SelectStatement pStatement);

      /** <p>Returns a subselect previously being added with
       * {@link #setSubSelect(SelectStatement)}, or null.</p>
       */
      public SelectStatement getSubSelect();
}
