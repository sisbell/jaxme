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
package org.apache.ws.jaxme.sqls;


/** Interface of a "case value when x then a when y then b else c end" statement
 */
public interface Case {
	/** Interface of a single "when x then a" clause.
	 */
    public interface When {
    	/** Returns the condition. In a "when x then a" clause
         * the condition is "x".
    	 */
        public Object getCondition();
        /** Returns the value. In a "when x then a" clause
         * the condition is "a".
         */
        public Object getValue();
    }

    /** Sets the value being checked.
     */
    public void setCheckedValue(Object pValue);
    /** Returns the value being checked.
     */
    public Object getCheckedValue();
    /** Adds a new clause "when pCondition then pValue".
     */
    public void addWhen(Object pCondition, Object pValue);
    /** Adds a new when clause.
     */
    public void addWhen(When pWhen);
    /** Sets the value for the "else" clause.
     */
    public void setElseValue(Object pValue);
    /** Returns the value for the "else" clause.
     */
    public Object getElseValue();
    /** Returns the case clauses type.
     */
    public Column.Type getType();
    /** Returns the array of "when" clauses.
     */
    public When[] getWhens();
}
