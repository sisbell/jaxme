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

import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.PMException;

import com.softwareag.tamino.db.api.connection.TConnection;
import com.softwareag.tamino.db.api.connection.TConnectionFactory;
import com.softwareag.tamino.db.api.connection.TServerNotAvailableException;


/** {@link org.apache.ws.jaxme.PM} implementation for accessing
 * a Tamino database via the basic version of the TaminoAPI4J.
 */
public class TaminoAPI4JDbPm extends TaminoAPI4JPm {
	private String url, user, password;

	/** Sets the database URL. This includes the database name,
	 * but not the database collection.
	 */
	public String getUrl() {
		return url;
	}

	/** Returns the database URL. This includes the database name,
	 * but not the database collection.
	 */
	public void setUrl(String pUrl) {
		url = pUrl;
	}

	/** Returns the database user.
	 */
	public String getUser() {
		return user;
	}

	/** Sets the database user.
	 */
	public void setUser(String pUser) {
		user = pUser;
	}

	/** Returns the database users password.
	 */
	public String getPassword() {
		return password;
	}

	/** Sets the database users password.
	 */
	public void setPassword(String pPassword) {
		password = pPassword;
	}

    public void init(JMManager pManager) throws JAXBException {
        super.init(pManager);
        url = pManager.getProperty("url");
        user = pManager.getProperty("user");
        password = pManager.getProperty("password");
    }

	protected TConnection getTConnection() throws PMException {
		TConnectionFactory factory = TConnectionFactory.getInstance();
		try {
			return factory.newConnection(url, user, password);
		} catch (TServerNotAvailableException e) {
			throw new PMException(e);
		}
	}
}
