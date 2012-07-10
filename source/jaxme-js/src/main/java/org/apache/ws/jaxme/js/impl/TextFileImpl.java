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
package org.apache.ws.jaxme.js.impl;

import org.apache.ws.jaxme.js.IndentationEngineImpl;
import org.apache.ws.jaxme.js.TextFile;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TextFileImpl extends IndentationEngineImpl implements TextFile {
  private final String packageName, fileName;
  private String contents;

  public TextFileImpl(String pPackageName, String pFileName) {
    packageName = pPackageName;
    fileName = pFileName;
  }

  public String getPackageName() { return packageName; }
  public String getFileName() { return fileName; }
  public String getContents() {
    if (contents == null) {
      return super.asString();
    } else {
     return contents;
    }
  }
  public void setContents(String pContents) { contents = pContents; }
}
