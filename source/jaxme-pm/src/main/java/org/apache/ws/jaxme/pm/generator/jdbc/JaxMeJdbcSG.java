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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.AttributeSGChain;
import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGFactoryChain;
import org.apache.ws.jaxme.generator.sg.SchemaSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSGChain;
import org.apache.ws.jaxme.generator.sg.impl.AttributeSGImpl;
import org.apache.ws.jaxme.generator.sg.impl.JaxMeSchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.SGFactoryChainImpl;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.SQLFactoryImpl;
import org.apache.ws.jaxme.util.ClassLoader;
import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.parser.XsObjectCreator;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.types.XSBase64Binary;
import org.apache.ws.jaxme.xs.types.XSBoolean;
import org.apache.ws.jaxme.xs.types.XSByte;
import org.apache.ws.jaxme.xs.types.XSDate;
import org.apache.ws.jaxme.xs.types.XSDateTime;
import org.apache.ws.jaxme.xs.types.XSDouble;
import org.apache.ws.jaxme.xs.types.XSFloat;
import org.apache.ws.jaxme.xs.types.XSInt;
import org.apache.ws.jaxme.xs.types.XSShort;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.types.XSTime;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>A schema writer for creation of an object relational
 * mapping.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JaxMeJdbcSG extends SGFactoryChainImpl {
  Logger log = LoggerAccess.getLogger(JaxMeJdbcSG.class);

  /** A database mode specifies, how JDBC metadata is
   * being interpreted. The main use is for Oracle, which
   * has a rather peculiar understanding of JDBC metadata.
   */
  public static class Mode {
    private String name;
    private Mode(String pName) { name = pName; }
    public String toString() { return name; }
    /** Returns the modes name.
     */
    public String getName() { return name; }
    public int hashCode() { return name.hashCode(); }
    public boolean equals(Object o) {
      if (o == null  ||  !(o instanceof Mode)) {
        return false;
      } else {
        return name.equals(((Mode) o).name);
      }
    }
    /** Returns an instance of <code>Mode</code> with
     * the given name.
     * @throws IllegalArgumentException The mode name is invalid.
     */
    public static Mode valueOf(String pMode) {
      if ("GENERIC".equalsIgnoreCase(pMode)) {
        return GENERIC;
      } else if ("ORACLE".equalsIgnoreCase(pMode)) {
        return ORACLE;
      } else {
        throw new IllegalArgumentException("Valid database modes are either of 'generic' or 'oracle', not " + pMode);
      }
    }

    /** <p>Default database mode; types are taken as reported by the JDBC
     * driver.</p>
     */
    public static final Mode GENERIC = new Mode("Generic");
    /** <p>Oracle database mode; the type NUMERIC is interpreted as FLOAT,
     * TINYINT, SMALLINT, INTEGER, BIGINT, or DOUBLE, depending on scale
     * and precision. This mode is turned on by setting the option
     * <code>jdbc.dbmode</code> or if the method
     * {@link java.sql.DatabaseMetaData#getDatabaseProductName()}
     * returns the value "Oracle".</p>
     */
    public static final Mode ORACLE = new Mode("Oracle");
  }

  private class JdbcAttribute implements XSAttribute {
    private final Locator locator;
    private final XsQName name;
    private final XSType type;
    private final boolean isOptional;
    private final XSObject parent;

    /** An attribute, which matches a database column.
     */
    public JdbcAttribute(XSObject pParent, Locator pLocator, XsQName pName, XSType pType, boolean pOptional) {
      parent = pParent;
      locator = pLocator;
      name = pName;
      type = pType;
      isOptional = pOptional;
    }

    public boolean isGlobal() { return false; }
    /** For compatibility only: The value true is unsupported.
     * @throws IllegalStateException An attempt was made, to make the
     * attribute public.
     */
    public void setGlobal(boolean pGlobal) {
      if (!pGlobal) {
        throw new IllegalStateException("This attribute cannot be made global");
      }
    }
    public XsQName getName() { return name; }
    public XSType getType() { return type; }
    public boolean isOptional() { return isOptional; }
    public XSAnnotation[] getAnnotations() { return new XSAnnotation[0]; }
    public Locator getLocator() { return locator; }
    public void validate() throws SAXException {}
    public boolean isTopLevelObject() { return false; }
    public XSObject getParentObject() { return parent; }
    public XSSchema getXSSchema() { return parent.getXSSchema(); }

    public String getDefault() { return null; }
    public String getFixed() { return null; }
    public Attributes getOpenAttributes() { return null; }
  }

  /** <p>Namespace URI of the JDBC schema writer.</p>
   */
  public static final String JAXME_JDBC_SCHEMA_URI = "http://ws.apache.org/jaxme/namespaces/jaxme2/jdbc-mapping";

  private SGFactory sgFactory;
  private SQLFactory sqlFactory;
  private String key;

  /** Creates a new instance.
   */
  public JaxMeJdbcSG(SGFactoryChain o) {
    super(o);
  }

  /** Returns the key, under which the {@link JaxMeJdbcSG} is
   * registered in the factory.
   */
  public String getKey() {
    return key;
  }

  public void init(SGFactory pFactory) {
    super.init(pFactory);
    sgFactory = pFactory;
    SchemaReader schemaReader = pFactory.getGenerator().getSchemaReader();
    if (schemaReader instanceof JaxMeSchemaReader) {
      JaxMeSchemaReader jaxmeSchemaReader = (JaxMeSchemaReader) schemaReader;
      jaxmeSchemaReader.addXsObjectCreator(new XsObjectCreator(){
        public XsObject newBean(XsObject pParent, Locator pLocator, XsQName pQName) throws SAXException {
            if (pParent instanceof XsEAppinfo) {
	          if (JAXME_JDBC_SCHEMA_URI.equals(pQName.getNamespaceURI())) {
	            if ("table".equals(pQName.getLocalName())) {
	              return new TableDetails(JaxMeJdbcSG.this, pParent);
	            } else if ("connection".equals(pQName.getLocalName())) {
	              return new ConnectionDetails(JaxMeJdbcSG.this, pParent);
	            } else {
	              throw new LocSAXException("Invalid element name: " + pQName, pLocator);
	            }
	          }
            }
            return null;
          }
      });
    } else {
      throw new IllegalStateException("The schema reader " + schemaReader.getClass().getName() +
                                       " is not an instance of " + JaxMeSchemaReader.class.getName());
    }

    String s = schemaReader.getGenerator().getProperty("jdbc.sqlFactory");
    if (s == null) {
      sqlFactory = new SQLFactoryImpl();
    } else {
      Class c;
      try {
        c = ClassLoader.getClass(s, SQLFactory.class);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("SQLFactory class " + s + ", specified by property jdbc.sqlFactory, not found.");
      }
      try {
        sqlFactory = (SQLFactory) c.newInstance();
      } catch (InstantiationException e) {
        throw new IllegalStateException("Unable to instantiate SQLFactory class " + c.getName());
      } catch (IllegalAccessException e) {
        throw new IllegalStateException("Illegal access to the default constructor of SQLFactory class " + c.getName());
      }
    }

    key = pFactory.getGenerator().getKey();
  }

  protected SGFactory getSGFactory() {
    return sgFactory;
  }

  public Generator getGenerator(SGFactory pFactory) {
    return super.getGenerator(pFactory);
  }

  protected Mode getDatabaseMode(ConnectionDetails pDetails,
                                  Connection pConn) throws SQLException {
    if (pDetails == null) {
      String v = pConn.getMetaData().getDatabaseProductName();
      try {
        return Mode.valueOf(v);
      } catch (Exception e) {
        return Mode.GENERIC;
      }
    } else {
      return pDetails.getDbMode();
    }
  }

  /** <p>Guess an SQL type, based on reported type, scale and
   * precision.</p>
   */
  protected int getDbType(Mode pDbMode, int pDbType, long pScale,
                            long pPrecision, String pDbTypeName) {
    if (Mode.GENERIC.equals(pDbMode)) {
      return pDbType;
    } else if (pDbType == Types.OTHER) {
      if ("CLOB".equalsIgnoreCase(pDbTypeName)) {
        return Types.CLOB;
      } else if ("BLOB".equalsIgnoreCase(pDbTypeName)) {
        return Types.BLOB;
      } else {
        return Types.OTHER;
      }
    } else if (pDbType == Types.NUMERIC  ||  "NUMBER".equalsIgnoreCase(pDbTypeName)) {
      if (pScale == 0) {
        if (pPrecision == 0) { return Types.FLOAT; }
        if (pPrecision <= 2) { return Types.TINYINT; }
        if (pPrecision <= 4) { return Types.SMALLINT; }
        if (pPrecision <= 9) { return Types.INTEGER; }
        return Types.BIGINT;
      } else if (pScale == -127) {
        // Uses binary precision - See page 4-37 of the OCI book
        if (pPrecision < 24) {
          return Types.FLOAT;  // 53 is double cutoff
        }
      } else {
        // Uses decimal precision - See page 4-37 of the OCI book
        if (pPrecision < 8) {
          return Types.FLOAT;   // 15 is double cutoff
        }
      }
    } else if (pDbType != Types.NUMERIC) {
      return pDbType;
    }
    return Types.DOUBLE;
  }

  /** <p>Creates a new attribute or chooses an existing atomic
   * child element. Returns the {@link AttributeSG} or
   * {@link ParticleSG}.</p>
   */
  protected Object addColumn(ComplexTypeSG pTypeSG, XSType pType, Column pColumn) throws SAXException {
    final String mName = "addColumn";
    log.entering(mName, pColumn.getQName());
    /*  May be there already is an attribute or child element
     * with default settings?
     */
    List allChilds = new ArrayList();
    AttributeSG[] attributes = pTypeSG.getAttributes();
    if (attributes != null) {
      allChilds.addAll(Arrays.asList(attributes));
    }
    if (!pTypeSG.hasSimpleContent()) {
      ComplexContentSG cct = pTypeSG.getComplexContentSG();
      GroupSG groupSG = cct.getRootParticle().getGroupSG();
      ParticleSG[] childs = groupSG.getParticles();
      if (childs != null) {
        for (int i = 0;  i < childs.length;  i++) {
          ParticleSG child = childs[i];
          // Accept only simple elements
          if (child.isElement()) {
            ObjectSG objectSG = child.getObjectSG();
            if (!objectSG.getTypeSG().isComplex()) {
              allChilds.add(child);
            }
          }
        }
      }
    }

    Object theChild = null;
    for (Iterator iter = allChilds.iterator();  iter.hasNext();  ) {
      Object currentChild = iter.next();
      String localName;
      if (currentChild instanceof AttributeSG) {
        localName = ((AttributeSG) currentChild).getName().getLocalName();
      } else if (currentChild instanceof ParticleSG) {
        localName = ((ParticleSG) currentChild).getObjectSG().getName().getLocalName();
      } else {
        throw new IllegalStateException("Expected either attribute or element.");
      }
      if (localName != null  &&  localName.equalsIgnoreCase(pColumn.getName().getName())) {
        if (theChild == null) {
          theChild = currentChild;
        } else {
          log.warn(mName, "Multiple matching attributes or child elements found for column " +
                   pColumn.getQName());
        }
      }
    }

    if (theChild == null) {
      // We have to create a new attribute matching the column
      XSType xsType;
      if (pColumn.isBinaryColumn()) {
        xsType = XSBase64Binary.getInstance();
      } else if (pColumn.isStringColumn()) {
        xsType = XSString.getInstance();
      } else {
        Column.Type myType = pColumn.getType();
        if (Column.Type.BIT.equals(myType)) {
          xsType = XSBoolean.getInstance();
        } else if (Column.Type.DATE.equals(myType)) {
          xsType = XSDate.getInstance();
        } else if (Column.Type.DOUBLE.equals(myType)) {
          xsType = XSDouble.getInstance();
        } else if (Column.Type.FLOAT.equals(myType)) {
          xsType = XSFloat.getInstance();
        } else if (Column.Type.SMALLINT.equals(myType)) {
          xsType = XSShort.getInstance();
        } else if (Column.Type.TIME.equals(myType)) {
          xsType = XSTime.getInstance();
        } else if (Column.Type.TIMESTAMP.equals(myType)) {
          xsType = XSDateTime.getInstance();
        } else if (Column.Type.TINYINT.equals(myType)) {
          xsType = XSByte.getInstance();
        } else if (Column.Type.INTEGER.equals(myType)) {
          xsType = XSInt.getInstance();
        } else {
          throw new IllegalStateException("Unknown column type: " + myType);
        }
      }

      XSAttribute attr = new JdbcAttribute(pType, pType.getLocator(),
                                           new XsQName((String) null, pColumn.getName().getName()), xsType,
                                           pColumn.isNullable());
      AttributeSGChain chain = (AttributeSGChain) pTypeSG.newAttributeSG(attr);
      AttributeSG attributeSG = new AttributeSGImpl(chain);
      pTypeSG.addAttributeSG(attributeSG);
      theChild = attributeSG;
    }

    return theChild;
  }

  /** <p>We use the interface Connector in order to allow people use of this
   * class, even if they don't have the javax.sql package around.</p>
   */
  private interface Connector {
  	/** Returns a new database connection.
  	 */
    public Connection getConnection(TableDetails pDetails) throws SAXException;
  }

  private class DriverManagerConnector implements Connector {
    public Connection getConnection(TableDetails pTableDetails) throws SAXException {
      final String mName = "DriverManagerConnector.getConnection";
      Class c = null;
      Exception ex = null;
      log.fine(mName, "Loading driver " + pTableDetails.getDriver());
      try {
        Class.forName(pTableDetails.getDriver());
      } catch (Exception e) {
      }
      if (c == null) {
        try {
          java.lang.ClassLoader cl = Thread.currentThread().getContextClassLoader();
          if (cl == null) {
            cl = getClass().getClassLoader(); 
          }
          c = cl.loadClass(pTableDetails.getDriver());
        } catch (Exception e) {
          if (ex == null) { ex = e; }
        }
      }
      if (c == null) {
        if (ex == null) { ex = new ClassNotFoundException(); }
        throw new LocSAXException("Unable to load driver class " + pTableDetails.getDriver()
                                   + ": " + ex.getClass().getName() + ", " + ex.getMessage(),
                                   pTableDetails.getLocator());
      }
      log.fine(mName, "Connecting to database " + pTableDetails.getUrl() + " as " + pTableDetails.getUser());
      try {
        Connection conn = DriverManager.getConnection(pTableDetails.getUrl(), pTableDetails.getUser(),
                                                      pTableDetails.getPassword());
        if (conn == null) {
          throw new LocSAXException("Unable to connect to " + pTableDetails.getUrl()
                                     + " as user " + pTableDetails.getUser()
                                     + ": DriverManager returned a null connection",
                                     pTableDetails.getLocator());
        }
        return conn;
      } catch (SQLException e) {
        throw new LocSAXException("Unable to connect to " + pTableDetails.getUrl()
                                   + " as user " + pTableDetails.getUser()
                                   + ": SQL State = " + e.getSQLState() + ", error code = "
                                   + e.getErrorCode() + ", " + e.getMessage(),
                                   pTableDetails.getLocator(), e);
      }
    }
  }

  private class DatasourceConnector implements Connector {
    public Connection getConnection(TableDetails pTableDetails) throws SAXException {
      javax.naming.InitialContext ic;
      try {
        ic = new javax.naming.InitialContext();
      } catch (NamingException e) {
        throw new LocSAXException("Failed to create initial JNDI context: "
                                   + e.getClass().getName() + ", " + e.getMessage(),
                                   pTableDetails.getLocator(), e);
      }
      javax.sql.DataSource ds;
      try {
        ds = (javax.sql.DataSource) ic.lookup(pTableDetails.getDatasource());
      } catch (NamingException e) {
        throw new LocSAXException("Failed to lookup datasource " + pTableDetails.getDatasource()
                                   + ": " + e.getClass().getName() + ", " + e.getMessage(),
                                   pTableDetails.getLocator(), e);
      }
      try {
        Connection conn = ds.getConnection(pTableDetails.getUser(), pTableDetails.getPassword());
        if (conn == null) {
          throw new LocSAXException("Unable to connect to " + pTableDetails.getUrl()
                                     + " as user " + pTableDetails.getUser()
                                     + ": Datasource returned a null connection",
                                     pTableDetails.getLocator());
        }
        return conn;
      } catch (SQLException e) {
        throw new LocSAXException("Unable to connect to datasource " + pTableDetails.getUrl()
                                   + " as user " + pTableDetails.getUser()
                                   + ": SQL State = " + e.getSQLState() + ", error code = "
                                   + e.getErrorCode() + ", " + e.getMessage(),
                                   pTableDetails.getLocator(), e);
      }
    }
  }

  protected CustomTableData addTableData(ComplexTypeSG pTypeSG, XSType pType,
                                          TableDetails pTableDetails) throws SAXException {
    final String mName = "addTableData";
    log.entering(mName, new Object[]{pTypeSG, pTableDetails});
    String tableName = pTableDetails.getName();

    Connection conn;
    if (pTableDetails.getDriver() == null) {
      conn = new DatasourceConnector().getConnection(pTableDetails);
    } else {
      conn = new DriverManagerConnector().getConnection(pTableDetails);
    }

    try {
      int offset = tableName.indexOf('.');
      String schemaName;
      if (offset > 0) {
        schemaName = tableName.substring(0, offset);
        tableName = tableName.substring(offset+1);
      } else {
        schemaName = null;
      }
      Table table;
      try {
        table = sqlFactory.getTable(conn, schemaName, tableName);
        conn.close();
        conn = null;
      } catch (SQLException e) {
        throw new SAXException("Failed to read table " + pTableDetails.getName() + ": " + e.getMessage(), e);
      }
      if (table.getPrimaryKey() == null) {
        throw new IllegalStateException("The table " + table.getQName() + " does not have a primary key.");
      }
      CustomTableData customTableData = new CustomTableData(this, table, pTypeSG, pTableDetails);
      for (Iterator iter = table.getColumns();  iter.hasNext();  ) {
        Column col = (Column) iter.next();
        Object sg = addColumn(pTypeSG, pType, col);
        CustomColumnData colData = new CustomColumnData(col.getName().getName(), sg);
        col.setCustomData(colData);
      }

      return customTableData;
    } finally {
      if (conn != null) { try { conn.close(); } catch (Throwable ignore) {} }
    }
  }

  public Object newTypeSG(SGFactory pController, XSType pType, JAXBProperty.BaseType pBaseType) throws SAXException {
    return new JdbcTypeSG(this, (TypeSGChain) super.newTypeSG(pController, pType, pBaseType), pType);
  }

  public Object newTypeSG(SGFactory pController, XSType pType, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException {
    return new JdbcTypeSG(this, (TypeSGChain) super.newTypeSG(pController, pType, pName, pBaseType), pType);
  }

  public Object newTypeSG(SGFactory pController, XSType pType, Context pClassContext, XsQName pName, JAXBProperty.BaseType pBaseType) throws SAXException {
    return new JdbcTypeSG(this, (TypeSGChain) super.newTypeSG(pController, pType, pClassContext, pName, pBaseType), pType);
  }

  public Object newSchemaSG(SGFactory pController, XSSchema pSchema) throws SAXException {
    SchemaSGChain chain = (SchemaSGChain) super.newSchemaSG(pController, pSchema);
    chain = new JdbcSchemaSG(this, chain);
    return chain;
  }
}
