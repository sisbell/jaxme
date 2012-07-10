/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind;

import javax.xml.namespace.QName;
import java.io.Serializable;


/**
 * @since JAXB 2.0
 */
public class JAXBElement<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Marker class for an element with global scope.
     */
    public static final class GlobalScope {
        // This class is intentionally empty.
    }

    /**
     * The XML elements tag name.
     */
    protected final QName name;
    /**
     * The type, to which the XML element is bound.
     */
    protected final Class<T> declaredType;
    /**
     * Scope of the XML elements declaration, either of
     * <ul>
     *   <li>{@link GlobalScope}, if this is a globally declared element.</li>
     *   <li>A locally declared complex types class representation.</li>
     * </ul>
     */
    protected final Class<?> scope;
    /**
     * The actual element value.
     */
    protected T value;
    /**
     * Value of the XML elements xsi:nil attribute.
     * (Defaults to false.)
     */
    protected boolean nil;

    /**
     * Creates a new instance.
     * @param pName The XML elements tag name.
     * @param pType The type, to which the XML element is bound.
     * @param pScope The XML elements scope, either of
     *   {@link GlobalScope GlobalScope.class} (default) or
     *   a locally declared complex types Java representation.
     * @param pValue The actual XML elements value.
     * @see #getScope()
     * @see #isTypeSubstituted()
     */
    public JAXBElement(QName pName, Class<T> pType, 
		       Class<?> pScope, T pValue) {
        if (pType == null) {
            throw new IllegalArgumentException("The type argument must not be null.");
        }
        if (pName==null) {
            throw new IllegalArgumentException("The name argument must not be null.");
        }
        declaredType = pType;
        scope = pScope == null ? GlobalScope.class : pScope;
        name = pName;
        setValue(pValue);
    }

    /**
     * Creates a new instance with global scope. Shortcut for
     * <pre>JAXBElement(pName, pType, null, pValue)</pre>.
     */
    public JAXBElement(QName pName, Class<T> pType, T pValue ) {
        this(pName, pType, null, pValue);
    }

    /**
     * Returns the type, to which the XML element is bound.
     */
    public Class<T> getDeclaredType() {
        return declaredType;
    }

    /**
     * Returns the XML elements tag name.
     */
    public QName getName() {
        return name;
    }

    /**
     * Sets the actual XML elements value.
     * Setting this property to null is allowed, if {@link #isNil()}
     * returns true.
     * @see #isTypeSubstituted()
     */
    public void setValue(T t) {
        this.value = t;
    }

    /**
     * Returns the actual XML elements value.
     * This property might return null, if {@link #isNil()}
     * returns true.
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the scope of the XML elements declaration, either of
     * <ul>
     *   <li>{@link GlobalScope}, if this is a globally declared element.</li>
     *   <li>A locally declared complex types class representation.</li>
     * </ul>
     */
    public Class<?> getScope() {
        return scope;
    }
    
    /**
     * Returns, whether this XML elements content model is nil.
     * This is the case, if {@link #getValue()} return null, or
     * if {@link #setNil(boolean)} was invoked with the value
     * true.
     */
    public boolean isNil() {
        return value == null ||  nil;
    }

    /**
     * Sets, whether this XML elements content model is nil.
     * @see #isNil()
     */
    public void setNil(boolean pNil) {
        nil = pNil;
    }
    
    /**
     * Returns true, if this is a globally declared element.
     * Shortcut for <pre>getScope() == GlobalScope.class</pre>.
     */
    public boolean isGlobalScope() {
        return scope == GlobalScope.class;
    }

    /**
     * Returns true, if this XML elements actual value has
     * a different value than the declared class. Shortcut
     * for <pre>getValue().getClass() != getType()</pre>.
     */
    public boolean isTypeSubstituted() {
        return value != null  &&  value.getClass() != declaredType;
    }

}
