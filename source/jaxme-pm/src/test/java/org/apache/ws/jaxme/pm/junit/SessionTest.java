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
 
package org.apache.ws.jaxme.pm.junit;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;
import org.apache.ws.jaxme.PM;
import org.apache.ws.jaxme.PMException;
import org.apache.ws.jaxme.impl.JAXBContextImpl;
import org.apache.ws.jaxme.test.pm.session.Session;
import org.apache.ws.jaxme.test.pm.session.impl.SessionTypePM;


/** Unit test for the JDBC manager.
 */
public class SessionTest extends TestCase {
  private static int num;

  /** Creates a new instance of the test with the given name.
   */
  public SessionTest(String pName) {
    super(pName);
  }        

  private JAXBContextImpl getFactory() throws JAXBException {
    String s = Session.class.getName();
    int offset = s.lastIndexOf('.');
    s = s.substring(0, offset);
    return (JAXBContextImpl) JAXBContext.newInstance(s);
  }

  private PM getPM() throws JAXBException, PMException {
    return getFactory().getJMPM(Session.class);
  }

  private Session getSession(PM pm) throws JAXBException {
    Session session = (Session) pm.create();
    session.setCookie("abcdefg" + num++);
    session.setEXPIRETIME((short) 900);
    session.setIpAddress("192.168.5.1");
    Calendar now = Calendar.getInstance();
    session.setLastAction(now);
    session.setLOGINTIME(now);
    session.setPRECEDENCE(3.27);
    session.setRANDOMSEED(0.01);
    return session;
  }

  /** Tests, whether the factory can be created.
   */
  public void testCreateFactory() throws Exception {
    getFactory();
  }

  /** Tests, whether a manager created.
   */
  public void testCreateManager() throws Exception {
    PM pm = getPM();
    assertTrue(pm instanceof SessionTypePM);
    SessionTypePM sessionTypePM = (SessionTypePM) pm;
    Connection conn = sessionTypePM.getConnection();
    assertNotNull(conn);
    assertTrue(conn.getAutoCommit());
  }

  /** Tests, whether all entries can be removed from
   * the database.
   */
  public void testClear() throws Exception {
    PM pm = getPM();
    for (Iterator iter = pm.select(null);  iter.hasNext();  ) {
      pm.delete((Session) iter.next());
    }
  }

  /** Tests, whether a new row can be created,
   * which is not stored into the database.
   */
  public void testCreate() throws Exception {
    PM pm = getPM();
    getSession(pm);
  }

  /** Tests, whether a new row can be created
   * and stored into the database.
   */
  public void testInsert() throws Exception {
    PM pm = getPM();
    Session session = getSession(pm);
    session.setID(1);
    pm.insert(session);
    session = getSession(pm);
    session.setID(2);
    session.setIpAddress("192.168.5.127");
    pm.insert(session);
  }

  /** Test, that reads all rows from the database.
   */
  public void testSelectAll() throws Exception {
    PM pm = getPM();
    Iterator iter = pm.select("1=1 ORDER BY ID");
    assertTrue(iter.hasNext());
    Session session = (Session) iter.next();
    assertEquals(1, session.getID());
    assertTrue(iter.hasNext());
    session = (Session) iter.next();
    assertEquals(2, session.getID());
    assertTrue(!iter.hasNext());
  }

  /** Test, that reads a single row from the database.
   */
  public void testSelect() throws Exception {
    PM pm = getPM();
    Iterator iter = pm.select("IPADDRESS='192.168.5.127'");
    assertTrue(iter.hasNext());
    Session session = (Session) iter.next();
    assertEquals("192.168.5.127", session.getIpAddress());
    assertEquals(2, session.getID());
    assertTrue(!iter.hasNext());
    assertTrue(!pm.select("IPADDRESS='192.168.5.128'").hasNext());
  }

  /** Test, that updates a row in the database.
   */
  public void testUpdate() throws Exception {
    PM pm = getPM();
    Iterator iter = pm.select("IPADDRESS='192.168.5.127'");
    Session session = (Session) iter.next();
    session.setIpAddress("192.168.5.128");
    pm.update(session);

    assertTrue(!pm.select("IPADDRESS='192.168.5.127'").hasNext());
    iter = pm.select("IPADDRESS='192.168.5.128'");
    assertTrue(iter.hasNext());
    session = (Session) iter.next();
    assertEquals("192.168.5.128", session.getIpAddress());
    assertEquals(2, session.getID());
    assertTrue(!iter.hasNext());

    iter = pm.select("IPADDRESS='192.168.5.128'");
    session = (Session) iter.next();
    session.setIpAddress("192.168.5.127");
    pm.update(session);

    testSelect();
  }

  /** Test, that deletes a row in the database.
   */
  public void testDelete() throws Exception {
    testClear();
    PM pm = getPM();
    assertTrue(!pm.select(null).hasNext());
  }
}
