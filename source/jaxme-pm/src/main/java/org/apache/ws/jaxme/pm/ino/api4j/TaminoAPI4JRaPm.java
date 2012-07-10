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
package org.apache.ws.jaxme.pm.ino.api4j;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.PMException;

import com.softwareag.tamino.db.api.connection.TConnection;
import com.softwareag.tamino.db.api.connection.TServerNotAvailableException;
import com.softwareag.tamino.db.api.connector.TaminoDataSource;


/** {@link org.apache.ws.jaxme.PM} implementation for accessing
 * a Tamino database via the resource adapter version of the
 * TaminoAPI4J.
 */
public class TaminoAPI4JRaPm extends TaminoAPI4JPm {
    private String jndiReference;

    /** Returns the JNDI reference.
     */
    public String getJndiReference() {
        return jndiReference;
    }

    /** Sets the JNDI reference.
     */
    public void setJndiReference(String pJndiReference) {
        jndiReference = pJndiReference;
    }

    protected TConnection getTConnection() throws PMException {
    	TaminoDataSource tds;
    	try {
    		InitialContext ic = new InitialContext();
    		tds = (TaminoDataSource) ic.lookup(jndiReference);
    	} catch (NamingException e) {
    		throw new PMException(e);
    	}
    	try {
			return tds.getConnection();
		} catch (TServerNotAvailableException e) {
			throw new PMException(e);
		}
    }

    public void init(JMManager pManager) throws JAXBException {
        super.init(pManager);
        jndiReference = pManager.getProperty("jndiReference");
        if (jndiReference == null  ||  jndiReference.length() == 0) {
            throw new JAXBException("The property 'jndiReference' must be set.");
        }
    }
}
