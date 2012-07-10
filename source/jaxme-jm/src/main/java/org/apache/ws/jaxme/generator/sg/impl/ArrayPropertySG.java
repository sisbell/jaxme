package org.apache.ws.jaxme.generator.sg.impl;

import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.PropertySGChain;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.TypedValue;
import org.xml.sax.SAXException;


/** A subclass of {@link MultiplePropertySG}, which generates
 * arrays rather than lists.
 */
public class ArrayPropertySG extends MultiplePropertySG {
	protected ArrayPropertySG(PropertySGChain pBase, ObjectSG pObjectSG, int pMinOccurs, int pMaxOccurs) throws SAXException {
		super(pBase, pObjectSG, pMinOccurs, pMaxOccurs);
	}

	private JavaQName getArrayClass() throws SAXException {
		return JavaQNameImpl.getArray(getInstanceClass());
	}

	public JavaMethod getXMLGetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		JavaQName arrayClass = getArrayClass();
		String fieldName = pController.getXMLFieldName();
		String methodName = pController.getXMLGetMethodName();
		JavaMethod result = pSource.newJavaMethod(methodName, arrayClass, JavaSource.PUBLIC);
		if (!pSource.isInterface()) {
			Object o = new Object[]{"new ", arrayClass.getInstanceClass(),
									"[", fieldName, ".size()]"};
			if (isAutoBoxing()) {
				LocalJavaField res = result.newJavaField(getArrayClass());
				res.addLine(o);
				DirectAccessible i = result.addForList(int.class, fieldName);
				result.addLine(res, "[", i, "] = ", asPrimitive(i, fieldName), ";");
				result.addEndFor();
				result.addLine("return ", res, ";");
			} else {
				result.addLine("return (", arrayClass, ") ", fieldName,
						".toArray(", o, ");");
			}
		}
		return result;
	}

	protected JavaMethod getXMLGetElementMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		JavaQName instanceClass = getInstanceClass();
		String fieldName = pController.getXMLFieldName();
		String methodName = pController.getXMLGetMethodName();
		JavaMethod result = pSource.newJavaMethod(methodName, instanceClass, JavaSource.PUBLIC);
		Parameter index = result.addParam(int.class, "pIndex");
		if (!pSource.isInterface()) {
			result.addLine("return ", asPrimitive(index, fieldName), ";");
		}
		return result;
	}

	protected JavaMethod getXMLGetLengthMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		String fieldName = pController.getXMLFieldName();
		String methodName = pController.getXMLGetMethodName() + "Length";
		JavaMethod result = pSource.newJavaMethod(methodName, int.class, JavaSource.PUBLIC);
		if (!pSource.isInterface()) {
			result.addLine("return ", fieldName, ".size();");
		}
		return result;
	}

	private Object asPrimitive(DirectAccessible pIndex, String pFieldName) throws SAXException {
		return asPrimitive(new Object[]{"((", getObjectClass(), ") ",
										pFieldName, ".get(", pIndex, "))"});
	}

	private Object asPrimitive(Object pObject) throws SAXException {
		if (isAutoBoxing()) {
			return new Object[]{pObject, ".", getInstanceClass().getPrimitiveConversionMethod(), "()"};
		} else {
			return pObject;
		}
	}

	public JavaMethod getXMLSetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		JavaQName arrayClass = getArrayClass();
		String fieldName = pController.getXMLFieldName();
		String methodName = pController.getXMLSetMethodName();
		JavaMethod result = pSource.newJavaMethod(methodName, "void", JavaSource.PUBLIC);
		Parameter array = result.addParam(arrayClass, "pValue");
		if (!pSource.isInterface()) {
			result.addLine(fieldName, ".clear();");
			result.addIf(array, " != null");
			DirectAccessible i = result.addForArray(int.class, array);
			Object o = new Object[]{array, "[", i, "]"};
			result.addLine(fieldName, ".add(", asObject(o), ");");
			result.addEndFor();
			result.addEndIf();
		}
		return result;
	}

	protected JavaMethod getXMLSetElementMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		JavaQName instanceClass = getInstanceClass();
		String fieldName = pController.getXMLFieldName();
		String methodName = pController.getXMLSetMethodName();
		JavaMethod result = pSource.newJavaMethod(methodName, "void", JavaSource.PUBLIC);
		Parameter index = result.addParam(int.class, "pIndex");
		Parameter element = result.addParam(instanceClass, "pValue");
		if (!pSource.isInterface()) {
			result.addLine(fieldName, ".set(", index, ", ", asObject(element), ");");
		}
		return result;
	}

	public void forAllValues(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException {
		LocalJavaField array = pMethod.newJavaField(getArrayClass());
		array.addLine(pController.getValue(pElement));
		DirectAccessible i = pMethod.addForArray(array);
		Object v;
		boolean isCasting = !OBJECT_TYPE.equals(getInstanceClass());
		if (isCasting  &&  pSGlet instanceof SGlet.TypedSGlet) {
			isCasting = ((SGlet.TypedSGlet) pSGlet).isCasting();
		}
		if (isCasting) {
			v = new Object[]{"(", getInstanceClass(), ")", array, "[", i, "]"};
		} else {
			v = new Object[]{array, "[", i, "]"};
		}
		TypeSG tSG = getObjectSG().getTypeSG();
		if (tSG.isComplex()) {
			pSGlet.generate(pMethod, v);
		} else {
			tSG.getSimpleTypeSG().forAllValues(pMethod, v, pSGlet);
		}
		pMethod.addEndFor();
	}

	public void forAllNonNullValues(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException {
		LocalJavaField array = pMethod.newJavaField(getArrayClass());
		array.addLine(pController.getValue(pElement));
		DirectAccessible i = pMethod.addForArray(array);
		TypeSG typeSG = getObjectSG().getTypeSG();
		Object v;
		boolean isCasting = !OBJECT_TYPE.equals(typeSG.getRuntimeType());
		JavaQName qName = typeSG.getRuntimeType();
		if (isCasting  &&  pSGlet instanceof SGlet.TypedSGlet) {
			SGlet.TypedSGlet typedSGlet = (SGlet.TypedSGlet) pSGlet;
			isCasting = typedSGlet.isCasting();
			if (typedSGlet.getType() != null) {
				qName = typedSGlet.getType();
			}
		}
		if (isCasting) {
			v = new Object[]{"(", qName, ")", array, "[", i, "]"};
		} else {
			v = new Object[]{array, "[", i, "]"};
		}
		if (typeSG.isComplex()) {
			pSGlet.generate(pMethod, v);
		} else {
			typeSG.getSimpleTypeSG().forAllNonNullValues(pMethod, v, pSGlet);
		}
		pMethod.addEndFor();
	}

	public void generate(PropertySG pController, JavaSource pSource) throws SAXException {
		super.generate(pController, pSource);
		getXMLSetElementMethod(pController, pSource);
		getXMLGetElementMethod(pController, pSource);
		getXMLGetLengthMethod(pController, pSource);
	}

	public void setValue(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, Object pValue, JavaQName pType)
			throws SAXException {
		pMethod.addLine(pElement, ".", pController.getXMLSetMethodName(), "(", pValue, ");"); 
	}

	public void addValue(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, TypedValue pValue, JavaQName pType) throws SAXException {
		if (getObjectSG().getTypeSG().isComplex()) {
			LocalJavaField g = pMethod.newJavaField(getInstanceClass());
			g.addLine("(", getInstanceClass(), ") ", pValue);
			pValue = g;
		}
		JavaQName arrayClass = getArrayClass();
		LocalJavaField f = pMethod.newJavaField(arrayClass);
		f.addLine(pController.getValue(pElement));
		pMethod.addIf(f, " == null  ||  ", f, ".length == 0");
		pMethod.addLine(f, " = new ", arrayClass, "{", pValue, "};");
		pController.setValue(pMethod, pElement, f, pType);
		pMethod.addElse();
		LocalJavaField g = pMethod.newJavaField(arrayClass);
		g.addLine("new ", getInstanceClass(), "[", f, ".length + 1]");
		pMethod.addLine(System.class, ".arraycopy(", f, ", 0, ", g, ", 0, ", f, ".length);");
		pMethod.addLine(g, "[", f, ".length] = ", pValue, ";");
		pController.setValue(pMethod, pElement, g, pType);
		pMethod.addEndIf();
	}
}
