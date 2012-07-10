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


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: IndentationEngine.java 231785 2004-02-16 23:39:59Z jochen $
 */
public interface IndentationEngine {
  public static final Object NOTHING = "";

  /** <p>Increases the current level of indentation.</p>
   */
  public void indent();
  /** <p>Decreases the current level of indentation.</p>
   */
  public void unindent();

  /** <p>Adds the IndentationEngine's contents to the given
   * {@link IndentationTarget}.</p>
   */
  public void write(IndentationTarget pTarget) throws IOException;

  /** <p>Adds the given objects contents to the given
   * {@link IndentationTarget}.</p>
   */
  public void write(IndentationTarget pTarget, Object pObject) throws IOException;

  /** <p>Clears the IndentationEngine's contents.</p>
   */
  public void clear();
  /** <p>Sets the current level of indentation.</p>
   */
  public void setLevel(int pLevel);
  /** <p>Returns the current level of indentation.</p>
   */
  public int getLevel();

  /** <p>Adds the given tokens as a complete line, using the
   * given level of indentation.</p>
   */
  public void addLine(int pLevel, Object[] pTokens);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(), pTokens)</code>.</p>
   */
  public void addLine(Object[] pTokens);
  /** <p>Adds an empty line, shortcut for
   * <code>addLine(getIndentationLevel, new Object[0])</<code>.</p>
   */
  public void addLine();
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pLine})</code>.</p>
   */
  public void addLine(Object pLine);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2})</code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3})</code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4})</code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5})</code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6}) </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7}) </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8}) </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8, pToken9}) </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8, pToken9, pToken10}) </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8, pToken9, pToken10,
   *              pToken11})  </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8, pToken9, pToken10,
   *              pToken11, pToken12})  </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8, pToken9, pToken10,
   *              pToken11, pToken12, pToken13})  </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12,
                       Object pToken13);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8, pToken9, pToken10,
   *              pToken11, pToken12, pToken13, pToken14})  </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12,
                       Object pToken13, Object pToken14);
  /** <p>Shortcut for <code>addLine(getIndentationLevel(),
   * new Object[]{pToken1, pToken2, pToken3, pToken4, pToken5,
   *              pToken6, pToken7, pToken8, pToken9, pToken10,
   *              pToken11, pToken12, pToken13, pToken15})  </code>.</p>
   */
  public void addLine(Object pToken1, Object pToken2, Object pToken3,
                       Object pToken4, Object pToken5, Object pToken6,
                       Object pToken7, Object pToken8, Object pToken9,
                       Object pToken10, Object pToken11, Object pToken12,
                       Object pToken13, Object pToken14, Object pToken15);

  /** <p>Moves the cursor to the top of the method.</p>
   */
  public void moveToTop();

  /** <p>Moves the cursor to the bottom of the method.</p>
   */
  public void moveToBottom();

  /** <p>Sets a placeholder with the given name.</p>
   * @param pName The placeholders name
   * @param pAutoRemove Whether the placeholder must be removed by invoking {@link PlaceHolder#remove()} (false)
   * or not (true).
   */
  public PlaceHolder newPlaceHolder(String pName, boolean pAutoRemove);

  /** <p>Searches for the placeholder with the given name. The cursor
   * will be set to the line after the placeholder, if it is found.
   * In that case subsequent invocations of {@link IndentationEngine#addLine()}
   * will add code to the lines following the placeholder. Otherwise
   * the cursor is unchanged.</p>
   * @param pName The placeholders name
   * @return The placeholder or null, if it wasn't found.
   */
  public PlaceHolder getPlaceHolder(String pName);
}
