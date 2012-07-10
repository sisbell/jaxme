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

import org.apache.ws.jaxme.sqls.SQLFactory;


/** <p>Interface of a DB2 BufferPool. This object is used to create a
 * <code>CREATE BUFFERPOOL ...</code> statement.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface BufferPool {
   public interface Name extends SQLFactory.Ident {
   }

   /** <p>Returns the BufferPool's name. BufferPool names are unique within the
    * database.</p>
    */
   public Name getName();

   /** <p>Returns the BufferPool's page size. See the secion "CREATE BUFFERPOOL"
    * in the DB2 reference manual for limitations on the value. Examples: 8192
    * (bytes) or 8K (Kilobytes).</p>
    * <p>Default is null, in which case the DB2 default (4K, as of this writing)
    * applies.</p>
    */
   public PageSize getPageSize();

   /** <p>Returns the buffer pools size in number of pages.</p>
    */
   public int getNumberOfPages();

   /** <p>Sets whether extended storage may be used. Defaults to null, in which
    * case the DB2 defaults are choosen.</p>
    */ 
   public void setExtendedStorage(Boolean pExtendedStorage);

   /** <p>Returns whether extended storage may be used. Defaults to false.</p>
    */ 
   public Boolean getExtendedStorage();
}
