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
 
package org.apache.ws.jaxme.pm.ino;

import org.apache.ws.jaxme.JMElement;

/** <p>Interface of a document stored in Tamino.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface InoObject extends JMElement {
	/** Sets the objects document name. May be null,
     * because a document is typically not required.
	 */
    public void setInoDocname(String pDocname);

    /** Sets the objects ino:id. May be null,
     * because an ino:id is no longer required
     * nowadays.
     */
    public void setInoId(String pId);

    /** Returns the objects document name. May be null,
     * because a document is typically not required.
     */
    public String getInoDocname();

    /** Returns the objects ino:id. May be null,
     * because an ino:id is no longer required
     * nowadays.
     */
    public String getInoId();
}
