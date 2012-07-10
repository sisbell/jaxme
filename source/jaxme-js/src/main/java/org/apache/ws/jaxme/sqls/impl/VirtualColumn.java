/*
 * Copyright 2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.sqls.impl;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;


/** <p>A virtual column is a named item that can be added
 * to the result set. For example:</p>
 * <pre>
 *   SELECT name, vorname, MAX(a) AS max FROM ...
 * </pre>
 * <p>The example uses a virtual column max. The value of
 * max is calculated from other values.</p>
 *
 * @author <a href="mailto:jwi@softwareag.com">Jochen Wiedmann</a>
 */
public class VirtualColumn extends AbstractColumn implements ColumnReference {
    private Column.Name alias;
    private Object value;
    
    public VirtualColumn(Column.Name pName, Column.Type pType) {
        super(pName, pType);
    }
    
    public VirtualColumn(String pName, Column.Type pType) {
        super(new ColumnImpl.NameImpl(pName), pType);
    }
    
    public Table getTable() { return null; }
    public String getQName() { return getName().toString(); }
    public boolean isPrimaryKeyPart() { return false; }
    public TableReference getTableReference() { return null; }
    public Column getColumn() { return this; }
    public boolean isVirtual() { return true; }
    
    public void setAlias(String pName) {
        setAlias(new ColumnImpl.NameImpl(pName));
    }
    
    public void setAlias(Name pName) {
        alias = pName;
    }
    
    public Name getAlias() {
        return alias;
    }
    
    public void setValue(String pValue) {
        value = pValue;
    }
    
    public void setValue(SelectStatement pValue) {
        value = pValue;
    }
    
    public void setValue(Function pValue) {
        value = pValue;
    }
    
    public Object getValue() {
        return value;
    }
}
