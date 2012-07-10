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
package org.apache.ws.jaxme.xs.types;

import org.apache.ws.jaxme.xs.XSAtomicType;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class AbstractAtomicType extends AbstractSimpleType implements XSAtomicType {
	private static final String[] ZERO_PATTERNS = new String[0];
	public boolean isAtomic() { return true; }

  public XSAtomicType getAtomicType() { return this; }

  public Long getFractionDigits() { return null; }
  public Long getLength() { return null; }
  public String getMaxExclusive() { return null; }
  public String getMaxInclusive() { return null; }
  public Long getMaxLength() { return null; }
  public String getMinExclusive() { return null; }
  public String getMinInclusive() { return null; }
  public Long getMinLength() { return null; }
  public Long getTotalDigits() { return null; }
  public boolean isReplacing() { return false; }
  public boolean isCollapsing() { return false; }
  public boolean isBuiltin() { return true; }
  	public String[] getPatterns() { return ZERO_PATTERNS; }
}
