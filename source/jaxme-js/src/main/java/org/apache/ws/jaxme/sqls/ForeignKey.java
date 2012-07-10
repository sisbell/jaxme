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

import java.io.Serializable;
import java.util.Iterator;


/** <p>Interface of a foreign key.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface ForeignKey extends ColumnSet {
   public class Mode implements Serializable {
      private String name;
      private Mode(String pName) {
         name = pName;
      }
      public String getName() { return name; }
      public String toString() { return name; }
      public static final Mode CASCADE = new Mode("CASCADE");
      public static final Mode REJECT = new Mode("REJECT");
      public static final Mode SETNULL = new Mode("SETNULL");
      private static final Mode[] instances = new Mode[]{
         CASCADE, REJECT, SETNULL
      };
      public static Mode[] getInstances() {
         return instances;
      }
      public static Mode valueOf(String pMode) {
         for (int i = 0;  i < instances.length;  i++) {
            if (instances[i].getName().equals(pMode)) {
               return instances[i];
            }
         }
         throw new IllegalArgumentException("Unknown mode: " + pMode);
      }
      public int hashCode() {
         return name.hashCode();
      }
      public boolean equals(Object o) {
         return o != null  &&  (o instanceof Mode)  &&  name.equals(((Mode) o).name);
      }
   }

   public interface ColumnLink {
      /** <p>Returns the column referencing a column in the referenced
       * table.</p>
       */
      public Column getLocalColumn();
      /** <p>Returns the column being referenced in the referenced
       * table.</p>
       */
      public Column getReferencedColumn();
   }

   /** <p>Returns the referenced table.</p>
    */
   public Table getReferencedTable();

   /** <p>Sets the OnDelete mode.</p>
    */
   public void setOnDelete(Mode pMode);

   /** <p>Returns the OnDelete mode.</p>
    */
   public Mode getOnDelete();

   /** <p>Sets the OnUpdate mode.</p>
    */
   public void setOnUpdate(Mode pMode);

   /** <p>Returns the OnUpdate mode.</p>
    */
   public Mode getOnUpdate();

   /** <p>Adds a reference between the given columns.</p>
    * @param pColumn A column of the table, on which the foreign key is
    *   defined
    * @param pReferencedColumn A column of the referenced table
    */
   public void addColumnLink(Column pColumn, Column pReferencedColumn);

   /** <p>Adds a reference between the given columns.</p>
    * @param pName Column name of the table, on which the foreign
    *   key is defined
    * @param pReferencedName Column name of the referenced table.
    */
   public void addColumnLink(Column.Name pName, Column.Name pReferencedName);

   /** <p>Adds a reference between the given columns.</p>
    * @param pName Column name of the table, on which the foreign
    *   key is defined
    * @param pReferencedName Column name of the referenced table.
    */
   public void addColumnLink(String pName, String pReferencedName);

   /** <p>Returns all column references in the foreign key. Any instance
    * returned by the {@link Iterator} is an instance of
    * {@link ForeignKey.ColumnLink}.</p>
    */
   public Iterator getColumnLinks();

   /** <p>Returns the set of referenced columns.</p>
    */
   public ColumnSet getReferencedColumns();
}
