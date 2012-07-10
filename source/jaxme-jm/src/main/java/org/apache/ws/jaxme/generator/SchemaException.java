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
package org.apache.ws.jaxme.generator;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: SchemaException.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class SchemaException extends Exception {
  private Throwable inner;

  public SchemaException(String pMsg) {
	 super(pMsg);
  }

  public SchemaException(Throwable pInner) {
    inner = pInner;
  }

  public SchemaException(String pMsg, Throwable pInner) {
    this(pMsg);
    inner = pInner;
  }

  public Throwable getInner() {
    return inner;
  }

  public void printStackTrace() {
    super.printStackTrace();
    if (inner != null) {
      System.err.println("Root cause:");
      inner.printStackTrace();
    }
  }

  public void printStackTrace(PrintStream pStream) {
    super.printStackTrace(pStream);
    if (inner != null) {
      pStream.println("Root cause:");
      inner.printStackTrace(pStream);
    }
  }

  public void printStackTrace(PrintWriter pWriter) {
    super.printStackTrace(pWriter);
    if (inner != null) {
      pWriter.println("Root cause:");
      inner.printStackTrace(pWriter);
    }
  }
}
