package org.apache.ws.jaxme.js.beanreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/** The {@link BeanSchemaWriter} is an object, which takes
 * as input an instance of {@link JavaSource}, and creates
 * a corresponding instance of XML Schema.
 */
public class BeanSchemaWriter {
	private static final JavaQName OBJECT_TYPE = JavaQNameImpl.getInstance(Object.class);
	private static final JavaQName BYTE_ARRAY_TYPE = JavaQNameImpl.getArray(JavaQNameImpl.BYTE);
	static final String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema-instance";

	private final BeanInfoFactory factory;
	private boolean isFiringStartDocument = true;
	private boolean isFiringXmlnsAttributes = true;
	private final List prefixes = new ArrayList();
	private final Map types = new HashMap();
	private final Map remainingComplexTypes = new HashMap();

	/** Creates a new instance.
	 */
	public BeanSchemaWriter(BeanInfoFactory pFactory) {
		factory = pFactory;
	}

	private String getAttrPrefix(String pURI, ContentHandler pHandler,
			AttributesImpl pAttrs) throws SAXException {
		if (pURI == null  ||  pURI.length() == 0) {
			return "";
		} else {
			return getPrefix(pURI, pHandler, pAttrs);
		}
	}

	private String getPrefix(String pURI, ContentHandler pHandler,
			AttributesImpl pAttrs) throws SAXException {
		if (pURI == null) {
			pURI = "";
		}
		for (int i = prefixes.size()-2;  i >= 0;  i -= 2) {
			if (prefixes.get(i+1).equals(pURI)) {
				return (String) prefixes.get(i);
			}
		}
		final String newPrefix;
		if (XML_SCHEMA_URI.equals(pURI)) {
			newPrefix = "xs";
		} else {
			String testPrefix = "p";
			int i = 0;
			while (prefixes.contains(testPrefix)) {
				testPrefix = "p" + i++;
			}
			newPrefix = testPrefix;
		}
		prefixes.add(newPrefix);
		prefixes.add(pURI);
		pHandler.startPrefixMapping(newPrefix, pURI);
		if (isFiringXmlnsAttributes()) {
			pAttrs.addAttribute(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, newPrefix,
					XMLConstants.XMLNS_ATTRIBUTE + ":" + newPrefix,
					"CDATA", pURI);
		}
		return newPrefix;
	}

	private int getPrefixContext() {
		return prefixes.size();
	}

	private void setPrefixContext(int pContext, ContentHandler pHandler)
			throws SAXException {
		while (prefixes.size() > pContext) {
			prefixes.remove(prefixes.size()-1);
			String prefix = (String) prefixes.remove(prefixes.size()-1);
			pHandler.endPrefixMapping(prefix);
		}
	}

	/** Returns, whether the bean schema writer is firing the
	 * {@link ContentHandler#startDocument()}, and
	 * {@link ContentHandler#endDocument()} events. By default,
	 * it does.
	 */
	public boolean isFiringStartDocument() {
		return isFiringStartDocument;
	}

	/** Sets, whether the bean schema writer is firing the
	 * {@link ContentHandler#startDocument()}, and
	 * {@link ContentHandler#endDocument()} events. By default,
	 * it does.
	 */
	public void setFiringStartDocument(boolean pIsFiringStartDocument) {
		isFiringStartDocument = pIsFiringStartDocument;
	}

	/** Returns, whether XML namespace declarations are fired not
	 * only as {@link ContentHandler#startPrefixMapping(String, String)}
	 * as events, but as attributes in
	 * {@link ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)}
	 * events as well. Defaults to true.
	 */
	public boolean isFiringXmlnsAttributes() {
		return isFiringXmlnsAttributes;
	}

	/** Sets, whether XML namespace declarations are fired not
	 * only as {@link ContentHandler#startPrefixMapping(String, String)}
	 * as events, but as attributes in
	 * {@link ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)}
	 * events as well. Defaults to true.
	 */
	public void setFiringXmlnsAttributes(boolean pFiringXmlnsAttributes) {
		isFiringXmlnsAttributes = pFiringXmlnsAttributes;
	}

	private void writeComplexType(ContentHandler pHandler, BeanInfo pBeanInfo,
			String pXsType) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		int ctx = getPrefixContext();
		addAttr(attrs, pHandler, "name", pXsType);
		startXsElement(pHandler, "complexType", attrs);
		JavaQName superClass = pBeanInfo.getSuperType();
		if (superClass == null  ||  OBJECT_TYPE.equals(superClass)) {
			writeComplexTypeContents(pHandler, pBeanInfo);
		} else {
			BeanInfo beanInfo = factory.getBeanInfo(superClass);
			QName qName = getXsType(beanInfo);
			startXsElement(pHandler, "complexContent", new AttributesImpl());
			int ctx2 = getPrefixContext();
			AttributesImpl attrs2 = new AttributesImpl();
			addAttr(attrs2, pHandler, "base", getQName(qName, pHandler, attrs2));
			startXsElement(pHandler, "extension", attrs2);
			writeComplexTypeContents(pHandler, pBeanInfo);
			endXsElement(pHandler, "extension");
			setPrefixContext(ctx2, pHandler);
			endXsElement(pHandler, "complexContent");
		}
		endXsElement(pHandler, "complexType");
		setPrefixContext(ctx, pHandler);
	}

	private void writeComplexTypeContents(ContentHandler pHandler, BeanInfo pBeanInfo) throws SAXException {
		BeanProperty[] properties = pBeanInfo.getBeanProperties();
		List elementProperties = new ArrayList();
		List attributeProperties = new ArrayList();
		for (int i = 0;  i < properties.length;  i++) {
			BeanProperty p = properties[i];
			if (isAttributeProperty(p)) {
				attributeProperties.add(p);
			} else {
				elementProperties.add(p);
			}
		}
		if (elementProperties.size() > 0) {
			startXsElement(pHandler, "sequence", new AttributesImpl());
			for (Iterator iter = elementProperties.iterator();  iter.hasNext();  ) {
				writeParticle(pHandler, (BeanProperty) iter.next());
			}
			endXsElement(pHandler, "sequence");
		}
		for (Iterator iter = attributeProperties.iterator();  iter.hasNext();  ) {
			writeAttribute(pHandler, (BeanProperty) iter.next());
		}
	}

	private void writeParticle(ContentHandler pHandler, BeanProperty pProperty)
			throws SAXException {
		JavaQName type = pProperty.getType();
		final String minOccurs = "0", maxOccurs;
		if (type.isArray()) {
			maxOccurs = "unbounded";
			type = type.getInstanceClass();
			if (type.isArray()  &&  !BYTE_ARRAY_TYPE.equals(type)) {
				throw new IllegalStateException("Unable to handle multidimensional arrays.");
			}
		} else {
			maxOccurs = null;
		}

		int ctx = getPrefixContext();
		AttributesImpl attrs = new AttributesImpl();
		final QName xsType;
		if (factory.isSimpleType(type)) {
			xsType = pProperty.getXsType();
		} else {
			BeanInfo beanInfo = factory.getBeanInfo(type);
			xsType = getXsType(beanInfo);
		}
		addAttr(attrs, pHandler, "type", getQName(xsType, pHandler, attrs));
		addAttr(attrs, pHandler, "name", pProperty.getName());
		addAttr(attrs, pHandler, "minOccurs", minOccurs);
		if (maxOccurs != null) {
			addAttr(attrs, pHandler, "maxOccurs", maxOccurs);
		}
		startXsElement(pHandler, "element", attrs);
		endXsElement(pHandler, "element");
		setPrefixContext(ctx, pHandler);
	}

	private void writeAttribute(ContentHandler pHandler, BeanProperty pProperty)
			throws SAXException {
		int ctx = getPrefixContext();
		AttributesImpl attrs = new AttributesImpl();
		addAttr(attrs, pHandler, "name", pProperty.getName());
		if (pProperty.getType().isPrimitive()) {
			addAttr(attrs, pHandler, "use", "required");
		}
		addAttr(attrs, pHandler, "type", getQName(pProperty.getXsType(), pHandler, attrs));
		startXsElement(pHandler, "attribute", attrs);
		endXsElement(pHandler, "attribute");
		setPrefixContext(ctx, pHandler);
	}

	private boolean isAttributeProperty(BeanProperty pProperty) {
		if (factory.isSimpleType(pProperty.getType())) {
			return true;
		}
		if (pProperty.getType().isArray()) {
			return false;
		}
		return false;
	}

	private QName getXsType(BeanInfo pBeanInfo) throws SAXException {
		JavaQName qName = pBeanInfo.getType();
		QName xsType = (QName) types.get(qName);
		if (xsType == null) {
			final String xsTypePrefix = "T_" + pBeanInfo.getElementName();
			String localPart = xsTypePrefix;
			for (int i = 0;  ;  i++) {
				xsType = new QName(pBeanInfo.getTargetNamespace(), localPart);
				if (!types.values().contains(xsType)) {
					break;
				}
				localPart = xsTypePrefix + i;
			}
			types.put(qName, xsType);
			remainingComplexTypes.put(xsType, pBeanInfo);
		}
		return xsType;
	}

	private void addAttr(AttributesImpl pAttrs, ContentHandler pHandler,
			String pURI, String pLocalName, String pValue)
			throws SAXException {
		String prefix = getAttrPrefix(pURI, pHandler, pAttrs);
		String qName = prefix.length() == 0 ?
				pLocalName : prefix + ":" + pLocalName;
		pAttrs.addAttribute(pURI, pLocalName, qName, "CDATA", pValue);
	}

	private void addAttr(AttributesImpl pAttrs, ContentHandler pHandler,
			String pLocalName, String pValue) throws SAXException {
		addAttr(pAttrs, pHandler, "", pLocalName, pValue);
	}

	private String getQName(QName pQName, ContentHandler pHandler, AttributesImpl pAttrs)
			throws SAXException {
		final String prefix = getPrefix(pQName.getNamespaceURI(), pHandler, pAttrs);
		if (prefix == null  ||  prefix.length() == 0) {
			return pQName.getLocalPart();
		} else {
			return prefix + ":" + pQName.getLocalPart();
		}
	}

	protected void writeElement(ContentHandler pHandler, BeanInfo pBeanInfo)
			throws SAXException {
		QName xsType = getXsType(pBeanInfo);
		int ctx = getPrefixContext();
		AttributesImpl attrs = new AttributesImpl();
		addAttr(attrs, pHandler, "name", pBeanInfo.getElementName());
		addAttr(attrs, pHandler, "type", getQName(xsType, pHandler, attrs));
		startXsElement(pHandler, "element", attrs);
		endXsElement(pHandler, "element");
		setPrefixContext(ctx, pHandler);
	}

	/** Writes the XML schema to the given content handler.
	 * @throws SAXException Invoking the target handler failed.
	 */
	public void write(BeanInfo pBeanInfo, ContentHandler pHandler)
			throws SAXException {
		final boolean firingStartDocument = isFiringStartDocument();
		if (firingStartDocument) {
			pHandler.startDocument();
		}
		int ctx = getPrefixContext();
		AttributesImpl attrs = new AttributesImpl();
		final String targetNs = pBeanInfo.getTargetNamespace();
		// Ensure, that a prefix is declared for the target namespace
		getPrefix(targetNs, pHandler, attrs);
		if (targetNs != null  &&  !"".equals(targetNs)) {
			addAttr(attrs, pHandler, "targetNamespace", targetNs);
		}
		startXsElement(pHandler, "schema", attrs);
		writeElement(pHandler, pBeanInfo);
		while (!remainingComplexTypes.isEmpty()) {
			Iterator iter = remainingComplexTypes.entrySet().iterator();
			Map.Entry entry = (Map.Entry) iter.next();
			iter.remove();
			QName xsType = (QName) entry.getKey();
			BeanInfo beanInfo = (BeanInfo) entry.getValue();
			writeComplexType(pHandler, beanInfo, xsType.getLocalPart());
		}
		endXsElement(pHandler, "schema");
		setPrefixContext(ctx, pHandler);
		if (firingStartDocument) {
			pHandler.endDocument();
		}
	}

	private void startXsElement(ContentHandler pHandler, String pLocalName,
			AttributesImpl pAttrs) throws SAXException {
		String prefix = getPrefix(XML_SCHEMA_URI, pHandler, pAttrs);
		pHandler.startElement(XML_SCHEMA_URI,
				pLocalName, prefix + ":" + pLocalName, pAttrs);
	}

	private void endXsElement(ContentHandler pHandler, String pLocalName)
			throws SAXException {
		String prefix = getPrefix(XML_SCHEMA_URI, pHandler, null);
		pHandler.endElement(XML_SCHEMA_URI,
				pLocalName, prefix + ":" + pLocalName);
	}
}
