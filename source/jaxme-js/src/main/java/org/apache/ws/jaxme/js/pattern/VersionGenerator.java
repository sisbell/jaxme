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
package org.apache.ws.jaxme.js.pattern;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaComment;
import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnSet;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.Table;


/** <p>The VersionGenerator is able to clone a version of a row
 * in a database. That is nothing special. A simple INSERT does
 * the same.</p>
 * <p>The difference is that the VersionGenerator is able to
 * clone rows in other tables referencing the cloned table
 * as well, updating the references, and clone and update rows
 * referencing these cloned and updated rows, and so on.</p>
 * <p>In other words: The VersionGenerator derives a new
 * version of a complex object stored in the database.</p>
 * <p>The VersionGenerator operates on a list of tables. This
 * list must not contain forward or self references. In other
 * words: Under no circumstances may a table in the list contain
 * a foreign key referencing another table, which follows later.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class VersionGenerator {
	/** <p>The ColumnUpdater is able to update one or more columns in a
	 * table. Such a column update is required, because the cloned tables
	 * must not have the same primary key.</p>
	 * <p>The typical use is to specify a ColumnUpdater for the head
	 * table, which bumps the version number. For any child table
	 * you could specify a ColumnUpdater which generates a new primary
	 * key.</p>
	 */
	public interface ColumnUpdater {
		/** <p>Generates code for updating a table row. The row is supplied
		 * as an array of objects. The order of objects matches the columns
		 * of the given table.</p>
		 * @param pMethod The method being created; may be used to generate
		 *   local fields, etc.
		 * @param pTableInfo The table being updated
		 * @param pMap A local Java field with a Map of rows, that have already
		 *   been cloned. The keys and values are both instances of the generated
		 *   inner class DataCache. The keys are holding the primary keys of
		 *   the original rows (which have been cloned) and the values are the
		 *   primary keys of the created rows (the generated clones).
		 * @param pConnection A local Java field with an open database connection
		 * @param pRow A local Java field with an array of values being cloned
		 */
		public void update(JavaMethod pMethod, TableInfo pTableInfo,
						   DirectAccessible pConnection,
						   DirectAccessible pMap,
						   DirectAccessible pRow);
	}
	
	/** <p>This class is used internally to maintain the informations on
	 * the tables being cloned.</p>
	 */
	public static class TableInfo {
		private final Table table;
		private final String propertyName;
		private boolean isReferenced;
		private boolean hasPrimaryKey;
		private final List columnUpdaters = new ArrayList();
		TableInfo(Table pTable, String pPropertyName) {
			table = pTable;
			propertyName = pPropertyName;
		}
		public Table getTable() { return table; }
		public String getPropertyName() { return propertyName; }
		public void add(ColumnUpdater pColumnUpdater) {
			columnUpdaters.add(pColumnUpdater);
		}
		public Iterator getColumnUpdaters() { return columnUpdaters.iterator(); }
		
		public void setReferenced(boolean pReferenced) {
			isReferenced = true;
		}
		public boolean isReferenced() {
			return isReferenced;
		}
		public boolean hasPrimaryKey() {
			return hasPrimaryKey;
		}
		public void setPrimaryKey(boolean pPrimaryKey) {
			hasPrimaryKey  = pPrimaryKey;
		}
	}
	
	/** <p>An implementation of {@link ColumnUpdater}, which updates foreign
	 * key references.</p>
	 */
	private class ForeignKeyUpdater implements ColumnUpdater {
		private final ForeignKey foreignKey;
		private final TableInfo referencedTable;
		
		private ForeignKeyUpdater(ForeignKey pForeignKey, TableInfo pReferencedTable) {
			foreignKey = pForeignKey;
			referencedTable = pReferencedTable;
		}
		
		public void update(JavaMethod pMethod, TableInfo pTableInfo,
						   DirectAccessible pConnection,
						   DirectAccessible pMap, DirectAccessible pRow) {
			Table table = foreignKey.getTable();
			List params = new ArrayList();
			for (Iterator iter = foreignKey.getColumns();  iter.hasNext();  ) {
				Column primaryKeyColumn = (Column) iter.next();
				int index = -1;
				int i = 0;
				for (Iterator iter2 = table.getColumns();  iter2.hasNext();  ++i) {
					Column tableColumn = (Column) iter2.next();
					if (tableColumn.equals(primaryKeyColumn)) {
						index = i;
						break; 
					}
				}
				if (index == -1) {
					throw new IllegalStateException("Key column " + primaryKeyColumn.getQName() + " not found.");
				}
				if (params.size() > 0) {
					params.add("  ||  ");
				}
				params.add(new Object[]{pRow, "[" + index + "] != null"});
			}

			pMethod.addIf(params);
			LocalJavaField referencedKey = getCacheDataClassInstance(pMethod,
																	 referencedTable,
																	 foreignKey, pRow);
			LocalJavaField mappedKey = pMethod.newJavaField(referencedKey.getType());
			mappedKey.addLine("(", referencedKey.getType(), ") ", pMap, ".get(",
					referencedKey, ")");
			pMethod.addIf(mappedKey, " == null");
			pMethod.addThrowNew(IllegalStateException.class,
					JavaSource.getQuoted("Unknown reference: "), " + ",
					referencedKey);
			pMethod.addEndIf();
			int valNum = 0;
			for (Iterator iter = referencedTable.getTable().getPrimaryKey().getColumns();  iter.hasNext();  ) {
				Column referencedKeyColumn = (Column) iter.next();
				Column localColumn = null;
				for (Iterator iter2 = foreignKey.getColumnLinks();  iter2.hasNext();  ) {
					ForeignKey.ColumnLink link = (ForeignKey.ColumnLink) iter2.next();
					if (link.getReferencedColumn().equals(referencedKeyColumn)) {
						localColumn = link.getLocalColumn();
						break;
					}
				}
				if (localColumn == null) {
					throw new IllegalStateException("Unable to find the column referencing " + referencedKeyColumn.getQName());
				}
				
				int colNum = -1, i = 0;
				for (Iterator iter2 = pTableInfo.getTable().getColumns();  iter2.hasNext();  ) {
					Column col = (Column) iter2.next();
					if (col.equals(localColumn)) {
						colNum = i;
						break;
					}
					++i;
				}
				if (colNum == -1) {
					throw new IllegalStateException("Unable to find the column " + localColumn.getQName() + " in table " + pTableInfo.getTable().getQName());
				}
				pMethod.addLine(pRow, "[" + colNum + "] = ", mappedKey, ".getValues()[" + valNum++ + "];");
			}

			pMethod.addEndIf();
		}
	}
	
	private List tablesByOrder = new ArrayList();
	private Map tablesByName = new HashMap();
	private Set propertyNames = new HashSet();
	private boolean generatingLogging;
	
	private boolean isReferencing(Table pReferencingTable, Table pReferencedTable) {
		for (Iterator iter = pReferencingTable.getForeignKeys();  iter.hasNext();  ) {
			ForeignKey foreignKey = (ForeignKey) iter.next();
			if (foreignKey.getReferencedTable().equals(pReferencedTable)) {
				return true;
			}
		}
		return false;
	}
	
	/** <p>Returns whether the generator is creating logging statements. By default
	 * no logging statements are created.</p>
	 * <p>The default
	 * implementation creates logging statements suitable for the JaxMe logging
	 * package. To change this, create a subclass and overwrite the following
	 * methods: {@link #logEntering(JavaMethod, Object)},
	 * {@link #logExiting(JavaMethod, Object)}, {@link #logFinest(JavaMethod, Object, Object)},
	 * {@link #logFinestEntering(JavaMethod, Object)}, and
	 * {@link #logFinestExiting(JavaMethod, Object)}.</p>
	 * @see #setGeneratingLogging(boolean)
	 */
	public boolean isGeneratingLogging() {
		return generatingLogging;
	}
	
	/** <p>Sets whether the generator is creating logging statements. By default
	 * no logging statements are created.</p>
	 * <p>The default
	 * implementation creates logging statements suitable for the JaxMe logging
	 * package. To change this, create a subclass and overwrite the following
	 * methods: {@link #logEntering(JavaMethod, Object)},
	 * {@link #logExiting(JavaMethod, Object)}, {@link #logFinest(JavaMethod, Object, Object)},
	 * {@link #logFinestEntering(JavaMethod, Object)}, and
	 * {@link #logFinestExiting(JavaMethod, Object)}.</p>
	 * @see #isGeneratingLogging()
	 */
	public void setGeneratingLogging(boolean pGeneratingLogging) {
		generatingLogging = pGeneratingLogging;
	}
	
	/** <p>Creates the code for initialization of the logging framework.
	 * The default implementation generates code creating an instance of
	 * {@link org.apache.ws.jaxme.logging.Logger}.</p>
	 */
	protected void initLogging(JavaSource pSource) {
		if (isGeneratingLogging()) {
			JavaField jf = pSource.newJavaField("logger", Logger.class, JavaSource.PRIVATE);
			jf.setFinal(true);
			jf.setStatic(true);
			jf.addLine(LoggerAccess.class, ".getLogger(", pSource.getQName(), ".class)");
		}
	}
	
	/** <p>Creates code for logging the entrance into a method with
	 * fine level.</p>
	 * <p><em>Note:</em> The method should consider the {@link #isGeneratingLogging()}
	 * value.</p>
	 *
	 * @param pMethod The method in which a logging statement should be inserted
	 * @param pValues An array of additional values, possibly null
	 */
	protected void logEntering(JavaMethod pMethod, Object pValues) {
		if (isGeneratingLogging()) {
			LocalJavaField mName = pMethod.newJavaField(String.class, "mName");
			mName.addLine(JavaSource.getQuoted(pMethod.getLoggingSignature()));
			mName.setFinal(true);
			if (pValues == null) {
				pMethod.addLine("logger.entering(", mName, ");");
			} else {
				pMethod.addLine("logger.entering(", mName, ", ", pValues, ");");         
			}
		}
	}
	
	/** <p>Creates code for logging the entrance into a method with
	 * finest level.</p>
	 * <p><em>Note:</em> The method should consider the {@link #isGeneratingLogging()}
	 * value.</p>
	 * <p><em>Implementation note:</em> The default implementation is
	 * equivalent to <code>logFinest(pMethod, "->", pValues)</code>.</p>
	 *
	 * @param pMethod The method in which a logging statement should be inserted
	 * @param pValues An array of additional values, possibly null
	 */
	protected void logFinestEntering(JavaMethod pMethod, Object pValues) {
		if (isGeneratingLogging()) {
			LocalJavaField mName = pMethod.newJavaField(String.class, "mName");
			mName.addLine(JavaSource.getQuoted(pMethod.getLoggingSignature()));
			mName.setFinal(true);
			logFinest(pMethod, JavaSource.getQuoted("->"), pValues);
		}
	}
	
	/** <p>Creates code for logging the exit from a method with
	 * fine level.</p>
	 * <p><em>Note:</em> The method should consider the {@link #isGeneratingLogging()}
	 * value.</p>
	 *
	 * @param pMethod The method in which a logging statement should be inserted
	 * @param pValues An array of additional values, possibly null
	 */
	protected void logExiting(JavaMethod pMethod, Object pValues) {
		if (isGeneratingLogging()) {
			if (pValues == null) {
				pMethod.addLine("logger.exiting(mName);");
			} else {
				pMethod.addLine("logger.exiting(mName, ", pValues, ");");        
			}
		}
	}
	
	/** <p>Creates code for logging the exit from a method with
	 * fine level.</p>
	 * <p><em>Note:</em> The method should consider the {@link #isGeneratingLogging()}
	 * value.</p>
	 * <p><em>Implementation note:</em> The default implementation is
	 * equivalent to <code>logFinest(pMethod, "<-", pValues)</code>.</p>
	 *
	 * @param pMethod The method in which a logging statement should be inserted
	 * @param pValues An array of additional values, possibly null
	 */
	protected void logFinestExiting(JavaMethod pMethod, Object pValues) {
		logFinest(pMethod, JavaSource.getQuoted("->"), pValues);
	}
	
	/** <p>Creates code for logging a message with finest level.</p>
	 * <p><em>Note:</em> The method should consider the {@link #isGeneratingLogging()}
	 * value.</p>
	 *
	 * @param pMethod The method in which a logging statement should be inserted
	 * @param pMsg The message being logged
	 * @param pValues An array of additional values, possibly null
	 */
	protected void logFinest(JavaMethod pMethod, Object pMsg, Object pValues) {
		if (isGeneratingLogging()) {
			if (pValues == null) {
				pMethod.addLine("logger.finest(mName, ", pMsg, ");");
			} else {
				pMethod.addLine("logger.finest(mName, ", pMsg, ", ", pValues, ");");
			}
		}
	}
	
	/** Adds a new table to the list of tables. The table must not
	 * contain a forward reference. Additionally, the table must not
	 * be referenced by any other table, which has already been added
	 * to the list.</p>
	 * @param pTable The table being cloned
	 * @param pUpdater The column updater to use for changing the
	 *   updated columns.
	 */
	public void addTable(Table pTable, ColumnUpdater pUpdater) {
		String name = pTable.getQName();
		if (tablesByName.containsKey(pTable)) {
			throw new IllegalStateException("A table " + name + " has already been added.");
		}
		if (isReferencing(pTable, pTable)) {
			throw new IllegalStateException("The table " + name + " is containing self references.");
		}
		for (Iterator iter = tablesByOrder.iterator();  iter.hasNext();  ) {
			TableInfo tableInfo = (TableInfo) iter.next();
			if (isReferencing(tableInfo.getTable(), pTable)) {
				throw new IllegalStateException("The table " + tableInfo.getTable().getQName() +
						" contains a forward reference to the table " + name + ".");
			}
		}
		
		String s = pTable.getName().getName();
		String t = s;
		int num = 0;
		for (;;) {
			t = Character.toUpperCase(t.charAt(0)) + t.substring(1);
			if (!propertyNames.contains(t)) {
				break;
			}
			t = s + num++;
		}
		propertyNames.add(t);
		
		TableInfo tableInfo = new TableInfo(pTable, t);
		tableInfo.setPrimaryKey(pTable.getPrimaryKey() != null);
		if (pUpdater != null) {
			tableInfo.add(pUpdater);
		}
		
		for (Iterator iter = pTable.getForeignKeys();  iter.hasNext();  ) {
			ForeignKey foreignKey = (ForeignKey) iter.next();
			Table referencedTable = foreignKey.getReferencedTable();
			for (Iterator iter2 = tablesByOrder.iterator();  iter2.hasNext();  ) {
				TableInfo referencedTableInfo = (TableInfo) iter2.next();
				if (referencedTableInfo.getTable().equals(referencedTable)) {
					ColumnUpdater columnUpdater = new ForeignKeyUpdater(foreignKey, referencedTableInfo);
					tableInfo.add(columnUpdater);
					if (!referencedTableInfo.hasPrimaryKey()) {
						throw new IllegalStateException("The table " + pTable.getQName() +
								" is referencing table " + referencedTable.getQName() +
						", which doesn't have a primary key.");
					}
					referencedTableInfo.setReferenced(true);
				}
			}
		}
		
		tablesByName.put(name, tableInfo);
		tablesByOrder.add(tableInfo);
	}
	
	/** <p>Returns the name of the inner class CacheData.</p>
	 */
	protected JavaQName getCacheDataClassName(JavaQName pQName) {
		return JavaQNameImpl.getInnerInstance(pQName, "CacheData");
	}
	
	/** <p>Generates the innner class CacheData.</p>
	 */
	protected JavaInnerClass getCacheDataClass(JavaSource pSource) {
		JavaInnerClass jic = pSource.newJavaInnerClass("CacheData", JavaSource.PRIVATE);
		
		JavaField name = jic.newJavaField("name", String.class, JavaSource.PRIVATE);
		name.setFinal(true);
		JavaField values = jic.newJavaField("values", Object[].class, JavaSource.PRIVATE);
		values.setFinal(true);
		
		JavaConstructor jcon = jic.newJavaConstructor(JavaSource.PRIVATE);
		DirectAccessible pName = jcon.addParam(String.class, "pName");
		DirectAccessible pValues = jcon.addParam(Object[].class, "pValues");
		jcon.addLine(name, " = ", pName, ";");
		jcon.addLine(values, " = ", pValues, ";");
		
		JavaMethod getNameMethod = jic.newJavaMethod("getName", String.class, JavaSource.PUBLIC);
		getNameMethod.addLine("return ", name, ";");
		
		JavaMethod getValuesMethod = jic.newJavaMethod("getValues", Object[].class, JavaSource.PUBLIC);
		getValuesMethod.addLine("return ", values, ";");
		
		{
			JavaMethod jm = jic.newJavaMethod("toString", String.class, JavaSource.PUBLIC);
			LocalJavaField sb = jm.newJavaField(StringBuffer.class, "sb");
			sb.addLine("new ", StringBuffer.class, "(", name, ")");
			DirectAccessible loopVar = jm.addForArray(values);
			jm.addLine(sb, ".append(", JavaSource.getQuoted(", "), ").append(",
					values, "[", loopVar, "]);");
			jm.addEndFor();
			jm.addLine("return ", sb, ".toString();");
			
		}
		
		{
			JavaMethod jm = jic.newJavaMethod("hashCode", int.class, JavaSource.PUBLIC);
			LocalJavaField hashCodeResult = jm.newJavaField(int.class, "result");
			hashCodeResult.addLine(name, ".hashCode() + ", values, ".length;");
			DirectAccessible loopVar = jm.addForArray(values);
			LocalJavaField o = jm.newJavaField(Object.class, "o");
			o.addLine(values, "[", loopVar, "]");
			jm.addIf(o, " != null");
			jm.addLine(hashCodeResult, " += ", o, ".hashCode();");
			jm.addEndIf();
			jm.addEndFor();
			jm.addLine("return ", hashCodeResult, ";");
		}
		
		{
			JavaMethod jm = jic.newJavaMethod("equals", boolean.class, JavaSource.PUBLIC);
			DirectAccessible o = jm.addParam(Object.class, "o");
			jm.addIf(o, " == null  ||  !(", o, " instanceof ", jic.getQName(), ")");
			jm.addLine("return false;");
			jm.addEndIf();
			LocalJavaField other = jm.newJavaField(jic.getQName(), "other");
			other.addLine("(", jic.getQName(), ") ", o);
			jm.addIf("!", name, ".equals(", other, ".name)  ||  ", values, ".length != ",
					other, ".values.length");
			jm.addLine("return false;");
			jm.addEndIf();
			DirectAccessible loopVar = jm.addForArray(values);
			LocalJavaField v = jm.newJavaField(Object.class, "v");
			v.addLine(values, "[", loopVar, "]");
			jm.addIf(v, " == null");
			jm.addIf(other, ".values[", loopVar, "] != null");
			jm.addLine("return false;");
			jm.addEndIf();
			jm.addElse();
			jm.addIf("!", v, ".equals(", other, ".values[", loopVar, "])");
			jm.addLine("return false;");
			jm.addEndIf();
			jm.addEndIf();
			jm.addEndFor();
			jm.addLine("return true;");
		}
		
		return jic;
	}
	
	/** <p>Generates code for reading a ResultSet's column.</p>
	 */
	protected void setResultSetValue(JavaMethod pMethod, Column pColumn,
									 DirectAccessible pResultSet,
									 int pColumnNum, Object pTarget) {
		Integer p = new Integer(pColumnNum+1);
		
		Column.Type type = pColumn.getType();
		if (Column.Type.BIGINT.equals(type)) {
			LocalJavaField l = pMethod.newJavaField(long.class);
			l.addLine(pResultSet, ".getLong(", p, ");");
			pMethod.addIf("!", pResultSet, ".wasNull()");
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = new ",
					Long.class, "(", l, ");");
			pMethod.addEndIf();
		} else if (pColumn.isBinaryColumn()) {
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = ",
					pResultSet, ".getBytes(", p, ");");
		} else if (Column.Type.BIT.equals(type)) {
			LocalJavaField b = pMethod.newJavaField(boolean.class);
			b.addLine(pResultSet, ".getBoolean(", p, ");");
			pMethod.addIf("!", pResultSet, ".wasNull()");
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = ", b,
					" ? ", Boolean.class, ".TRUE : ", Boolean.class, ".FALSE;");
			pMethod.addEndIf();
		} else if (pColumn.isStringColumn()) {
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = ",
					pResultSet, ".getString(", p, ");");
		} else if (Column.Type.DATE.equals(type)) {
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = ",
					pResultSet, ".getDate(", p, ");");
		} else if (Column.Type.DOUBLE.equals(type)) {
			LocalJavaField d = pMethod.newJavaField(double.class);
			d.addLine(pResultSet, ".getDouble(", p, ");");
			pMethod.addIf("!", pResultSet, ".wasNull()");
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = new ",
					Double.class, "(", d, ");");
			pMethod.addEndIf();
		} else if (Column.Type.FLOAT.equals(type)) {
			LocalJavaField f = pMethod.newJavaField(float.class);
			f.addLine(pResultSet, ".getFloat(", p, ");");
			pMethod.addIf("!", pResultSet, ".wasNull()");
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = new ",
					Float.class, "(", f, ");");
			pMethod.addEndIf();
		} else if (Column.Type.INTEGER.equals(type)) {
			LocalJavaField i = pMethod.newJavaField(int.class);
			i.addLine(pResultSet, ".getInt(", p, ");");
			pMethod.addIf("!", pResultSet, ".wasNull()");
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = new ",
					Integer.class, "(", i, ");");
			pMethod.addEndIf();
		} else if (Column.Type.SMALLINT.equals(type)) {
			LocalJavaField s = pMethod.newJavaField(short.class);
			s.addLine(pResultSet, ".getShort(", p, ");");
			pMethod.addIf("!", pResultSet, ".wasNull()");
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = new ",
					Short.class, "(", s, ");");
			pMethod.addEndIf();
		} else if (Column.Type.TIME.equals(type)) {
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = ",
					pResultSet, ".getTime(", p, ");");
		} else if (Column.Type.TIMESTAMP.equals(type)) {
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = ",
					pResultSet, ".getTimestamp(", p, ");");
		} else if (Column.Type.TINYINT.equals(type)) {
			LocalJavaField b = pMethod.newJavaField(short.class);
			b.addLine(pResultSet, ".getByte(", p, ");");
			pMethod.addIf("!", pResultSet, ".wasNull()");
			pMethod.addLine(pTarget, "[", Integer.toString(pColumnNum), "] = new ",
					Byte.class, "(", b, ");");
			pMethod.addEndIf();
		} else {
			throw new IllegalStateException("Unknown column type: " + type);
		}
	}
	
	/** <p>Generates code for setting a PreparedStatement's parameter.</p>
	 */
	protected void setPreparedStatementValue(JavaMethod pMethod, Column pColumn,
											 Object pStmt, Object pParamNum,
											 Object pValue) {
		if (!(pValue instanceof DirectAccessible)) {
			LocalJavaField v = pMethod.newJavaField(Object.class);
			v.addLine(pValue);
			pValue = v;
		}
		
		Column.Type type = pColumn.getType();
		pMethod.addIf(pValue, " == null");
		pMethod.addLine(pStmt, ".setNull(", pParamNum, ", ", Types.class, ".",
				type, ");");
		pMethod.addElse();
		if (Column.Type.BIGINT.equals(type)) {
			pMethod.addLine(pStmt, ".setLong(", pParamNum, ", ((", Long.class,
					") ", pValue, ").longValue());");
		} else if (pColumn.isBinaryColumn()) {
			pMethod.addLine(pStmt, ".setBytes(", pParamNum, ", (", byte[].class,
					") ", pValue, ");");
		} else if (Column.Type.BIT.equals(type)) {
			pMethod.addLine(pStmt, ".setBoolean(", pParamNum, ", ((", Boolean.class,
					") ", pValue, ").booleanValue());");
		} else if (pColumn.isStringColumn()) {
			pMethod.addLine(pStmt, ".setString(", pParamNum, ", (", String.class,
					") ", pValue, ");");
		} else if (Column.Type.DATE.equals(type)) {
			pMethod.addLine(pStmt, ".setDate(", pParamNum, ", (", Date.class,
					") ", pValue, ");");
		} else if (Column.Type.DOUBLE.equals(type)) {
			pMethod.addLine(pStmt, ".setDouble(", pParamNum, ", ((", Double.class,
					") ", pValue, ").doubleValue());");
		} else if (Column.Type.FLOAT.equals(type)) {
			pMethod.addLine(pStmt, ".setFloat(", pParamNum, ", ((", Float.class,
					") ", pValue, ").floatValue());");
		} else if (Column.Type.INTEGER.equals(type)) {
			pMethod.addLine(pStmt, ".setInt(", pParamNum, ", ((", Integer.class,
					") ", pValue, ").intValue());");
		} else if (Column.Type.SMALLINT.equals(type)) {
			pMethod.addLine(pStmt, ".setShort(", pParamNum, ", ((", Short.class,
					") ", pValue, ").shortValue());");
		} else if (Column.Type.TIME.equals(type)) {
			pMethod.addLine(pStmt, ".setTime(", pParamNum, ", (", Time.class,
					") ", pValue, ");");
		} else if (Column.Type.TIMESTAMP.equals(type)) {
			pMethod.addLine(pStmt, ".setTimestamp(", pParamNum, ", (", Timestamp.class,
					") ", pValue, ");");
		} else if (Column.Type.TINYINT.equals(type)) {
			pMethod.addLine(pStmt, ".setByte(", pParamNum, ", ((", Byte.class, ") ",
					pValue, ").byteValue());");
		} else {
			throw new IllegalStateException("Unknown column type: " + type);
		}
		
		pMethod.addEndIf();
	}
	
	/** <p>Generates code for reading all rows matching the given key.</p>
	 */
	protected void getSelectRowsCode(JavaMethod pMethod,
									 TableInfo pTableInfo,
									 ColumnSet pColumnSet,
									 DirectAccessible pConn,
									 DirectAccessible pMap,
									 DirectAccessible pValues,
									 boolean pReturnValue) {
		Table table = pTableInfo.getTable();
		SelectStatement statement = table.getSelectStatement();
		statement.getWhere().addColumnSetQuery(pColumnSet, statement.getTableReference());
		String s = table.getSchema().getSQLFactory().newSQLGenerator().getQuery(statement);
		Object query = JavaSource.getQuoted(s);
		if (isGeneratingLogging()) {
			LocalJavaField q = pMethod.newJavaField(String.class);
			q.addLine(query);
			query = q;
			logFinest(pMethod, query, pValues);
		}
		
		LocalJavaField stmt = pMethod.newJavaField(PreparedStatement.class);
		stmt.addLine(pConn, ".prepareStatement(", JavaSource.getQuoted(s), ");");
		LocalJavaField isStmtClosed = pMethod.newJavaField(boolean.class);
		isStmtClosed.addLine("false");
		pMethod.addTry();
		
		int paramNum = 0;
		for (Iterator iter = pColumnSet.getColumns();  iter.hasNext();  ) {
			Object v = new Object[]{pValues, "[", Integer.toString(paramNum), "]"};
			Column column = (Column) iter.next();
			setPreparedStatementValue(pMethod, column, stmt, Integer.toString(++paramNum), v);
		}
		LocalJavaField rs = pMethod.newJavaField(ResultSet.class, "rs");
		rs.addLine(stmt, ".executeQuery()");
		LocalJavaField isRsClosed = pMethod.newJavaField(boolean.class);
		isRsClosed.addLine("false");
		LocalJavaField result;
		if (pReturnValue) {
			result = pMethod.newJavaField(Object[].class, "result");
			result.addLine("null");
		} else {
			result = null;
		}
		pMethod.addTry();
		
		pMethod.addWhile(rs, ".next()");
		if (result != null) {
			pMethod.addIf(result, " != null");
			pMethod.addThrowNew(IllegalStateException.class,
					JavaSource.getQuoted("Expected a single row only."));
			pMethod.addEndIf();
		}
		
		int resultColumnSize = 0;
		for (Iterator iter = table.getColumns();  iter.hasNext();  iter.next()) {
			++resultColumnSize;
		}
		LocalJavaField row = pMethod.newJavaField(Object[].class, "row");
		row.addLine("new ", Object.class, "[" + resultColumnSize + "];");
		
		int resultColumnNum = 0;
		for (Iterator iter = table.getColumns();  iter.hasNext();  ) {
			Column column = (Column) iter.next();
			setResultSetValue(pMethod, column, rs, resultColumnNum++, row);
		}
		
		pMethod.addLine(((result == null) ? "" : "result = "),
				getInsertRowMethodName(pTableInfo), "(", pConn, ", ", pMap, ", ",
				row, ");");
		
		pMethod.addEndWhile();
		pMethod.addLine(isRsClosed, " = true;");
		pMethod.addLine(rs, ".close();");
		
		pMethod.addFinally();
		pMethod.addIf("!", isRsClosed);
		pMethod.addTry();
		pMethod.addLine(rs, ".close();");
		pMethod.addCatch(Throwable.class, "pIgnore");
		pMethod.addEndTry();
		pMethod.addEndIf();
		pMethod.addEndTry();
		
		pMethod.addLine(isStmtClosed, " = true;");
		pMethod.addLine(stmt, ".close();");
		if (result != null) {
			logExiting(pMethod, result);
			pMethod.addLine("return ", result, ";");
		} else {
			logFinestExiting(pMethod, null);
		}
		
		pMethod.addFinally();
		pMethod.addIf("!", isStmtClosed);
		pMethod.addTry();
		pMethod.addLine(stmt, ".close();");
		pMethod.addCatch(Throwable.class, "pIgnore");
		pMethod.addEndTry();
		pMethod.addEndIf();
		pMethod.addEndTry();
		
	}
	
	/** <p>Returns the name of the method for cloning one row from the
	 * given table.</p>
	 */
	protected String getInsertRowMethodName(TableInfo pTableInfo) {
		return "clone" + pTableInfo.getPropertyName() + "Row";
	}
	
	/** <p>Creates an instance of the inner class CacheData by reading
	 * the key from the given row.</p>
	 */
	protected LocalJavaField getCacheDataClassInstance(JavaMethod pMethod,
													   TableInfo pTableInfo,
													   ColumnSet pColumnSet,
													   DirectAccessible pValues) {
		Table table = pColumnSet.getTable();
		List params = new ArrayList();
		for (Iterator iter = pColumnSet.getColumns();  iter.hasNext();  ) {
			Column primaryKeyColumn = (Column) iter.next();
			int index = -1;
			int i = 0;
			for (Iterator iter2 = table.getColumns();  iter2.hasNext();  ++i) {
				Column tableColumn = (Column) iter2.next();
				if (tableColumn.equals(primaryKeyColumn)) {
					index = i;
					break; 
				}
			}
			if (index == -1) {
				throw new IllegalStateException("Key column " + primaryKeyColumn.getQName() + " not found.");
			}
			if (params.size() > 0) {
				params.add(", ");
			}
			params.add(new Object[]{pValues, "[" + index + "]"});
		}
		
		JavaQName cacheDataClass = getCacheDataClassName(pMethod.getJavaSource().getQName());
		LocalJavaField jf = pMethod.newJavaField(cacheDataClass);
		jf.addLine("new ", cacheDataClass, "(",
				   JavaSource.getQuoted(pTableInfo.getPropertyName()),
				   ", new ", Object[].class, "{", params, "})");
		return jf;
	}

	/** <p>Updates a row by reading the values from an instance of the inner
	 * class CacheData.</p>
	 */
	protected void getApplyCacheData(JavaMethod pMethod,
									 TableInfo pTableInfo,
									 ColumnSet pColumnSet,
									 DirectAccessible pRow,
									 DirectAccessible pData) {
		Table table = pTableInfo.getTable();
		for (Iterator iter = pColumnSet.getColumns();  iter.hasNext();  ) {
			Column primaryKeyColumn = (Column) iter.next();
			int index = -1;
			int i = 0;
			for (Iterator iter2 = table.getColumns();  iter2.hasNext();  ++i) {
				Column tableColumn = (Column) iter2.next();
				if (tableColumn.equals(primaryKeyColumn)) {
					index = i;
					break; 
				}
			}
			if (index == -1) {
				throw new IllegalStateException("Key column " + primaryKeyColumn.getQName() + " not found.");
			}
			pMethod.addLine(pRow, "[" + index + "] = ", pData, "[" + (i+1) + "];");
		}
	}
	
	/** <p>Creates a method for cloning one row from the given table.</p>
	 */
	protected JavaMethod getInsertRowMethod(JavaSource pSource, TableInfo pTableInfo) {
		Table table = pTableInfo.getTable();
		JavaMethod jm = pSource.newJavaMethod(getInsertRowMethodName(pTableInfo),
				Object[].class, JavaSource.PRIVATE);
		jm.addThrows(SQLException.class);
		DirectAccessible connection = jm.addParam(Connection.class, "pConn");
		DirectAccessible map = jm.addParam(Map.class, "pMap");
		DirectAccessible values = jm.addParam(Object[].class, "pValue");
		
		logFinestEntering(jm, values);
		
		LocalJavaField baseKey = null;
		if (table.getPrimaryKey() != null) {
			baseKey = getCacheDataClassInstance(jm, pTableInfo,
					table.getPrimaryKey(), values);
			jm.addIf(map, ".containsKey(", baseKey, ")");
			logFinestExiting(jm, JavaSource.getQuoted("null (Already cloned)"));
			jm.addLine("return null;");
			jm.addEndIf();
		}
		
		for (Iterator iter = pTableInfo.getColumnUpdaters();  iter.hasNext();  ) {
			((ColumnUpdater) iter.next()).update(jm, pTableInfo, connection, map, values);
		}
		
		jm.addLine(jm.getName(), "(", connection, ", ", values, ");");
		
		if (baseKey != null) {
			LocalJavaField clonedKey = getCacheDataClassInstance(jm, pTableInfo,
					table.getPrimaryKey(),
					values);
			jm.addLine(map, ".put(", baseKey, ", ", clonedKey, ");");
		}
		
		
		// Clone objects referencing this object
		LocalJavaField referencedValues = null;
		for (Iterator referencingIter = tablesByOrder.iterator();  referencingIter.hasNext();  ) {
			TableInfo prevTableInfo = (TableInfo) referencingIter.next();
			Table prevTable = prevTableInfo.getTable();
			for (Iterator fkIter = prevTable.getForeignKeys();  fkIter.hasNext();  ) {
				ForeignKey fk = (ForeignKey) fkIter.next();
				if (fk.getReferencedTable().equals(table)) {
					if (referencedValues == null) {
						referencedValues = jm.newJavaField(Object[].class);
						referencedValues.addLine(baseKey, ".getValues()");
					}
					getSelectRowsCode(jm, prevTableInfo, fk, connection, map, referencedValues, false);
				}
			}
		}
		
		logFinestExiting(jm, values);
		jm.addLine("return ", values, ";");
		
		return jm;
	}
	
	/** <p>Creates a method for cloning one row from the given table.</p>
	 */
	protected JavaMethod getInnerInsertRowMethod(JavaSource pSource, TableInfo pTableInfo) {
		Table table = pTableInfo.getTable();
		JavaMethod jm = pSource.newJavaMethod(getInsertRowMethodName(pTableInfo),
				JavaQNameImpl.VOID, JavaSource.PRIVATE);
		jm.addThrows(SQLException.class);
		DirectAccessible connection = jm.addParam(Connection.class, "pConn");
		DirectAccessible values = jm.addParam(Object[].class, "pValue");
		
		logFinestEntering(jm, null);
		
		InsertStatement insertStatement = table.getInsertStatement();
		String s = table.getSchema().getSQLFactory().newSQLGenerator().getQuery(insertStatement);
		Object query = JavaSource.getQuoted(s);
		if (isGeneratingLogging()) {
			LocalJavaField q = jm.newJavaField(String.class);
			q.addLine(query);
			query = q;
			logFinest(jm, query, values);
		}
		LocalJavaField stmt = jm.newJavaField(PreparedStatement.class, "stmt");
		stmt.setFinal(true);
		stmt.addLine(connection, ".prepareStatement(", query, ");");
		LocalJavaField isStmtClosed = jm.newJavaField(boolean.class, "isStmtClosed");
		isStmtClosed.addLine("false");
		jm.addTry();
		
		int paramNum = 0;
		for (Iterator iter = table.getColumns();  iter.hasNext();  ) {
			Column column = (Column) iter.next();
			Object v = new Object[]{ values, "[", Integer.toString(paramNum), "]" };
			setPreparedStatementValue(jm, column, stmt, Integer.toString(++paramNum), v);
		}
		
		jm.addLine(stmt, ".executeUpdate();");
		jm.addLine(isStmtClosed, " = true;");
		jm.addLine(stmt, ".close();");
		
		jm.addFinally();
		jm.addIf("!", isStmtClosed);
		jm.addTry();
		jm.addLine(stmt, ".close();");
		jm.addCatch(Throwable.class, "ignore");
		jm.addEndTry();
		jm.addEndIf();
		jm.addEndTry();
		
		logFinestExiting(jm, null);
		return jm;
	}
	
	
	/** <p>Actually creates the public "clone" method.</p>
	 */
	protected JavaMethod getPublicCloneMethod(JavaSource pSource) {
		TableInfo headTable;
		{
			Iterator iter = tablesByOrder.iterator();
			if (!iter.hasNext()) {
				throw new IllegalStateException("No tables have been added.");
			}
			headTable = (TableInfo) iter.next();
		}
		
		JavaQName resultType = JavaQNameImpl.getInstance(Object[].class);
		JavaMethod jm = pSource.newJavaMethod("clone", resultType, JavaSource.PUBLIC);
		jm.addThrows(SQLException.class);
		JavaComment jc = jm.newComment();
		jc.addLine("<p>This method takes as input the key values of a row in the table " +
				headTable.getTable().getQName() + ".");
		jc.addLine("The key values are given by the array <code>pRow</code>:");
		jc.addLine("<ul>");
		int i = 0;
		for (Iterator iter = headTable.getTable().getPrimaryKey().getColumns();
				iter.hasNext();  i++) {
			Column col = (Column) iter.next();
			jc.addLine("  <li><code>pRow[" + i+ "] = " + col.getQName() + "</code></li>");
		}
		jc.addLine("</ul>");
		jc.addLine("The method updates the rows version number and creates a new row");
		jc.addLine("with the updated values.</p>");
		{
			Iterator iter = tablesByOrder.iterator();
			iter.next();
			if (iter.hasNext()) {
				jc.addLine("<p>Once the new row is created, the method searches for entries");
				jc.addLine("referencing the old row in the following tables:");
				jc.addLine("<table>");
				do {
					TableInfo subTable = (TableInfo) iter.next();
					jc.addLine("  <tr><td>" + subTable.getTable().getQName() + "</td></tr>");
				} while (iter.hasNext());
				jc.addLine("All the referencing entries are cloned as well, with updated");
				jc.addLine("references.");
			}
		}
		
		DirectAccessible conn = jm.addParam(Connection.class, "pConn");
		DirectAccessible row = jm.addParam(resultType, "pRow");
		
		logEntering(jm, new Object[]{"new ", Object[].class, "{", conn, ", ", row, "}"});
		
		LocalJavaField map = jm.newJavaField(Map.class, "clonedObjects");
		map.addLine("new ", HashMap.class, "()");
		
		getSelectRowsCode(jm, headTable, headTable.getTable().getPrimaryKey(),
				conn, map, row, true);
		return jm;
	}
	
	/** <p>Creates a method for updating one row in the head table.</p>
	 */
	public JavaMethod getCloneMethod(JavaSource pSource) {
		initLogging(pSource);
		
		for (Iterator iter = tablesByOrder.iterator();  iter.hasNext();  ) {
			TableInfo tableInfo = (TableInfo) iter.next();
			
			getInsertRowMethod(pSource, tableInfo);
			getInnerInsertRowMethod(pSource, tableInfo);
		}
		getCacheDataClass(pSource);
		getPublicCloneMethod(pSource);
		
		return null;
	}
}
