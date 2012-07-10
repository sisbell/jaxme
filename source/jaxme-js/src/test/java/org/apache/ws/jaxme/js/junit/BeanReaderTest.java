package org.apache.ws.jaxme.js.junit;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.Util;
import org.apache.ws.jaxme.js.apps.JavaSourceResolver;
import org.apache.ws.jaxme.js.beanreader.BeanInfo;
import org.apache.ws.jaxme.js.beanreader.BeanInfoFactory;
import org.apache.ws.jaxme.js.beanreader.BeanProperty;
import org.apache.ws.jaxme.js.beanreader.BeanReaderException;
import org.apache.ws.jaxme.js.beanreader.BeanSchemaWriter;
import org.apache.ws.jaxme.js.util.JavaParser;

import antlr.RecognitionException;
import antlr.TokenStreamException;


/** Test case for the {@link BeanSchemaWriter}.
 */
public class BeanReaderTest extends TestCase {
	private static final String JAVA_SOURCE_1 =
		"package foo.bar;\n" +
		"\n" +
		"import java.math.BigDecimal;\n" +
		"import java.math.BigInteger;\n" +
		"import java.util.Calendar;\n" +
		"\n" +
		"import foo.bar.PrimitiveSimpleTypeBean.SimpleTypeBean;\n" +
		"\n" +
		"public class MyBean {\n" +
		"	public static class SimpleTypeArraysBean extends PrimitiveSimpleTypeArraysBean {\n" +
		"		private Byte[] objectByteArray;\n" +
		"		private Short[] objectShortArray;\n" +
		"		private Integer[] objectIntArray;\n" +
		"		private Long[] objectLongArray;\n" +
		"		private Float[] objectFloatArray;\n" +
		"		private Double[] objectDoubleArray;\n" +
		"		private Boolean[] objectBooleanArray;\n" +
		"		private Calendar[] calendarArray;\n" +
		"		private BigInteger[] bigIntegerArray;\n" +
		"		private BigDecimal[] bigDecimalArray;\n" +
		"		private byte[][] base64Array;\n" +
		"\n" +
		"		public Byte[] getObjectByteArray() { return objectByteArray; }\n" +
		"		public Short[] getObjectShortArray() { return objectShortArray; }\n" +
		"		public Integer[] getObjectIntArray() { return objectIntArray; }\n" +
		"		public Long[] getObjectLongArray() { return objectLongArray; }\n" +
		"		public Float[] getObjectFloatArray() { return objectFloatArray; }\n" +
		"		public Double[] getObjectDoubleArray() { return objectDoubleArray; }\n" +
		"		public Boolean[] getObjectBooleanArray() { return objectBooleanArray; }\n" +
		"		public Calendar[] getCalendarArray() { return calendarArray; }\n" +
		"		public BigInteger[] getBigIntegerArray() { return bigIntegerArray; }\n" +
		"		public BigDecimal[] getBigDecimalArray() { return bigDecimalArray; }\n" +
		"		public byte[][] getBase64Array() { return base64Array; }\n" +
		"\n" +
		"		public void setObjectByteArray(Byte[] pValue) { objectByteArray = pValue; }\n" +
		"		public void setObjectShortArray(Short[] pValue) { objectShortArray = pValue; }\n" +
		"		public void setObjectIntArray(Integer[] pValue) { objectIntArray = pValue; }\n" +
		"		public void setObjectLongArray(Long[] pValue) { objectLongArray = pValue; }\n" +
		"		public void setObjectFloatArray(Float[] pValue) { objectFloatArray = pValue; }\n" +
		"		public void setObjectDoubleArray(Double[] pValue) { objectDoubleArray = pValue; }\n" +
		"		public void setObjectBooleanArray(Boolean[] pValue) { objectBooleanArray = pValue; }\n" +
		"		public void setCalendarArray(Calendar[] pValue) { calendarArray = pValue; }\n" +
		"		public void setBigDecimalArray(BigDecimal[] pValue) { bigDecimalArray = pValue; }\n" +
		"		public void setBigIntegerArray(BigInteger[] pValue) { bigIntegerArray = pValue; }\n" +
		"		public void setBase64Array(byte[][] pValue) { base64Array = pValue; }\n" +
		"	}\n" +
		"\n" +
		"	private SimpleTypeBean simpleTypeBean;\n" +
		"	private SimpleTypeArraysBean simpleTypeArraysBean;\n" +
		"	private SimpleTypeBean[] simpleTypeArray;\n" +
		"	private SimpleTypeArraysBean[] simpleTypeArraysArray;\n" +
		"   private MyBean mySubBean;\n" +
		"\n" +
		"	public SimpleTypeBean getSimpleTypeBean() { return simpleTypeBean; }\n" +
		"	public void setSimpleTypeBean(SimpleTypeBean pBean) { simpleTypeBean = pBean; }\n" +
		"	public SimpleTypeArraysBean getSimpleTypeArraysBean() { return simpleTypeArraysBean; }\n" +
		"	public void setSimpleTypeArraysBean(SimpleTypeArraysBean pBean) { simpleTypeArraysBean = pBean; }\n" +
		"	public SimpleTypeBean[] getSimpleTypeArray() { return simpleTypeArray; }\n" +
		"	public void setSimpleTypeArray(SimpleTypeBean[] pArray) { simpleTypeArray = pArray; }\n" +
		"	public SimpleTypeArraysBean[] getSimpleTypeArraysArray() { return simpleTypeArraysArray; }\n" +
		"	public void setSimpleTypeArraysArray(SimpleTypeArraysBean[] pArray) { simpleTypeArraysArray = pArray; }\n" +
		"   public MyBean getMySubBean() { return mySubBean; }\n" +
		"   public void setMySubBean(MyBean pBean) { mySubBean = pBean; }\n" +
		"}\n";

	private static final String JAVA_SOURCE_2 =
		"package foo.bar;\n" +
		"\n" +
		"public class PrimitiveSimpleTypeArraysBean {\n" +
		"	private short[] primitiveShortField;\n" +
		"	private int[] primitiveIntField;\n" +
		"	private long[] primitiveLongField;\n" +
		"	private float[] primitiveFloatField;\n" +
		"	private double[] primitiveDoubleField;\n" +
		"	private boolean[] primitiveBooleanField;\n" +
		"	\n" +
		"	public short[] getPrimitiveShortField() { return primitiveShortField; }\n" +
		"	public int[] getPrimitiveIntField() { return primitiveIntField; }\n" +
		"	public long[] getPrimitiveLongField() { return primitiveLongField; }\n" +
		"	public float[] getPrimitiveFloatField() { return primitiveFloatField; }\n" +
		"	public double[] getPrimitiveDoubleField() { return primitiveDoubleField; }\n" +
		"	public boolean[] isPrimitiveBooleanField() { return primitiveBooleanField; }\n" +
		"	\n" +
		"	public void setPrimitiveShortField(short[] pValue) { primitiveShortField = pValue; }\n" +
		"	public void setPrimitiveIntField(int[] pValue) { primitiveIntField = pValue; }\n" +
		"	public void setPrimitiveLongField(long[] pValue) { primitiveLongField = pValue; }\n" +
		"	public void setPrimitiveFloatField(float[] pValue) { primitiveFloatField = pValue; }\n" +
		"	public void setPrimitiveDoubleField(double[] pValue) { primitiveDoubleField = pValue; }\n" +
		"	public void setPrimitiveBooleanField(boolean[] pValue) { primitiveBooleanField = pValue; }\n" +
		"}\n";

	private static final String JAVA_SOURCE_3 =
		"package foo.bar;\n" +
		"\n" +
		"import java.math.BigDecimal;\n" +
		"import java.math.BigInteger;\n" +
		"import java.util.Calendar;\n" +
		"\n" +
		"public class PrimitiveSimpleTypeBean {\n" +
		"	public static class SimpleTypeBean extends PrimitiveSimpleTypeBean {\n" +
		"		private Byte objectByteField;\n" +
		"		private Short objectShortField;\n" +
		"		private Integer objectIntField;\n" +
		"		private Long objectLongField;\n" +
		"		private Float objectFloatField;\n" +
		"		private Double objectDoubleField;\n" +
		"		private Boolean objectBooleanField;\n" +
		"		private Calendar calendarField;\n" +
		"		private BigDecimal bigDecimalField;\n" +
		"		private BigInteger bigIntegerField;\n" +
		"		private byte[] base64Field;\n" +
		"		\n" +
		"		public Byte getObjectByteField() { return objectByteField; }\n" +
		"		public Short getObjectShortField() { return objectShortField; }\n" +
		"		public Integer getObjectIntField() { return objectIntField; }\n" +
		"		public Long getObjectLongField() { return objectLongField; }\n" +
		"		public Float getObjectFloatField() { return objectFloatField; }\n" +
		"		public Double getObjectDoubleField() { return objectDoubleField; }\n" +
		"		public Boolean getObjectBooleanField() { return objectBooleanField; }\n" +
		"		public Calendar getCalendarField() { return calendarField; }\n" +
		"		public BigDecimal getBigDecimalField() { return bigDecimalField; }\n" +
		"		public BigInteger getBigIntegerField() { return bigIntegerField; }\n" +
		"		public byte[] getBase64Field() { return base64Field; }\n" +
		"\n" +
		"		public void setObjectByteField(Byte pValue) { objectByteField = pValue; }\n" +
		"		public void setObjectShortField(Short pValue) { objectShortField = pValue; }\n" +
		"		public void setObjectIntField(Integer pValue) { objectIntField = pValue; }\n" +
		"		public void setObjectLongField(Long pValue) { objectLongField = pValue; }\n" +
		"		public void setObjectFloatField(Float pValue) { objectFloatField = pValue; }\n" +
		"		public void setObjectDoubleField(Double pValue) { objectDoubleField = pValue; }\n" +
		"		public void setObjectBooleanField(Boolean pValue) { objectBooleanField = pValue; }\n" +
		"		public void setCalendarField(Calendar pValue) { calendarField = pValue; }\n" +
		"		public void setBigDecimalField(BigDecimal pValue) { bigDecimalField = pValue; }\n" +
		"		public void setBigIntegerField(BigInteger pValue) { bigIntegerField = pValue; }\n" +
		"		public void setBase64Field(byte[] pValue) { base64Field = pValue; }\n" +
		"\n" +
		"		// Example of overwriting a parent objects property\n" +
		"		public void setPrimitiveIntField(int pValue) { super.setPrimitiveIntField(pValue); }\n" +
		"		public int getPrimitiveIntField() { return super.getPrimitiveIntField(); }\n" +
		"	}\n" +
		"\n" +
		"	private byte primitiveByteField;\n" +
		"	private short primitiveShortField;\n" +
		"	private int primitiveIntField;\n" +
		"	private long primitiveLongField;\n" +
		"	private float primitiveFloatField;\n" +
		"	private double primitiveDoubleField;\n" +
		"	private boolean primitiveBooleanField;\n" +
		"	\n" +
		"	public byte getPrimitiveByteField() { return primitiveByteField; }\n" +
		"	public short getPrimitiveShortField() { return primitiveShortField; }\n" +
		"	public int getPrimitiveIntField() { return primitiveIntField; }\n" +
		"	public long getPrimitiveLongField() { return primitiveLongField; }\n" +
		"	public float getPrimitiveFloatField() { return primitiveFloatField; }\n" +
		"	public double getPrimitiveDoubleField() { return primitiveDoubleField; }\n" +
		"	public boolean isPrimitiveBooleanField() { return primitiveBooleanField; }\n" +
		"	\n" +
		"	public void setPrimitiveByteField(byte pValue) { primitiveByteField = pValue; }\n" +
		"	public void setPrimitiveShortField(short pValue) { primitiveShortField = pValue; }\n" +
		"	public void setPrimitiveIntField(int pValue) { primitiveIntField = pValue; }\n" +
		"	public void setPrimitiveLongField(long pValue) { primitiveLongField = pValue; }\n" +
		"	public void setPrimitiveFloatField(float pValue) { primitiveFloatField = pValue; }\n" +
		"	public void setPrimitiveDoubleField(double pValue) { primitiveDoubleField = pValue; }\n" +
		"	public void setPrimitiveBooleanField(boolean pValue) { primitiveBooleanField = pValue; }\n" +
		"}\n";

	final String[] sources = new String[]{
			JAVA_SOURCE_1, JAVA_SOURCE_2, JAVA_SOURCE_3
	};

	private String asKey(JavaQName pQName) {
		return pQName.toString().replace('$', '.');
	}

	private void register(Map pSourcesByName, JavaSource pJs) {
		String qName = asKey(pJs.getQName());
		pSourcesByName.put(qName, pJs);
		JavaInnerClass[] innerClasses = pJs.getInnerClasses();
		for (int i = 0;  i < innerClasses.length;  i++) {
			register(pSourcesByName, innerClasses[i]);
		}
	}

	private JavaSourceResolver getResolver()
			throws TokenStreamException, RecognitionException {
		final Map sourcesByName = new HashMap();
		final JavaSourceFactory jsf = new JavaSourceFactory();
		JavaParser parser = new JavaParser(jsf);
		for (int i = 0;  i < sources.length;  i++) {
			List list = parser.parse(new StringReader(sources[i]));
			for (int j = 0;  j < list.size();  j++) {
				JavaSource js = (JavaSource) list.get(i);
				register(sourcesByName, js);
			}
		}
		return new JavaSourceResolver(){
			public JavaSource getJavaSource(JavaQName pQName) {
				String key = asKey(pQName);
				JavaSource js = (JavaSource) sourcesByName.get(key);
				if (js != null) {
					return js;
				}
				Class c;
				try {
					c = Class.forName(key);
				} catch (ClassNotFoundException e) {
					return null;
				}
				return Util.newJavaSource(jsf, c);
			}
		};
	}

	/** Tests creating a bean info factory.
	 */
	public void testBeanInfoFactory() throws Exception {
		BeanInfoFactory bif = newBeanInfoFactory();
		checkRootBean(bif);
		checkPrimitiveSimpleTypeBean(bif);
		checkSimpleTypeBean(bif);
	}

	private void checkSimpleTypeBean(BeanInfoFactory pFactory) {
		JavaQName primitiveSimpleTypeBeanType = JavaQNameImpl.getInstance("foo.bar.PrimitiveSimpleTypeBean");
		JavaQName simpleTypeBeanType = JavaQNameImpl.getInstance("foo.bar.PrimitiveSimpleTypeBean.SimpleTypeBean");
		BeanInfo bi = pFactory.getBeanInfo(simpleTypeBeanType);
		assertEquals("simpleTypeBean", bi.getElementName());
		assertEquals(primitiveSimpleTypeBeanType, bi.getSuperType());
		assertNull(bi.getTargetNamespace());
		assertEquals(asKey(simpleTypeBeanType), asKey(bi.getType()));
		BeanProperty[] props = bi.getBeanProperties();
		assertEquals(11, props.length);
	}

	private void checkPrimitiveSimpleTypeBean(BeanInfoFactory pFactory) {
		JavaQName primitiveSimpleTypeBeanType = JavaQNameImpl.getInstance("foo.bar.PrimitiveSimpleTypeBean");
		BeanInfo bi = pFactory.getBeanInfo(primitiveSimpleTypeBeanType);
		assertEquals("primitiveSimpleTypeBean", bi.getElementName());
		assertNull(bi.getSuperType());
		assertNull(bi.getTargetNamespace());
		assertEquals(primitiveSimpleTypeBeanType, bi.getType());
		BeanProperty[] props = bi.getBeanProperties();
		assertEquals(7, props.length);
	}

	private void checkRootBean(BeanInfoFactory pFactory) {
		JavaQName myBeanType = JavaQNameImpl.getInstance("foo.bar.MyBean");
		BeanInfo bi = pFactory.getBeanInfo(myBeanType);
		assertEquals("myBean", bi.getElementName());
		assertNull(bi.getSuperType());
		assertNull(bi.getTargetNamespace());
		assertEquals(myBeanType, bi.getType());
		BeanProperty[] myBeanProperties = bi.getBeanProperties();
		assertEquals(5, myBeanProperties.length);
	}

	private BeanInfoFactory newBeanInfoFactory() throws BeanReaderException, TokenStreamException, RecognitionException {
		return new BeanInfoFactory(getResolver(), getRootBeanType());
	}

	private JavaQName getRootBeanType() {
		return JavaQNameImpl.getInstance("foo.bar.MyBean");
	}

	/** Tests creating an XML schema.
	 */
	public void testCreateSchema() throws Exception {
		StringWriter sw = new StringWriter();
		TransformerHandler t = ((SAXTransformerFactory) TransformerFactory.newInstance()).newTransformerHandler();
		t.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
		t.setResult(new StreamResult(sw));
		BeanInfoFactory beanInfoFactory = newBeanInfoFactory();
		BeanInfo beanInfo = beanInfoFactory.getBeanInfo(getRootBeanType());
		new BeanSchemaWriter(beanInfoFactory).write(beanInfo, t);
	}
}
