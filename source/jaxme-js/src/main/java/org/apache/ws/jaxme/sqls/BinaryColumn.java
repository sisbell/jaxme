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
package org.apache.ws.jaxme.sqls;


/** <p>Interface of a column with datatype {@link Column.Type#BINARY}
 * or {@link Column.Type#VARBINARY}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface BinaryColumn extends Column {
  /** <p>Returns whether the column has fixed or variable length.</p>
   */
  public boolean hasFixedLength();

  /** <p>If the column has fixed length: Sets the columns length.
   * Otherwise sets the columns maximum length.</p>
   */
  public void setLength(Long pLength);

  /** <p>Shortcut for <code>setLength(new Integer(pLength))</code>.</p>
   */
  public void setLength(long pLength);

  /** <p>If the column has fixed length: Returns the columns length.
   * Otherwise returns the columns maximum length.</p>
   */
  public Long getLength();
}
