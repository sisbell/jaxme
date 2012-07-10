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

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Parts {
  /** <p>Inserts a constant value.</p>
   */
  public void addPart(Value pValue);

  /** <p>Inserts a column reference.</p>
   */
  public void addPart(ColumnReference pColumn);

  /** <p>Inserts a set of column references.</p>
   */
  public void addPart(ColumnReference[] pPart);

  /** Inserts a "case foo when x then a when y then b else c end"
   * clause.
   */
  public void addPart(Case pCase);

  /** <p>Inserts a subselect.</p>
   */
  public void addPart(SelectStatement pPart);
  
  /** <p>Inserts a String. The String will be properly escaped.</p>
   * @throws NullPointerException The paremeter <code>pString</code> is null.
   */
  public void addPart(String pString);
  
  /** <p>Inserts a NULL value.</p>
   */
  public void addPart();
  
  /** <p>Inserts a byte value, which will be inserted without quotes.</p>
   */
  public void addPart(byte pByte);
  
  /** <p>Inserts an int value, which will be inserted without quotes.</p>
   */
  public void addPart(int pInt);
  
  /** <p>Inserts a long value, which will be inserted without quotes.</p>
   */
  public void addPart(long pLong);
  
  /** <p>Inserts a short value, which will be inserted without quotes.</p>
   */
  public void addPart(short pShort);
  
  /** <p>Inserts a float value, which will be inserted without quotes.</p>
   */
  public void addPart(float pFloat);
  
  /** <p>Inserts a double value, which will be inserted without quotes.</p>
   */
  public void addPart(double pDouble);
  
  /** <p>Inserts a boolean value, which will be inserted as the word
   * <code>TRUE</code>, or <code>FALSE</code>, respectively.</p>
   */
  public void addPart(boolean pBoolean);

  /** <p>Inserts a function.</p>
   */
  public void addPart(Function pFunction);

  /** Inserts an arithmetic expression.
   */
  public void addPart(Expression pExpression);

  /** <p>Inserts raw SQL code.</p>
   */ 
  public void addRawSQLPart(String pRawSQL);
  
  /** <p>Inserts a placeholder.</p>
   */
  public void addPlaceholder();
  
  /** <p>Returns the number of parts.</p>
   */
  public int getNumParts();

  /** Returns the minimum number of parts.
   */
  public int getMinimumParts();

  /** Returns the maximum number of parts. Zero indicates unlimited.
   */
  public int getMaximumParts();

  /** <p>Returns an Iterator to the parts that have been added.</p>
   */
  public Iterator getParts();

  	/** Creates an arithmetic sum.
	 */
	public Expression createSUM();

	/** Creates an arithmetic product.
	 */
	public Expression createPRODUCT();

	/** Creates an arithmetic difference.
	 */
	public Expression createDIFFERENCE();

	/** Creates an arithmetic quotient.
	 */
	public Expression createQUOTIENT();
}
