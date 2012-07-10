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

import org.apache.ws.jaxme.sqls.Case;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.Expression;
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.Parts;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.Statement;
import org.apache.ws.jaxme.sqls.Value;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class PartsImpl implements Parts {
  private final Statement statement;
  private final List parts = new ArrayList();

  protected PartsImpl(Statement pStatement) {
    statement = pStatement;
  }

  protected void add(Object o) {
    parts.add(o);
  }

  /** Returns the statement, to which the part belongs.
   */
  public Statement getStatement() {
    return statement;
  }

  public void addPart(Value pValue) {
    if (pValue == null) {
      throw new NullPointerException("A constant value must not be null.");
    }
    parts.add(pValue);
  }

  public void addPart(ColumnReference pColumn) {
    if (pColumn == null) {
      throw new NullPointerException("Referenced column must not be null.");
    }
    add(pColumn);
  }

  public void addPart(ColumnReference[] pColumns) {
    if (pColumns == null) {
      throw new NullPointerException("The array of referenced columns must not be null.");
    }
    for (int i = 0;  i < pColumns.length;  i++) {
      if (pColumns[i] == null) {
        throw new NullPointerException("The referenced column with number " + i + " must not be null.");
      }
    }
    add(pColumns);
  }

  public void addPart(SelectStatement pStatement) {
    if (pStatement == null) {
      throw new NullPointerException("The subselect statement must not be null.");
    }
    add(pStatement);
  }

  public void addPart(String pString) {
    add(new ValueImpl(Value.Type.STRING, pString));
  }

  public void addPart() {
    add(new ValueImpl(Value.Type.NULL, null));
  }

  public void addPart(byte pByte) {
    add(new ValueImpl(Value.Type.BYTE, new Byte(pByte)));
  }

  public void addPart(int pInt) {
    add(new ValueImpl(Value.Type.INT, new Integer(pInt)));
  }

  public void addPart(long pLong) {
    add(new ValueImpl(Value.Type.LONG, new Long(pLong)));
  }

  public void addPart(short pShort) {
    add(new ValueImpl(Value.Type.SHORT, new Short(pShort)));
  }

  public void addPart(float pFloat) {
    add(new ValueImpl(Value.Type.FLOAT, new Float(pFloat)));
  }

  public void addPart(double pDouble) {
    add(new ValueImpl(Value.Type.DOUBLE, new Double(pDouble)));
  }

  public void addPart(boolean pBoolean) {
    add(new ValueImpl(Value.Type.BOOLEAN, pBoolean ? Boolean.TRUE : Boolean.FALSE));
  }

  public void addPart(Function pFunction) {
    add(pFunction);
  }

  public void addPart(Expression pExpression) {
	add(pExpression);
  }

  public void addPlaceholder() {
    add(new ValueImpl(Value.Type.PLACEHOLDER, null));
  }

  /** <p>Inserts raw SQL code.</p>
   */ 
  public void addRawSQLPart(String pRawSQL) {
    add(getStatement().getSQLFactory().getObjectFactory().newRawSQL(pRawSQL));
  }

  public int getNumParts() {
    return parts.size();
  }

  public Iterator getParts() {
    return parts.iterator();
  }

    public void addPart(Case pCase) {
        add(pCase);
    }

	private Expression newExpression(Expression.Type pType) {
		Statement st = getStatement();
		Expression result = st.getSQLFactory().getObjectFactory().createExpression(st, pType);
		addPart(result);
		return result;
	}

	public Expression createSUM() { return newExpression(Expression.SUM); }
	public Expression createPRODUCT() { return newExpression(Expression.PRODUCT); }
	public Expression createDIFFERENCE() { return newExpression(Expression.DIFFERENCE); }
	public Expression createQUOTIENT() { return newExpression(Expression.QUOTIENT); }
}
