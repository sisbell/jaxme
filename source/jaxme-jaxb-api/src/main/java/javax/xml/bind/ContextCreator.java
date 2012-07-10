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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * This class is actually implementing the various
 * JAXBContext.newInstance() methods. It isn't part of the
 * public API, which is why it is package private.
 */
class ContextCreator {
    private static final String JAXB_CONTEXT_CLASS = JAXBContext.class.getName();
    private static final String META_INF_SERVICE = "META-INF/services/" + JAXB_CONTEXT_CLASS;

    /**
     * Interface of a package list.
     */
    private interface PackageList {
        /**
         * Returns, whether the list contains more packages.
         */
        boolean hasMore();
        /**
         * Returns the next package name in the list.
         */
        String nextPackage();
        /**
         * Returns the next class loader in the list.
         * @return
         */
        ClassLoader nextClassLoader();
    }

    /**
     * Attempts to find a JAXBContext factory class
     * via jaxb.properties.
     */
    private static Class<?> getFactoryClassFromJaxbProperties(String pFactoryProperty,
            PackageList pPackageList) throws JAXBException {
        while (pPackageList.hasMore()) {
            String packageName = pPackageList.nextPackage();
            ClassLoader cl = pPackageList.nextClassLoader();
            if (packageName == null) {
                continue;
            }
            String resourceName = packageName.replace('.', '/') + "/jaxb.properties";
            URL resource = cl.getResource(resourceName);
            if (resource == null) {
                continue;
            }
            Properties props = new Properties();
            InputStream istream = null;
            try {
                istream = resource.openStream();
                props.load(istream);
                istream.close();
                istream = null;
            } catch (IOException e) {
                throw new JAXBException("Failed to load property file " + resource, e);
            } finally {
                if (istream != null) { try { istream.close(); } catch (Throwable t) { /* Ignore me */ } }
            }
            String className = props.getProperty(pFactoryProperty);
            if (className == null) {
                throw new JAXBException("The property " + pFactoryProperty
                        + " is not set in " + resource);
            }
            return loadFactoryClass(cl, className);
        }
        return null;
    }

    /**
     * Attempts to find a JAXBContext factory class
     * in the system properties.
     */
    private static Class<?> getFactoryClassFromSystemProperties(
            ClassLoader pClassLoader) throws JAXBException {
        final String className;
        try {
            className = System.getProperty(JAXB_CONTEXT_CLASS);
        } catch (SecurityException e) {
            return null;
        }
        if (className == null) {
            return null;
        }
        return loadFactoryClass(pClassLoader, className);
    }

    /**
     * Attempts to find a JAXBContext factory class
     * in META-INF/services.
     */
    private static Class<?> getFactoryClassFromMetaInfServices(
            ClassLoader pClassLoader) throws JAXBException {
        URL url = pClassLoader.getResource(META_INF_SERVICE);
        if (url == null) {
            return null;
        }
        InputStream istream = null;
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(istream, "UTF-8"));
            String factoryClassName = r.readLine().trim();
            r.close();
            istream = url.openStream();
            istream.close();
            istream = null;
            if (factoryClassName == null  ||  "".equals(factoryClassName)) {
                throw new JAXBException("No factory class name found in " + url);
            }
            return loadFactoryClass(pClassLoader, factoryClassName);
        } catch (IOException e) {
            throw new JAXBException("Failed to load " + url + ": " + e.getMessage(), e);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (Throwable t) {
                    // Ignore me
                }
            }
        }
    }

    /**
     * Loads the JAXBContext factory class.
     */
    private static Class<?> loadFactoryClass(ClassLoader pClassLoader,
            String pFactoryClass) throws JAXBException {
        try {
            return pClassLoader.loadClass(pFactoryClass);
        } catch (ClassNotFoundException e) {
            throw new JAXBException("Unable to load class "
                    + pFactoryClass
                    + " via ClassLoader " + pClassLoader);
        }
    }

    private static JAXBContext createInstance(Method pMethod, Object[] pArgs)
            throws JAXBException {
        try {
            Object o = pMethod.invoke(null, pArgs);
            if (o == null) {
                throw new JAXBException("The method "
                        + pMethod + " returned null.");
            }
            if(!(o instanceof JAXBContext)) {
                throw new JAXBException("The created instance of "
                        + o.getClass().getName()
                        + " doesn't implement " + JAXB_CONTEXT_CLASS);
            }
            return (JAXBContext) o;
        } catch (IllegalAccessException e) {
            throw new JAXBException("Illegal access to method "
                    + pMethod + ": " + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof JAXBException) {
                throw (JAXBException)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            throw new JAXBException("Failed to invoke method "
                    + pMethod + ": " + e.getMessage(), e);
        }
    }

    /**
     * Implements {@link JAXBContext#newInstance(String, ClassLoader, Map)}.
     */
    static JAXBContext newContext(String pFactoryProperty,
            final String pPath,
            final ClassLoader pClassLoader,
            Map<String,?> pProperties)
        throws JAXBException {
        if (pPath == null) {
            throw new JAXBException("The context path must not be null.");
        }
        if (pClassLoader == null) {
            throw new JAXBException("The classloader must not be null.");
        }
        final PackageList packageList = new PackageList(){
            private final StringTokenizer st = new StringTokenizer(pPath, ":");
            public boolean hasMore() {
                return st.hasMoreTokens();
            }
            public ClassLoader nextClassLoader() {
                return pClassLoader;
            }
            public String nextPackage() {
                return st.nextToken();
            }
        };
        Class<?> factoryClass = getFactoryClassFromJaxbProperties(pFactoryProperty, packageList);
        if (factoryClass == null) {
            factoryClass = getFactoryClassFromSystemProperties(pClassLoader);
            if (factoryClass == null) {
                factoryClass = getFactoryClassFromMetaInfServices(pClassLoader);
                if (factoryClass == null) {
                    throw new JAXBException("Unable to determine JAXBContext class.");
                }
            }
        }

        Method m;
        Object[] args;
        try {
            m = factoryClass.getMethod("createContext", String.class, ClassLoader.class, Map.class);
            args = new Object[]{pPath, pClassLoader, pProperties};
        } catch (NoSuchMethodException e1) {
            try {
                m = factoryClass.getMethod("createContext", String.class, ClassLoader.class);
                args = new Object[]{pPath, pClassLoader};
            } catch (NoSuchMethodException e2) {
                throw new JAXBException("Factory class "
                        + factoryClass.getName()
                        + " doesn't have a method createContext(String,ClassLoader,Map).");
            }
        }
        return createInstance(m, args);
    }


    /**
     * Implements {@link JAXBContext#newInstance(Class[], Map)}.
     */
    static JAXBContext newContext(String pFactoryProperty,
            final Class<?>[] classes,
            Map<String,?> properties)
            throws JAXBException {
        final PackageList packageList = new PackageList() {
            int i = 0;
            public boolean hasMore() { return i < classes.length; }
            public ClassLoader nextClassLoader() {
                return classes[i++].getClassLoader();
            }
            public String nextPackage() {
                final Class<?> c = classes[i];
                final Package pkg = c.getPackage();
                String pkgName = pkg.getName();
                if (pkgName != null) {
                    return pkgName;
                }
                int offset = c.getName().lastIndexOf('.');
                if (offset == -1) {
                    return null;
                }
                return c.getName().substring(0, offset);
            }
        };

        Class<?> factoryClass = getFactoryClassFromJaxbProperties(pFactoryProperty, packageList);
        if (factoryClass == null) {
            factoryClass = getFactoryClassFromSystemProperties(Thread.currentThread().getContextClassLoader());
            if (factoryClass == null) {
                factoryClass = getFactoryClassFromMetaInfServices(Thread.currentThread().getContextClassLoader());
                if (factoryClass == null) {
                    throw new JAXBException("Unable to determine JAXBContext class.");
                }
            }
        }

        Method m;
        try {
            m = factoryClass.getMethod("createContext", Class[].class, Map.class);
        } catch (NoSuchMethodException e) {
            throw new JAXBException("Factory class "
                    + factoryClass.getName()
                    + " doesn't have a method createContext(Class[],Map).");
        }
        return createInstance(m, new Object[]{classes, properties});
    }
}
