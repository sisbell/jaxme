/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind.util;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;


/** This utility class allows to use a JAXB object as the
 * source of a stylesheet transformation.<br>
 * If you depend on any methods from
 * {@link javax.xml.transform.sax.SAXSource}, you should
 * use this class. In particular, you must not use the
 * methods
 * {@link javax.xml.transform.sax.SAXSource#setInputSource(InputSource)},
 * or
 * {@link javax.xml.transform.sax.SAXSource#setXMLReader(XMLReader)}
 * on an instance of <code>JAXBSource</code>.<br>
 * If you depend on these methods, a replacement for the
 * <code>JAXBSource</code> can be obtained as follows:
 * <pre>
 *     javax.xml.bind.JAXBContext context;
 *     javax.xml.bind.Element object;
 *     java.io.StringWriter sw = new java.io.StringWriter();
 *     context.createMarshaller().marshal(object, sw);
 *     org.xml.sax.InputSource isource = new org.xml.sax.InputSource(new java.io.StringReader(sw.toString()));
 *     javax.xml.transform.sax.SAXSource source = new javax.xml.transform.sax.SAXSource(isource);
 * </pre>
 * 
 * @author JSR-31
 * @since JAXB1.0
 */
public class JAXBSource extends SAXSource {
    /** This private class basically wraps a marshaller and calls its marshal
     * method when the parse methods are called on the XMLReader.
     **/
    private class JAXBSourceXMLReader implements XMLReader {
        private EntityResolver resolver;
        private DTDHandler dtdHandler;
        private ErrorHandler errorHandler;
        private XMLFilterImpl contentHandlerProxy = new XMLFilterImpl();
        
        public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name.equals("http://xml.org/sax/features/namespaces")
                || name.equals("http://xml.org/sax/features/namespace-prefixes")) {
                return true;
            } else {
                throw new SAXNotRecognizedException("Unknown feature: " + name);
            }
        }

        public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
            if(name.equals("http://xml.org/sax/features/namespaces")
               || name.equals("http://xml.org/sax/features/namespace-prefixes")) {
                if(!value) {
                    throw new SAXNotSupportedException("The feature " + name + " cannot be disabled.");
                }
            } else {
                throw new SAXNotRecognizedException("Unknown feature: " + name);
            }
        }
        
        public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            throw new SAXNotRecognizedException("Unknown property: " + name);
        }

        public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
            throw new SAXNotRecognizedException("Unknown property: " + name);
        }

        public EntityResolver getEntityResolver() {
            return resolver;
        }

        public void setEntityResolver(EntityResolver resolver) {
            this.resolver = resolver;
        }
        
        public DTDHandler getDTDHandler() {
            return dtdHandler;
        }

        public void setDTDHandler(DTDHandler handler) {
            this.dtdHandler = handler;
        }
        
        public ContentHandler getContentHandler() {
            return contentHandlerProxy.getContentHandler();
        }

        public void setContentHandler(ContentHandler handler) {
            contentHandlerProxy.setContentHandler(handler);
        }
        
        public ErrorHandler getErrorHandler() {
            return errorHandler;
        }

        public void setErrorHandler(ErrorHandler handler) {
        	errorHandler = handler;
        }
        
        public void parse(String systemId) throws IOException, SAXException {
            throw new IllegalStateException("The XMLReader created by a JAXBSource must not be used to parse a system ID.");
        }

		public void parse(InputSource pInput) throws IOException, SAXException {
            if (pInput != inputSource) {
            	throw new IllegalArgumentException("The XMLReader created by an instance of JAXBSource can only be used to parse the InputSource returned by the same JAXBSource.");
            }
            try {
                marshaller.marshal(contentObject, contentHandlerProxy);
            } catch(JAXBException e) {
                SAXParseException spe = new SAXParseException(e.getMessage(), null, null, -1, -1, e);
                if(errorHandler!=null) {
                    errorHandler.fatalError(spe);
                }
                throw spe;
            }
		}
    }

    private final Marshaller marshaller;
    private final Object contentObject;
    private final InputSource inputSource;

    /** <p>Creates a new instance of JAXBSource. The given
     * {@link javax.xml.bind.JAXBContext} will be used to
     * construct a {@link javax.xml.bind.Marshaller} and
     * invoke the constructor
     * {@link #JAXBSource(javax.xml.bind.Marshaller, Object)}.</p>
     */
    public JAXBSource(javax.xml.bind.JAXBContext pContext, Object pObject) throws JAXBException {
    	this(pContext.createMarshaller(), pObject);
    }
    
    /** <p>Creates a new instance of JAXBSource.</p>
     */
    public JAXBSource(javax.xml.bind.Marshaller pMarshaller, Object pObject) throws JAXBException {
    	marshaller = pMarshaller;
    	contentObject = pObject;
        inputSource = new InputSource();
        super.setInputSource(inputSource);
        super.setXMLReader(new JAXBSourceXMLReader());
    }
}
