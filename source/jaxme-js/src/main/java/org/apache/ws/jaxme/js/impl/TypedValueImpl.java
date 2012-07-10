package org.apache.ws.jaxme.js.impl;

import java.io.IOException;

import org.apache.ws.jaxme.js.IndentationEngine;
import org.apache.ws.jaxme.js.IndentationTarget;
import org.apache.ws.jaxme.js.IndentedObject;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.TypedValue;


/** Default implementation of a typed value.
 */
public class TypedValueImpl implements TypedValue, IndentedObject {
	private final Object value;
	private final JavaQName type;

	/** Creates a new instance with the given value and
	 * the given type.
	 */
	public TypedValueImpl(Object pValue, JavaQName pType) {
		value = pValue;
		type = pType;
	}
	/** Creates a new instance with the given value and
	 * the given type.
	 */
	public TypedValueImpl(Object pValue, Class pType) {
		this(pValue, JavaQNameImpl.getInstance(pType));
	}
	public JavaQName getType() { return type; }
	public void write(IndentationEngine pEngine, IndentationTarget pTarget) throws IOException {
		pEngine.write(pTarget, value);
	}
}
