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
package org.apache.ws.jaxme.generator.sg.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.util.JavaNamer;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class AbstractContext implements Context {
    private XsQName name;
    private JavaQName xmlInterfaceName, xmlImplementationName, xmlHandlerName, xmlSerializerName;
    private JavaQName xmlValidatorName, pmName;

    public XsQName getName() { return name; }
    protected void setName(XsQName pName) { name = pName; }
    public JavaQName getXMLHandlerName() { return xmlHandlerName; }
    protected void setXMLHandlerName(JavaQName pXmlHandlerName) { xmlHandlerName = pXmlHandlerName; }
    public JavaQName getXMLImplementationName() { return xmlImplementationName; }
    protected void setXMLImplementationName(JavaQName pXmlImplementationName) { xmlImplementationName = pXmlImplementationName; }
    public JavaQName getXMLInterfaceName() { return xmlInterfaceName; }
    protected void setXMLInterfaceName(JavaQName pXmlInterfaceName) {
        xmlInterfaceName = pXmlInterfaceName;
    }
    public JavaQName getXMLSerializerName() { return xmlSerializerName; }
    protected void setXMLSerializerName(JavaQName pXmlSerializerName) { xmlSerializerName = pXmlSerializerName; }
    protected void setXMLValidatorName(JavaQName pXmlValidatorName) { xmlValidatorName = pXmlValidatorName; }
    public JavaQName getXMLValidatorName() { return xmlValidatorName; }
    protected void setPMName(JavaQName pPMName) { pmName = pPMName; }
    public JavaQName getPMName() { return pmName; }

    protected static String getClassNameFromLocalName(Locator pLocator, String pLocalName,
            										  SchemaSG pSchemaSG) throws SAXException {
        if (pLocalName == null  ||  pLocalName.length() == 0) {
            throw new LocSAXException("Invalid local name: " + pLocalName, pLocator);
        }
        return JavaNamer.convert(pLocalName, pSchemaSG);
    }

    protected static String getPackageName(SchemaSG pSchemaSG,
            							   JAXBSchemaBindings pSchemaBindings,
            							   Locator pLocator,
            							   XsQName pQName)
    		throws SAXException {
        String packageName = pSchemaSG.getFactory().getGenerator().getProperty("jaxme.package.name");
        if (packageName == null) {
            if (pSchemaBindings != null) {
                JAXBSchemaBindings.Package jaxbPackage = pSchemaBindings.getPackage();
                if (jaxbPackage != null) {
                    packageName = jaxbPackage.getName();
                }
            }
        }
      
        if (packageName == null) {
            packageName = getPackageNameFromURI(pLocator, pQName.getNamespaceURI());
        }
        return packageName;
    }

    protected static String getPackageNameFromURL(URL pURL) {
        List tokens = new ArrayList();
        for (StringTokenizer st = new StringTokenizer(pURL.getHost(), ".");
        	 st.hasMoreTokens();  ) {
            tokens.add(st.nextToken());
        }
        Collections.reverse(tokens);
        for (StringTokenizer st = new StringTokenizer(pURL.getPath(), "/");
        	 st.hasMoreTokens();  ) {
            tokens.add(st.nextToken());
        }
  
        if (tokens.size() == 0) {
            throw new IllegalArgumentException("Could not parse URL " + pURL);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0;  i < tokens.size();  i++) {
            if (i > 0) {
                sb.append('.');
            }
            String tok = (String) tokens.get(i);
            if (tok.length() == 0) {
                throw new IllegalArgumentException("Could not parse URL " + pURL);
            }
            for (int j = 0;  j < tok.length();  j++) {
                char c = Character.toLowerCase(tok.charAt(j));
                if ((j == 0  &&  Character.isJavaIdentifierStart(c))
                	||  (j > 0  && Character.isJavaIdentifierPart(c))) {
                    sb.append(c);
                } else {
                    sb.append('_');
                }
            }
        }
        return sb.toString();
    }
  
    protected static String getPackageNameFromURI(Locator pLocator, String pURI) throws SAXException {
        if (pURI == null  ||  pURI.length() == 0) {
            throw new LocSAXException("Unable to derive package name from an empty namespace URI. Use the schemaBindings to specify a package name.", pLocator);
        }
        try {
            URL url = new java.net.URL(pURI);
            return getPackageNameFromURL(url);
        } catch (MalformedURLException e) {
            throw new LocSAXException("Unable to derive package name from an URI, which is no URL: " + pURI,
                    pLocator);
        }
    }
    
    public String toString() {
        return super.toString() + " [" + getName() + "," + getXMLInterfaceName() + "," +
        getXMLImplementationName() + "]";
    }
}
