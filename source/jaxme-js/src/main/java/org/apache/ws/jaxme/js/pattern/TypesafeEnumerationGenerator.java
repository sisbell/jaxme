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
package org.apache.ws.jaxme.js.pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ws.jaxme.js.JavaComment;
import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.Util;

/** <p>This class is a generator for the typesafe enumeration
 * pattern. It creates a class that contains only a few,
 * specified instances. Other instances cannot be created.</p>
 * <p>Any instance has a name and a value. The name
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: TypesafeEnumerationGenerator.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class TypesafeEnumerationGenerator {
  public static class Item {
    private String name;
	 private String value;

	 /** <p>Sets the enumeration items name. This must be a valid
	  * Java identifier.</p>
	  */
	 public void setName(String pName) {
		if (pName != null) {
		  Util.checkJavaIdentifier(pName);
		}
		name = pName;
	 }

	 /** <p>Returns the enumeration items name.</p>
	  */
	 public String getName() {
		return name;
	 }

	 /** <p>Sets the enumeration items value.</p>
	  */
	 public void setValue(String pValue) {
		value = pValue;
	 }

	 /** <p>Returns the enumeration items value.</p>
	  */
	 public String getValue() {
		return value;
	 }

	 /** <p>Verifies the enumeration item.</p>
	  * @throws IllegalStateException The enumeration items value is not set.
	  */
	 public void finish() {
		if (getValue() == null) {
		  throw new IllegalStateException("The enumeration items 'value' attribute is not set.");
		}
		if (getName() == null) {
		  try {
			 Util.asJavaIdentifier(getValue().toUpperCase());
		  } catch (Exception e) {
			 throw new IllegalStateException("The enumeration items 'name' attribute is not set " +
					                            " and the value " + getValue() +
					                            " cannot be converted into a valid Java identifier.");
		  }
		}
	 }
  }

  private boolean isAddingEquals = true;

  /** <p>Sets whether the generator should add implementations for the methods
	* {@link Object#equals(java.lang.Object)} and {@link Object#hashCode()}
	* or not. Defaults to true.</p>
	*/
  public void setAddingEquals(boolean pAddingEquals) {
	 isAddingEquals = pAddingEquals;
  }

  /** <p>Returns whether the generator should add implementations for the methods
	* {@link Object#equals(java.lang.Object)} and {@link Object#hashCode()}
	* or not. Defaults to true.</p>
	*/
  public boolean isAddingEquals() {
  	 return isAddingEquals;
  }

  /** <p>Generates a new typesafe enumeration.</p>
	* @param pFactory The factory to use for generating the items.
	* @param pTargetClass Fully qualified name of the class being generated.
	* @param pItems The enumeration items; a public, static, final instance
	*   will be generated for any element in the array
	*/
  public JavaSource generate(JavaSourceFactory pFactory, JavaQName pTargetClass,
	                           Item[] pItems) {
	 JavaSource result = pFactory.newJavaSource(pTargetClass, JavaSource.PUBLIC);
    doGenerate(result, pItems);
	 return result;
  }

  /** <p>Generates a new typesafe enumeration, which is an inner class of
	* the class <code>pSource</code>.</p>
	* @param pSource The class, which shall have an inner class
	* @param pName Name of the inner class
	* @param pItems The enumeration items; a public, static, final instance
	*   will be generated for any element in the array
	*/
  public JavaInnerClass generate(JavaSource pSource, String pName, Item[] pItems) {
	 JavaInnerClass result = pSource.newJavaInnerClass(pName, JavaSource.PUBLIC);
	 result.setStatic(true);
	 doGenerate(result, pItems);
	 return result;
  }

  /** <p>Does the actual generation, invoked by the <code>generate()</code>
	* frontends.</p>
	*/
  private void doGenerate(JavaSource pSource, Item[] pItems) {
	 Set names = new HashSet();
	 Set values = new HashSet();
	 for (int i = 0;  i < pItems.length;  i++) {
		String itemName = pItems[i].getName();
		String itemValue = pItems[i].getValue();
		if (itemName == null) {
		  itemName = Util.asJavaIdentifier(itemValue.toUpperCase());
		  pItems[i].setName(itemName);
		}
		if (names.contains(itemName)) {
		  throw new IllegalStateException("The item name " + itemName + " is not unique.");
		}
		if (values.contains(itemValue)) {
		  throw new IllegalStateException("The item value " + itemValue + " is not unique.");
		}
		names.add(itemName);
		names.add(itemValue);
	 }

	 pSource.addImplements(Serializable.class);
	 JavaField name = pSource.newJavaField("name", String.class, JavaSource.PRIVATE);
	 name.setFinal(true);
	 JavaField value = pSource.newJavaField("value", String.class, JavaSource.PRIVATE);
	 name.setFinal(true);
	 JavaConstructor jcon = pSource.newJavaConstructor(JavaSource.PRIVATE);
	 jcon.addParam(String.class, "pName");
	 jcon.addParam(String.class, "pValue");
	 jcon.addLine("name = pName;");
	 jcon.addLine("value = pValue;");
    List instanceList = new ArrayList();
	 for (int i = 0;  i < pItems.length;  i++) {
      Item item = pItems[i];
		String itemName = item.getName();
		String itemValue = item.getValue();
		JavaField instance = pSource.newJavaField(itemName, pSource.getQName(),
			                                       JavaSource.PUBLIC);
		instance.newComment().addLine("The enumeration item with name " + itemName +
			                           " and value " + itemValue + ".");
		instance.setStatic(true);
		instance.setFinal(true);
		instance.addLine("new ", pSource.getQName(), "(", JavaSource.getQuoted(itemName),
		                 ", ", JavaSource.getQuoted(itemValue), ")");

		if (i > 0) instanceList.add(", ");
	   instanceList.add(instance);
    }

	 JavaQName arrayType = JavaQNameImpl.getArray(pSource.getQName());
	 JavaField allInstances = pSource.newJavaField("allInstances", arrayType,
		                                              JavaSource.PRIVATE);
	 allInstances.setStatic(true);
	 allInstances.setFinal(true);
	 allInstances.addLine("new ", arrayType, "{", instanceList, "}");

	 JavaMethod getName = pSource.newJavaMethod("getName", String.class,
		                                           JavaSource.PUBLIC);
	 getName.newComment().addLine("The enumeration items name.");
	 getName.addLine("return ", name, ";");

	 JavaMethod getValue = pSource.newJavaMethod("getValue", String.class,
	                                            JavaSource.PUBLIC);
	 getValue.newComment().addLine("The enumeration items value.");
	 getValue.addLine("return ", value, ";");

	 JavaMethod getInstances = pSource.newJavaMethod("getInstances", arrayType,
		                                              JavaSource.PUBLIC);
	 getInstances.setStatic(true);
	 getInstances.addLine("return ", allInstances, ";");

	 JavaMethod getInstanceByName = pSource.newJavaMethod("getInstanceByName",
		                                                   pSource.getQName(),
		                                                   JavaSource.PUBLIC);
    getInstanceByName.setStatic(true);
	 getInstanceByName.addParam(String.class, "pName");
	 JavaComment jc = getInstanceByName.newComment();
	 jc.addLine("Returns the item with the given name.</p>");
	 jc.addThrows(IllegalArgumentException.class.getName() +
		           " The name <code>pName</code> is invalid and no such item exists.");
	 getInstanceByName.addLine(String.class, " s = pName.intern();");
	 boolean first = true;
	 for (int i = 0;  i < pItems.length;  i++) {
		Item item = pItems[i];
		Object[] args = new Object[]{JavaSource.getQuoted(item.getName()), " == s"};
	 	getInstanceByName.addIf(first, args);
	 	getInstanceByName.addLine("return ", item.getName(), ";");
	 	first = false;
	 }
	 getInstanceByName.addElse();
	 getInstanceByName.addLine("throw new ", IllegalArgumentException.class, "(",
	                           JavaSource.getQuoted("Invalid name: "),
                              " + pName);");
	 getInstanceByName.addEndIf();

	 JavaMethod getInstanceByValue = pSource.newJavaMethod("getInstanceByValue",
																			 pSource.getQName(),
																			 JavaSource.PUBLIC);
	 getInstanceByValue.setStatic(true);
	 getInstanceByValue.addParam(String.class, "pValue");
	 jc = getInstanceByValue.newComment();
	 jc.addLine("Returns the item with the given value.</p>");
	 jc.addThrows(IllegalArgumentException.class.getName() +
					  " The name <code>pValue</code> is invalid and no such item exists.");
	 getInstanceByValue.addLine(String.class, " s = pValue.intern();");
	 first = true;
	 for (int i = 0;  i < pItems.length;  i++) {
		Item item = pItems[i];
		Object[] args = new Object[]{JavaSource.getQuoted(item.getValue()), " == s"};
		getInstanceByValue.addIf(first, args);
		getInstanceByValue.addLine("return ", item.getName(), ";");
		first = false;
	 }
	 getInstanceByValue.addElse();
	 getInstanceByValue.addLine("throw new ", IllegalArgumentException.class, "(",
									    JavaSource.getQuoted("Invalid name: "),
									    " + pValue);");
	 getInstanceByValue.addEndIf();

	 if (isAddingEquals()) {
	 	JavaMethod equals = pSource.newJavaMethod("equals", JavaQNameImpl.BOOLEAN,
	 	                                          JavaSource.PUBLIC);
	 	equals.addParam(Object.class, "o");
	 	equals.addIf("o == null  ||  !(o instanceof ", pSource.getQName(), ")");
	 	equals.addLine("return false;");
	 	equals.addEndIf();
	 	equals.addLine("return name.equals(((", pSource.getQName(), ") o).name);");

      JavaMethod hashCode = pSource.newJavaMethod("hashCode", JavaQNameImpl.INT,
                                                  JavaSource.PUBLIC);
      hashCode.addLine("return name.hashCode();");
	 }
  }
}
