package org.apache.ws.jaxme.js.beanreader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.apps.JavaSourceResolver;


/** A {@link BeanInfoFactory} is reading and analyzing
 * a given bean class. It provides the information required
 * by the {@link BeanSchemaWriter}.
 */
public class BeanInfoFactory {
	private static final JavaQName OBJECT_TYPE = JavaQNameImpl.getInstance(Object.class);
	private static final JavaQName STRING_TYPE = JavaQNameImpl.getInstance(String.class);
	private static final JavaQName BYTE_TYPE = JavaQNameImpl.getInstance(Byte.class);
	private static final JavaQName BYTE_ARRAY_TYPE = JavaQNameImpl.getArray(JavaQNameImpl.BYTE);
	private static final JavaQName SHORT_TYPE = JavaQNameImpl.getInstance(Short.class);
	private static final JavaQName INTEGER_TYPE = JavaQNameImpl.getInstance(Integer.class);
	private static final JavaQName LONG_TYPE = JavaQNameImpl.getInstance(Long.class);
	private static final JavaQName FLOAT_TYPE = JavaQNameImpl.getInstance(Float.class);
	private static final JavaQName DOUBLE_TYPE = JavaQNameImpl.getInstance(Double.class);
	private static final JavaQName BOOLEAN_TYPE = JavaQNameImpl.getInstance(Boolean.class);
	private static final JavaQName CALENDAR_TYPE = JavaQNameImpl.getInstance(Calendar.class);
	private static final JavaQName BIG_DECIMAL_TYPE = JavaQNameImpl.getInstance(BigDecimal.class);
	private static final JavaQName BIG_INTEGER_TYPE = JavaQNameImpl.getInstance(BigInteger.class);
	private static final Map xsTypesByJavaTypes = new HashMap();
	private static final JavaQName[] SIMPLE_TYPES;
	static {
		xsTypesByJavaTypes.put(STRING_TYPE, new QName(BeanSchemaWriter.XML_SCHEMA_URI, "string"));
		final QName byteQName = new QName(BeanSchemaWriter.XML_SCHEMA_URI, "byte");
		xsTypesByJavaTypes.put(BYTE_TYPE, byteQName);
		xsTypesByJavaTypes.put(JavaQNameImpl.BYTE, byteQName);
		xsTypesByJavaTypes.put(BYTE_ARRAY_TYPE, new QName(BeanSchemaWriter.XML_SCHEMA_URI, "base64"));
		final QName shortQName = new QName(BeanSchemaWriter.XML_SCHEMA_URI, "short");
		xsTypesByJavaTypes.put(SHORT_TYPE, shortQName);
		xsTypesByJavaTypes.put(JavaQNameImpl.SHORT, shortQName);
		final QName intQName = new QName(BeanSchemaWriter.XML_SCHEMA_URI, "int");
		xsTypesByJavaTypes.put(INTEGER_TYPE, intQName);
		xsTypesByJavaTypes.put(JavaQNameImpl.INT, intQName);
		final QName longQName = new QName(BeanSchemaWriter.XML_SCHEMA_URI, "long");
		xsTypesByJavaTypes.put(LONG_TYPE, longQName);
		xsTypesByJavaTypes.put(JavaQNameImpl.LONG, longQName);
		final QName floatQName = new QName(BeanSchemaWriter.XML_SCHEMA_URI, "float");
		xsTypesByJavaTypes.put(FLOAT_TYPE, floatQName);
		xsTypesByJavaTypes.put(JavaQNameImpl.FLOAT, floatQName);
		final QName doubleQName = new QName(BeanSchemaWriter.XML_SCHEMA_URI, "double");
		xsTypesByJavaTypes.put(DOUBLE_TYPE, doubleQName);
		xsTypesByJavaTypes.put(JavaQNameImpl.DOUBLE, doubleQName);
		final QName booleanQName = new QName(BeanSchemaWriter.XML_SCHEMA_URI, "boolean");
		xsTypesByJavaTypes.put(BOOLEAN_TYPE, booleanQName);
		xsTypesByJavaTypes.put(JavaQNameImpl.BOOLEAN, booleanQName);
		xsTypesByJavaTypes.put(CALENDAR_TYPE, new QName(BeanSchemaWriter.XML_SCHEMA_URI, "dateTime"));
		xsTypesByJavaTypes.put(BIG_DECIMAL_TYPE, new QName(BeanSchemaWriter.XML_SCHEMA_URI, "decimal"));
		xsTypesByJavaTypes.put(BIG_INTEGER_TYPE, new QName(BeanSchemaWriter.XML_SCHEMA_URI, "integer"));
		SIMPLE_TYPES = (JavaQName[]) xsTypesByJavaTypes.keySet().toArray(new JavaQName[xsTypesByJavaTypes.size()]);
	}
	
	private final JavaSourceResolver resolver;
	private String targetNamespace;
	private Map beans = new HashMap();

	/** Creates a new instance.
	 */
	public BeanInfoFactory(JavaSourceResolver pClassLoader, JavaQName pQName)
			throws BeanReaderException {
		resolver = pClassLoader;
		JavaSource js = pClassLoader.getJavaSource(pQName);
		if (js == null) {
			throw new BeanReaderException("Failed to resolve class " + pQName);
		}
		createBeanInfo(js, false);
	}

	/** Verifies, that the bean class is public, not
	 * abstract, and is no interface.
	 */
	private void verifyAvailable(JavaSource pJs, boolean pIsSuperClass)
			throws BeanReaderException {
		if (!pIsSuperClass  &&  !pJs.getProtection().equals(JavaSource.PUBLIC)) {
			throw new BeanReaderException("The class " + pJs.getType()
					+ " is not public.");
		}
		if (!pIsSuperClass  &&  pJs.isAbstract()) {
			throw new BeanReaderException("The class " + pJs.getType()
					+ " is abstract.");
		}
		if (pJs.isInterface()) {
			throw new BeanReaderException("The class " + pJs.getType()
					+ " is an interface.");
		}
	}

	/** Verifies, that the bean class has a default constructor.
	 */
	private void verifyDefaultConstructor(JavaSource pJs) throws BeanReaderException {
		JavaConstructor[] constructors = pJs.getConstructors();
		if (constructors.length == 0) {
			return;
		}
		for (int i = 0;  i < constructors.length;  i++) {
			JavaConstructor jc = constructors[i];
			if (jc.getProtection().equals(JavaSource.PUBLIC)  &&
				jc.getParams().length == 0) {
				return;
			}
		}
		throw new BeanReaderException("The class " + pJs.getType()
				+ " doesn't have a default constructor.");
	}

	private JavaQName getSuperClass(JavaSource pJs) throws BeanReaderException {
		JavaQName[] superClasses = pJs.getExtends();
		switch (superClasses.length) {
			case 0:
				return null;
			case 1:
				JavaQName superClass = superClasses[0];
				if (OBJECT_TYPE.equals(superClass)) {
					return null;
				}
				JavaSource js = resolver.getJavaSource(superClass);
				if (js == null) {
					throw new BeanReaderException("Failed to resolve class: " + superClass);
				}
				createBeanInfo(js, true);
				return superClass;
			default:
				throw new BeanReaderException("The class " + pJs.getQName() + " is extending multiple classes.");
		}
	}

	private BeanProperty[] getBeanProperties(JavaSource pJs,
			BeanProperty[] pSuperTypeProperties)
			throws BeanReaderException {
		final Set propertyNames = new HashSet();
		JavaMethod[] methods = pJs.getMethods();
		final List result = new ArrayList();
		for (int i = 0;  i < methods.length;  i++) {
			JavaMethod m = methods[i];
			if (!JavaSource.PUBLIC.equals(m.getProtection())
				||  m.isStatic()
				||  !m.getName().startsWith("set")) {
				continue;
			}
			String name = m.getName().substring(3);
			Parameter[] parameters = m.getParams();
			if (parameters.length != 1) {
				continue;
			}
			JavaQName type = parameters[0].getType();
			JavaQName[] zeroParameters = new JavaQName[0];
			JavaMethod getter = pJs.getMethod("get" + name, zeroParameters);
			if (getter == null) {
				if (JavaQNameImpl.BOOLEAN.equals(type)) {
					getter = pJs.getMethod("is" + name, zeroParameters);
				}
				if (getter == null) {
					continue;
				}
			}
			if (!type.equals(getter.getType())  ||
				!JavaSource.PUBLIC.equals(getter.getProtection())  ||
				getter.isStatic()) {
				continue;
			}
			BeanProperty p = createBeanProperty(propertyNames, name, type);
			boolean isSuperTypeProperty = false;
			for (int j = 0;  j < pSuperTypeProperties.length;  j++) {
				if (pSuperTypeProperties[j].equals(p)) {
					isSuperTypeProperty = true;
					break;
				}
			}
			if (!isSuperTypeProperty) {
				result.add(p);
			}
		}
		return (BeanProperty[]) result.toArray(new BeanProperty[result.size()]);
	}

	private JavaQName getInstanceType(JavaQName pType) {
		if (pType.isArray()  &&  !BYTE_ARRAY_TYPE.equals(pType)) {
			return pType.getInstanceClass();
		} else {
			return pType;
		}
	}

	private QName getXsType(JavaQName pType) throws BeanReaderException {
		JavaQName instanceType = getInstanceType(pType);
		if (instanceType.isArray()) {
			if (instanceType.isArray()  &&  !BYTE_ARRAY_TYPE.equals(instanceType)) {
				throw new BeanReaderException("Multidimensional arrays are unsupported: " + pType);
			}
		}
		return (QName) xsTypesByJavaTypes.get(instanceType);
	}

	private String getXsTypeName(Set pPropertyNames, JavaQName pType)
			throws BeanReaderException {
		JavaQName instanceType = getInstanceType(pType);
		JavaSource js = resolver.getJavaSource(instanceType);
		if (js == null) {
			throw new BeanReaderException("Failed to resolve property type " + instanceType);
		}
		createBeanInfo(js, false);
		final String propertyPrefix = asXmlIdentifier(pType.getInnerClassName());
		String propertyName = propertyPrefix;
		for (int i = 0;  pPropertyNames.contains(propertyName);  i++) {
			propertyName = propertyPrefix + i;
		}
		pPropertyNames.add(propertyName);
		return propertyName;
	}

	private class BeanPropertyImpl implements BeanProperty {
		private final JavaQName type;
		private final QName xsType;
		private final String xsTypeName, name;
		BeanPropertyImpl(JavaQName pType, QName pXsType,
				String pXsTypeName, String pName) {
			type = pType;
			xsType = pXsType;
			xsTypeName = pXsTypeName;
			name = pName;
		}
		public JavaQName getType() { return type; }
		public QName getXsType() {
			return xsType == null ? new QName(getTargetNamespace(), xsTypeName) : xsType;
		}
		public String getName() {
			return name;
		}
		public boolean equals(Object pOther) {
			if (pOther == null  ||  !(pOther instanceof BeanPropertyImpl)) {
				return false;
			}
			BeanPropertyImpl other = (BeanPropertyImpl) pOther;
			return type.equals(other.type)  &&  name.equals(other.name);
		}
		public int hashCode() {
			return (type.hashCode() << 7) + name.hashCode();
		}
	}

	private BeanProperty createBeanProperty(Set pPropertyNames,
			final String pName, final JavaQName pType)
			throws BeanReaderException {
		final QName xsType = getXsType(pType);
		final String xsTypeName;
		if (xsType == null) {
			xsTypeName = getXsTypeName(pPropertyNames, pType);
		} else {
			xsTypeName = null;
		}
		return new BeanPropertyImpl(pType, xsType, xsTypeName, pName);
	}

	private String asBeanKey(JavaQName pQName) {
		return pQName.toString().replace('$', '.');
	}

	private class BeanInfoImpl implements BeanInfo {
		private BeanProperty[] properties;
		private final String elementName;
		private final JavaQName type, superType;
		BeanInfoImpl(String pElementName, JavaQName pType,
				JavaQName pSuperType) {
			elementName = pElementName;
			type = pType;
			superType = pSuperType;
		}
		public String getTargetNamespace() {
			return BeanInfoFactory.this.getTargetNamespace();
		}
		void setBeanProperties(BeanProperty[] pProperties) { properties = pProperties; }
		public BeanProperty[] getBeanProperties() { return properties; }
		public String getElementName() { return elementName; }
		public JavaQName getType() { return type; }
		public JavaQName getSuperType() { return superType; }
	}

	private void createBeanInfo(JavaSource pJs, boolean pIsSuperClass)
			throws BeanReaderException {
		final JavaQName type = pJs.getQName();
		final String key = asBeanKey(type);
		if (beans.containsKey(key)) {
			return;
		}
		verifyAvailable(pJs, pIsSuperClass);
		if (!pIsSuperClass) {
			verifyDefaultConstructor(pJs);
		}

		final JavaQName superType = getSuperClass(pJs);
		final BeanProperty[] superTypeProperties;
		if (superType == null) {
			superTypeProperties = new BeanProperty[0];
		} else {
			superTypeProperties = getBeanInfo(superType).getBeanProperties();
		}
		final String elementName = asXmlIdentifier(type.getInnerClassName());
		BeanInfoImpl beanInfo = new BeanInfoImpl(elementName, type, superType);
		beans.put(key, beanInfo);
		final BeanProperty[] properties = getBeanProperties(pJs, superTypeProperties);
		beanInfo.setBeanProperties(properties);
	}

	private String asXmlIdentifier(String pJavaIdentifier) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0;  i < pJavaIdentifier.length();  i++) {
			char c = pJavaIdentifier.charAt(i);
			if (i == 0) {
				c = Character.toLowerCase(c);
				if (c < 'a'  ||  c > 'z') {
					c = '_';
				}
			} else {
				if ((c < 'a'  ||  c > 'z')  &&  (c < 'A'  ||  c > 'Z')  &&
					(c < '0'  ||  c > '9')) {
					c = '_';
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/** Returns, whether the given Java type can be mapped to
	 * an atomic type.
	 */
	public boolean isSimpleType(JavaQName pType) {
		for (int i = 0;  i < SIMPLE_TYPES.length;  i++) {
			if (SIMPLE_TYPES[i].equals(pType)) {
				return true;
			}
		}
		return false;
	}

	/** Returns the bean info for the given type.
	 */
	public BeanInfo getBeanInfo(JavaQName pType) {
		BeanInfo result = (BeanInfo) beans.get(asBeanKey(pType));
		if (result == null) {
			throw new IllegalStateException("Unknown bean type: " + pType);
		}
		return result;
	}

	/** Returns the factories target namespace.
	 */
	public String getTargetNamespace() {
		return targetNamespace;
	}

	/** Sets the factories target namespace.
	 */
	public void setTargetNamespace(String pTargetNamespace) {
		targetNamespace = pTargetNamespace;
	}
}
