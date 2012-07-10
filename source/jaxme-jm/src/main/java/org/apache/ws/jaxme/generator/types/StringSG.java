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
package org.apache.ws.jaxme.generator.types;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.AtomicTypeSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.impl.REFactory;
import org.apache.ws.jaxme.impl.REHandler;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.JavaSource.Protection;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class StringSG extends AtomicTypeSGImpl {
  /** <p>Creates a new instance of StringTypeSG.java.</p>
   */
  public StringSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
  }

  /** The string type.
   */
  public final static JavaQName STRING_TYPE = JavaQNameImpl.getInstance(String.class);
  protected String getDatatypeName() { return "String"; }
  protected JavaQName getDatatypeType() { return STRING_TYPE; }

  public JavaQName getRuntimeType(SimpleTypeSG pController) { return STRING_TYPE; }

  public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) {
    return new TypedValueImpl(JavaSource.getQuoted(pValue), STRING_TYPE);
  }

  public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, Object pData) {
    return new TypedValueImpl(pValue, STRING_TYPE);
  }

  public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    LocalJavaField f = pMethod.newJavaField(STRING_TYPE);
    f.addLine(pValue);
    pMethod.addIf(f, " != null");
    pSGlet.generate(pMethod, pValue);
    pMethod.addEndIf();
  }

  public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    pSGlet.generate(pMethod, pValue);
  }

  	public boolean isCausingParseConversionEvent(SimpleTypeSG pController) {
		final AtomicTypeSG atomicType = pController.getAtomicType();
		/* See addValidation(SimpleTypeSG, JavaMethod, DirectAccessible) for
		 * when an exception can be thrown.
		 */
		return atomicType.getLength() != null
		   ||  atomicType.getMinLength() != null
		   ||  atomicType.getMaxLength() != null
		   ||  atomicType.getPatterns() != null;
  	}

  	private String newReFieldName(JavaSource pJs) {
  		for (int i = 0;  ;  i++) {
  			String name = "_re_" + i;
  			JavaField jf = pJs.getField(name);
  			if (jf == null) {
  				return name;
  			}
  		}
  	}

  	public void addHandlerValidation(SimpleTypeSG pController, JavaMethod pJm, TypedValue pValue, Object pStringValue)
			throws SAXException {
  		String[] patterns = pController.getAtomicType().getPatterns();
  		if (patterns == null  ||  patterns.length == 0) {
  			return;
  		}
  		JavaSource js = pJm.getJavaSource();

  		final List expr = new ArrayList();
  		for (int j = 0;  j < patterns.length;  j++) {
  			String name = newReFieldName(js);
  			JavaField jf = js.newJavaField(name, REHandler.Matcher.class, JavaSource.PRIVATE);
  			jf.setStatic(true);
  			jf.setFinal(true);
  			jf.addLine(REFactory.class, ".getREHandler().getMatcher(",
  					   JavaSource.getQuoted(patterns[j]), ")");
  			if (!expr.isEmpty()) {
  			    expr.add("  &&  ");
  			}
  			expr.add("!");
  			expr.add(jf);
  			expr.add(".matches(");
  			expr.add(pStringValue);
  			expr.add(")");
  		}
  		pJm.addIf(expr.toArray());
  		if (patterns.length > 1) {
  		    List msg = new ArrayList();
  		    msg.add(JavaSource.getQuoted("Value doesn't match any of the patterns "));
  		    for (int i = 0;  i < patterns.length;  i++) {
  		        if (i > 0) {
  		            msg.add(" + ");
  		            msg.add(JavaSource.getQuoted(", "));
  		        }
  		        msg.add(" + ");
  		        msg.add(JavaSource.getQuoted(patterns[i]));
  		    }
  		    pJm.addThrowNew(IllegalArgumentException.class, msg.toArray());
  		} else {
  		    pJm.addThrowNew(IllegalArgumentException.class,
  	                JavaSource.getQuoted("Value doesn't match pattern "),
  	                    " + ", JavaSource.getQuoted(patterns[0]));
  		}
  		pJm.addEndIf();
  	}

    public void addValidation(SimpleTypeSG pController, JavaMethod pMethod, DirectAccessible pValue)
			throws SAXException {
		/* If you add additional checks here, you should possibly modify
		 * the isCausingParseConversionEvent(SimpleTypeSG) method as well.
		 */
		final AtomicTypeSG atomicType = pController.getAtomicType();
		Long length = atomicType.getLength();
		Long maxLength = atomicType.getMaxLength();
		Long minLength = atomicType.getMinLength();

		if (minLength != null  &&  minLength.longValue() < 0) {
			throw new LocSAXException("Negative value for minLength detected: " + minLength, getLocator());
		}
		if (maxLength != null) { 
			if (maxLength.longValue() < 0) {
				throw new LocSAXException("Negative value for maxLength detected: " + maxLength, getLocator());
			}
			if (minLength != null) {
				if (maxLength.longValue() < minLength.longValue()) {
					throw new LocSAXException("MaxLength value of " + maxLength + " is smaller than minLength value of " + minLength,
							getLocator());
				}
				if (maxLength.longValue() == minLength.longValue()) {
					length = maxLength;
				}
			}
		}
		if (length != null) { 
			if (length.longValue() < 0) {
				throw new LocSAXException("Negative value for length detected: " + length, getLocator());
			}
			if (maxLength != null) {
				if (maxLength.longValue() < length.longValue()) {
					throw new LocSAXException("MaxLength value of " + maxLength + " is smaller than length value of " + length,
							getLocator());
				}
				maxLength = null;  // Avoid unnecessary checks
			}
			if (minLength != null) { 
				if (minLength.longValue() > length.longValue()) { 
					throw new LocSAXException("MinLength value of " + minLength + " is larger than length value of " + length,
							getLocator());
				}
				minLength = null;  // Avoid unnecessary checks
			}
		}
		
		if (length != null  ||  maxLength != null  ||  minLength != null) {
			if (pValue.isNullable()) {
				pMethod.addIf(pValue, " != null");
			}
			if (maxLength != null) {
				pMethod.addIf(pValue, ".length()", " > ", maxLength);
				pMethod.addThrowNew(IllegalArgumentException.class,
						JavaSource.getQuoted("Length of " + maxLength + " characters exceeded: "),
											 " + ", pValue);
				pMethod.addEndIf();
			}
			if (minLength != null) {
				pMethod.addIf(pValue, ".length()", " < ", minLength);
				pMethod.addThrowNew(IllegalArgumentException.class,
						JavaSource.getQuoted("Minimum length of " + minLength + " characters not matched: "),
											 " + ", pValue);
				pMethod.addEndIf();
			}
			if (length != null) {
				pMethod.addIf(pValue, ".length()", " != ", length);
				pMethod.addThrowNew(IllegalArgumentException.class,
						JavaSource.getQuoted("Length of " + length + " characters not matched: "),
											 " + ", pValue);
				pMethod.addEndIf();
			}
			if (pValue.isNullable()) {
				pMethod.addEndIf();
			}
		}
	}
}
