package org.apache.ws.jaxme.generator.sg.impl.ccsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.jaxme.WildcardAttribute;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.impl.JMSAXDriverController;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/** Implementation of
 * {@link org.apache.ws.jaxme.generator.sg.impl.ccsg.ParticleVisitor}
 * for generating the driver class.
 */
public class DriverGeneratingVisitor extends ParticleVisitorImpl {
	private final JavaSource js;

	/** Creates a new instance, which writes methods into the
	 * given class.
	 */
	public DriverGeneratingVisitor(JavaSource pJs) {
		js = pJs;
	}

	/** This method builds a list of the names, which are being
	 * used in the element. The list is used for generating the
	 * method
	 * {@link org.apache.ws.jaxme.impl.JMSAXDriver#getPreferredPrefix(String)}.
	 * @throws SAXException 
	 */
	protected List getNames(ComplexTypeSG pType) throws SAXException {
		final List names = new ArrayList();
		ParticleVisitor visitor = new ParticleVisitorImpl(){
			private void addAttributes(ComplexTypeSG pType) {
				AttributeSG[] myAttributes = pType.getAttributes();
				for (int i = 0;  i < myAttributes.length;  i++) {
					XsQName qName = myAttributes[i].getName();
					if (qName != null) {
						names.add(qName);
					}
				}
			}
			public void emptyType(ComplexTypeSG pType) {
				addAttributes(pType);
			}
			public void simpleContent(ComplexTypeSG pType) {
				addAttributes(pType);
			}
			public void startComplexContent(ComplexTypeSG pType) {
				addAttributes(pType);
			}
			public void simpleElementParticle(GroupSG pGroup, ParticleSG pParticle) throws SAXException {
				names.add(pParticle.getObjectSG().getName());
			}
			public void complexElementParticle(GroupSG pGroup, ParticleSG pParticle) throws SAXException {
				names.add(pParticle.getObjectSG().getName());
			}
		};
		new ParticleWalker(visitor).walk(pType);
		return names;
	}

	private JavaMethod newGetPreferredPrefixMethod(ComplexTypeSG pType) throws SAXException {
		List names = getNames(pType);
		Map uris = new HashMap();
		for (Iterator iter = names.iterator();  iter.hasNext();  ) {
			XsQName qName = (XsQName) iter.next();
			String prefix = qName.getPrefix();
			if (prefix != null) {
				String uri = qName.getNamespaceURI();
				if (uri == null) uri = "";
				if (!uris.containsKey(uri)) {
					uris.put(uri, prefix);
				}
			}
		}

		JavaMethod jm = js.newJavaMethod("getPreferredPrefix", String.class, JavaSource.PUBLIC);
		DirectAccessible pURI = jm.addParam(String.class, "pURI");
		if (!uris.isEmpty()) {
			jm.addIf(pURI, " == null");
			jm.addLine(pURI, " = \"\";");
			jm.addEndIf();
			boolean first = true;
			for (Iterator iter = uris.entrySet().iterator();  iter.hasNext();  ) {
				Map.Entry entry = (Map.Entry) iter.next();
				String uri = (String) entry.getKey();
				String prefix = (String) entry.getValue();
				jm.addIf(first, pURI, ".equals(", JavaSource.getQuoted(uri), ")");
				jm.addLine("return ", JavaSource.getQuoted(prefix), ";");      
				first = false;
			}
			jm.addEndIf();
		}
		jm.addLine("return null;");
		return jm;
	}

	private JavaMethod newGetAttributesMethod(ComplexTypeSG pType) throws SAXException {
		JavaMethod jm = js.newJavaMethod("getAttributes", AttributesImpl.class, JavaSource.PUBLIC);
		final DirectAccessible pController = jm.addParam(JMSAXDriverController.class, "pController");
		DirectAccessible pObject = jm.addParam(Object.class, "pObject");
		jm.addThrows(SAXException.class);
		final LocalJavaField result = jm.newJavaField(AttributesImpl.class);
		result.addLine("new ", AttributesImpl.class, "()");
		AttributeSG[] myAttributes = pType.getAttributes();
		if (myAttributes.length > 0) {
			JavaQName elementInterface = pType.getClassContext().getXMLInterfaceName();
			LocalJavaField element = jm.newJavaField(elementInterface);
			element.addLine("(", elementInterface, ") ", pObject);
			
			for (int i = 0;  i < myAttributes.length;  i++) {
				final AttributeSG attribute = myAttributes[i];
				if (attribute.isWildcard()) {
				    LocalJavaField anyAttributes = jm.newJavaField(WildcardAttribute[].class);
				    anyAttributes.addLine(element, ".", attribute.getPropertySG().getXMLGetMethodName() + "Array", "()");
				    DirectAccessible index = jm.addForArray(anyAttributes);
				    LocalJavaField wildcardAttribute = jm.newJavaField(WildcardAttribute.class);
				    wildcardAttribute.addLine(anyAttributes, "[", index, "]");
					LocalJavaField qName = jm.newJavaField(QName.class);
					qName.addLine(wildcardAttribute, ".getName()");
					LocalJavaField uri = jm.newJavaField(String.class);
					uri.addLine(qName, ".getNamespaceURI()");
					LocalJavaField localPart = jm.newJavaField(String.class);
					localPart.addLine(qName, ".getLocalPart()");
					jm.addLine(result, ".addAttribute(", uri, ", ", localPart,
							   ", ", pController, ".getAttrQName(this, ", uri, ", ", localPart,
	                           "), \"CDATA\", ", wildcardAttribute, ".getValue());");
					jm.addEndFor();
				} else {
					SGlet sgLet = new SGlet(){
						public void generate(JavaMethod pMethod, Object pValue) throws SAXException {
							String uri = JavaSource.getQuoted(attribute.getName().getNamespaceURI());
							String localName = JavaSource.getQuoted(attribute.getName().getLocalName());
							pMethod.addLine(result, ".addAttribute(", uri, ", ", localName,
											", ", pController, ".getAttrQName(this, ", uri, ", ", localName, "), \"CDATA\", ",
											attribute.getTypeSG().getSimpleTypeSG().getCastToString(pMethod, pValue, pController), ");");
						}
					};
					attribute.forAllNonNullValues(jm, element, sgLet);
				}
			}
		}
		jm.addLine("return ", result, ";");
		return jm;
	}

	private JavaMethod newMarshalChildsMethod(ComplexTypeSG pType) throws SAXException {
		MarshalChildsMethodGeneratingVisitor visitor = new MarshalChildsMethodGeneratingVisitor(js);
		new ParticleWalker(visitor).walk(pType);
		return visitor.getMethod();
	}

	private void generate(ComplexTypeSG pType) throws SAXException {
		newGetAttributesMethod(pType);
		newGetPreferredPrefixMethod(pType);
		newMarshalChildsMethod(pType);
	}

	public void emptyType(ComplexTypeSG pType) throws SAXException {
		generate(pType);
	}

	public void simpleContent(ComplexTypeSG pType) throws SAXException {
		generate(pType);
	}

	public void startComplexContent(ComplexTypeSG pType) throws SAXException {
		generate(pType);
	}

	public void complexElementParticle(GroupSG pGroup, ParticleSG pParticle) throws SAXException {
		TypeSG tSG = pParticle.getObjectSG().getTypeSG();
		if (tSG.isComplex()  &&  !tSG.isGlobalClass()) {
			tSG.getComplexTypeSG().getXMLSerializer(js);
		}
	}
}
