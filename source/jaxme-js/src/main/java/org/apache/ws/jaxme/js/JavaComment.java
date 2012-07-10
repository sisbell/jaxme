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
package org.apache.ws.jaxme.js;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/** <p>A class representing a Java comment.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JavaComment {
  boolean forJavaDoc;

  /** <p>Creates a new JavaComment which ought to be included into JavaDoc.
   * Use JavaComment(false) for comments which should be excluded.</p>
   */
  JavaComment() { forJavaDoc = true; }

  /** <p>Creates a new JavaComment.</p>
   *
   * @param javaDoc True, if this comment should appear in JavaDoc, false otherwise
   */
  JavaComment(boolean javaDoc) { forJavaDoc = javaDoc; }

  private List authors;
  /** <p>Returns the JavaDoc author field or null, if there is no
   * author field.</p>
   *
   * @see #addAuthor
   */
  public List getAuthors() { return authors; }
  /** <p>Sets the JavaDoc author field. Use null to disable the author field.</p>
   *
   * @see #getAuthors
   */
  public void addAuthor(String author) {
  	 if (authors == null) {
  	 	authors = new ArrayList();
  	 }
    authors.add(author);
  }

  private String version;
  /** <p>Returns the JavaDoc version field or null, if there is no
   * version field.</p>
   *
   * @see #setVersion
   */
  public String getVersion() { return version; }
  /** <p>Sets the JavaDoc version field. Use null to disable the version field.</p>
   *
   * @see #getVersion
   */
  public void setVersion(String pVersion) {
    version = (pVersion != null  &&  pVersion.length() > 0) ? pVersion : null;
  }

  private String returns;
  /** <p>Returns the JavaDoc return field or null, if there is no
   * return field.</p>
   *
   * @see #setReturn
   */
  public String getReturn() { return returns; }
  /** <p>Sets the JavaDoc return field. Use null to disable the return field.</p>
   *
   * @see #getReturn
   */
  public void setReturn(String pReturns) {
    returns = (pReturns != null  &&  pReturns.length() > 0) ? pReturns : null;
  }

  private List see = new ArrayList();
  /** <p>Returns an array list of Strings which should be used for "see" fields.</p>
   *
   * @see #addSee
   */
  public List getSee() { return see; }
  /** <p>Adds an element to the list of Strings which should be used for
   * "see" fields.</p>
   *
   * @see #getSee
   */
  public void addSee(String pSee) { see.add(pSee); }

  private ArrayList lines = new ArrayList();
  /** <p>Returns an array of lines being the comments content.</p>
   *
   * @see #addLine
   */
  public ArrayList getLines() { return lines; }
  /** <p>Adds a line to the comments content.</p>
   *
   * @see #getLines
   */
  public void addLine(String s) {
    if (s == null) s = "";
    boolean done = false;
    for (java.util.StringTokenizer st = new java.util.StringTokenizer(s, "\n");
         st.hasMoreTokens();  ) {
      String t = st.nextToken();
      if (t.length() > 0  &&  t.charAt(t.length()-1) == '\r') {
        t = t.substring(0, t.charAt(t.length()-1));
      }
      lines.add(t);
      done = true;
    }
    if (!done) lines.add("");
  }

  private List params = new ArrayList();
  /** <p>Returns an array of values for the JavaDoc param field.</p>
   *
   * @see #addParam
   */
  public List getParams() { return params; }
  /** <p>Adds a JavaDoc "param" field.</p>
   *
   * @see #getParams
   */
  public void addParam(String s) { params.add(s); }

  private List throwsList = new ArrayList();
  /** <p>Returns an array of values for the JavaDoc throw field.</p>
   *
   * @see #addThrows
   */
  public List getThrows() { return throwsList; }
  /** <p>Adds a JavaDoc "throw" field.</p>
   *
   * @see #getThrows
   */
  public void addThrows(String s) { throwsList.add(s); }

  /** <p>Returns a string representation of this comment.</p>
   */
  public void write(IndentationTarget pTarget) throws IOException {
    String sep = "/*" + (forJavaDoc ? "* " : " ");
    String othersep = " * ";
    if (lines.size() > 0) {
      for (int i = 0;  i < lines.size();  i++) {
        if (i > 0) pTarget.indent(0);
        pTarget.write(sep);
        pTarget.write(lines.get(i).toString());
        pTarget.write();
        sep = othersep;
      }
    } else {
      pTarget.write(sep);
      pTarget.write();
      sep = othersep;
    }
    pTarget.indent(0);
    pTarget.write(sep);
    pTarget.write();

    for (int i = 0;  i < params.size();  i++) {
      pTarget.indent(0);
      pTarget.write(sep);
      pTarget.write("@param ");
      pTarget.write(params.get(i).toString());
      pTarget.write();
    }
    if (returns != null) {
      pTarget.indent(0);
      pTarget.write(sep);
      pTarget.write("@return ");
      pTarget.write(returns);
      pTarget.write();
    }
    for (int i = 0;  i < throwsList.size();  i++) {
      pTarget.indent(0);
      pTarget.write(sep);
      pTarget.write("@throws ");
      pTarget.write(throwsList.get(i).toString());
      pTarget.write();
    }
    for (int i = 0;  i < see.size();  i++) {
      pTarget.indent(0);
      pTarget.write(sep);
      pTarget.write("@see ");
      pTarget.write(see.get(i).toString());
      pTarget.write();
    }
    List myAuthors = getAuthors();
    if (myAuthors != null) {
  		for (Iterator iter = myAuthors.iterator();  iter.hasNext();  ) {
        pTarget.indent(0);
        pTarget.write(sep);
        pTarget.write("@author ");
		    pTarget.write((String) iter.next());
        pTarget.write();
      }
    }
    if (version != null) {
      pTarget.indent(0);
      pTarget.write(sep);
      pTarget.write("@version ");
      pTarget.write(version);
      pTarget.write();
    }
    pTarget.indent(0);
    pTarget.write(" */");
    pTarget.write();
  }
}
