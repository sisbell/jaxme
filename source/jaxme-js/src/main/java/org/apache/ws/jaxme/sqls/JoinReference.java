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

/** <p>This interface allows to specify join constraints.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface JoinReference extends SelectTableReference {
   /** <p>Returns whether this is a left join, as created by
    * {@link SelectTableReference#join(Table)}.</p>
    */
   public boolean isJoin();

   /** <p>Returns whether this is a left outer join, as created by
    * {@link SelectTableReference#leftOuterJoin(Table)}.</p>
    */
   public boolean isLeftOuterJoin();

   /** <p>If this is a left join or a left outer join: Returns
    * the joins left table.</p>
    */
   public SelectTableReference getLeftJoinedTableReference();

   /** <p>Returns the references ON condition, if any. The method
    * result is a combined constraint with {@link CombinedConstraint#getType()}
    * == {@link org.apache.ws.jaxme.sqls.CombinedConstraint.Type#AND}.</p>
    */
   public CombinedConstraint getOn();
}
