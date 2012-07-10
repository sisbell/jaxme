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
package org.apache.ws.jaxme.sqls.oracle;

import java.sql.DatabaseMetaData;
import java.sql.Types;

import org.apache.ws.jaxme.sqls.BinaryColumn;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ObjectFactory;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.StringColumn;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.SQLFactoryImpl;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class OraSQLFactoryImpl extends SQLFactoryImpl implements OraSQLFactory {
    public SQLGenerator newSQLGenerator() {
        return new OraSQLGeneratorImpl();
    }
    
    protected Column readColumn(Table pTable, String pColumnName, int pDataType, String pTypeName,
            long pColumnSize, int pDecimalDigits, int pNullable) {
        Column.Type type;
        if ("NUMBER".equalsIgnoreCase(pTypeName)) {
            pDataType = Types.NUMERIC;
        }
        switch (pDataType) {
            case Types.BIGINT:        type = Column.Type.BIGINT;    break;
            case Types.BINARY:        type = Column.Type.BINARY;    break;
            case Types.BIT:           type = Column.Type.BIT;       break;
            case Types.BLOB:          type = Column.Type.VARBINARY; break;
            case 16: /* Types.BOOLEAN, introduced in Java 1.4 */
                                      type = Column.Type.BIT;       break;
            case Types.CHAR:          type = Column.Type.CHAR;      break;
            case Types.CLOB:          type = Column.Type.VARCHAR;   break;
            case Types.DATE:          type = Column.Type.DATE;      break;
            case Types.DOUBLE:        type = Column.Type.DOUBLE;    break;
            case Types.FLOAT:         type = Column.Type.FLOAT;     break;
            case Types.INTEGER:       type = Column.Type.INTEGER;   break;
            case Types.LONGVARBINARY: type = Column.Type.VARBINARY; break;
            case Types.LONGVARCHAR:   type = Column.Type.VARCHAR;   break;
            case Types.NUMERIC:
                if (pDecimalDigits == 0) {
                    if (pColumnSize == 0) {
                        type = Column.Type.FLOAT;
                    } else if (pColumnSize <= 2) {
                        type = Column.Type.TINYINT;
                    } else if (pColumnSize <= 4) {
                        type = Column.Type.SMALLINT;
                    } else if (pColumnSize <= 9) {
                        type = Column.Type.INTEGER;
                    } else {
                        type = Column.Type.BIGINT;
                    }
                } else if (pDecimalDigits == -127) {
                    // Uses binary precision - See page 4-37 of the OCI book
                    if (pColumnSize < 24) {
                        type = Column.Type.FLOAT;  // 53 is double cutoff
                    } else {
                        type = Column.Type.DOUBLE;
                    }
                } else {
                    // Uses decimal precision - See page 4-37 of the OCI book
                    if (pColumnSize < 8) {
                        type = Column.Type.FLOAT;   // 15 is double cutoff
                    } else {
                        type = Column.Type.DOUBLE;
                    }
                }
            break;
            case Types.OTHER:
                if ("CLOB".equalsIgnoreCase(pTypeName)) {
                    type = Column.Type.CLOB;
                } else {
                    type = Column.Type.BLOB;
                }
            break;        
            case Types.SMALLINT:      type = Column.Type.SMALLINT;  break;
            case Types.TIMESTAMP:     type = Column.Type.TIMESTAMP; break;
            case Types.TIME:          type = Column.Type.TIME;      break;
            case Types.TINYINT:       type = Column.Type.TINYINT;   break;
            case Types.VARBINARY:     type = Column.Type.VARBINARY; break;
            case Types.VARCHAR:       type = Column.Type.VARCHAR;   break;
            default: throw new IllegalArgumentException("Column " + pColumnName +
                    " in table " + pTable.getQName() +
                    " has unknown JDBC data type " +
                    pDataType);
        }
        Column column = pTable.newColumn(pColumnName, type);
        if (column instanceof StringColumn) {
            ((StringColumn) column).setLength(pColumnSize);
        } else if (column instanceof BinaryColumn) {
            ((BinaryColumn) column).setLength(pColumnSize);
        }
        if (pNullable == DatabaseMetaData.columnNullable) {
            column.setNullable(true);
        }
        return column;
    }
    
    protected ObjectFactory newObjectFactory() {
        return new OraObjectFactoryImpl();
    }
    
    public SelectStatement newSelectStatement() {
        return new OraSelectStatementImpl(this);
    }
}
