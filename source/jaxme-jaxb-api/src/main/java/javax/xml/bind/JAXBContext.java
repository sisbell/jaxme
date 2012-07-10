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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.w3c.dom.Node;


/** <p>The <code>JAXBContext</code> provides the JAXB users anchor to
 * the implmentation and hos generated classes. A JAXBContext is used to
 * obtain instances of {@link javax.xml.bind.Marshaller},
 * {@link javax.xml.bind.Unmarshaller}, and
 * {@link javax.xml.bind.Validator}. To obtain a JAXBContext, the
 * application invokes
 * <pre>
 *   JAXBContext context = JAXBContext.newInstance("com.mycompany:com.mycompany.xml");
 * </pre>
 * The list of colon separated package names matches the list in the
 * schemas used to generate classes. In other words: If you have a
 * schema using package name "com.mycompany.xml", then this package
 * name has to be part of the list.</p>
 * <p>The <code>JAXBContext</code> class will scan the given list of packages
 * for a file called <samp>jaxb.properties</samp>. This file contains the
 * name of an instantiation class in the property
 * {@link #JAXB_CONTEXT_FACTORY}. (See {@link #newInstance(String)} for
 * details on how the class name is obtained.) Once such a file is found, the
 * given class is loaded via {@link ClassLoader#loadClass(java.lang.String)}.
 * The <code>JAXBContext</code> class demands, that the created object
 * has a method
 * <pre>
 *   public static JAXBContext createContext(String pPath, ClassLoader pClassLoader)
 *     throws JAXBException;
 * </pre>
 * This method is invoked with the same path and {@link ClassLoader} than
 * above. See {@link #newInstance(String,ClassLoader)}} for details on the choice
 * of the {@link ClassLoader}.</p>
 * <p>The created context will scan the same package path for implementation
 * specific configuration details (in the case of the <code>JaxMe</code>
 * application a file called <samp>Configuration.xml</samp> in any of the
 * packages) and do whatever else is required to initialize the runtime.
 * In particular it will invoke
 * {@link DatatypeConverter#setDatatypeConverter(DatatypeConverterInterface)}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 * @see Marshaller
 * @see Unmarshaller
 * @see Validator
 */
public abstract class JAXBContext {
    private static final Map<String,?> EMPTY_MAP = Collections.emptyMap();

    /**
     * Creates a new instance.
     */
    protected JAXBContext() {
        // Does nothing
    }

    /** <p>This is the name of the property used to determine the name
     * of the initialization class: "javax.xml.bind.context.factory".
     * The name is used by {@link #newInstance(String)} and
     * {@link #newInstance(String,ClassLoader)}. It contains a class
     * name. The class must contain a static method
     * <pre>
     *   public static JAXBContext createContext(String, ClassLoader) throws JAXBException;
     * </pre>
     * which is invoked to create the actual instance of JAXBContext.</p>
     */
    public static final java.lang.String JAXB_CONTEXT_FACTORY = "javax.xml.bind.context.factory";

    /**
     * <p>Creates a new instance of <code>JAXBContext</code> by applying
     * the following algorithm:
     * <ol>
     *   <li>The first step is to determine the name of an initialization class.
     *     For any of the package names in the list given by
     *     <code>pPath</code> the <code>JAXBContext</code> class will try to find a file
     *     called <samp>jaxb.properties</samp>. This file's got to be in
     *     standard property file format. The <code>JAXBContext</code> class
     *     will load the property file.</li>
     *   <li>A property called "javax.xml.bind.context.factory" (see
     *     {@link #JAXB_CONTEXT_FACTORY}) is evaluated. It must contain the
     *     name of an initialization class. The initialization class is
     *     loaded via
     *     <code>Thread.currentThread().getContextClassLoader().loadClass(String)</code>.</li>
     *   <li>The initialization class must contain a method
     *     <pre>
     *       public static JAXBContext createContext(String, ClassLoader) throws JAXBException;
     *     </pre>
     *     which is invoked with the <code>pPath</code> argument and the
     *     {@link ClassLoader} of the <code>JAXBContext</class> class as
     *     parameters. The result of this method is also used as the
     *     result of the <code>newInstance(String)</code> method.</li>
     * </ol>
     * @param pPath A colon separated path of package names where to look for
     *   <samp>jaxb.properties</samp> files. The package names must match the
     *   generated classes which you are going to use in your application.
     * @return An initialized instance of <code>JAXBContext</code>.
     * @throws JAXBException An error occurred while creating the JAXBContext instance. 
     */  
    public static JAXBContext newInstance(java.lang.String pPath) throws JAXBException {
        return newInstance(pPath, Thread.currentThread().getContextClassLoader());
    }

    /**
     * <p>Creates a new instance of <code>JAXBContext</code> by applying
     * the following algorithm:
     * <ol>
     *   <li>The first step is to determine the name of an initialization class.
     *     For any of the package names in the list given by
     *     <code>pPath</code> the <code>JAXBContext</code> class will try to find a file
     *     called <samp>jaxb.properties</samp>. This file's got to be in
     *     standard property file format. The <code>JAXBContext</code> class
     *     will load the property file.</li>
     *   <li>A property called "javax.xml.bind.context.factory" (see
     *     {@link #JAXB_CONTEXT_FACTORY}) is evaluated. It must contain the
     *     name of an initialization class. The initialization class is
     *     loaded via
     *     <code>pClassLoader.loadClass(String)</code>.</li>
     *   <li>The initialization class must contain a method
     *     <pre>
     *       public static JAXBContext createContext(String, ClassLoader) throws JAXBException;
     *     </pre>
     *     which is invoked with the parameters <code>pPath</code> and
     *     <code>pClassLoader</code>. The result of this method is also
     *     used as the result of the <code>newInstance(String)</code>
     *     method.</li>
     * </ol>
     * @param pPath A colon separated path of package names where to look for
     *   <samp>jaxb.properties</samp> files. The package names must match the
     *   generated classes which you are going to use in your application.
     * @param pClassLoader The {@link ClassLoader} to use for loading
     *   resources and classes.
     * @return An initialized instance of <code>JAXBContext</code>.
     * @throws JAXBException An error occurred while creating the JAXBContext instance.
     */
    public static JAXBContext newInstance(String pPath, ClassLoader pClassLoader) throws JAXBException {
        return newInstance(pPath, pClassLoader, EMPTY_MAP);
    }

    /**
     * <p>Creates a new instance of <code>JAXBContext</code>.
     * This is mostly the same than {@link #newInstance(String, ClassLoader)},
     * except that it accepts provider-specific properties for configuring
     * the new context.</p>
     * @param pPath A colon separated path of package names where to look for
     *   <samp>jaxb.properties</samp> files. The package names must match the
     *   generated classes which you are going to use in your application.
     * @param pClassLoader The {@link ClassLoader} to use for loading
     *   resources and classes.
     * @param pProperties A set of properties, which allow to configure the
     *   new <code>JAXBContext</code>.
     * @return An initialized instance of <code>JAXBContext</code>.
     * @throws JAXBException An error occurred while creating the JAXBContext instance.
     * @since JAXB2.0
     */
    public static JAXBContext newInstance( String pPath, ClassLoader pClassLoader, Map<String,?>  pProperties  )
        throws JAXBException {
        return ContextCreator.newContext(JAXB_CONTEXT_FACTORY,
                pPath, pClassLoader, pProperties);
    }

    /**
     * <p>Creates a new instance of <code>JAXBContext</code> by applying
     * the following algorithm:
     * <ol>
     *   <li>The first step is to determine the name of an initialization
     *     class. For any of the package names in the list given by
     *     <code>pClasses</code> the <code>JAXBContext</code> class will
     *     try to find a file called <samp>jaxb.properties</samp>. This
     *     file's got to be in standard property file format. The
     *     <code>JAXBContext</code> class will load the property file.</li>
     *   <li>A property called "javax.xml.bind.context.factory" (see
     *     {@link #JAXB_CONTEXT_FACTORY}) is evaluated. It must contain the
     *     name of an initialization class.</li>
     *   <li>The initialization class must contain a method
     *     <pre>
     *       public static JAXBContext createContext(Class[], Map) throws JAXBException;
     *     </pre>
     *     which is invoked with the parameters <code>pClasses</code> and
     *     an empty property set. The result of this method is also
     *     used as the result of the <code>newInstance(String)</code>
     *     method.</li>
     * </ol>
     * @param pClasses Array of JAXB object classes, which must be
     *   supported by the created JAXBContext.
     * @throws JAXBException An error occurred while creating the JAXBContext instance.
     * @throws IllegalArgumentException The parameter or an element
     *   in the array is null.
     * @since JAXB2.0
     */
    public static JAXBContext newInstance(Class<?>... pClasses)
        throws JAXBException {
        return newInstance(pClasses, EMPTY_MAP);
    }

    /**
     * <p>Creates a new instance of <code>JAXBContext</code> by applying
     * the following algorithm:
     * <ol>
     *   <li>The first step is to determine the name of an initialization
     *     class. For any of the package names in the list given by
     *     <code>pClasses</code> the <code>JAXBContext</code> class will
     *     try to find a file called <samp>jaxb.properties</samp>. This
     *     file's got to be in standard property file format. The
     *     <code>JAXBContext</code> class will load the property file.</li>
     *   <li>A property called "javax.xml.bind.context.factory" (see
     *     {@link #JAXB_CONTEXT_FACTORY}) is evaluated. It must contain the
     *     name of an initialization class.</li>
     *   <li>The initialization class must contain a method
     *     <pre>
     *       public static JAXBContext createContext(Class[], Map) throws JAXBException;
     *     </pre>
     *     which is invoked with the parameters <code>pClasses</code> and
     *     <code>pProperties</code>. The result of this method is also
     *     used as the result of the <code>newInstance(String)</code>
     *     method.</li>
     * </ol>
     * @param pClasses Array of JAXB object classes, which must be
     *   supported by the created JAXBContext.
     * @param pProperties Property set for configuring the
     *   created JAXBContext.
     * @throws JAXBException An error occurred while creating the JAXBContext instance.
     * @throws IllegalArgumentException The parameter or an element
     *   in the array is null.
     * @since JAXB2.0
     */
    public static JAXBContext newInstance(Class<?>[] pClasses,
            Map<String,?> pProperties) throws JAXBException {
        if (pClasses == null) {
            throw new IllegalArgumentException("The parameter must not be null.");
        }
        for (int i = 0;  i < pClasses.length;  i++) {
            if (pClasses[i] == null) {
                throw new IllegalArgumentException("The pClasses argument must not contain null arguments.");
            }
        }
        return ContextCreator.newContext(JAXB_CONTEXT_FACTORY, pClasses, pProperties);
    }

    /** <p>Creates a new instance of {@link Marshaller}. The
     * {@link Marshaller} can be used
     * to convert JAXB objects into XML data.</p>
     * <p><em>Note</em>: Marshallers are reusable, but not reentrant (thread safe).</p>
     */
    public abstract Marshaller createMarshaller() throws JAXBException;

    /** <p>Creates a new instance of {@link Unmarshaller}. The
     * {@link Unmarshaller} can be used
     * to convert XML data into JAXB objects.</p>
     * <p><em>Note</em>: Unmarshallers are reusable, but not reentrant (thread safe).</p>
     */
    public abstract Unmarshaller createUnmarshaller() throws JAXBException;

    /** <p>Creates a new instance of {@link Validator}. The
     * {@link Validator} can be used to validate JAXB objects.</p>
     * @deprecated Validators are deprecated in favour of the
     * {@link javax.xml.validation.Validator validation framework}
     * from JAXP 1.3.
     */
    @SuppressWarnings("deprecation")
    public abstract Validator createValidator() throws JAXBException;

    /**
     * Creates a {@link Binder}, which may be used to associate
     * JAXB objects with XML nodes.
     * @param domType A class, which indicates the DOM API to
     *   use for the XML nodes. This is typically the node class,
     *   for example {@link Node}.
     * @return A new {@link Binder}.
     * @throws UnsupportedOperationException The DOM API given
     *   by <code>pDomType</code> is unknown.
     * @since JAXB 2.0
     */
    public <T> Binder<T> createBinder(
            @SuppressWarnings("unused") Class<T> pDomType) {
        throw new UnsupportedOperationException("Creating Binders is unsupported");
    }

    /**
     * Creates a {@link Binder}, which may be used to associate
     * JAXB objects with DOM nodes.
     * @return A new {@link Binder}.
     * @throws UnsupportedOperationException The JAXB Provider doesn't
     *   support Binders.
     * @since JAXB 2.0
     */
    public Binder<Node> createBinder() {
        return createBinder(Node.class);
    }

    /**
     * Creates a {@link JAXBIntrospector} for introspecting JAXB objects.
     * @return A new {@link JAXBIntrospector}.
     * @throws UnsupportedOperationException The JAXB Provider doesn't
     *   support JAXBIntrospectors.
     * @since JAXB 2.0
     */
    public JAXBIntrospector createJAXBIntrospector() {
        throw new UnsupportedOperationException("Creating JAXBIntrospectors is unsupported.");
    }

    /**
     * Writes this contexts schema documents to the given destination.
     * @param pResolver An object, which controls the destination.
     * @throws IOException An error occurred while writing the schema
     *   documents.
     * @throws UnsupportedOperationException The JAXB Provider doesn't
     *   support writing schema documents.
     * @since JAXB 2.0
     */
    @SuppressWarnings("unused")
    public void generateSchema(SchemaOutputResolver pResolver) throws IOException  {
        throw new UnsupportedOperationException("Writing schema documents is unsupported.");
    }
}
