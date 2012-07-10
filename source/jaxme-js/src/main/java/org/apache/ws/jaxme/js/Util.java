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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** <p>Various static utility methods.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class Util {
	/** <p>Returns whether the given name is a valid Java identifier.
	 * Works by using {@link Character#isJavaIdentifierStart(char)} and
	 * {@link Character#isJavaIdentifierPart(char)}.</p>
	 * @throws IllegalArgumentException The name is not valid. An explanation
	 *   why is given in the detail message.
	 */
	public static void checkJavaIdentifier(String pName) {
		if (pName.length() == 0) {
			throw new IllegalArgumentException("A valid Java identifier must not be empty.");
		}
		char c = pName.charAt(0);
		if (!Character.isJavaIdentifierStart(c)) {
			throw new IllegalArgumentException("The identifier " + pName +
			                                    " is no valid Java identifier, because its first character is " + c);
		}
		for (int i = 1;  i < pName.length();  i++) {
			if (!Character.isJavaIdentifierPart(c)) {
				throw new IllegalArgumentException("The identifier " + pName +
				                                    " is no valid Java identifier, because it contains the character " + c);
			}
		}
	}

	/** <p>Takes as input an arbitrary String and maps it to a String,
	 * which is a valid Java identifier. Mapping works as follows:
	 * <ol>
	 *   <li>For the first character, invokes {@link Character#isJavaIdentifierStart(char)}.
	 *     If that method returns false, replaces the character with an
	 *     underscore ('_').</li>
	 *   <li>For any following character, invokes {@link Character#isJavaIdentifierPart(char)}.
	 *     If that method returns false, replaces the character with an
	 *     underscore ('_').</li>
	 * </ol>
	 * </p>
	 * @param pIdentifier The identifier being mapped
	 * @throws IllegalArgumentException The parameter <code>pIdentifier</code>
	 *   cannot be converted into a Java identifier, because it is null or
	 *   empty. 
	 */
	public static String asJavaIdentifier(String pIdentifier) {
		if (pIdentifier == null  ||  pIdentifier.length() == 0) {
			throw new IllegalArgumentException("A null or empty String cannot be converted into a valid Java identifier."); 
		}
		StringBuffer sb = new StringBuffer();
		char c = pIdentifier.charAt(0);
		sb.append(Character.isJavaIdentifierStart(c) ? c : '_');
		for (int i = 1;  i < pIdentifier.length();  i++) {
			c = pIdentifier.charAt(i);
			sb.append(Character.isJavaIdentifierPart(c) ? c : '_');
		}
		return sb.toString();
	}

	/** Converts the given class into an instance of
	 * {@link JavaSource}.
	 */
	public static JavaSource newJavaSource(JavaSourceFactory pFactory, Class pClass) {
		JavaQName qName = JavaQNameImpl.getInstance(pClass);
		int modifiers = pClass.getModifiers();
		JavaSource js = pFactory.newJavaSource(qName, JavaSource.Protection.valueOf(modifiers));
		js.setAbstract(Modifier.isAbstract(modifiers));
		js.setStatic(Modifier.isStatic(modifiers));
		js.setType(Modifier.isInterface(modifiers) ? JavaSource.INTERFACE : JavaSource.CLASS);
		Class superClass = pClass.getSuperclass();
		if (superClass != null) {
			js.addExtends(superClass);
		}
		Class[] interfaces = pClass.getInterfaces();
		if (interfaces != null) {
			for (int i = 0;  i < interfaces.length;  i++) {
				js.addImplements(interfaces[i]);
			}
		}
		Constructor[] constructors = pClass.getConstructors();
		if (constructors != null) {
			for (int i = 0;  i < constructors.length;  i++) {
				js.newJavaConstructor(constructors[i]);
			}
		}
		Method[] methods = pClass.getMethods();
		if (methods != null) {
			for (int i = 0;  i < methods.length;  i++) {
				js.newJavaMethod(methods[i]);
			}
		}
		Field[] fields = pClass.getFields();
		if (fields != null) {
			for (int i = 0;  i < fields.length;  i++) {
				js.newJavaField(fields[i]);
			}
		}
		return js;
	}
}
