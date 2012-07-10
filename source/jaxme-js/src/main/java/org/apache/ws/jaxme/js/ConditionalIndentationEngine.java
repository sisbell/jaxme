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

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface ConditionalIndentationEngine extends IndentationEngine {
  /** <p>Shortcut for
   * <code>addLine("if (", pTokens, ") {"); indent();</code>.</p>
   */
  public void addIf(Object[] pTokens);
  /** <p>Shortcut for <code>addIf(new Object[]{pLine})</code>.</p>
   */
  public void addIf(Object pLine);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2);
  /** <p>Shortcut for
   * <code>addIf(new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4})</code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5})</code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6}) </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7}) </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8}) </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9}) </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8, Object pToken9);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10})
   * </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8, Object pToken9,
                    Object pToken10);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11})  </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8, Object pToken9,
                    Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12})        </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8, Object pToken9,
                    Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13})  </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8, Object pToken9,
                    Object pToken10, Object pToken11, Object pToken12,
                    Object pToken13);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken14})  </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8, Object pToken9,
                    Object pToken10, Object pToken11, Object pToken12,
                    Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken15})  </code>.</p>
   */
  public void addIf(Object pToken1, Object pToken2, Object pToken3,
                    Object pToken4, Object pToken5, Object pToken6,
                    Object pToken7, Object pToken8, Object pToken9,
                    Object pToken10, Object pToken11, Object pToken12,
                    Object pToken13, Object pToken14, Object pToken15);

  /** <p>Shortcut for <code>unindent(); addLine("} else if (", pTokens, ") {");
   * indent();</code>.</p>
   */
  public void addElseIf(Object[] pTokens);
  /** <p>Shortcut for <code>addIf(new Object[]{pLine})</code>.</p>
   */
  public void addElseIf(Object pLine);
  /** <p>Shortcut for
   * <code>addElseIf(new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2);
  /** <p>Shortcut for
   * <code>addElseIf(new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4})</code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5})</code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6}) </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7}) </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8}) </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9}) </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10})
   * </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11})  </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12})        </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13})  </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12,
                        Object pToken13);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken14})  </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12,
                        Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addElseIf(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken15})  </code>.</p>
   */
  public void addElseIf(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12,
                        Object pToken13, Object pToken14, Object pToken15);

  /** <p>Shortcut for <code>pFirst ? addIf(pTokens) :
   * addElseIf(pTokens)</code>.</p>
   */
  public void addIf(boolean pFirst, Object[] pTokens);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2, pToken3,
   * pToken4})</code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5})</code>.</p>
   */
    public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                      Object pToken3, Object pToken4, Object pToken5);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6}) </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6);
  /** <p>Shortcut for <code>addIf(pFirst, new Object[]{pToken1, pToken2,
   * pToken3, pToken4, pToken5, pToken6, pToken7}) </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7);
  /** <p>Shortcut for <code>addIf(pFirst, new Object[]{pToken1, pToken2,
   * pToken3, pToken4, pToken5, pToken6, pToken7, pToken8}) </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addIf(pFirst, new Object[]{pToken1, pToken2,
   * pToken3, pToken4, pToken5, pToken6, pToken7, pToken8, pToken9})
   * </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8,
                    Object pToken9);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10})
   * </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8,
                    Object pToken9, Object pToken10);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11})  </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8,
                    Object pToken9, Object pToken10, Object pToken11);
  /** <p>Shortcut for
   * <code>addIf(pFirst, new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12})        </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8,
                    Object pToken9, Object pToken10, Object pToken11,
                    Object pToken12);
  /** <p>Shortcut for <code>addIf(pFirst, new Object[]{pToken1, pToken2,
   * pToken3, pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13})  </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8,
                    Object pToken9, Object pToken10, Object pToken11,
                    Object pToken12, Object pToken13);
  /** <p>Shortcut for <code>addIf(pFirst, new Object[]{pToken1, pToken2,
   * pToken3, pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken14})  </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8,
                    Object pToken9, Object pToken10, Object pToken11,
                    Object pToken12, Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addIf(pFirst, new Object[]{pToken1, pToken2,
   * pToken3, pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken15})  </code>.</p>
   */
  public void addIf(boolean pFirst, Object pToken1, Object pToken2,
                    Object pToken3, Object pToken4, Object pToken5,
                    Object pToken6, Object pToken7, Object pToken8,
                    Object pToken9, Object pToken10, Object pToken11,
                    Object pToken12, Object pToken13, Object pToken14,
                    Object pToken15);

  /** <p>Shortcut for <code>unindent(); addLine("} else {"); indent();</code>.
   */
  public void addElse();
  /** <p>Shortcut for <code>unindent(); addLine("}");</code>.
   */
  public void addEndIf();

  /** <p>Shortcut for <code>addLine(new Object[]{"throw new",
   * pExceptionClass, "(", pArgs, ");"});</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass, Object[] pArgs);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass, new Object[]{pToken1})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass, Object pToken1);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                          Object pToken1, Object pToken2);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                           Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                           Object pToken1, Object pToken2, Object pToken3,
                                                              Object pToken4);
 /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
  * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5})</code>.</p>
  */
 public void addThrowNew(JavaQName pExceptionClass,
                          Object pToken1, Object pToken2, Object pToken3,
                                                             Object pToken4, Object pToken5);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                           Object pToken1, Object pToken2, Object pToken3,
                                                              Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7}) </code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                           Object pToken1, Object pToken2, Object pToken3,
                                                              Object pToken4, Object pToken5, Object pToken6,
                                                              Object pToken7);
 /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
  * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
  * pToken8})</code>.</p>
  */
 public void addThrowNew(JavaQName pExceptionClass,
                          Object pToken1, Object pToken2, Object pToken3,
                                                             Object pToken4, Object pToken5, Object pToken6,
                                                             Object pToken7, Object pToken8);
 /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
  * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
  * pToken8, pToken9})</code>.</p>
  */
 public void addThrowNew(JavaQName pExceptionClass,
                          Object pToken1, Object pToken2, Object pToken3,
                                                             Object pToken4, Object pToken5, Object pToken6,
                                                             Object pToken7, Object pToken8, Object pToken9);
 /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
  * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
  * pToken8, pToken9, pToken10}) </code>.</p>
  */
 public void addThrowNew(JavaQName pExceptionClass,
                          Object pToken1, Object pToken2, Object pToken3,
                                                             Object pToken4, Object pToken5, Object pToken6,
                                                             Object pToken7, Object pToken8, Object pToken9,
                                                             Object pToken10);
 /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
  * new Object[]{pToken1, pToken2, pToken3,
  * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
  * pToken11})</code>.</p>
  */
 public void addThrowNew(JavaQName pExceptionClass,
                          Object pToken1, Object pToken2, Object pToken3,
                                                             Object pToken4, Object pToken5, Object pToken6,
                                                             Object pToken7, Object pToken8, Object pToken9,
                                                             Object pToken10, Object pToken11);
 /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
  * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
  * pToken8, pToken9, pToken10, pToken11, pToken12})</code>.</p>
  */
 public void addThrowNew(JavaQName pExceptionClass,
                          Object pToken1, Object pToken2, Object pToken3,
                                                             Object pToken4, Object pToken5, Object pToken6,
                                                             Object pToken7, Object pToken8, Object pToken9,
                                                             Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10, pToken11, pToken12, pToken13})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                           Object pToken1, Object pToken2, Object pToken3,
                                                              Object pToken4, Object pToken5, Object pToken6,
                                                              Object pToken7, Object pToken8, Object pToken9,
                                                              Object pToken10, Object pToken11, Object pToken12,
                                                              Object pToken13);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10, pToken11, pToken12, pToken13, pToken14})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                           Object pToken1, Object pToken2, Object pToken3,
                                                              Object pToken4, Object pToken5, Object pToken6,
                                                              Object pToken7, Object pToken8, Object pToken9,
                                                              Object pToken10, Object pToken11, Object pToken12,
                                                              Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10, pToken11, pToken12, pToken13, pToken15})</code>.</p>
   */
  public void addThrowNew(JavaQName pExceptionClass,
                           Object pToken1, Object pToken2, Object pToken3,
                                Object pToken4, Object pToken5, Object pToken6,
                                                         Object pToken7, Object pToken8, Object pToken9,
                                                         Object pToken10, Object pToken11, Object pToken12,
                                                         Object pToken13, Object pToken14, Object pToken15);
  /** <p>Shortcut for <code>addThrowNew(JavaQNameImpl.getInstance(pExceptionClass),
   * pArgs)</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass, Object[] pArgs);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass, new Object[]{pToken1})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass, Object pToken1);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7}) </code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8, Object pToken9);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10}) </code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8, Object pToken9,
                                                                   Object pToken10);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8, Object pToken9,
                                                                   Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10, pToken11, pToken12})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8, Object pToken9,
                                                                   Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10, pToken11, pToken12, pToken13})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8, Object pToken9,
                                                                   Object pToken10, Object pToken11, Object pToken12,
                                                                   Object pToken13);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10, pToken11, pToken12, pToken13, pToken14})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8, Object pToken9,
                                                                   Object pToken10, Object pToken11, Object pToken12,
                                                                   Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addThrowNew(pExceptionClass,
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5, pToken6, pToken7,
   * pToken8, pToken9, pToken10, pToken11, pToken12, pToken13, pToken15})</code>.</p>
   */
  public void addThrowNew(Class pExceptionClass,
                                                                   Object pToken1, Object pToken2, Object pToken3,
                                                                   Object pToken4, Object pToken5, Object pToken6,
                                                                   Object pToken7, Object pToken8, Object pToken9,
                                                                   Object pToken10, Object pToken11, Object pToken12,
                                                                   Object pToken13, Object pToken14, Object pToken15);

  /** <p>Shortcut for <code>addLine(new Object[]{"try {"}); indent();</code>.</p>
   */
  public void addTry();
  /** <p>Shortcut for <code>addLine(unindent(); new Object[]{"} catch (",
   * pExceptionClass, " ", pVar, ") {"); indent();</code></p>
   * @return An object being used to reference the catched exception.
   */
  public DirectAccessible addCatch(JavaQName pExceptionClass, Object pVar);
  /** <p>Shortcut for <code>addCatch(JavaQNameImpl.getInstance(pExceptionClass), pVar)</code>.</p>
   * @return An object being used to reference the catched exception.
   */
  public DirectAccessible addCatch(Class pExceptionClass, Object pVar);
  /** <p>Shortcut for <code>addCatch(pExceptionClass, "e");</code></p>
   * @return An object being used to reference the catched exception.
   */
  public DirectAccessible addCatch(JavaQName pExceptionClass);
  /** <p>Shortcut for <code>addCatch(pExceptionClass, "e");</code></p>
   * @return An object being used to reference the catched exception.
   */
  public DirectAccessible addCatch(Class pExceptionClass);
  /** <p>Shortcut for <code>unindent(); addLine("} finally {"); indent();</code>.</p>
   */
  public void addFinally();
  /** <p>Shortcut for <code>unindent(); addLine("}");</code>.</p>
   */
  public void addEndTry();

  /** <p>Shortcut for <code>addLine("for (", pArgs, ") {"); indent();</code>.</p>
   */
  public void addFor(Object[] pArgs);

  /** <p>Shortcut for <code>addFor(new Object[]{pVarClass, " ", pVar, " = 0;  ",
   *                                            pVar, " &lt; ", pArray, ".length;  ",
   *                                            pVar, "++"})</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForArray(JavaQName pVarClass, Object pVar, Object pArray);
  /** <p>Shortcut for <code>addForArray(JavaQNameImpl.getInstance(pVarClass),
   *                                    pVar, pArray)</code>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForArray(Class pVarClass, Object pVar, Object pArray);
  /** <p>Shortcut for <code>addForArray(pVar, pArray);</code>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForArray(Object pVar, Object pArray);
  /** <p>Shortcut for <code>addForArray(pVarClass, getLocalVariableName(),
   *                                     pArray)</code></p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForArray(JavaQName pVarClass, Object pArray);
  /** <p>Shortcut for <code>addForArray(pVarClass, getLocalVariableName(),
   *                                     pArray)</code></p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForArray(Class pVarClass, Object pArray);
  /** <p>Shortcut for <code>addForArray(int.class, pArray);</code>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForArray(Object pArray);

  /** <p>Shortcut for <code>addFor(new Object[]{pVarClass, " ", pVar, " = 0;  ",
   *                                            pVar, " &lt; ", pList, ".size();  ",
   *                                            pVar, "++"})</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForList(JavaQName pVarClass, Object pVar, Object pList);
  /** <p>Shortcut for <code>addForList(JavaQNameImpl.getInstance(pVarClass),
   *                                   pVar, pList)</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForList(Class pVarClass, Object pVar, Object pList);
  /** <p>Shortcut for <code>addFor(int.class, pVar, pList)</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForList(Object pVar, Object pList);
  /** <p>Shortcut for <code>addForList(pVarClass, getLocalVariableName(), pList)</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForList(JavaQName pVarClass, Object pList);
  /** <p>Shortcut for <code>addForList(JavaQNameImpl.getInstance(pVarClass),
   *                                   pList)</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForList(Class pVarClass, Object pList);
  /** <p>Shortcut for <code>addForList(int.class, pVar, pList)</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForList(Object pList);

  /** <p>Shortcut for <code>addFor(new Object[]{pVarClass, " ", pVar, " = ",
   *                                            pIterator, ";  ",
   *                                            pVar, ".hasNext();  ) {"});</code>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForIterator(JavaQName pVarClass, Object pVar, Object pIterator);
  /** <p>Shortcut for <code>addFor(new Object[]{pVarClass, " ", pVar, " = ",
   *                                            pIterator, ";  ",
   *                                            pVar, ".hasNext();  ) {"});</code>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForIterator(Class pVarClass, Object pVar, Object pIterator);
  /** <p>Shortcut for <code>addFor(JavaQNameImpl.getInstance(java.util.Iterator.class),
   *                               pVar, pIterator);</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForIterator(Object pVar, Object pIterator);
  /** <p>Shortcut for
   * <code>addForIterator(pVarClass, getLocalVariableName(), pIterator)</code>.</p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForIterator(JavaQName pVarClass, Object pIterator);
  /** <p>Shortcut for
   * <code>addForIterator(pVarClass, getLocalVariableName(), pIterator)</code>.</p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForIterator(Class pVarClass, Object pIterator);
  /** <p>Shortcut for <code>addForIterator(Iterator.class, pIterator)</code>.</p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForIterator(Object pIterator);

  /** <p>Shortcut for <code>addForIterator(pVarClass, pVar,
   *                                       new Object[]{"(", pVar, ").iterator()"},);
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForCollection(JavaQName pVarClass, Object pVar, Object pCollection);
  /** <p>Shortcut for <code>addForCollection(JavaQNameImpl.getInstance(pVarClass),
   *                                         pVar, pCollection)</code></p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForCollection(Class pVarClass, Object pVar, Object pCollection);
  /** <p>Shortcut for <code>addForCollection(java.util.Iterator.class,
   *                                         pVar, pCollection);</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForCollection(Object pVar, Object pCollection);
  /** <p>Shortcut for <code>addForCollection(pVarClass, getLocalVariableName(),
   *                                         pCollection)</code></p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForCollection(JavaQName pVarClass, Object pCollection);
  /** <p>Shortcut for <code>addForCollection(pVarClass, getLocalVariableName(),
   *                                         pCollection)</code></p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForCollection(Class pVarClass, Object pCollection);
  /** <p>Shortcut for <code>addForCollection(java.util.Iterator.class,
   *                                         pCollection);</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForCollection(Object pCollection);

  /** <p>Shortcut for <code>addFor(new Object[]{pVarClass, " ", pVar, " = ",
   *                                            pEnumeration, ";  ",
   *                                            pVar, ".hasMoreElements();  ) {"});</code>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForEnumeration(JavaQName pVarClass, Object pVar, Object pEnumeration);
  /** <p>Shortcut for <code>addFor(new Object[]{pVarClass, " ", pVar, " = ",
   *                                            pEnumeration, ";  ",
   *                                            pVar, ".hasMoreElements();  ) {"});</code>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForEnumeration(Class pVarClass, Object pVar, Object pEnumeration);
  /** <p>Shortcut for <code>addFor(JavaQNameImpl.getInstance(java.util.Enumeration.class),
   *                               pVar, pEnumeration);</code>.</p>
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForEnumeration(Object pVar, Object pEnumeration);
  /** <p>Shortcut for
   * <code>addForEnumeration(pVarClass, getLocalVariableName(), pEnumeration)</code>.</p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForEnumeration(JavaQName pVarClass, Object pEnumeration);
  /** <p>Shortcut for
   * <code>addForEnumeration(pVarClass, getLocalVariableName(), pEnumeration)</code>.</p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForEnumeration(Class pVarClass, Object pEnumeration);
  /** <p>Shortcut for <code>addForEnumeration(Enumeration.class, pIterator)</code>.</p>
   *
   * @return An object being used to reference the variable
   *    <code>pVar</code> in the method.
   */
  public DirectAccessible addForEnumeration(Object pEnumeration);

  /** <p>Shortcut for <code>unindent(); addLine("}");</code>.</p>
   */
  public void addEndFor();
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1})</code>.</p>
   */
  public void addFor(Object pToken1);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3,
   *                                            pToken4})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7,
   *                                            pToken8})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7, pToken8, 
   *                                            pToken9})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7, pToken8,
   *                                            pToken9, pToken10})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7, pToken8,
   *                                            pToken9, pToken10, pToken11})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7, pToken8,
   *                                            pToken9, pToken10, pToken11,
   *                                            pToken12})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7, pToken8,
   *                                            pToken9, pToken10, pToken11, pToken12,
   *                                            pToken13})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7, pToken8,
   *                                            pToken9, pToken10, pToken11, pToken12,
   *                                            pToken13, pToken14})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addFor(new Object[]{pToken1, pToken2, pToken3, pToken4,
   *                                            pToken5, pToken6, pToken7, pToken8,
   *                                            pToken9, pToken10, pToken11, pToken12,
   *                                            pToken13, pToken14, pToken15})</code>.</p>
   */
  public void addFor(Object pToken1, Object pToken2, Object pToken3, Object pToken4,
                      Object pToken5, Object pToken6, Object pToken7, Object pToken8,
                      Object pToken9, Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14, Object Token15);

  /** <p>Shortcut for <code>addLine("while (", pExpr, ") {"); indent();</code>.</p>
   */
  public void addWhile(Object[] pExpr);
  /** <p>Shortcut for <code>addWhile(new Object[]{"(", pIterator, ").hasNext()"});</code>
   */
  public void addWhileIterator(Object pIterator);
  /** <p>Shortcut for <code>unindent(); addLine("}");</code>.</p>
   */
  public void addEndWhile();
  /** <p>Shortcut for <code>addWhile(new Object[]{pToken1})</code>.</p>
   */
  public void addWhile(Object pToken1);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3,
   * pToken4})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4);
  /** <p>Shortcut for <code>addWhile(new Object[]{pToken1, pToken2,
   * pToken3, pToken4, pToken5})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7, pToken8})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7, pToken8, pToken9})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7, pToken8, pToken9, pToken10})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7, pToken8, pToken9, pToken10, pToken11})
   * </code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addWhile(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10, pToken11,
   * pToken12})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
   * pToken13})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12,
                       Object pToken13);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7, pToken8, pToken9, pToken10, pToken11, pToken12,
   * pToken13, pToken14})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12,
                       Object pToken13, Object pToken14);
  /** <p>Shortcut for
   * <code>addWhile(new Object[]{pToken1, pToken2, pToken3, pToken4,
   * pToken5, pToken6, pToken7, pToken8, pToken9, pToken10, pToken11,
   * pToken12, pToken13, pToken14, pToken15})</code>.</p>
   */
  public void addWhile(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12,
                       Object pToken13, Object pToken14, Object Token15);
  /** <p>Shortcut for <code>addLine("switch (", pTokens, ") {"); indent();
   * indent();</code>.</p>
   */
  public void addSwitch(Object[] pTokens);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pLine})</code>.</p>
   */
  public void addSwitch(Object pLine);
  /** <p>Shortcut for
   * <code>addSwitch(new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2);
  /** <p>Shortcut for
   * <code>addSwitch(new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4})</code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5})</code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6}) </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7}) </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8}) </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9}) </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10}) </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                         Object pToken4, Object pToken5, Object pToken6,
                         Object pToken7, Object pToken8, Object pToken9,
                         Object pToken10);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11})  </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12})  </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13})  </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12,
                        Object pToken13);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken14})  </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12,
                        Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addSwitch(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken15})  </code>.</p>
   */
  public void addSwitch(Object pToken1, Object pToken2, Object pToken3,
                        Object pToken4, Object pToken5, Object pToken6,
                        Object pToken7, Object pToken8, Object pToken9,
                        Object pToken10, Object pToken11, Object pToken12,
                        Object pToken13, Object pToken14, Object pToken15);

  /** <p>Shortcut for <code>unindent(); addLine("case ", pTokens, ":");
   * indent();</code>.</p>
   */
  public void addCase(Object[] pTokens);
  /** <p>Shortcut for <code>addCase(new Object[]{pLine})</code>.</p>
   */
  public void addCase(Object pLine);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2);
  /** <p>Shortcut for
   * <code>addCase(new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4})</code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5})</code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6}) </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7}) </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8}) </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9}) </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7, Object pToken8, Object pToken9);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10})
   * </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7, Object pToken8, Object pToken9,
                      Object pToken10);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11})  </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7, Object pToken8, Object pToken9,
                      Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12})  </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7, Object pToken8, Object pToken9,
                      Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13})  </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12,
                       Object pToken13);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken14})  </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7, Object pToken8, Object pToken9,
                      Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addCase(new Object[]{pToken1, pToken2, pToken3,
   * pToken4, pToken5, pToken6, pToken7, pToken8, pToken9, pToken10,
   * pToken11, pToken12, pToken13, pToken15})  </code>.</p>
   */
  public void addCase(Object pToken1, Object pToken2, Object pToken3,
                      Object pToken4, Object pToken5, Object pToken6,
                      Object pToken7, Object pToken8, Object pToken9,
                      Object pToken10, Object pToken11, Object pToken12,
                      Object pToken13, Object pToken14, Object pToken15);

  /** <p>Shortcut for
   * <code>unindent(); addLine("default:"); indent();</code>.</p>
   */
  public void addDefault();
  /** <p>Shortcut for <code>addLine("break;");</code>.</p>
   */
  public void addBreak();

  /** <p>Shortcut for <code>unindent(); unindent(); addLine("}");</code>.</p>
   */
  public void addEndSwitch();
}
