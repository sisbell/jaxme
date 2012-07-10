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
 
package org.apache.ws.jaxme.pm.generator.jdbc;

import org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG.Mode;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectImpl;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ConnectionDetails extends XsObjectImpl {
  private final JaxMeJdbcSG jdbcSG;
  protected ConnectionDetails(JaxMeJdbcSG pJdbcSG, XsObject pParent) {
    super(pParent);
    this.jdbcSG = pJdbcSG;
  }

  private Mode mode;
  private String driver, url, user, password, datasource;
  private Boolean usingDatasource;
  /** Sets the JDBC driver.
   */
  public void setDriver(String pDriver) { driver = pDriver; }
  /** Returns the JDBC driver.
   */
  public String getDriver() {
    return this.jdbcSG.getSGFactory().getGenerator().getProperty("jdbc.driver", driver);
  }
  /** Sets the JDBC URL.
   */
  public void setUrl(String pUrl) { url = pUrl; }
  /** Returns the JDBC URL.
   */
  public String getUrl() {
    return this.jdbcSG.getSGFactory().getGenerator().getProperty("jdbc.url", url);
  }
  /** Sets the JDBC user.
   */
  public void setUser(String pUser) { user = pUser; }
  /** Returns the JDBC user.
   */
  public String getUser() {
    return this.jdbcSG.getSGFactory().getGenerator().getProperty("jdbc.user", user);
  }
  /** Sets the JDBC password.
   */
  public void setPassword(String pPassword) { password = pPassword; }
  /** Returns the JDBC password.
   */
  public String getPassword() {
    return this.jdbcSG.getSGFactory().getGenerator().getProperty("jdbc.password", password);
  }
  /** Sets the JDBC datasource.
   * @see #setUsingDatasource(Boolean)
   */
  public void setDatasource(String pDatasource) { datasource = pDatasource; }
  /** Returns the JDBC datasource.
   * @see #isUsingDatasource()
   */
  public String getDatasource() {
    return this.jdbcSG.getSGFactory().getGenerator().getProperty("jdbc.datasource", datasource);
  }
  /** Sets, whether a JDBC datasource is being used. By default
   * the JDBC driver and URL are used.
   */
  public void setUsingDatasource(Boolean pUsingDatasource) {
    usingDatasource = pUsingDatasource;
  }
  /** Returns, whether a JDBC datasource is being used. By default
   * the JDBC driver and URL are used.
   */
  public Boolean isUsingDatasource() {
    String s = this.jdbcSG.getSGFactory().getGenerator().getProperty("jdbc.usingDatasource");
    return s == null ? usingDatasource : Boolean.valueOf(s);
  }
  /** Sets the database mode. Must be either of
   * {@link org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG.Mode#GENERIC}
   * (default), or
   * {@link org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG.Mode#ORACLE}.
   */
  public void setDbMode(String pMode) {
    mode = Mode.valueOf(pMode);
  }
  /** Returns the database mode. Either of
   * {@link org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG.Mode#GENERIC}
   * (default), or
   * {@link org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG.Mode#ORACLE}.
   */
  public Mode getDbMode() {
    String s = this.jdbcSG.getSGFactory().getGenerator().getProperty("jdbc.dbMode");
    return s == null ? mode : Mode.valueOf(s);
  }
  /** Copies the given details into the current.
   */
  public void cloneFrom(ConnectionDetails pFrom) {
    mode = pFrom.mode;
    driver = pFrom.driver;
    url = pFrom.url;
    user = pFrom.user;
    password = pFrom.password;
    datasource = pFrom.datasource;
    usingDatasource = pFrom.usingDatasource;
  }

  public void validate() throws SAXException {
    boolean driverIsSet = driver != null  &&  driver.length() > 0;
    boolean datasourceIsSet = datasource != null  &&  datasource.length() > 0;
    if (driverIsSet) {
      if (!datasourceIsSet) {
        throw new LocSAXException("Either of the 'driver' or 'datasource' attributes must be set.", getLocator());
      }
      if (url == null  &&  url.length() == 0) {
        throw new LocSAXException("Missing attribute: 'url'", getLocator());
      }
    } else {
    }
    if (driverIsSet  &&  datasourceIsSet) {
      throw new LocSAXException("The 'driver' and 'datasource' attributes are mutually exclusive.", getLocator());
    }
  }
}