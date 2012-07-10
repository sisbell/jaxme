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

import org.apache.ws.jaxme.sqls.BinaryColumn;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.StringColumn;
import org.apache.ws.jaxme.sqls.Table;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class AbstractColumn implements Column, StringColumn, BinaryColumn {
  private final Column.Name name;
  private final Column.Type type;
  private boolean nullable;
  private Long length;
  private Object customData;

  protected AbstractColumn(Column.Name pName, Column.Type pType) {
    if (pName == null) {
      throw new NullPointerException("The column name must not be null.");
    }
    if (pType == null) {
      throw new NullPointerException("The column type must not be null.");
    }
    name = pName;
    type = pType;
  }

  public Column.Name getName() { return name; }
  public Column.Type getType() { return type; }
  public boolean isNullable() { return nullable; }
  public void setNullable(boolean pNullable) { nullable = pNullable; }


  public boolean equals(Object o) {
    if (o == null  ||  !(o instanceof Column)) {
      return false;
    }
    Column other = (Column) o;
    Table table = getTable();
    if (table == null) {
      if (other.getTable() != null) {
        return false;
      }
    } else {
      if (!table.equals(other.getTable())) {
        return false;
      }
    }
    return getName().equals(other.getName());
  }

  public int hashCode() {
    return getTable().hashCode() + getName().hashCode();
  }

  public boolean hasFixedLength() {
    Column.Type myType = getType();
    if (Column.Type.CHAR.equals(myType)  ||
        Column.Type.BINARY.equals(myType)) {
      return true;
    } else if (Column.Type.VARCHAR.equals(myType)
               ||  Column.Type.VARBINARY.equals(myType)
               ||  Column.Type.BLOB.equals(myType)
               ||  Column.Type.OTHER.equals(myType)
               ||  Column.Type.CLOB.equals(myType)) {
      return false;
    } else {
      throw new IllegalStateException("Invalid data type for fixed length.");
    }
  }

  public boolean isStringColumn() {
    Column.Type myType = getType();
    return Column.Type.CHAR.equals(myType) || Column.Type.VARCHAR.equals(myType) ||
            Column.Type.CLOB.equals(myType);
  }

  public boolean isBinaryColumn() {
    Column.Type myType = getType();
    return Column.Type.BINARY.equals(myType) || Column.Type.VARBINARY.equals(myType) ||
            Column.Type.BLOB.equals(myType) || Column.Type.OTHER.equals(myType);
  }

  public void setLength(Long pLength) {
    length = pLength;
  }

  public void setLength(long pLength) {
    setLength(new Long(pLength));
  }

  public Long getLength() {
    return length;
  }

  public Object getCustomData() {
    return customData;
  }

  public void setCustomData(Object pCustomData) {
    customData = pCustomData;
  }
}
