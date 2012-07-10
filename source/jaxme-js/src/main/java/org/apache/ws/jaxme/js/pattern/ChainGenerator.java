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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.Parameter;

import antlr.RecognitionException;
import antlr.TokenStreamException;


/** <p>This class generates so-called event chains. A chain is an
 * interface and an object implementing the interface. Internally
 * the implementation is using a list of chained objects, which
 * you can assume to implement the same interface.</p>
 * <p>Any event is passed to the first object in the list. The object
 * may decide to resolve the event immediately and return. It may
 * also call pass the event to the next object, take the returned
 * value, possibly modify it and return the result. Finally, the
 * chained object may decide to emit another event (which is passed
 * along the same chain), and use the returned value.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ChainGenerator {
	private ClassLoader classLoader;
	private String controllerInterfaceName;
	private JavaSource controllerInterface;
	private JavaQName chainInterface, proxyClass, implClass;
	
	/** <p>Sets the controller interface name.</p>
	 */
	public void setControllerInterfaceName(String pInterfaceName) throws ClassNotFoundException {
		controllerInterfaceName = pInterfaceName;
	}
	
	/** Returns the controller interface.
	 */
	public JavaSource getControllerInterface() {
		return controllerInterface;
	}
	
	private JavaSource loadSource(String pName,
			JavaSourceFactory pFactory)
	throws RecognitionException, TokenStreamException, IOException {
		URL url = classLoader.getResource(pName.replace('.', '/') + ".java");
		if (url == null) {
			return null;
		} else {
			return new SourceReflector(url).getJavaSource(pFactory);
		}
	}
	
	private void loadSources(JavaQName pQName,
			JavaSourceFactory pFactory,
			List pSources, Set pNames)
	throws RecognitionException, TokenStreamException, IOException {
		if (pNames.contains(pQName)) {
			return;
		}
		pNames.add(pQName);
		JavaSource js = loadSource(pQName.toString(), pFactory);
		if (js == null) {
			return;
		}
		pSources.add(js);
		JavaQName[] superInterfaces = js.getExtends();
		for (int i = 0;  i < superInterfaces.length;  i++) {
			loadSources(superInterfaces[i],
					pFactory, pSources, pNames);
		}
	}
	
	private JavaSource[] loadSources(String pName)
	throws RecognitionException, TokenStreamException, IOException {
		JavaSourceFactory jsf = new JavaSourceFactory();
		List sources = new ArrayList();
		Set names = new HashSet();
		loadSources(JavaQNameImpl.getInstance(pName),
				jsf, sources, names);
		if (sources.isEmpty()) {
			return null;
		} else {
			return (JavaSource[]) sources.toArray(new JavaSource[sources.size()]);
		}
	}
	
	/** <p>Initializes the controller interface.</p>
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws TokenStreamException
	 * @throws RecognitionException
	 */
	protected JavaSource[] initControllerInterface()
	throws ClassNotFoundException, RecognitionException, TokenStreamException, IOException {
		JavaSource[] result;
		try {
			CompiledClassReflector r = new CompiledClassReflector(controllerInterfaceName, classLoader);
			result = new JavaSource[]{r.getJavaSource(new JavaSourceFactory())};
		} catch (ClassNotFoundException e) {
			result = loadSources(controllerInterfaceName);
			if (result == null) {
				throw e;
			}
		}
		if (!result[0].isInterface()) {
			throw new ClassCastException("The controller must be an interface");
		}
		return result;
	}
	
	/** <p>Sets the interface name being generated for the chain objects.</p>
	 */
	public void setChainInterfaceName(String pInterfaceName) {
		JavaQName qName = JavaQNameImpl.getInstance(pInterfaceName);
		setChainInterface(qName);
	}
	
	/** <p>Sets the interface being generated for the chain objects.</p>
	 */
	public void setChainInterface(JavaQName pInterface) {
		chainInterface = pInterface;
	}
	
	/** <p>Returns the interface being generated for the chain objects.</p>
	 */
	public JavaQName getChainInterface() {
		return chainInterface;
	}
	
	/** <p>Sets the class name being generated for the chain objects.</p>
	 */
	public void setProxyClassName(String pClassName) {
		JavaQName qName = JavaQNameImpl.getInstance(pClassName);
		setProxyClass(qName);
	}
	
	/** <p>Sets the class being generated for the chain objects.</p>
	 */
	public void setProxyClass(JavaQName pClassName) {
		proxyClass = pClassName;
	}
	
	/** <p>Returns the class being generated for the chain objects. Defaults
	 * to <code>getChainInterface() + "Impl"</code>.</p>
	 */
	public JavaQName getProxyClass() {
		if (proxyClass == null) {
			JavaQName chainClass = getChainInterface();
			if (chainClass == null) {
				return null; 
			} else {
				return JavaQNameImpl.getInstance(chainClass.getPackageName(),
						chainClass.getClassName() + "Impl");
			}
		} else {
			return proxyClass;
		}
	}
	
	/** <p>Sets the name of the chain implementation class.</p>
	 */
	public void setImplementationClassName(String pClassName) {
		setImplementationClass(JavaQNameImpl.getInstance(pClassName));
	}
	
	/** <p>Sets the chain implementation class.</p>
	 */
	public void setImplementationClass(JavaQName pClassName) {
		implClass = pClassName;
	}
	
	/** <p>Returns the chain implementation classes name. Defaults to
	 * <code>getControllerInterface() + "Impl"</code>.</p>
	 */
	public JavaQName getImplementationClass() {
		if (implClass == null) {
			if (controllerInterface == null) {
				return null;
			} else {
				JavaQName controllerClass = controllerInterface.getQName();
				return JavaQNameImpl.getInstance(controllerClass.getPackageName(),
						controllerClass.getClassName() + "Impl");
			}
		} else {
			return implClass;
		}
	}
	
	/** Validates the input data.
	 */
	public void finish() {
		if (controllerInterface == null) {
			throw new NullPointerException("A controller interface must be given.");
		}
		if (chainInterface == null) {
			throw new NullPointerException("A chain interface must be given.");
		}
	}
	
	private class ProxyInterfaceGenerator extends ProxyGenerator {
		public JavaMethod getInterfaceMethod(JavaSource pSource, JavaMethod pMethod) {
			JavaMethod jm = pSource.newJavaMethod(pMethod);
			Parameter[] parameters = jm.getParams();
			JavaQName controllerInterfaceQName = getControllerInterface().getQName();
			jm.clearParams();
			jm.addParam(controllerInterfaceQName, "pController");
			for (int i = 0;  i < parameters.length;  i++) {
				jm.addParam(parameters[i]);
			}
			return jm;
		}
		public JavaSource generate(JavaSourceFactory pInterfaceFactory,
				JavaQName pTargetClass,
				InterfaceDescription[] pDescription) throws Exception {
			JavaSource result = super.generate(pInterfaceFactory, pTargetClass,
					pDescription);
			result.clearImplements();
			return result;
		}
	}
	
	private class ProxyImplementationGenerator extends ProxyGenerator {
		protected JavaField getBackingObjectField(JavaSource pJs, InterfaceDescription[] pInterfaces) {
			return pJs.newJavaField("backingObject", getChainInterface(), JavaSource.PRIVATE);
		}
		protected JavaConstructor getConstructor(JavaSource pJs,
				InterfaceDescription[] pInterfaces) {
			JavaConstructor jcon = pJs.newJavaConstructor(JavaSource.PROTECTED);
			jcon.addParam(getChainInterface(), "o");
			jcon.addIf("o == null");
			jcon.addThrowNew(NullPointerException.class,
					JavaSource.getQuoted("The supplied object must not be null."));
			jcon.addEndIf();
			jcon.addLine("backingObject = o;");
			return jcon;
		}
		
		public JavaMethod getInterfaceMethod(JavaSource pSource, JavaMethod pMethod) {
			JavaMethod jm = pSource.newJavaMethod(pMethod);
			Parameter[] parameters = jm.getParams();
			JavaQName controllerInterfaceQName = getControllerInterface().getQName();
			jm.clearParams();
			jm.addParam(controllerInterfaceQName, "pController");
			for (int i = 0;  i < parameters.length;  i++) {
				jm.addParam(parameters[i]);
			}
			List callParameters = new ArrayList();
			callParameters.add("pController");
			for (int i = 0;  i < parameters.length;  i++) {
				Parameter parameter = parameters[i];
				callParameters.add(", "); 
				callParameters.add(parameter.getName());
			}
			jm.addLine((JavaQNameImpl.VOID.equals(pMethod.getType()) ? "" : "return "),
					"backingObject.",
					pMethod.getName(), "(", callParameters, ");");
			return jm;
		}
		public JavaSource generate(JavaSourceFactory pImplementationFactory,
				JavaQName pTargetClass,
				InterfaceDescription[] pDescription) throws Exception {
			JavaSource result = super.generate(pImplementationFactory, pTargetClass, pDescription);
			result.clearImplements();
			result.addImplements(getChainInterface());
			return result;
		}
	}
	
	private class ControllerImplementationGenerator extends ProxyGenerator {
		protected JavaField getBackingObjectField(JavaSource pJs, InterfaceDescription[] pInterfaces) {
			return pJs.newJavaField("backingObject", getChainInterface(), JavaSource.PRIVATE);
		}
		protected JavaConstructor getConstructor(JavaSource pJs,
				InterfaceDescription[] pInterfaces) {
			JavaConstructor jcon = pJs.newJavaConstructor(JavaSource.PUBLIC);
			jcon.addParam(getChainInterface(), "o");
			jcon.addIf("o == null");
			jcon.addThrowNew(NullPointerException.class,
					JavaSource.getQuoted("The supplied object must not be null."));
			jcon.addEndIf();
			jcon.addLine("backingObject = o;");
			return jcon;
		}
		public JavaMethod getInterfaceMethod(JavaSource pSource,
				JavaMethod pMethod) {
			JavaMethod jm = pSource.newJavaMethod(pMethod);
			Parameter[] parameters = jm.getParams();
			List callParameters = new ArrayList();
			callParameters.add("this");
			for (int i = 0;  i < parameters.length;  i++) {
				Parameter parameter = parameters[i];
				callParameters.add(", "); 
				callParameters.add(parameter.getName());
			}
			jm.addLine((JavaQNameImpl.VOID.equals(pMethod.getType()) ? "" : "return "),
					"backingObject.",
					pMethod.getName(), "(", callParameters, ");");
			return jm;
		}
		protected JavaMethod getGetHeadOfChainMethod(JavaSource pSource) {
			JavaMethod jm = pSource.newJavaMethod("getHeadOfChain",
					getChainInterface(),
					JavaSource.PUBLIC);
			jm.addLine("return backingObject;");
			return jm;
		}
		public JavaSource generate(JavaSourceFactory pImplementationFactory,
				JavaQName pTargetClass,
				InterfaceDescription[] pDescription) throws Exception {
			JavaSource result = super.generate(pImplementationFactory, pTargetClass, pDescription);
			getGetHeadOfChainMethod(result);
			return result;
		}
	}
	
	/** Performs the actual work by generating classes using
	 * the given <code>pFactory</code>.
	 */
	public JavaSource[] generate(JavaSourceFactory pFactory, ClassLoader pClassLoader) throws Exception {
		classLoader = pClassLoader;
		JavaSource[] sources = initControllerInterface();
		controllerInterface = sources[0];
		InterfaceDescription[] interfaces = new InterfaceDescription[sources.length];
		for (int i = 0;  i < interfaces.length;  i++) {
			InterfaceDescription controllerDescription = new InterfaceDescription(classLoader);
			controllerDescription.setInterface(sources[i].getQName().toString());
			controllerDescription.setMandatory(true);
			interfaces[i] = controllerDescription;
		}
		
		ProxyGenerator proxyInterfaceGenerator = new ProxyInterfaceGenerator();
		JavaSource proxyInterface = proxyInterfaceGenerator.generate(pFactory, getChainInterface(), interfaces);
		proxyInterface.setType(JavaSource.INTERFACE);
		
		ProxyGenerator proxyImpGenerator = new ProxyImplementationGenerator();
		JavaSource proxyImplementation = proxyImpGenerator.generate(pFactory, getProxyClass(), interfaces);
		
		ProxyGenerator controllerImplementationGenerator = new ControllerImplementationGenerator();
		JavaSource controllerImplementation = controllerImplementationGenerator.generate(pFactory, getImplementationClass(), interfaces);
		
		return new JavaSource[]{controllerImplementation, proxyInterface, proxyImplementation};
	}
}
