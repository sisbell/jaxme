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


/** This class implements a generic JavaSource object.
 */
public abstract class JavaSourceObject extends IndentationEngineImpl {
  protected JavaSourceObject(String pName, JavaQName pType, JavaSource.Protection pProtection) {
    setName(pName);
    setType(pType);
    setProtection(pProtection);
  }

  protected JavaSourceObject(String pName, JavaQName pType) {
    this(pName, pType, null);
  }

  protected JavaSourceObject(String pName, String pType, JavaSource.Protection pProtection) {
    setName(pName);
    if (pType == null) {
      throw new NullPointerException("Type must not be null");
    }
    setType(JavaQNameImpl.getInstance(pType));
    setProtection(pProtection);
  }

  protected JavaSourceObject(String pName, String pType) {
    this(pName, pType, null);
  }

  private String name;
  /** Returns this JavaSource objects name.
   * @see #setName
   */
  public String getName() { return name; }
  /** Sets this JavaSource objects name.
   * @see #getName
   */
  public void setName(String n) { name = n; }

  private boolean isFinal = false;
  /** Returns whether this is a final JavaSource object.
   *
   * @see #setFinal
   */
  public boolean isFinal() { return isFinal; }
  /** Sets whether this is a final JavaSource object.
   *
   * @see #isFinal
   */
  public void setFinal(boolean pFinal) { isFinal = pFinal; }

  private boolean isStatic = false;
  /** Returns whether this is a static JavaSource object.
   * @see #setStatic
   */
  public boolean isStatic() { return isStatic; }
  /** Sets whether this is a static JavaSource object.
   * @see #isStatic
   */
  public void setStatic(boolean pStatic) { isStatic = pStatic; }

  private JavaQName type;
  /** Returns this JavaSource objects type.
   * @see #setType
   */
  public JavaQName getType() { return type; }
  /** Sets this JavaSource objects type.
   * @see #getType
   */
  public void setType(JavaQName t) { type = t; }

  private JavaSource.Protection protection = JavaSource.DEFAULT_PROTECTION;
  /** Returns this JavaSource objects protection.
   * @see #setProtection
   */
  public JavaSource.Protection getProtection() { return protection; }
  /** Sets this JavaSource objects protection.
   * @param p null, "public", "protected" or "private"
   * @see #getProtection
   */
  public void setProtection(JavaSource.Protection p) {
  	 protection = (p == null) ? JavaSource.DEFAULT_PROTECTION : p;
  }

  private JavaComment comment;
  /** Returns a comment describing this JavaSource object.
   * @see #newComment
   */
  public JavaComment getComment() { return comment; }
  /** Creates a new Javadoc comment describing this JavaSource object.
   * @see #getComment
   */
  public JavaComment newComment() {
    if (comment == null) {
	   comment = new JavaComment();
	   return comment;
	 } else {
	   throw new IllegalStateException("A Javadoc comment has already been created for this object.");
	 }
  }

  private boolean bAbstract = false;
  /** Returns whether this JavaSource object is abstract.
   */
  public boolean isAbstract() {
    return bAbstract;
  }
  /** Sets whether this JavaSource object is abstract.
   */
  public void setAbstract(boolean isAbstract) {
    this.bAbstract = isAbstract;
  }

  private JavaSource javaSource;
  protected void setJavaSource(JavaSource pSource) {
    javaSource = pSource;
  }
  /** Returns the class, to which this JavaSource object belongs.
   */
  public JavaSource getJavaSource() {
    return javaSource;
  }
}