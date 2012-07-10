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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.ws.jaxme.JMUnmarshaller;
import org.apache.ws.jaxme.util.DOMSerializer;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/** JaxMe's {@link javax.xml.bind.Unmarshaller} implementation.
 */
public class JMUnmarshallerImpl extends JMControllerImpl implements JMUnmarshaller {
    private static final SAXParserFactory spf;

	static {
        spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        spf.setNamespaceAware(true);
    }
    
    private boolean validating;
    private Listener listener;
    private AttachmentUnmarshaller attachmentUnmarshaller;

	public boolean isValidating() { return validating; }
    public void setValidating(boolean pValidating) { validating = pValidating; }
    
    public Object unmarshal(URL pURL) throws JAXBException {
        InputSource isource;
        try {
            isource = new InputSource(pURL.openStream());
            isource.setSystemId(pURL.toString());
        } catch (IOException e) {
            throw new UnmarshalException("Failed to open URL " + pURL, e);
        }
        return unmarshal(isource);
    }
    
    public Object unmarshal(File pFile) throws JAXBException {
        InputSource isource;
        try {
            isource = new InputSource(new FileInputStream(pFile));
            isource.setSystemId(pFile.toURI().toURL().toString());
        } catch (IOException e) {
            throw new UnmarshalException("Failed to open file " + pFile, e);
        }
        return unmarshal(isource);
    }

    public Object unmarshal(Reader pReader) throws JAXBException {
    	return unmarshal(new InputSource(pReader));
    }
    
    public Object unmarshal(InputStream pStream) throws JAXBException {
        return unmarshal(new InputSource(pStream));
    }
    
    public Object unmarshal(InputSource pSource) throws JAXBException {
        UnmarshallerHandler uh = getUnmarshallerHandler();
        try {
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(uh);
            xr.parse(pSource);
        } catch (SAXException e) {
            if (e.getException() != null) {
                throw new UnmarshalException(e.getException());
            } else {
                throw new UnmarshalException(e);
            }
        } catch (IOException e) {
            throw new UnmarshalException(e);
        } catch (ParserConfigurationException e) {
            throw new UnmarshalException(e);
        }
        return uh.getResult();
    }

    public Object unmarshal(Node pNode) throws JAXBException {
        UnmarshallerHandler uh = getUnmarshallerHandler();
        DOMSerializer ds = new DOMSerializer();
        try {
            ds.serialize(pNode, uh);
        } catch (SAXException e) {
            if (e.getException() != null) {
                throw new UnmarshalException(e.getException());
            } else {
                throw new UnmarshalException(e);
            }
        }
        return uh.getResult();
    }
    
    public Object unmarshal(Source pSource) throws JAXBException {
        if (pSource instanceof SAXSource) {
            SAXSource ss = (SAXSource) pSource;
            InputSource is = ss.getInputSource();
            if (is == null) {
                throw new UnmarshalException("The SAXResult doesn't have its InputSource set.");
            }
            XMLReader xr = ss.getXMLReader();
            if (xr == null) {
                return unmarshal(is);
            } else {
                UnmarshallerHandler uh = getUnmarshallerHandler();
                xr.setContentHandler(uh);
                try {
                    xr.parse(is);
                } catch (IOException e) {
                    throw new JAXBException(e);
                } catch (SAXException e) {
                    if (e.getException() != null) {
                        throw new JAXBException(e.getException());
                    } else {
                        throw new JAXBException(e);
                    }
                }
                return uh.getResult();
            }
        } else if (pSource instanceof StreamSource) {
            StreamSource ss = (StreamSource) pSource;
            InputSource iSource = new InputSource();
            iSource.setPublicId(ss.getPublicId());
            iSource.setSystemId(ss.getSystemId());
            Reader r = ss.getReader();
            if (r == null) {
                InputStream is = ss.getInputStream();
                if (is == null) {
                    throw new IllegalArgumentException("The StreamSource doesn't have its Reader or InputStream set.");
                } else {
                    iSource.setByteStream(is);
                }
            } else {
                iSource.setCharacterStream(r);
            }
            return unmarshal(iSource);
        } else if (pSource instanceof DOMSource) {
            Node node = ((DOMSource) pSource).getNode();
            if (node == null) {
                throw new IllegalArgumentException("The DOMSource doesn't have its Node set.");
            }
            return unmarshal(node);
        } else {
            throw new IllegalArgumentException("Unknown type of Source: " + pSource.getClass().getName() +
            ", only SAXSource, StreamSource and DOMSource are supported.");
        }
    }
    
    public UnmarshallerHandler getUnmarshallerHandler() {
        return new JMUnmarshallerHandlerImpl(this);
    }

    public Listener getListener() {
    	return listener;
	}

    public void setListener(Listener pListener) {
    	listener = pListener;
	}

    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
		return attachmentUnmarshaller;
	}

    public void setAttachmentUnmarshaller(AttachmentUnmarshaller pAttachmentUnmarshaller) {
		attachmentUnmarshaller = pAttachmentUnmarshaller;
	}

	@SuppressWarnings("unchecked")
    public <A extends XmlAdapter> A getAdapter(Class<A> pType) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public Schema getSchema() {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	@SuppressWarnings("unchecked")
	public <A extends XmlAdapter> void setAdapter(Class<A> pType,
			A pAdapter) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	@SuppressWarnings("unchecked")
	public void setAdapter(XmlAdapter pAdapter) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public void setSchema(Schema pSchema) {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public <T> JAXBElement<T> unmarshal(XMLEventReader pReader, Class<T> pType)
			throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public Object unmarshal(XMLEventReader pReader) throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public <T> JAXBElement<T> unmarshal(XMLStreamReader pReader, Class<T> pType)
			throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public Object unmarshal(XMLStreamReader pReader) throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public <T> JAXBElement<T> unmarshal(Node pNode, Class<T> pType)
			throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}

	public <T> JAXBElement<T> unmarshal(Source pSource, Class<T> pType)
			throws JAXBException {
    	throw new UnsupportedOperationException("This method is unsupported by this version of JaxMe.");
	}
}
