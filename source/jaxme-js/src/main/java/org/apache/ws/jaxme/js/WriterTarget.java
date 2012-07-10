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
import java.io.Writer;

/** <p>An IndentationTarget writing into a given Writer.</p>
 * 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: WriterTarget.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class WriterTarget implements IndentationTarget {
  public static final String INDENTATION_STRING = "  ";
  public static final String LINE_SEPARATOR = "\n";
  private Writer target;
  private String indentationString;
  private String lineSeparator;

  public WriterTarget() {}

  public WriterTarget(Writer pTarget) {
    target = pTarget;
  }

  public void setTarget(Writer pTarget) {
    target = pTarget;
  }

  public Writer getTarget() {
    return target;
  }

  public void setIndentationString(String pIndentationString) {
    indentationString = pIndentationString;
  }

  public String getIndentationString() {
    return indentationString == null ? INDENTATION_STRING : indentationString;
  }

  public void setLineSeparator(String pLineSeparator) {
    lineSeparator = pLineSeparator;
  }

  public String getLineSeparator() {
    return lineSeparator == null ? LINE_SEPARATOR : lineSeparator;
  }

  public boolean isInterface() {
    return false;
  }

  public void indent(int pLevel) throws IOException {
    for (int i = 0;  i < pLevel;  i++) {
      target.write(getIndentationString());
    }
  }

  public String asString(JavaQName pQName) {
    return pQName.toString();
  }

  public void write(String pValue) throws IOException {
    target.write(pValue);
  }

  public void write() throws IOException {
    target.write(getLineSeparator());
  }
}
