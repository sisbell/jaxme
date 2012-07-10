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

package org.apache.ws.jaxme.pm.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.pm.impl.PMImpl;
import org.apache.ws.jaxme.util.ClassLoader;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class PMJdbcImpl extends PMImpl {
  private String driver, url, user, password;

  /** <p>Creates a new instance of PMJdbcImpl.</p>
   */
  public PMJdbcImpl() {
    super();
  }

  public void init(JMManager pManager) throws JAXBException {
    super.init(pManager);
    String myDriver = pManager.getProperty("jdbc.driver");
    if (myDriver == null  ||  myDriver.length() == 0) {
      throw new JAXBException("Missing property: 'jdbc.driver' (JDBC driver class)");
    }
    driver = myDriver;
    try {
      ClassLoader.getClass(myDriver, Driver.class);
    } catch (ClassNotFoundException e) {
      throw new JAXBException("Unable to load driver class " + myDriver);
    }
    String myUrl = pManager.getProperty("jdbc.url");
    if (myUrl == null  ||  myUrl.length() == 0) {
      throw new JAXBException("Missing property: 'jdbc.url' (JDBC database URL)");
    }
    url = myUrl;

    user = pManager.getProperty("jdbc.user");

    password = pManager.getProperty("jdbc.password");
  }

  /** Returns the configured JDBC driver.
   */
  public String getJdbcDriver() {
    return driver;
  }

  /** Returns the configured JDBC URL.
   */
  public String getJdbcUrl() {
    return url;
  }

  /** Returns the configured JDBC user.
   */
  public String getJdbcUser() {
    return user;
  }

  /** Returns the configured JDBC password.
   */
  public String getJdbcPassword() {
    return password;
  }

  /** Creates a new database connection.
   */
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(getJdbcUrl(), getJdbcUser(), getJdbcPassword());
  }
}
