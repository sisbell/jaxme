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
package org.apache.ws.jaxme.sqls.impl;

import org.apache.ws.jaxme.sqls.Value;

/** <p>Implementation of a value.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ValueImpl implements Value {
  private final Value.Type type;
  private final Object value;

  public ValueImpl(Value pValue) {
    this(pValue.getType(), pValue.getValue());
  }
  public ValueImpl(Value.Type pType, Object pValue) {
    if (pType == null) {
      throw new NullPointerException("The value type must not be null.");
    }
    if ((pType.equals(Type.PLACEHOLDER)  ||  pType.equals(Type.NULL))  &&
	pValue != null) {
      throw new IllegalArgumentException("The value argument must be null for the type " +
                                          pType + ".");
    }
    type = pType;
    value = pValue;
  }

  public Value.Type getType() { return type; }
  public Object getValue() { return value; }
  public int hashCode() {
    int result = type.hashCode();
    if (value != null) {
      result = value.hashCode();
    }
    return result;
  }
  public boolean equals(Object o) {
    if (o == null  ||  !(o instanceof Value)) {
      return false;
    }
    Value v = (Value) o;
    if (!type.equals(v.getType())) { return false; }
    if (value == null) {
      return v.getValue() == null;
    } else {
      return value.equals(v.getValue());
    }
  }

    public static Object asValue(Object pValue) {
    	if (pValue == null) {
    		return new ValueImpl(Value.Type.NULL, pValue);
        } else if (pValue instanceof String) {
        	return new ValueImpl(Value.Type.STRING, pValue);
        } else if (pValue instanceof Boolean) {
        	return new ValueImpl(Value.Type.BOOLEAN, pValue);
        } else if (pValue instanceof Byte) {
            return new ValueImpl(Value.Type.BYTE, pValue);
        } else if (pValue instanceof Short) {
            return new ValueImpl(Value.Type.SHORT, pValue);
        } else if (pValue instanceof Integer) {
            return new ValueImpl(Value.Type.INT, pValue);
        } else if (pValue instanceof Long) {
            return new ValueImpl(Value.Type.LONG, pValue);
        } else if (pValue instanceof Float) {
            return new ValueImpl(Value.Type.FLOAT, pValue);
        } else if (pValue instanceof Double) {
            return new ValueImpl(Value.Type.DOUBLE, pValue);
        } else {
        	return pValue;
        }
    }
}
