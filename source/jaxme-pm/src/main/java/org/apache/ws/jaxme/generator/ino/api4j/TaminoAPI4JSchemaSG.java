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

package org.apache.ws.jaxme.generator.ino.api4j;


import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SchemaSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.impl.SchemaSGChainImpl;
import org.apache.ws.jaxme.impl.JAXBContextImpl;
import org.apache.ws.jaxme.pm.ino.api4j.TaminoAPI4JDbPm;
import org.apache.ws.jaxme.pm.ino.api4j.TaminoAPI4JRaPm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TaminoAPI4JSchemaSG extends SchemaSGChainImpl {
    private final TaminoAPI4JSG baseSG;

    /** Creates a new instance with the given backing chains.
     */
    public TaminoAPI4JSchemaSG(SchemaSGChain pChain, TaminoAPI4JSG pBaseSG) {
        super(pChain);
        baseSG = pBaseSG;
    }

    protected TaminoAPI4JSG getBaseSG() {
        return baseSG;
    }

    private Element createProperty(Element pParent, String pName, String pValue) {
        Element element = pParent.getOwnerDocument().createElementNS(JAXBContextImpl.CONFIGURATION_URI, "Property");
        pParent.appendChild(element);
        element.setAttributeNS(null, "name", pName);
        element.setAttributeNS(null, "value", pValue);
        return element;
    }

    public Document getConfigFile(SchemaSG pController, String pPackageName,
								  List pContextList) throws SAXException {
        final String uri = JAXBContextImpl.CONFIGURATION_URI;
        final Document doc = super.getConfigFile(pController, pPackageName, pContextList);
        final Element root = doc.getDocumentElement();
        final Iterator iter = pContextList.iterator();
        TaminoAPI4JSG.RaDetails raDetails = baseSG.getRaDetails();
        TaminoAPI4JSG.DbDetails dbDetails = baseSG.getDbDetails();
        for (Node node = root.getFirstChild();  node != null;  node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE
                    &&  JAXBContextImpl.CONFIGURATION_URI.equals(node.getNamespaceURI())
                    &&  "Manager".equals(node.getLocalName())) {
                Element manager = (Element) node;
                final ObjectSG objectSG = (ObjectSG) iter.next();
                final TypeSG typeSG = objectSG.getTypeSG();
                if (!typeSG.isComplex()) {
                	continue;
                }

                final Class managerClass;
                if (raDetails != null) {
                    String collection = raDetails.getCollection();
                    if (collection != null  &&  collection.length() > 0) {
                        createProperty(manager, "collection", collection);
                    }
                    String jndiRef = raDetails.getJndiReference();
                    if (jndiRef != null  &&  jndiRef.length() > 0) {
                        createProperty(manager, "jndiReference", jndiRef);
                    }
                    managerClass = TaminoAPI4JRaPm.class;
                } else if (dbDetails != null) {
                	String collection = dbDetails.getCollection();
                    if (collection != null  &&  collection.length() > 0) {
                        createProperty(manager, "collection", collection);
                    }
                    String url = dbDetails.getUrl();
                    if (url != null  &&  url.length() > 0) {
                        createProperty(manager, "url", url);
                    }
                    String user = dbDetails.getUser();
                    if (user != null  &&  user.length() > 0) {
                        createProperty(manager, "user", user);
                    }
                    String password = dbDetails.getPassword();
                    if (password != null  &&  password.length() > 0) {
                        createProperty(manager, "password", password);
                    }
                    managerClass = TaminoAPI4JDbPm.class;
                } else {
                	managerClass = null;
                }
                if (managerClass != null) {
                    manager.setAttributeNS(uri, "pmClass", managerClass.getName());
                }
            }
        }
        if (iter.hasNext()) {
            throw new IllegalStateException("More managers expected than found");
        }
        return doc;
    }
}
