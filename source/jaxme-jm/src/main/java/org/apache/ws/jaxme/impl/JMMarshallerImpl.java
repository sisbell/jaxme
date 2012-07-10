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
package org.apache.ws.jaxme.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.apache.ws.jaxme.JMElement;
import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.JMMarshaller;
import org.apache.ws.jaxme.XMLWriter;
import org.apache.ws.jaxme.util.DOMBuilder;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JMMarshallerImpl.java 743117 2009-02-10 21:55:41Z jochen $
 */
public class JMMarshallerImpl extends JMControllerImpl implements JMMarshaller {
	/** Default value for {@link Marshaller#JAXB_ENCODING}.
	 * (UTF-8 encoding)
	 */
	public static final String DEFAULT_JAXB_ENCODING = "UTF-8";

	/** Default value for {@link #JAXME_INDENTATION_STRING}: Two blanks.
	 */
	public static final String DEFAULT_JAXME_INDENTATION_STRING = "  ";

	/** Default value for {@link #JAXME_INDENTATION_SEPARATOR}.
	 * ("\n", Line Feed)
	 */
	public static final String DEFAULT_JAXME_INDENTATION_SEPARATOR = "\n";

	/** Property name for setting the String used to indent
	 * the formatted output by one level.
	 * ("jaxme.indentation.string")
	 * Defaults to {@link #DEFAULT_JAXME_INDENTATION_STRING}.
	 *
	 * @see #setIndentationString
	 * @see #getIndentationString
	 */
	public static final String JAXME_INDENTATION_STRING = "jaxme.indentation.string";

	/** Property name for setting the String used as a
	 * line separator in the formatted output.
	 * ("jaxme.indentation.separator")
	 *
	 * @see #setIndentationSeparator
	 * @see #getIndentationSeparator
	 */
	public static final String JAXME_INDENTATION_SEPARATOR = "jaxme.indentation.separator";

	/** Property name for choosing whether the marshalled
	 * output should contain an XML declaration. The methods
	 * {@link #marshal(Object, OutputStream)} and
	 * {@link #marshal(Object, Writer)} recognize
	 * requests for XML declarations.
	 *
	 * @see #setXmlDeclaration(boolean)
	 * @see #getXmlDeclaration
	 */
	public static final String JAXME_XML_DECLARATION = "jaxme.xml.declaration";

	/** Property name for a SAX {@link ContentHandler} which is able to
	 * marshal a SAX stream into a character stream. The property value is
	 * an instance of {@link Class} implementing {@link XMLWriter}.
	 */
	public static final String JAXME_XML_WRITER = "jaxme.xml.writer";

	private static final Class<? extends XMLWriter> xmlWriterClassDefault;

	@SuppressWarnings("unchecked")
	private static Class<? extends XMLWriter> asXMLWriterClass(Class<?> pClass) {
		if (!XMLWriter.class.isAssignableFrom(pClass)) {
			throw new IllegalArgumentException("The class " + pClass.getName()
					+ " doesn't implement " + XMLWriter.class.getName());
		}
		return (Class<? extends XMLWriter>) pClass;
	}

	static {
		Class<?> c;
		try {
			Class.forName("java.nio.charset.Charset ");
			c = Class.forName("org.apache.ws.jaxme.impl.CharSetXMLWriter");
			c.newInstance();
		} catch (Exception e) {
			c = XMLWriterImpl.class;
		}
		xmlWriterClassDefault = asXMLWriterClass(c);
	}

	private String encoding = DEFAULT_JAXB_ENCODING;
	private boolean indentation = true;
	private String indentationString = DEFAULT_JAXME_INDENTATION_STRING;
	private String indentationSeparator = DEFAULT_JAXME_INDENTATION_SEPARATOR;
	private boolean xmlDeclaration = true;
	private Class<? extends XMLWriter> xmlWriterClass;
	private String noNamespaceSchemaLocation, schemaLocation;
	private Listener listener;
	private AttachmentMarshaller attachmentMarshaller;

	/** Sets the controllers encoding; to be used in
	 * marshalling. Defaults to {@link #DEFAULT_JAXB_ENCODING}.
	 *
	 * @param pEncoding Suggested encoding or null to restore
	 *    the default
	 */
	public void setEncoding(String pEncoding) throws PropertyException {
		if (pEncoding == null) {
			encoding = DEFAULT_JAXB_ENCODING;
		} else {
			encoding = pEncoding;
		}
	}

	/** Returns the controllers encoding; to be used in
	 * marshalling. Defaults to {@link #DEFAULT_JAXB_ENCODING}.
	 */
	public String getEncoding() { return encoding; }

	/** Sets the controllers class implementing {@link XMLWriter}.
	 * Defaults to {@link XMLWriterImpl}.
	 *
	 * @param pClass A class implementing {@link XMLWriterImpl} or
	 *   null to restore the default.
	 */
	public void setXMLWriterClass(Class<? extends XMLWriter> pClass) throws PropertyException {
		if (pClass == null) {
			// Restore
			xmlWriterClass = null;
		} else if (XMLWriter.class.isAssignableFrom(pClass)  &&  !pClass.isInterface()) {
			xmlWriterClass = pClass;
		} else {
			throw new PropertyException("The class " + pClass.getName() + " is not implementing " + XMLWriter.class.getName());
		}
	}

	/** <p>Returns the controllers class implementing {@link XMLWriter}.
	 * Defaults to {@link XMLWriterImpl}.</p>
	 */
	public Class<? extends XMLWriter> getXMLWriterClass() {
		return xmlWriterClass == null ? xmlWriterClassDefault : xmlWriterClass;
	}

  /** <p>Sets whether XML documents generated by the controller
   * ought to be formatted. Defaults to true.</p>
   */
  public void setIndentation(boolean pIndentation) {
    indentation = pIndentation;
  }

  /** <p>Returns whether XML documents generated by the controller
   * ought to be formatted. Defaults to true.</p>
   */
  public boolean getIndentation() {
    return indentation;
  }

  /** <p>Sets whether the methods <code>marshal(Object, Writer)</code>
   * and <code>marshal(Object, OutputStream)</code> ought to emit an
   * XML declaration.</p>
   */
  public void setXmlDeclaration(boolean pDeclaration) {
    xmlDeclaration = pDeclaration;
  }

  /** <p>Returns whether the methods <code>marshal(Object, Writer)</code>
   * and <code>marshal(Object, OutputStream)</code> ought to emit an
   * XML declaration.</p>
   */
  public boolean getXmlDeclaration() { return xmlDeclaration; }

  /** <p>Sets the string used to indent one level. Defaults to
   * {@link #DEFAULT_JAXME_INDENTATION_STRING}. Equivalent to
   * <code>setProperty(JAXME_INDENTATION_STRING, pStr)</code>.</p>
   *
   * @see #DEFAULT_JAXME_INDENTATION_STRING
   * @see #setProperty
   * @see #getProperty
   */
  public void setIndentationString(String pStr) { indentationString = pStr; }

  /** <p>Returns the string used to indent one level. Defaults to
   * {@link #DEFAULT_JAXME_INDENTATION_STRING}. Equivalent to
   * <code>getProperty(JAXME_INDENTATION_STRING)</code>.</p>
   *
   * @see #DEFAULT_JAXME_INDENTATION_STRING
   * @see #setProperty
   * @see #getProperty
   */
  public String getIndentationString() { return indentationString; }

  /** <p>Sets the string used as a line separator. Defaults to
   * {@link #DEFAULT_JAXME_INDENTATION_SEPARATOR}. Equivalent to
   * <code>setProperty(JAXME_INDENTATION_SEPARATOR, pStr)</code>.</p>
   *
   * @see #DEFAULT_JAXME_INDENTATION_SEPARATOR
   * @see #setProperty
   * @see #getProperty
   */
  public void setIndentationSeparator(String pStr) { indentationSeparator = pStr; }

  /** <p>Returns the string used as a line separator. Defaults to
   * {@link #DEFAULT_JAXME_INDENTATION_SEPARATOR}. Equivalent to
   * <code>getProperty(JAXME_INDENTATION_SEPARATOR)</code>.</p>
   *
   * @see #DEFAULT_JAXME_INDENTATION_SEPARATOR
   * @see #setProperty
   * @see #getProperty
   */
  public String getIndentationSeparator() { return indentationSeparator; }

  /** <p>Sets the schema location. The marshaller will use this to
   * create an attribute <code>xsi:schemaLocation</code>. Equivalent
   * to <code>setProperty(JAXB_SCHEMA_LOCATION, pValue)</code>.
   * Defaults to null, in which case the attribute isn't created.</p>
   * @see Marshaller#JAXB_SCHEMA_LOCATION
   * @see #setProperty(String, Object)
   * @see #getSchemaLocation()
   */
  public void setSchemaLocation(String pValue) throws PropertyException {
    if (pValue != null  &&  noNamespaceSchemaLocation != null) {
      throw new PropertyException("The properties schemaLocation and noNamespaceSchemaLocation are mutually exclusive.");
    }
    schemaLocation = pValue;
  }

  /** <p>Returns the schema location. The marshaller will use this to
   * create an attribute <code>xsi:schemaLocation</code>. Equivalent
   * to <code>setProperty(JAXB_SCHEMA_LOCATION, pValue)</code>.
   * Defaults to null, in which case the attribute isn't created.</p>
   * @see Marshaller#JAXB_SCHEMA_LOCATION
   * @see #setProperty(String, Object)
   * @see #setSchemaLocation(String)
   */
  public String getSchemaLocation() {
  	return schemaLocation;
  }

  /** <p>Sets the schema location without namespace. The marshaller
   * will use this to create an attribute <code>xsi:noNamespaceSchemaLocation</code>.
   * Equivalent to <code>setProperty(JAXB_NO_NAMESPACE_SCHEMA_LOCATION,
   * pValue)</code>. Defaults to null, in which case the attribute isn't
   * created.</p>
   * @see Marshaller#JAXB_NO_NAMESPACE_SCHEMA_LOCATION
   * @see #setProperty(String, Object)
   * @see #getNoNamespaceSchemaLocation()
   */
  public void setNoNamespaceSchemaLocation(String pValue) throws PropertyException {
    if (pValue != null  &&  noNamespaceSchemaLocation != null) {
      throw new PropertyException("The properties schemaLocation and noNamespaceSchemaLocation are mutually exclusive.");
    }
    noNamespaceSchemaLocation = pValue;
  }

  /** <p>Returns the schema location. The marshaller will use this to
   * create an attribute <code>xsi:noNamespaceSchemaLocation</code>. Equivalent
   * to <code>setProperty(JAXB_SCHEMA_LOCATION, pValue)</code>.
   * Defaults to null, in which case the attribute isn't created.</p>
   * @see Marshaller#JAXB_NO_NAMESPACE_SCHEMA_LOCATION
   * @see #setProperty(String, Object)
   * @see #setNoNamespaceSchemaLocation(String)
   */
  public String getNoNamespaceSchemaLocation() {
    return noNamespaceSchemaLocation;
  }

    @Override
    public void setProperty(String pProperty, Object pValue)
    		throws PropertyException {
    	if (pProperty.startsWith("jaxb.")) {
    		if (Marshaller.JAXB_ENCODING.equals(pProperty)) {
    			setEncoding((String) pValue);
    			return;
    		} else if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(pProperty)) {
    			setIndentation(((Boolean) pValue).booleanValue());
    			return;
    		} else if (Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(pProperty)) {
    			setNoNamespaceSchemaLocation((String) pValue);
    			return;
    		} else if (Marshaller.JAXB_SCHEMA_LOCATION.equals(pProperty)) {
    			setSchemaLocation((String) pValue);
    			return;
    		}
    	} else if (pProperty.startsWith("jaxme.")) {
    		if (JAXME_XML_WRITER.equals(pProperty)) {
    			setXMLWriterClass(asXMLWriterClass((Class<?>) pValue));
    			return;
    		} else if (JAXME_XML_DECLARATION.equals(pProperty)) {
    			setXmlDeclaration(((Boolean) pValue).booleanValue());
    			return;
    		} else if (JAXME_INDENTATION_SEPARATOR.equals(pProperty)) {
    			setIndentationSeparator((String) pValue);
    			return;
    		} else if (JAXME_INDENTATION_STRING.equals(pProperty)) {
    			setIndentationString((String) pValue);
    			return;
    		}
    	}
    	super.setProperty(pProperty, pValue);
    }

    @Override
    public Object getProperty(String pProperty) throws PropertyException {
    	if (pProperty.startsWith("jaxb.")) {
    		if (Marshaller.JAXB_ENCODING.equals(pProperty)) {
    			return getEncoding();
    		} else if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(pProperty)) {
    			return new Boolean(getIndentation());
    		}
    	} else if (pProperty.startsWith("jaxme.")) {
    		if (JAXME_INDENTATION_STRING.equals(pProperty)) {
    			return getIndentationString();
    		} else if (JAXME_XML_WRITER.equals(pProperty)) {
    			return getXMLWriterClass();
    		} else if (JAXME_XML_DECLARATION.equals(pProperty)) {
    			return getEncoding();
    		} else if (JAXME_INDENTATION_SEPARATOR.equals(pProperty)) {
    			return getIndentationSeparator();
    		}
    	}
    	return super.getProperty(pProperty);
    }


  /* Marshaller methods
   */
  public void marshal(Object pObject, OutputStream pStream) throws JAXBException {
    Writer writer;
    try {
      writer = new OutputStreamWriter(pStream, getEncoding());
    } catch(UnsupportedEncodingException e) {
      throw new MarshalException("Unsupported encoding: " + getEncoding(), e);
    }
    marshal(pObject, writer);
    try {
      writer.close();
    } catch (IOException e) {
      throw new MarshalException(e);
    }
  }

    public void marshal(Object pObject, File pFile) throws JAXBException {
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(pFile);
    		marshal(pObject, fos);
    		fos.close();
    		fos = null;
    	} catch (IOException e) {
    		throw new JAXBException("I/O error while writing file "
    				+ pFile.getPath() + ": " + e.getMessage(), e);
		} finally {
    		if (fos != null) { try { fos.close(); } catch (Throwable ignore) {} }
    	}
    }
  
  public void marshal(Object pObject, ContentHandler pHandler) throws JAXBException {
    JMElement element = (JMElement) pObject;
	QName qName = element.getQName();
    try {
		JMManager manager = getJAXBContextImpl().getManager(qName);
		JMSAXDriver driver = manager.getDriver();
		JMSAXDriverController controller = manager.getDriverController();
		controller.init(this, pHandler);
		controller.marshal(driver, qName.getPrefix(), qName.getNamespaceURI(), qName.getLocalPart(), element);
    } catch (SAXException e) {
      throw new MarshalException(e);
    }
  }

  public void marshal(Object pObject, Writer pWriter) throws JAXBException {
    if (getXmlDeclaration()) {
      try {
        pWriter.write("<?xml version='1.0' encoding='" + getEncoding() + "'?>");
        if (getIndentation()) {
          pWriter.write(getIndentationSeparator());
        }
      } catch (IOException e) {
        throw new MarshalException(e);
      }
    }
    XMLWriter w;
    Class<?> c = getXMLWriterClass();
    try {
      w = (XMLWriter) c.newInstance();
    } catch (Exception e) {
      throw new JAXBException("Failed to instantiate XMLWriter class " + c.getName(), e);
    }
    w.init(this);
    w.setWriter(pWriter);
    marshal(pObject, w);
  }

  public void marshal(Object pObject, Node pNode) throws JAXBException {
    DOMBuilder db = new DOMBuilder();
    db.setTarget(pNode);
    marshal(pObject, db);
  }

  public void marshal(Object pObject, Result pResult) throws JAXBException {
    if (pResult instanceof SAXResult) {
      ContentHandler ch = ((SAXResult) pResult).getHandler();
      if (ch == null) {
        throw new MarshalException("The SAXResult doesn't have its ContentHandler set.");
      }
      marshal(pObject, ch);
    } else if (pResult instanceof StreamResult) {
      StreamResult sr = (StreamResult) pResult;
      Writer w = sr.getWriter();
      if (w == null) {
        OutputStream s = sr.getOutputStream();
        if (s == null) {
          throw new MarshalException("The StreamResult doesn't have its Writer or OutputStream set.");
        }
        marshal(pObject, s);
      } else {
        marshal(pObject, w);
      }
    } else if (pResult instanceof DOMResult) {
      Node node = ((DOMResult) pResult).getNode();
      if (node == null) {
        throw new MarshalException("The DOMResult doesn't have its Node set.");
      }
      marshal(pObject, node);
    } else {
      throw new MarshalException("Unknown type of Result: " + pResult.getClass().getName() +
                                  ", only SAXResult, StreamResult and DOMResult are supported.");
    }
  }

  public Node getNode(java.lang.Object contentTree) throws JAXBException {
    throw new UnsupportedOperationException("JaxMe doesn't support live DOM views");
  }

  	public void setListener(Listener pListener) {
  		listener = pListener;
  	}

    public Listener getListener() {
    	return listener;
    }

    public Schema getSchema() {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
    }

    public void setSchema(Schema pSchema) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
    }

    @SuppressWarnings("unchecked")
	public void setAdapter(XmlAdapter pAdapter) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
    }

    @SuppressWarnings("unchecked")
	public <A extends XmlAdapter> void setAdapter(Class<A> pType, A pAdapter) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
    }

    @SuppressWarnings("unchecked")
	public <A extends XmlAdapter> A getAdapter( Class<A> type ) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
    }

    public void setAttachmentMarshaller(AttachmentMarshaller pAttachmentMarshaller) {
    	attachmentMarshaller = pAttachmentMarshaller;
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
    	return attachmentMarshaller;
    }

    public void marshal(Object pObject, XMLStreamWriter pWriter) throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
    }

    public void marshal(Object pObject, XMLEventWriter pWriter) throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
    }
}
