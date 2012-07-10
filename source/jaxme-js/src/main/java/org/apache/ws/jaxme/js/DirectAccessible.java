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

/** <p>This interface is an abstract base for fields and similar
 * objects. It doesn't define many methods, it mainly indicates, that
 * the implementations value is directly and fast accessible in
 * the generated code. The use is best demonstrated by an example.
 * Suggest the following piece of code:</p>
 * <pre>
 *     Object value;
 *     return new Object[]{"((", value, ") * (", value, "))"};
 * </pre>
 * <p>The example is well suited for the case, where <code>value</code>
 * is a variable name like "i". It is not suited, if "value" contains
 * an expensive method call like "sin(x)". It is even wrong in the
 * case "i++".</p>
 * <p>By using the interface <code>DirectAccessible</code>, you can
 * change the implementation of <code>getSquare()</code> to look
 * like this:
 * <pre>
 *     Object value;
 *     JavaQName type;
 *     if (!(value instanceof DirectAccessible)) {
 *       LocalJavaField v = pMethod.newJavaField(type);
 *       v.addLine(value);
 *       v.setFinal(true);
 *       value = v;
 *     }
 *     return new Object[]{"((", value, ") * (", value, "))"};
 * </pre>
 * <p>This results in code, which is far more readable and better
 * optimized.</p>
 *
 * @author <a href="mailto:jwi@softwareag.com">Jochen Wiedmann</a>
 */
public interface DirectAccessible extends TypedValue {
   /** <p>Returns whether the value is possibly null.</p>
    */
   public boolean isNullable();
   /** <p>Sets whether the value is possibly null.</p>
    */
   public void setNullable(boolean pNullable);
}
