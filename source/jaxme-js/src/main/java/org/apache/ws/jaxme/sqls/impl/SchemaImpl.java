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
package org.apache.ws.jaxme.sqls.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.Table;


/** <p>Implementation of a schema.</p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SchemaImpl implements Schema {
   public static class NameImpl extends SQLFactoryImpl.IdentImpl implements Schema.Name {
      NameImpl(String pName) {
         super(pName);
      }
      public boolean equals(Object o) {
         if (o == null  ||  !(o instanceof Schema.Name)) {
            return false;
         }
         return super.equals(o);
      }
   }

   private SQLFactory sqlFactory;
   private Schema.Name name;
   private List tables = new ArrayList();

   /** <p>Creates a new instance of SchemaImpl.</p>
    * @param pFactory The {@link org.apache.ws.jaxme.sqls.SQLFactory}
    *   creating this instance.
    * @param pName The schema name.
    */
	protected SchemaImpl(SQLFactory pFactory, Schema.Name pName) {
      sqlFactory = pFactory;
      name = pName;      
	}

   public SQLFactory getSQLFactory() {
      return sqlFactory;
   }

   public Schema.Name getName() {
      return name;
   }

   public Table newTable(String pName) {
      return newTable(new TableImpl.NameImpl(pName));
   }

	public Table newTable(Table.Name pName) {
      if (pName == null) {
         throw new NullPointerException("A table name must not be null.");
      }
      Integer maxLength = getSQLFactory().getMaxTableNameLength();
      if (maxLength != null  &&  pName.getName().length() > maxLength.intValue()) {
         throw new IllegalArgumentException("The length of the table name " + pName +
                                             " exceeds the maximum length of " + maxLength);
      }
      Table table = getTable(pName);
      if (table != null) {
         throw new IllegalStateException("A table named " + table.getName() +
                                          " already exists in the schema " + getName());
      }
      table = ((SQLFactoryImpl) getSQLFactory()).newTableImpl(this, pName);
      tables.add(table);
      return table;
   }

	public Table getTable(Table.Name pName) {
      if (pName == null) {
         throw new NullPointerException("A table name must not be null.");
      }
      for (Iterator iter = getTables();  iter.hasNext();  ) {
         Table table = (Table) iter.next();
         if (getSQLFactory().isTableNameCaseSensitive()) {
            if (pName.getName().equalsIgnoreCase(table.getName().getName())) {
               return table;
            }
         } else {
            if (pName.equals(table.getName())) {
               return table; 
            }
         }
      }
      return null;
	}

   public Table getTable(String pName) {
      return getTable(new TableImpl.NameImpl(pName));
   }

	public Iterator getTables() {
      return tables.iterator();
	}

   public boolean equals(Object o) {
      if (o == null  ||  !(o instanceof Schema)) {
         return false;
      }
      Schema other = (Schema) o;
      if (getName() == null) {
         return other.getName() == null;
      } else {
         return getName().equals(other.getName());
      }
   }

   public int hashCode() {
      return getName() == null ? 0 : getName().hashCode();
   }
}
