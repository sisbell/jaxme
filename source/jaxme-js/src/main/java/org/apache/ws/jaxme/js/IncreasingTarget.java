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


/** <p>A filtering indentation target, which pipes all output
 * to the actual target, except that it increases the indentation
 * level by 1.</p>
 * 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: IncreasingTarget.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class IncreasingTarget implements IndentationTarget {
  private IndentationTarget actualTarget;
  private Boolean isInterface;
  public IncreasingTarget(IndentationTarget pActualTarget) {
    actualTarget = pActualTarget;
  }
  public boolean isInterface() {
  	 return (isInterface == null) ? actualTarget.isInterface() : isInterface.booleanValue();
  }
  public void setInterface(Boolean pInterface) {
  	 isInterface = pInterface;
  }
  public void indent(int i) throws IOException {
    actualTarget.indent(i+1);
  }
  public String asString(JavaQName pQName) {
    return actualTarget.asString(pQName);
  }
  public void write(String pValue) throws IOException {
    actualTarget.write(pValue);
  }
  public void write() throws IOException {
    actualTarget.write();
  }
}
