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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.Parameter;


/** <p>This class is a generator for the proxy object pattern. A proxy
 * object performs the same task as an object created by the
 * {@link java.lang.reflect.Proxy} class: It delegates its method
 * calls to an internal instance.</p>
 * <p>In the case of {@link java.lang.reflect.Proxy} this works by
 * invoking a so-called {@link java.lang.reflect.InvocationHandler}.
 * The InvocationHandler calls the actual object via Java reflection.</p>
 * <p>In our case, the proxy object is an instance of a generated
 * class. The main advantage of the generated approach is that you
 * can customize the proxy class quite easily by overwriting it.
 * Compared to the creation of an InvocationHandler, this saves a
 * lot of hazzle.</p>
 */
public class ProxyGenerator {
    private JavaQName extendedClass;

	/** <p>Returns the class extended by the generated proxy class.
	 * Defaults to {@link java.lang.Object}.</p>
	 */
	public JavaQName getExtendedClass() {
		return extendedClass;
	}

	/** <p>Sets the class extended by the generated proxy class.
	 * Defaults to {@link java.lang.Object}.</p>
	 */
	public void setExtendedClass(JavaQName pExtendedClass) {
		extendedClass = pExtendedClass;
	}

	/** <p>Generated an instance of {@link JavaMethod} for the given
	 * {@link JavaMethod}.</p>
	 */
	protected JavaMethod getInterfaceMethod(JavaSource pJs,
											JavaMethod pMethod) {
		JavaMethod jm = pJs.newJavaMethod(pMethod);
		Parameter[] parameters = jm.getParams();
		List callParameters = new ArrayList();
		for (int i = 0;  i < parameters.length;  i++) {
			Parameter parameter = parameters[i];
			if (callParameters.size() > 0) {
				callParameters.add(", "); 
			}
			callParameters.add(parameter.getName());
		}
        if (pMethod.getType().equals(JavaQNameImpl.VOID)) {
        	jm.addLine("((", pMethod.getJavaSource().getQName(), ") backingObject).",
        			   pMethod.getName(), "(", callParameters, ");");
        } else {
            jm.addLine("return ",
            		   "((", pMethod.getJavaSource().getQName(), ") backingObject).",
					   pMethod.getName(), "(", callParameters, ");");
        }
		return jm;
	}

	/** <p>Generates the methods for a given interface.</p>
	 *
	 * @param pJs The Java class being generated
	 * @param pGeneratedMethods A set of already generated methods; each entry in the
	 *    set is an instance of {@link MethodKey}. The method creates a new instance
	 *    of {@link MethodKey} and adds it to the set. A warning is written to
	 *    {@link System#err}, if the method isn't unique.
	 * @throws ClassNotFoundException
	 */
	protected void generateInterfaceMethods(JavaSource pJs, Map pGeneratedMethods,
			                                JavaSource pInterface)
	        throws ClassNotFoundException {
        JavaMethod[] methods = pInterface.getMethods();
		for (int i = 0;  i < methods.length;  i++) {
			JavaMethod method = methods[i];
            if (method.isStatic()) {
            	continue;
            }
            if (!JavaSource.PUBLIC.equals(method.getProtection())) {
            	continue;
            }
			MethodKey key = new MethodKey(method);
			JavaMethod existingMethod = (JavaMethod) pGeneratedMethods.get(key);
			if (existingMethod == null) {
                JavaMethod generatedMethod = getInterfaceMethod(pJs, method);
                pGeneratedMethods.put(key, generatedMethod);
			} else {
                System.err.println("The methods "
                        + existingMethod.getJavaSource().getQName()
                        + "." + existingMethod.getName() + " and "
                        + pInterface.getQName() + "."
                        + method.getName() + " are identical, ignoring the latter.");
			}
		}
	}

	/** <p>Creates a constructor with protected access and a single argument,
	 * the backing object.</p>
	 */
	protected JavaConstructor getConstructor(JavaSource pJs,
			                                 InterfaceDescription[] pInterfaces)
            throws Exception{
		JavaConstructor jcon = pJs.newJavaConstructor(JavaSource.PROTECTED);
		jcon.addParam(Object.class, "o");
		jcon.addIf("o == null");
		jcon.addThrowNew(NullPointerException.class,
				         JavaSource.getQuoted("The supplied object must not be null."));
		jcon.addEndIf();
		for (int i = 0;  i < pInterfaces.length;  i++) {
			if (pInterfaces[i].isMandatory()) {
                JavaSource js = pInterfaces[i].getJavaSource();
				jcon.addIf("!(o instanceof ", js.getQName(), ")");
				jcon.addThrowNew(ClassCastException.class,
						         JavaSource.getQuoted("The supplied instance of "),
								 " + o.getClass().getName() + ",
								 JavaSource.getQuoted(" is not implementing "),
								 " + ", js.getQName(), ".class.getName()");
				jcon.addEndIf();
			}
		}
		jcon.addLine("backingObject = o;");
		return jcon;
	}

	/** <p>Creates the class.</p>
	 */
	protected JavaSource getJavaSource(JavaSourceFactory pFactory, JavaQName pTargetName) {
		return pFactory.newJavaSource(pTargetName, JavaSource.PUBLIC);
	}

	/** <p>Generates the <code>backingObject</code> field.</p>
	 */
	protected JavaField getBackingObjectField(JavaSource pJs, InterfaceDescription[] pInterfaces) {
		return pJs.newJavaField("backingObject", Object.class, JavaSource.PRIVATE);
	}

	/** <p>Generates a class implementing the given interfaces.</p>
	 * @param pFactory The ProxyGenerator will use this factory for creating
	 *    instances of JavaSource.
	 * @param pTargetName Name of the generated class
	 * @param pInterfaces The interfaces being implemented by the generated class.
	 * @throws ClassNotFoundException
	 */
	public JavaSource generate(JavaSourceFactory pFactory, JavaQName pTargetName,
			                   InterfaceDescription[] pInterfaces)
            throws Exception {
		JavaSource js = getJavaSource(pFactory, pTargetName);
		if (getExtendedClass() != null) {
			js.addExtends(getExtendedClass());
		}
		for (int i = 0;  i < pInterfaces.length;  i++) {
            JavaQName qName = pInterfaces[i].getJavaSource().getQName();
			js.addImplements(qName);
		}

		getBackingObjectField(js, pInterfaces);
		getConstructor(js, pInterfaces);

		Map methods = new HashMap();
		for (int i = 0;  i < pInterfaces.length;  i++) {
			generateInterfaceMethods(js, methods, pInterfaces[i].getJavaSource());
		}

		return js;
	}
}
