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

/** <p>An Observer is the corresponding interface to the
 * <code>Observable</code>: The Observer is watching the
 * Observable.</p>
 * <p>If the Observable changes its state, it calls the
 * Observers <code>setObservableState()</code> method.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: Observer.java 231881 2004-07-23 07:18:09Z jochen $
 */
public interface Observer {
  /** <p>The Observable indicates that its state has
   * changed. The Observer must inspect the Observable
   * to detect what exactly has changed.
   */
  public void notify(Object pObject);
}
