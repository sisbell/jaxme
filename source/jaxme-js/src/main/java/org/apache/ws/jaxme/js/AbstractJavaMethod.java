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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.js.JavaSource.Protection;


/** Base class of methods, constructors, and the like.
 */
public abstract class AbstractJavaMethod extends ConditionalIndentationJavaSourceObject {
	private List exceptions = new ArrayList();
	private List params = new ArrayList();
	
	protected AbstractJavaMethod(String pName, JavaQName pType,
			Protection pProtection) {
		super(pName, pType, pProtection);
	}
	
	/** <p>Returns whether the method is throwing the given exception.
	 * Note that this method doesn't care for inheritance. For example,
	 * if the method declares to be throwing an {@link java.net.MalformedURLException},
	 * then the value <code>isThrowing(java.io.IOException.class)</code>
	 * is still false.</p>
	 */
	public boolean isThrowing(JavaQName e) {
		if (e == null) {
			throw new NullPointerException("The exception argument must not be null.");
		}
		for (Iterator iter = exceptions.iterator();  iter.hasNext();  ) {
			if (e.equals(iter.next())) {
				return true;
			}
		}
		return false;
	}
	
	/** <p>Returns whether the method is throwing the given exception.
	 * Note that this method doesn't care for inheritance. For example,
	 * if the method declares to be throwing an {@link java.net.MalformedURLException},
	 * then the value <code>isThrowing(java.io.IOException.class)</code>
	 * is still false.</p>
	 */
	public boolean isThrowing(Class e) {
		if (e == null) {
			throw new NullPointerException("The exception argument must not be null.");
		}
		return isThrowing(JavaQNameImpl.getInstance(e));
	}
	
	/** <p>Adds an exception to this methods list of exceptions.</p>
	 *
	 * @see #getExceptions
	 */
	public void addThrows(JavaQName e) {
		if (e == null) {
			throw new NullPointerException("The exception argument must not be null.");
		}
		if (!exceptions.contains(e)) {
			exceptions.add(e);
		}
	}
	
	/** <p>Adds an exception to this methods list of exceptions.</p>
	 *
	 * @see #getExceptions
	 */
	public void addThrows(Class e) {
		if (e == null) {
			throw new NullPointerException("The exception argument must not be null.");
		}
		exceptions.add(JavaQNameImpl.getInstance(e));
	}
	
	/** <p>Adds a parameter that this method takes.</p>
	 *
	 * @see #getParams
	 * @return An object to use for referencing the parameter inside the method.
	 */
	public Parameter addParam(Class p, String v) {
		return addParam(JavaQNameImpl.getInstance(p), v);
	}
	
	/** <p>Adds a parameter that this method takes.</p>
	 *
	 * @see #getParams
	 * @return An object to use for referencing the parameter inside the method.
	 */
	public Parameter addParam(JavaQName pType, String pName) {
		if (pType == null) {
			throw new NullPointerException("Type argument must not be null");
		}
		if (pName == null) {
			throw new NullPointerException("Parameter name argument must not be null");
		}
		for (Iterator iter = params.iterator(); iter.hasNext();) {
			Parameter param = (Parameter) iter.next();
			if (param.getName().equals(pName)) {
				throw new IllegalArgumentException("Parameter name '" + pName + "' is already used for a parameter of type " + param.getType());
			}
		}
		Parameter p = new Parameter(pType, pName);
		params.add(p);
		return p;
	}
	
	/** <p>Adds a parameter that this method takes.</p>
	 *
	 * @see #getParams
	 * @return An object to use for referencing the parameter inside the method.
	 */
	public Parameter addParam(Parameter pParam) {
		return addParam(pParam.getType(), pParam.getName());
	}
	
	/** <p>Clears the list of parameters.</p>
	 */
	public void clearParams() {
		params.clear();
	}
	
	/** <p>Returns the list of exceptions thrown by this method.</p>
	 *
	 * @see #addThrows(JavaQName)
	 */
	public JavaQName[] getExceptions() {
		return (JavaQName[]) exceptions.toArray(new JavaQName[exceptions.size()]);
	}
	
	
	/** <p>Returns the list of parameters that this method takes. Any element
	 * in the list is an instance of {@link Parameter}.</p>
	 *
	 * @return the list of parameters
	 * @see #addParam(JavaQName, String)
	 */
	public Parameter[] getParams() {
		return (Parameter[]) params.toArray(new Parameter[params.size()]);
	}
	
	/** <p>Returns a list of the parameter names that this method takes. Any element
	 * in the list is an instance of {@link java.lang.String}.</p>
	 *
	 * @return the list of parameter names
	 * @see #addParam(JavaQName, String)
	 */
	public String[] getParamNames() {
		String[] res = new String[params.size()];
		for (int i = 0;  i < params.size();  i++) {
			res[i] = ((Parameter) params.get(i)).getName();        
		}
		return res;
	}
	
	/** <p>Returns an array of the parameter types that this method takes. This array can be used for JavaSource.getMethod() or JavaSource.getConstructor().</p>
	 *
	 * @return the list of parameter types
	 * @see #addParam(JavaQName, String)
	 */
	public JavaQName[] getParamTypes() {
		JavaQName[] res = new JavaQName[params.size()];
		for (int i = 0;  i < params.size();  i++) {
			res[i] = ((Parameter) params.get(i)).getType();        
		}
		return res;
	}
	
	/** <p>Removes an exception from this methods list of exceptions, if it is declared to be thrown.</p>
	 * @param exc the exception to be removed 
	 */
	public void removeThrows(JavaQName exc) {
		exceptions.remove(exc);
	}
	
	/** <p>Removes an exception from this methods list of exceptions, if it is declared to be thrown.</p>
	 * @param exc the exception to be removed 
	 */
	public void removeThrows(Class exc) {
		removeThrows(JavaQNameImpl.getInstance(exc));
	}

	/** <p>Clears the list of thrown exceptions.</p>
	 */
	public void clearThrows() {
		exceptions.clear();
	}
}
