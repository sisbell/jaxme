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
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SetStatement;
import org.apache.ws.jaxme.sqls.Value;
import org.apache.ws.jaxme.sqls.Column.Name;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class SetStatementImpl extends StatementImpl implements SetStatement {
  public static class SetValueImpl implements SetStatement.SetValue {
    private final ColumnReference columnReference;
    private final Object value;
    public SetValueImpl(ColumnReference pColumn, Object pValue) {
      if (pColumn == null) {
        throw new NullPointerException("Column reference must not be null.");
      }
      if (pValue == null) {
        throw new NullPointerException("Value must not be null.");
      }
      columnReference = pColumn;
      value = pValue;
    }

    public ColumnReference getColumnReference() { return columnReference; }
    public Object getValue() { return value; }
  }

  private List setValues = new ArrayList();

  protected SetStatementImpl(SQLFactory pFactory) {
    super(pFactory);
  }

  public Iterator getSetValues() { return setValues.iterator(); }

  public void addSet(ColumnReference pColumn, Value pValue) {
    addSet(pColumn, pValue);
  }
  protected void addSet(ColumnReference pRef, Object pValue) {
    if (!pRef.getTableReference().equals(getTableReference())) {
      throw new IllegalArgumentException("The column has an invalid table reference.");
    }
    setValues.add(new SetValueImpl(pRef, pValue));
  }
  public void addSet(Column pColumn, Object pValue) {
    addSet(getTableReference().newColumnReference(pColumn), pValue);
  }
  public void addSet(String pColumn, Object pValue) {
    addSet(getTableReference().newColumnReference(pColumn), pValue);
  }
  public void addSet(Column.Name pColumn, Object pValue) {
    addSet(getTableReference().newColumnReference(pColumn), pValue);
  }
  public void addSet(Column pColumn, Value pValue) {
    addSet(pColumn, (Object) pValue);
  }
  public void addSet(Column.Name pColumn, Value pValue) {
    addSet(getTableReference().newColumnReference(pColumn), (Object) pValue);
  }
  public void addSet(String pColumn, Value pValue) {
    addSet(getTableReference().newColumnReference(pColumn), (Object) pValue);
  }

  public void addSetNull(Column pColumn) {
    addSet(pColumn, new ValueImpl(Value.Type.NULL, null));
  }
  public void addSetNull(Column.Name pColumn) {
    addSet(pColumn, new ValueImpl(Value.Type.NULL, null));
  }
  public void addSetNull(String pColumn) {
    addSet(pColumn, new ValueImpl(Value.Type.NULL, null));
  }

  public void addSet(Column pColumn) {
    if (pColumn == null) {
      throw new NullPointerException("The column being set must not be null.");
    }
    addSet(pColumn, new ValueImpl(Value.Type.PLACEHOLDER, null));
  }
  public void addSet(Column.Name pColumn) {
    addSet(pColumn, new ValueImpl(Value.Type.PLACEHOLDER, null));
  }
  public void addSet(String pColumn) {
    addSet(pColumn, new ValueImpl(Value.Type.PLACEHOLDER, null));
  }

  public void addSet(Column pColumn, byte pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.BYTE, new Byte(pValue)));
  }
  public void addSet(Column.Name pColumn, byte pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.BYTE, new Byte(pValue)));
  }
  public void addSet(String pColumn, byte pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.BYTE, new Byte(pValue)));
  }

  public void addSet(Column pColumn, short pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.SHORT, new Short(pValue)));
  }
  public void addSet(Column.Name pColumn, short pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.SHORT, new Short(pValue)));
  }
  public void addSet(String pColumn, short pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.SHORT, new Short(pValue)));
  }

  public void addSet(Column pColumn, int pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.INT, new Integer(pValue)));
  }
  public void addSet(Column.Name pColumn, int pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.INT, new Integer(pValue)));
  }
  public void addSet(String pColumn, int pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.INT, new Integer(pValue)));
  }

  public void addSet(Column pColumn, long pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.LONG, new Long(pValue)));
  }
  public void addSet(Column.Name pColumn, long pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.LONG, new Long(pValue)));
  }
  public void addSet(String pColumn, long pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.LONG, new Long(pValue)));
  }

  public void addSet(Column pColumn, float pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.FLOAT, new Float(pValue)));
  }
  public void addSet(Column.Name pColumn, float pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.FLOAT, new Float(pValue)));
  }
  public void addSet(String pColumn, float pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.FLOAT, new Float(pValue)));
  }

  public void addSet(Column pColumn, boolean pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.BOOLEAN, pValue ? Boolean.TRUE : Boolean.FALSE));
  }
  public void addSet(Column.Name pColumn, boolean pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.BOOLEAN, pValue ? Boolean.TRUE : Boolean.FALSE));
  }
  public void addSet(String pColumn, boolean pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.BOOLEAN, pValue ? Boolean.TRUE : Boolean.FALSE));
  }

  public void addSet(Column pColumn, String pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.STRING, pValue));
  }
  public void addSet(Column.Name pColumn, String pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.STRING, pValue));
  }
  public void addSet(String pColumn, String pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.STRING, pValue));
  }

  public void addSetDateTime(Column pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.DATETIME, pValue));
  }
  public void addSetDateTime(Column.Name pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.DATETIME, pValue));
  }
  public void addSetDateTime(String pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.DATETIME, pValue));
  }

  public void addSetTime(Column pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.TIME, pValue));
  }
  public void addSetTime(Column.Name pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.TIME, pValue));
  }
  public void addSetTime(String pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.TIME, pValue));
  }

  public void addSetDate(Column pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.DATE, pValue));
  }
  public void addSetDate(Column.Name pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.DATE, pValue));
  }
  public void addSetDate(String pColumn, Calendar pValue) {
    addSet(pColumn, new ValueImpl(Value.Type.DATE, pValue));
  }
}
