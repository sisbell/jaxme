package org.apache.ws.jaxme.generator.sg.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain;
import org.apache.ws.jaxme.generator.sg.impl.SimpleTypeSGChainImpl;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/** Simple type chain for implementing
 * <code>jaxb:javaType/@parseMethod</code> and/or
 * <code>jaxb:javaType/@printMethod</code>.
 */
public class ParsePrintSG extends SimpleTypeSGChainImpl {
	private final JAXBJavaType javaType;

    /** Creates a new instance, which generates code
     * for the given type.
	 */
    public ParsePrintSG(SimpleTypeSGChain pType, JAXBJavaType pJavaType) {
		super(pType);
        javaType = pJavaType;
	}

    private JavaQName getType(SimpleTypeSG pController) {
        if (javaType.getName() == null) {
        	return pController.getRuntimeType();
        } else {
            return JavaQNameImpl.getInstance(javaType.getName());
        }
    }

    private void addCall(SimpleTypeSG pController, List pList, String pMethod,
                         JavaQName pType) {
        int offset = pMethod.indexOf('.');
        JavaQName qName;
        String method;
        if (offset == -1) {
            qName = getType(pController);
            method = pMethod;
        } else {
            qName = JavaQNameImpl.getInstance(pMethod.substring(0, offset));
            method = pMethod.substring(offset+1);
        }
        pList.add(qName);
        pList.add(".");
        pList.add(method);
    }

    private DirectAccessible getValue(JavaMethod pMethod, Object pValue, JavaQName pType) {
        if (pValue instanceof DirectAccessible) {
        	return (DirectAccessible) pValue;
        } else {
            LocalJavaField s = pMethod.newJavaField(pType);
            s.addLine(pValue);
            return s;
        }
    }

    private void addValues(List pList, DirectAccessible pValue, Object pData) {
    	pList.add("(");
        pList.add(pValue);
        if (javaType.hasNsContext()) {
            pList.add(", ");
            pList.add(pData);
            pList.add(".getNamespaceSupport()");
        }
        pList.add(")");
    }

    private TypedValue getParseCall(SimpleTypeSG pController, JavaMethod pMethod,
                                Object pValue, Object pData)
            throws SAXParseException {
        JavaQName type = getType(pController);
        DirectAccessible value = getValue(pMethod, pValue, JavaQNameImpl.getInstance(String.class));

        String parseMethod = javaType.getParseMethod().trim();
        List list = new ArrayList();
        if (parseMethod.startsWith("new")
            &&  parseMethod.length() > 3
            &&  Character.isWhitespace(parseMethod.charAt(3))) {
        	JavaQName qName = JavaQNameImpl.getInstance(parseMethod.substring(3).trim());
            list.add("new ");
            list.add(qName);
        } else {
            addCall(pController, list, parseMethod, type);
        }
        addValues(list, value, pData);
		return new TypedValueImpl(list, type);
    }

    private TypedValue getPrintCall(SimpleTypeSG pController, JavaMethod pMethod,
                                Object pValue, Object pData) {
    	JavaQName type = getType(pController);
        LocalJavaField f = pMethod.newJavaField(String.class);
        DirectAccessible value = getValue(pMethod, pValue, type);
        List list = new ArrayList();
        addCall(pController, list, javaType.getPrintMethod(), type);
        addValues(list, value, pData);
        pMethod.addTry();
        pMethod.addLine(f, " = ", list, ";");
        DirectAccessible e = pMethod.addCatch(Exception.class);
        pMethod.addLine(pData, ".printConversionEvent(pObject, ", 
                        JavaSource.getQuoted("Failed to convert value "),
                        " + ", value, " + ", JavaSource.getQuoted(": "),
                        " + ", e, ".getClass().getName(), ", e, ");");
        pMethod.addLine(f, " = null;");
        pMethod.addEndTry();
        return f;
    }

    public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod,
    		                            Object pValue, Object pData)
            throws SAXException {
    	if (javaType.getParseMethod() == null) {
    		return super.getCastFromString(pController, pMethod, pValue, pData);
        } else {
        	return getParseCall(pController, pMethod, pValue, pData);
        }
    }

    public TypedValue getCastFromString(SimpleTypeSG pController, String pValue)
            throws SAXException {
    	if (javaType.getParseMethod() == null) {
    		return super.getCastFromString(pController, pValue);
        } else {
        	throw new SAXParseException("Use of the parse method at compile time is unsupported.",
        			                    javaType.getLocator());
        }
    }

    public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod,
                                  Object pValue, DirectAccessible pData)
            throws SAXException {
        if (javaType.getPrintMethod() == null) {
        	return super.getCastToString(pController, pMethod, pValue, pData);
        } else {
        	return getPrintCall(pController, pMethod, pValue, pData);
        }
    }

	public boolean isCausingParseConversionEvent(SimpleTypeSG pController) {
		return true;
	}
}
