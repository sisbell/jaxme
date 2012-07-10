/*
 * Copyright 2003,2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.generator.sg.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.impl.SchemaReaderImpl;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGFactoryChain;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.util.DTDParser;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/** The default, JAXB compliant, schema reader.
 */
public class JAXBSchemaReader extends SchemaReaderImpl {
    private static final Logger log = LoggerAccess.getLogger(JAXBSchemaReader.class);
    private boolean isSupportingExtensions = false;
    private List sgFactoryChains = new ArrayList();

    /** Returns whether vendor extensions are being supported.
     */
    public boolean isSupportingExtensions() {
    	return isSupportingExtensions;
    }

    /** Sets whether vendor extensions are being supported.
     */
    public void setSupportingExtensions(boolean pSupportingExtensions) {
    	isSupportingExtensions = pSupportingExtensions;
    }

    public void addSGFactoryChain(Class pChainClass) {
    	final String mName = "addSGFactoryChain";
    	if (pChainClass == null) {
    		throw new NullPointerException("The pChainClass argument must not be null.");
    	}
    	log.finest(mName, "->", pChainClass.getName());
    	sgFactoryChains.add(pChainClass);
    	log.finest(mName, "<-", Integer.toString(sgFactoryChains.size()));
    }

    protected SGFactoryChain newSGFactoryChain(Generator pGenerator) {
    	return new JAXBSGFactory(pGenerator);
    }

    public SGFactory getSGFactory() throws SAXException {
    	final String mName = "getSGFactory";
    	log.finest(mName, "->");
    	SGFactoryChain chain = newSGFactoryChain(getGenerator());
    	log.finest(mName, "Created instance of " + chain.getClass().getName());
    	for (Iterator iter = sgFactoryChains.iterator();  iter.hasNext();  ) {
    		Class c = (Class) iter.next();
    		log.finest(mName, "Adding instance of " + c.getName());
    		Object o;
    		try {
    			Constructor con = c.getConstructor(new Class[]{SGFactoryChain.class});
    			o = con.newInstance(new Object[]{chain});
    		} catch (NoSuchMethodException e) {
    			throw new SAXException("The SGFactoryChain class " + c.getName() +
    			" has no constructor taking the backing chain as an argument.");
    		} catch (InvocationTargetException e) {
    			Throwable t = e.getTargetException();
    			String msg = "Failed to invoke the constructor of class " + c.getName() +
				" with an argument of type " + chain.getClass().getName() +
				": " + t.getClass().getName() + ", " + t.getMessage();
    			if (t instanceof Exception) {
    				throw new SAXException(msg, (Exception) t);
    			} else {
    				throw new SAXException(msg, e);
    			}
    		} catch (Exception e) {
    			throw new SAXException("Failed to invoke the constructor of class " + c.getName() +
    					" with an argument of type " + chain.getClass().getName() +
						": " + e.getClass().getName() + ", " + e.getMessage(), e);
    		}
    		chain = (SGFactoryChain) o;
    	}
    	SGFactory result = new SGFactoryImpl(chain);
    	result.init();
    	log.finest(mName, "<-", result);
    	return result;
    }

    /** Resets the schema readers internal state, allowing
     * to parse multiple schemas with a single instance.
     */
    protected void reset() {
    }

    public SchemaSG parse(InputSource pSource) throws Exception {
    	final String mName = "parse";
    	reset();
    	log.finest(mName, "->", pSource.getSystemId());
    	SGFactory factory = getSGFactory();
    	XSSchema schema;
        if (Boolean.valueOf(getGenerator().getProperty("jaxme.dtd.input")).booleanValue()) {
            DTDParser parser = new DTDParser();
            String targetNamespace = getGenerator().getProperty("jaxme.dtd.targetNamespace");
            if (targetNamespace != null  &&  !"".equals(targetNamespace)) {
            	parser.setTargetNamespace(new XsAnyURI(targetNamespace));
            }
            schema = parser.parse(pSource);
        } else {
        	XSParser parser = factory.newXSParser();
        	log.finest(mName, "Parser = " + parser + ", validating = " + getGenerator().isValidating());
        	parser.setValidating(getGenerator().isValidating());
        	schema = parser.parse(pSource);
        }
    	log.finest(mName, "Schema = " + schema);
    	SchemaSG result = factory.getSchemaSG(schema);
    	log.finest(mName, "<-", result);
    	return result;
    }
}
