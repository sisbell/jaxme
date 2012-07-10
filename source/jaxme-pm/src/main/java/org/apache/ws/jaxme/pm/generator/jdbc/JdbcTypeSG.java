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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.Observer;
import org.apache.ws.jaxme.PMException;
import org.apache.ws.jaxme.PMParams;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSGChain;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.TypeSGChain;
import org.apache.ws.jaxme.generator.sg.impl.TypeSGChainImpl;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.pm.jdbc.*;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JdbcTypeSG extends TypeSGChainImpl {
  private final JaxMeJdbcSG jdbcSG;
  private XSType xsType;

  protected JdbcTypeSG(JaxMeJdbcSG pJdbcSG, TypeSGChain o, XSType pType) {
    super(o);
    this.jdbcSG = pJdbcSG;
    if (pType == null) {
      throw new NullPointerException("The XSType argument must not be null.");
    }
    xsType = pType;
  }

  public Object newComplexTypeSG(TypeSG pController) throws SAXException {
    if (xsType == null) {
      throw new IllegalStateException("An instance of ComplexTypeSGChain has already been created.");
    }
    ComplexTypeSGChain chain = (ComplexTypeSGChain) super.newComplexTypeSG(pController);
    chain = new JdbcComplexTypeSG(jdbcSG, chain, xsType);
    xsType = null; // Make this available for garbage collection
    return chain;
  }

  public void generate(TypeSG pController) throws SAXException {
    super.generate(pController);
    if (!pController.isComplex()) {
      return;
    }
    CustomTableData customTableData = (CustomTableData) pController.getProperty(jdbcSG.getKey());
    if (customTableData != null) {
      JavaQName qName = pController.getComplexTypeSG().getClassContext().getPMName();
      JavaSource js = pController.getSchema().getJavaSourceFactory().newJavaSource(qName, JavaSource.PUBLIC);
      getPMClass(pController, js, customTableData);
    }
  }

  public void generate(TypeSG pController, JavaSource pSource) throws SAXException {
    super.generate(pController, pSource);
    if (!pController.isComplex()) {
      return;
    }
    CustomTableData customTableData = (CustomTableData) pController.getProperty(jdbcSG.getKey());
    if (customTableData != null) {
      JavaQName qName = pController.getComplexTypeSG().getClassContext().getPMName();
      JavaInnerClass jic = pSource.newJavaInnerClass(qName.getClassName());
      getPMClass(pController, jic, customTableData);
    }
  }

  /** <p>Generates code for setting a PreparedStatement's parameter.</p>
   */
  protected void setPreparedStatementValue(JavaMethod pMethod, Column pColumn,
                                             Object pStmt, Object pParamNum,
                                             Object pValue, TypeSG pTypeSG)
      throws SAXException {
    boolean isPrimitive = pTypeSG.getSimpleTypeSG().getRuntimeType().isPrimitive();
    Column.Type type = pColumn.getType();
    if (isPrimitive) {
      if (Column.Type.BIGINT.equals(type)) {
        pMethod.addLine(pStmt, ".setLong(", pParamNum, ", ", pValue, ");");
      } else if (Column.Type.BIT.equals(type)) {
        pMethod.addLine(pStmt, ".setBoolean(", pParamNum, ", ", pValue, ");");
      } else if (Column.Type.DOUBLE.equals(type)) {
        pMethod.addLine(pStmt, ".setDouble(", pParamNum, ", ", pValue, ");");
      } else if (Column.Type.FLOAT.equals(type)) {
        pMethod.addLine(pStmt, ".setFloat(", pParamNum, ", ", pValue, ");");
      } else if (Column.Type.INTEGER.equals(type)) {
        pMethod.addLine(pStmt, ".setInt(", pParamNum, ", ", pValue, ");");
      } else if (Column.Type.SMALLINT.equals(type)) {
        pMethod.addLine(pStmt, ".setShort(", pParamNum, ", ", pValue, ");");
      } else if (Column.Type.TINYINT.equals(type)) {
        pMethod.addLine(pStmt, ".setByte(", pParamNum, ", ", pValue, ");");
      }
    } else {
      if (!(pValue instanceof DirectAccessible)) {
         LocalJavaField v = pMethod.newJavaField(pTypeSG.getRuntimeType());
         v.addLine(pValue);
         pValue = v;
      }
      pMethod.addIf(pValue, " == null");
      pMethod.addLine(pStmt, ".setNull(", pParamNum, ", ", Types.class, ".", type, ");");
      pMethod.addElse();
      if (Column.Type.BIGINT.equals(type)) {
        pMethod.addLine(pStmt, ".setLong(", pParamNum, ", ", pValue, ".longValue());");
      } else if (pColumn.isBinaryColumn()) {
        pMethod.addLine(pStmt, ".setBytes(", pParamNum, ", ", pValue, ");");
      } else if (pColumn.isStringColumn()) {
        pMethod.addLine(pStmt, ".setString(", pParamNum, ", ", pValue, ");");
      } else if (Column.Type.DATE.equals(type)) {
        pMethod.addLine(pStmt, ".setDate(", pParamNum, ", new ", java.sql.Date.class,
                        "(", pValue, ".getTime().getTime()));");
      } else if (Column.Type.DOUBLE.equals(type)) {
        pMethod.addLine(pStmt, ".setDouble(", pParamNum, ", ", pValue, ".doubleValue());");
      } else if (Column.Type.FLOAT.equals(type)) {
        pMethod.addLine(pStmt, ".setFloat(", pParamNum, ", ", pValue, ".floatValue());");
      } else if (Column.Type.INTEGER.equals(type)) {
        pMethod.addLine(pStmt, ".setInt(", pParamNum, ", ", pValue, ".intValue());");
      } else if (Column.Type.SMALLINT.equals(type)) {
        pMethod.addLine(pStmt, ".setShort(", pParamNum, ", ", pValue, ".shortValue());");
      } else if (Column.Type.TIME.equals(type)) {
        pMethod.addLine(pStmt, ".setTime(", pParamNum, ", new ", java.sql.Date.class,
                        "(", pValue, ".getTime().getTime()));");
      } else if (Column.Type.TIMESTAMP.equals(type)) {
        pMethod.addLine(pStmt, ".setTimestamp(", pParamNum, ", new ", Timestamp.class,
                        "(", pValue, ".getTime().getTime()));");
      } else if (Column.Type.TINYINT.equals(type)) {
        pMethod.addLine(pStmt, ".setByte(", pParamNum, ", ", pValue, ".byteValue());");
      } else {
        throw new IllegalStateException("Unknown column type: " + type);
      }
      pMethod.addEndIf();
    }
  }

  /** <p>Generates code for fetching a value from a {@link ResultSet}.</p>
   */
  protected Object getResultSetValue(JavaMethod pMethod, Column pColumn,
                                      Object pRs, Object pParamNum,
                                      TypeSG pTypeSG)
      throws SAXException {
    boolean isPrimitive = pTypeSG.getSimpleTypeSG().getRuntimeType().isPrimitive();
    Column.Type type = pColumn.getType();
    if (isPrimitive) {
      if (Column.Type.BIGINT.equals(type)) {
        return new Object[]{pRs, ".getLong(", pParamNum, ")"};
      } else if (Column.Type.BIT.equals(type)) {
        return new Object[]{pRs, ".getBoolean(", pParamNum, ")"};
      } else if (Column.Type.DOUBLE.equals(type)) {
        return new Object[]{pRs, ".getDouble(", pParamNum, ")"};
      } else if (Column.Type.FLOAT.equals(type)) {
        return new Object[]{pRs, ".getFloat(", pParamNum, ")"};
      } else if (Column.Type.INTEGER.equals(type)) {
        return new Object[]{pRs, ".getInt(", pParamNum, ")"};
      } else if (Column.Type.SMALLINT.equals(type)) {
        return new Object[]{pRs, ".getShort(", pParamNum, ")"};
      } else if (Column.Type.TINYINT.equals(type)) {
        return new Object[]{pRs, ".getByte(", pParamNum, ")"};
      } else {
        throw new IllegalStateException("Unknown primitive type: " + type);
      }
    } else {
      if (pColumn.isStringColumn()) {
        return new Object[]{pRs, ".getString(", pParamNum, ")"};
      } else if (pColumn.isBinaryColumn()) {
        return new Object[]{pRs, ".getBytes(", pParamNum, ")"};
      } else if (Column.Type.DATE.equals(type)) {
        LocalJavaField cal = pMethod.newJavaField(Calendar.class);
        LocalJavaField date = pMethod.newJavaField(Date.class);
        date.addLine(pRs, ".getDate(", pParamNum, ")");
        pMethod.addIf(date, " == null");
        pMethod.addLine(cal, " = null;");
        pMethod.addElse();
        pMethod.addLine(cal, " = ", Calendar.class, ".getInstance();");
        pMethod.addLine(cal, ".setTime(", date, ");");
        pMethod.addLine(cal, ".set(", Calendar.class, ".HOUR, 0);");
        pMethod.addLine(cal, ".set(", Calendar.class, ".MINUTE, 0);");
        pMethod.addLine(cal, ".set(", Calendar.class, ".SECOND, 0);");
        pMethod.addLine(cal, ".set(", Calendar.class, ".MILLISECOND, 0);");
        pMethod.addEndIf();
        return cal;
      } else if (Column.Type.TIME.equals(type)) {
        LocalJavaField cal = pMethod.newJavaField(Calendar.class);
        LocalJavaField date = pMethod.newJavaField(Time.class);
        date.addLine(pRs, ".getTime(", pParamNum, ")");
        pMethod.addIf(date, " == null");
        pMethod.addLine(cal, " = null;");
        pMethod.addElse();
        pMethod.addLine(cal, " = ", Calendar.class, ".getInstance();");
        pMethod.addLine(cal, ".setTime(", date, ");");
        pMethod.addLine(cal, ".set(", Calendar.class, ".YEAR, 0);");
        pMethod.addLine(cal, ".set(", Calendar.class, ".MONTH, 0);");
        pMethod.addLine(cal, ".set(", Calendar.class, ".DAY_OF_MONTH, 0);");
        pMethod.addEndIf();
        return cal;
      } else if (Column.Type.TIMESTAMP.equals(type)) {
        LocalJavaField cal = pMethod.newJavaField(Calendar.class);
        LocalJavaField date = pMethod.newJavaField(Timestamp.class);
        date.addLine(pRs, ".getTimestamp(", pParamNum, ")");
        pMethod.addIf(date, " == null");
        pMethod.addLine(cal, " = null;");
        pMethod.addElse();
        pMethod.addLine(cal, " = ", Calendar.class, ".getInstance();");
        pMethod.addLine(cal, ".setTime(", date, ");");
        pMethod.addEndIf();
        return cal;
      } else if (Column.Type.BIT.equals(type)) {
        LocalJavaField b = pMethod.newJavaField(boolean.class);
        b.addLine(pRs, ".getBoolean(", pParamNum, ")");
        return new Object[]{"(", pRs, ".wasNull() ? null : ",
                               b, " ? ", Boolean.class, ".TRUE : ", Boolean.class, ".FALSE)"};
      } else if (Column.Type.BIGINT.equals(type)) {
        LocalJavaField l = pMethod.newJavaField(long.class);
        l.addLine(pRs, ".getLong(", pParamNum, ")");
        return new Object[]{"(", pRs, ".wasNull() ? null : new Long(", l, ")"};
      } else if (Column.Type.DOUBLE.equals(type)) {
        LocalJavaField d = pMethod.newJavaField(double.class);
        d.addLine(pRs, ".getDouble(", pParamNum, ")");
        return new Object[]{"(", pRs, ".wasNull() ? null : new Double(", d, ")"};
      } else if (Column.Type.FLOAT.equals(type)) {
        LocalJavaField f = pMethod.newJavaField(float.class);
        f.addLine(pRs, ".getFloat(", pParamNum, ")");
        return new Object[]{"(", pRs, ".wasNull() ? null : new Float(", f, ")"};
      } else if (Column.Type.INTEGER.equals(type)) {
        LocalJavaField i = pMethod.newJavaField(int.class);
        i.addLine(pRs, ".getInt(", pParamNum, ")");
        return new Object[]{"(", pRs, ".wasNull() ? null : new Integer(", i, ")"};
      } else if (Column.Type.SMALLINT.equals(type)) {
        LocalJavaField s = pMethod.newJavaField(short.class);
        s.addLine(pRs, ".getShort(", pParamNum, ")");
        return new Object[]{"(", pRs, ".wasNull() ? null : new Short(", s, ")"};
      } else if (Column.Type.TINYINT.equals(type)) {
        LocalJavaField b = pMethod.newJavaField(byte.class);
        b.addLine(pRs, ".getByte(", pParamNum, ")");
        return new Object[]{"(", pRs, ".wasNull() ? null : new Byte(", b, ")"};
      } else {
        throw new IllegalStateException("Unknown column type: " + type);
      }
    }
  }

  protected void getFinally(JavaMethod pMethod, DirectAccessible pRessource, Object pSqlMsg,
                              Object pJaxbMsg) {
    pMethod.addLine(pRessource, ".close();");
    pMethod.addLine(pRessource, " = null;");
    if (pSqlMsg != null) {
      DirectAccessible e = pMethod.addCatch(SQLException.class);
      pMethod.addThrowNew(PMException.class, pSqlMsg, " + ", JavaSource.getQuoted(": "),
                          " + ", e, ".getMessage(), ", e);
    }
    if (pJaxbMsg != null) {
      DirectAccessible e = pMethod.addCatch(JAXBException.class);
      pMethod.addIf(e, " instanceof ", PMException.class);
      pMethod.addLine("throw (", PMException.class, ") ", e, ";");
      pMethod.addElse();
      pMethod.addThrowNew(PMException.class, pSqlMsg, " + ", JavaSource.getQuoted(": "),
                          " + ", e, ".getMessage(), ", e);
      pMethod.addEndIf();
    }
    pMethod.addFinally();
    pMethod.addIf(pRessource, " != null");
    pMethod.addTry();
    pMethod.addLine(pRessource, ".close();");
    pMethod.addCatch(Throwable.class);
    pMethod.addEndTry();
    pMethod.addEndIf();
    pMethod.addEndTry();
  }

  private int getPreparedStatementParameters(JavaMethod pMethod, Object pStmt, DirectAccessible pElement,
																						 Iterator pColumns, int pParamNum)
      throws SAXException {
    for (Iterator iter = pColumns;  iter.hasNext();  ) {
      Column col = (Column) iter.next();
      CustomColumnData colData = (CustomColumnData) col.getCustomData();
      Object sg = colData.getSG();
      PropertySG propertySG;
      TypeSG typeSG;
      if (sg instanceof AttributeSG) {
        AttributeSG attrSG = (AttributeSG) sg;
        propertySG = attrSG.getPropertySG();
        typeSG = attrSG.getTypeSG();
      } else if (sg instanceof ParticleSG) {
        ParticleSG particleSG = (ParticleSG) sg;
        propertySG = particleSG.getPropertySG();
        typeSG = particleSG.getObjectSG().getTypeSG();
      } else {
        throw new IllegalStateException("Invalid SG type for column " + col.getName() + ": " + sg);
      }
      Object value = propertySG.getValue(pElement);
      setPreparedStatementValue(pMethod, col, pStmt, new Integer(++pParamNum), value, typeSG);
    }
    return pParamNum;
  }

  private int getResultSet(JavaMethod pMethod, DirectAccessible pRs, DirectAccessible pElement,
                            Iterator pColumns, int pParamNum)
      throws SAXException {
    for (Iterator iter = pColumns;  iter.hasNext();  ) {
      Column col = (Column) iter.next();
      CustomColumnData colData = (CustomColumnData) col.getCustomData();
      Object sg = colData.getSG();
      PropertySG propertySG;
      TypeSG typeSG;
      if (sg instanceof AttributeSG) {
        AttributeSG attrSG = (AttributeSG) sg;
        propertySG = attrSG.getPropertySG();
        typeSG = attrSG.getTypeSG();
      } else if (sg instanceof ParticleSG) {
        ParticleSG particleSG = (ParticleSG) sg;
        propertySG = particleSG.getPropertySG();
        typeSG = particleSG.getObjectSG().getTypeSG();
      } else {
        throw new IllegalStateException("Invalid SG type for column " + col.getName() + ": " + sg);
      }
      Object value = getResultSetValue(pMethod, col, pRs, new Integer(++pParamNum), typeSG);
      propertySG.setValue(pMethod, pElement, value, null);
    }
    return pParamNum;
  }

  protected JavaMethod getPMClassInsertMethod(TypeSG pController, JavaSource pSource, CustomTableData pData)
      throws SAXException {
    JavaMethod jm = pSource.newJavaMethod("insert", JavaQNameImpl.VOID, JavaSource.PUBLIC);
    Parameter pElement = jm.addParam(Element.class, "pElement");
    jm.addThrows(PMException.class);
    Table table = pData.getTable();

    String q = table.getSchema().getSQLFactory().newSQLGenerator().getQuery(table.getInsertStatement());
    LocalJavaField query = jm.newJavaField(String.class);
    query.setFinal(true);
    query.addLine(JavaSource.getQuoted(q));

    JavaQName qName = pController.getComplexTypeSG().getClassContext().getXMLInterfaceName();
    LocalJavaField elem = jm.newJavaField(qName);
    elem.addLine("(", qName, ") ", pElement);

    LocalJavaField connection = jm.newJavaField(Connection.class);
    connection.addLine("null");
    jm.addTry();
    jm.addLine(connection, " = getConnection();");
    LocalJavaField stmt = jm.newJavaField(PreparedStatement.class);
    stmt.addLine(connection, ".prepareStatement(", query, ")");
    jm.addTry();
    getPreparedStatementParameters(jm, stmt, elem, table.getColumns(), 0);
    jm.addLine(stmt, ".executeUpdate();");
    getFinally(jm, stmt, null, null);
    getFinally(jm, connection, new Object[]{JavaSource.getQuoted("Failed to execute query "),
                                            " + ", query}, null);
    return jm;
  }

  protected JavaMethod getPMClassUpdateMethod(TypeSG pController, JavaSource pSource, CustomTableData pData)
      throws SAXException {
    JavaMethod jm = pSource.newJavaMethod("update", JavaQNameImpl.VOID, JavaSource.PUBLIC);
    Parameter pElement = jm.addParam(Element.class, "pElement");
    jm.addThrows(PMException.class);
    Table table = pData.getTable();

    String q = table.getSchema().getSQLFactory().newSQLGenerator().getQuery(table.getUpdateStatement());
    LocalJavaField query = jm.newJavaField(String.class);
    query.setFinal(true);
    query.addLine(JavaSource.getQuoted(q));

    JavaQName qName = pController.getComplexTypeSG().getClassContext().getXMLInterfaceName();
    LocalJavaField elem = jm.newJavaField(qName);
    elem.addLine("(", qName, ") ", pElement);

    LocalJavaField connection = jm.newJavaField(Connection.class);
    connection.addLine("null");
    jm.addTry();
    jm.addLine(connection, " = getConnection();");

    LocalJavaField stmt = jm.newJavaField(PreparedStatement.class);
    stmt.addLine(connection, ".prepareStatement(", query, ")");
    jm.addTry();

    List nonKeyColumns = new ArrayList();
    for (Iterator iter = table.getColumns();  iter.hasNext();  ) {
      Column col = (Column) iter.next();
      if (!col.isPrimaryKeyPart()) {
        nonKeyColumns.add(col);
      }
    }
    int i = 0;
    i = getPreparedStatementParameters(jm, stmt, elem, nonKeyColumns.iterator(), i);
    getPreparedStatementParameters(jm, stmt, elem, table.getPrimaryKey().getColumns(), i);
    jm.addLine(stmt, ".executeUpdate();");

    getFinally(jm, stmt, null, null);
    getFinally(jm, connection, new Object[]{JavaSource.getQuoted("Failed to execute query "),
                                            " + ", query}, null);
    return jm;
  }

  protected JavaMethod getPMClassDeleteMethod(TypeSG pController, JavaSource pSource, CustomTableData pData)
      throws SAXException {
    JavaMethod jm = pSource.newJavaMethod("delete", JavaQNameImpl.VOID, JavaSource.PUBLIC);
    Parameter pElement = jm.addParam(Element.class, "pElement");
    jm.addThrows(PMException.class);
    Table table = pData.getTable();

    JavaQName qName = pController.getComplexTypeSG().getClassContext().getXMLInterfaceName();
    LocalJavaField elem = jm.newJavaField(qName);
    elem.addLine("(", qName, ") ", pElement);

    String q = table.getSchema().getSQLFactory().newSQLGenerator().getQuery(table.getDeleteStatement());    
    LocalJavaField query = jm.newJavaField(String.class);
    query.setFinal(true);
    query.addLine(JavaSource.getQuoted(q));

    LocalJavaField connection = jm.newJavaField(Connection.class);
    connection.addLine("null");
    jm.addTry();
    jm.addLine(connection, " = getConnection();");

    LocalJavaField stmt = jm.newJavaField(PreparedStatement.class);
    stmt.addLine(connection, ".prepareStatement(", query, ")");
    jm.addTry();
    getPreparedStatementParameters(jm, stmt, elem, table.getPrimaryKey().getColumns(), 0);
    jm.addLine(stmt, ".executeUpdate();");

    getFinally(jm, stmt, null, null);
    getFinally(jm, connection, new Object[]{JavaSource.getQuoted("Failed to execute query "),
                                            " + ", query}, null);
    return jm;
  }

  protected JavaMethod getPMClassSelectMethod(TypeSG pController, JavaSource pSource, CustomTableData pData)
      throws SAXException {
    JavaMethod jm = pSource.newJavaMethod("select", JavaQNameImpl.VOID, JavaSource.PUBLIC);
    Parameter pObserver = jm.addParam(Observer.class, "pObserver");
    Parameter pQuery = jm.addParam(String.class, "pQuery");
    Parameter pParams = jm.addParam(PMParams.class, "pParams");
    jm.addThrows(PMException.class);
    Table table = pData.getTable();

    JavaQName qName = pController.getComplexTypeSG().getClassContext().getXMLInterfaceName();
    StringBuffer sb = new StringBuffer();
    for (Iterator iter = table.getColumns();  iter.hasNext();  ) {
      Column col = (Column) iter.next();
      if (sb.length() > 0) sb.append(", ");
      sb.append(col.getName().getName());
    }
    LocalJavaField query = jm.newJavaField(String.class);
    jm.addIf(pParams, " != null  &&  pParams.isDistinct()");
    jm.addLine(query, " = ", JavaSource.getQuoted("SELECT DISTINCT"), ";");
    jm.addElse();
    jm.addLine(query, " = ", JavaSource.getQuoted("SELECT"), ";");
    jm.addEndIf();
    jm.addLine(query, " += ", JavaSource.getQuoted(" " + sb + " FROM " + table.getQName()), ";");
    jm.addIf(pQuery, " != null");
    jm.addLine(query, " += ", JavaSource.getQuoted(" WHERE "), " + ", pQuery, ";");
    jm.addEndIf();

    LocalJavaField connection = jm.newJavaField(Connection.class);
    connection.addLine("null");
    jm.addTry();
    jm.addLine(connection, " = getConnection();");
    LocalJavaField stmt = jm.newJavaField(PreparedStatement.class);
    stmt.addLine(connection, ".prepareStatement(", query, ")");
    jm.addTry();
    LocalJavaField rs = jm.newJavaField(ResultSet.class);
    rs.addLine(stmt, ".executeQuery();");
    jm.addTry();
    jm.addWhile(rs, ".next()");
    LocalJavaField elem = jm.newJavaField(qName);
    elem.addLine("(", qName, ") create()");
    getResultSet(jm, rs, elem, table.getColumns(), 0);
    jm.addLine(pObserver, ".notify(", elem, ");");
    jm.addEndWhile();

    getFinally(jm, rs, null, null);
    getFinally(jm, stmt, null, null);
    Object sqlMsg = new Object[]{JavaSource.getQuoted("Failed to execute query "),
                                 " + ", query};
    Object jaxbMsg = new Object[]{JavaSource.getQuoted("Failed to create instance of  "),
                                 " + ", qName, ".class.getName()"};
    getFinally(jm, connection, sqlMsg, jaxbMsg);
    return jm;
  }

  protected void getPMClass(TypeSG pController, JavaSource pSource, CustomTableData pTableData) throws SAXException {
    pSource.addExtends(PMJdbcImpl.class);
    getPMClassInsertMethod(pController, pSource, pTableData);
    getPMClassUpdateMethod(pController, pSource, pTableData);
    getPMClassDeleteMethod(pController, pSource, pTableData);
    getPMClassSelectMethod(pController, pSource, pTableData);
  } 
}
