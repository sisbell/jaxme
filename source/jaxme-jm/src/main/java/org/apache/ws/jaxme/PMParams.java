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
package org.apache.ws.jaxme;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


/** <p>Implementation of a parameter object for use in
 * {@link PM#select(Observer, String, PMParams)}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class PMParams implements Serializable {
  /** <p>A single parameter.</p>
   */
  public static class Param {
    private int type;
    private Object value;
    /** <p>Creates a new parameter with the given type and value.</p>
     */
    public Param(int pType, Object pValue) {
      type = pType;
      value = pValue;
    }
    public int getType() { return type; }
    public Object getValue() { return value; }
  }

  private List params;
  private int max, start;
  private boolean distinct;

  /** <p>Adds a new parameter.</p>
   */
  public void addParam(Param pParam) {
    if (params == null) { params = new ArrayList(); }
    params.add(pParam);
  }

  /** <p>Adds a String parameter.</p>
   */
  public void addParam(String pParam) {
    addParam(new Param(Types.VARCHAR, pParam));
  }

  /** <p>Adds a Long parameter.</p>
   */
  public void addParam(Long pParam) {
    addParam(new Param(Types.BIGINT, pParam));
  }

  /** <p>Adds a long parameter.</p>
   */
  public void addParam(long pParam) {
    addParam(new Param(Types.BIGINT, new Long(pParam)));
  }

  /** <p>Adds an Integer parameter.</p>
   */
  public void addParam(Integer pParam) {
    addParam(new Param(Types.INTEGER, pParam));
  }

  /** <p>Adds an int parameter.</p>
   */
  public void addParam(int pParam) {
    addParam(new Param(Types.INTEGER, new Integer(pParam)));
  }

  /** <p>Adds a Short parameter.</p>
   */
  public void addParam(Short pParam) {
    addParam(new Param(Types.SMALLINT, pParam));
  }

  /** <p>Adds a short parameter.</p>
   */
  public void addParam(short pParam) {
    addParam(new Param(Types.SMALLINT, new Short(pParam)));
  }

  /** <p>Adds a Byte parameter.</p>
   */
  public void addParam(Byte pParam) {
    addParam(new Param(Types.TINYINT, pParam));
  }

  /** <p>Adds a byte parameter.</p>
   */
  public void addParam(byte pParam) {
    addParam(new Param(Types.TINYINT, new Byte(pParam)));
  }

  /** <p>Adds a DateTime parameter.</p>
   */
  public void addDateTimeParam(Calendar pParam) {
    addParam(new Param(Types.TIMESTAMP, pParam));
  }

  /** <p>Adds a VARBINARY parameter.</p>
   */
  public void addParam(byte[] pParam) {
    addParam(new Param(Types.VARBINARY, pParam));
  }

  /** <p>Adds a Date parameter.</p>
   */
  public void addDateParam(Calendar pParam) {
    addParam(new Param(Types.DATE, pParam));
  }

  /** <p>Adds a Time parameter.</p>
   */
  public void addTimeParam(Calendar pParam) {
    addParam(new Param(Types.TIME, pParam));
  }

  /** <p>Sets the maximum number of result documents.</p>
   */
  public void setMaxResultDocuments(int pMax) {
    max = pMax;
  }

  /** <p>Returns the maximum number of result documents
   * or 0 (default) for an unlimited number.</p>
   */
  public int getMaxResultDocuments() {
    return max;
  }

  /** <p>Sets the maximum number of documents to skip
   * at the beginning (soft cursoring).</p>
   */
  public void setSkippedResultDocuments(int pStart) {
    start = pStart;
  }

  /** <p>Sets the maximum number of documents to skip
   * at the beginning or 0 (default) to skip no documents.</p>
   */
  public int getSkippedResultDocuments() {
    return start;
  }

  /** <p>Returns the number of parameters added with
   * <code>addParam()</code>.</p>
   */
  public int getNumParams() {
    return (params == null) ? 0 : params.size()/2;
  }

  /** <p>Returns an {@link Iterator} to the list of parameters. Any
   * element in the list is an instance of {@link PMParams.Param}.</p>
   */
  public Iterator getParams() {
    return params.iterator();
  }

  /** <p>Sets whether the query should guarantee to return only
   * distinct objects by activating the DISTINCT clause.</p>
   */
  public void setDistinct(boolean pDistinct) {
    distinct = pDistinct;
  }

  /** <p>Returns whether the query should guarantee to return only
   * distinct objects by activating the DISTINCT clause.</p>
   */
  public boolean isDistinct() {
    return distinct;
  }
}
