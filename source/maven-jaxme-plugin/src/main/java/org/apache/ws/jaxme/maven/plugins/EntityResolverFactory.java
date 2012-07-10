/*
 * Copyright 2006  The Apache Software Foundation
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
package org.apache.ws.jaxme.maven.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;


/**
 * A factory for entity resolvers.
 */
public class EntityResolverFactory {
	private abstract static class EntityResolver2Impl implements EntityResolver2 {
		public InputSource getExternalSubset(String name, String baseURI)
				throws SAXException, IOException {
			return null;
		}
		public InputSource resolveEntity(String pName, String pPublicId, String pBaseURI, String pSystemId)
				throws SAXException, IOException {
			return resolveEntity(pPublicId, pSystemId);
		}
	}

	private static class URLEntityResolver extends EntityResolver2Impl {
	    private final String prefix;
        URLEntityResolver(String pPrefix) {
            prefix = pPrefix;
        }
        public InputSource resolveEntity(String pPublicId, String pSystemId)
                throws SAXException, IOException {
            if (pSystemId == null) {
                return null;
            }
            final String u = prefix + pSystemId;
            final URL url;
            try {
                url = new URL(u);
            } catch (MalformedURLException e) {
                throw new SAXException("Malformed URL: " + u, e);
            }
            try {
                InputSource isource = new InputSource(url.openStream());
                isource.setSystemId(url.toExternalForm());
                return isource;
            } catch (IOException e) {
                throw new SAXException("Failed to open URL " + url + ": " + e.getMessage(), e);
            }
        }
    }

    private static class FilesEntityResolver extends EntityResolver2Impl {
		private final String prefix;
		FilesEntityResolver(String pPrefix) {
			prefix = pPrefix;
		}
		public InputSource resolveEntity(String pPublicId, String pSystemId)
				throws SAXException, IOException {
			if (pSystemId == null) {
				return null;
			}
			File file = new File(prefix + pSystemId);
			if (!file.exists()) {
				return null;
			}
			final InputSource isource;
			try {
				isource = new InputSource(new FileInputStream(file));
			} catch (IOException e) {
				throw new SAXException("Failed to open file " + file.getPath() + ": " + e.getMessage(), e);
			}
			isource.setSystemId(file.toURI().toURL().toExternalForm());
			return isource;
		}
	}

	private static class ClasspathEntityResolver extends EntityResolver2Impl {
		private final String prefix;
		ClasspathEntityResolver(String pPrefix) {
			prefix = pPrefix == null ? "" : pPrefix;
		}
		public InputSource resolveEntity(String pPublicId, String pSystemId) throws SAXException, IOException {
			if (pSystemId == null) {
				return null;
			}
			URL url = Thread.currentThread().getContextClassLoader().getResource(prefix + pSystemId);
			if (url == null) {
				return null;
			}
			final InputSource isource;
			try {
				isource = new InputSource(url.openStream());
			} catch (IOException e) {
				throw new SAXException("Failed to open URL " + url.toExternalForm() + ": " + e.getMessage(), e);
			}
			isource.setSystemId(url.toExternalForm());
			return isource;
		}
	}

	static EntityResolver getEntityResolver(String pMode)
			throws MojoFailureException, MojoExecutionException {
		if (pMode == null  ||  pMode.length() == 0  ||  "files".equals(pMode)) {
			return null;
		}
		if (pMode.startsWith("url:")) {
		    final String prefix = pMode.substring("url:".length());
            return new URLEntityResolver(prefix);
        }
        if (pMode.startsWith("files:")) {
			final String prefix = pMode.substring("files:".length());
			return new FilesEntityResolver(prefix);
		}
		if ("classpath".equals(pMode)) {
			return new ClasspathEntityResolver(null);
		}
		if (pMode.startsWith("classpath:")) {
			final String prefix = pMode.substring("classpath:".length());
			return new ClasspathEntityResolver(prefix);
		}
		if (pMode.startsWith("class:")) {
			final String className = pMode.substring("class:".length());
			try {
				return (EntityResolver) Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
			} catch (ClassNotFoundException e) {
				throw new MojoExecutionException("Failed to load resolver class " + className + ": " + e.getMessage(), e);
			} catch (InstantiationException e) {
				throw new MojoExecutionException("Failed to instantiate resolver class " + className + ": " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new MojoExecutionException("Illegal access to resolver class " + className + ": " + e.getMessage(), e);
			}
		}
		throw new MojoFailureException("Invalid value for resolver mode: " + pMode);
	}
}
