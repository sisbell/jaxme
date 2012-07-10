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
package org.apache.ws.jaxme.generator.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/** <p>The Generator is reading an input schema. The schema is
 * converted into a DOM tree. Finally one or more source
 * writers are executed.
 */
public class GeneratorImpl implements Generator {
	private static final Logger log = LoggerAccess.getLogger(GeneratorImpl.class);
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	static {
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
	}

	private SchemaReader schemaReader;
	private java.io.File targetDirectory;
	private java.io.File resourceTargetDirectory;
	private Map properties = new HashMap();
	private int nextKey;
	private boolean isValidating, isForcingOverwrite, isSettingReadOnly;
	private EntityResolver entityResolver;
	private final List bindings = new ArrayList();
	
	/** <p>Sets the {@link EntityResolver} being used to import external
	 * schemata.</p>
	 */
	public void setEntityResolver(EntityResolver pEntityResolver) {
		entityResolver = pEntityResolver;
	}
	
	/** <p>Returns the {@link EntityResolver} being used to import external
	 * schemata.</p>
	 */
	public EntityResolver getEntityResolver() {
		return entityResolver;
	}
	
	/** <p>Returns whether the generator is forcing an overwrite of files.</p>
	 */
	public boolean isForcingOverwrite() {
		return isForcingOverwrite;
	}
	
	/** <p>Sets whether the generator is forcing an overwrite of files.</p>
	 */
	public void setForcingOverwrite(boolean pIsForcingOverwrite) {
		isForcingOverwrite = pIsForcingOverwrite;
	}
	
	/** <p>Returns whether the generator will create files in read-only mode.</p>
	 */
	public boolean isSettingReadOnly() {
		return isSettingReadOnly;
	}
	
	/** <p>Sets whether the generator will create files in read-only mode.</p>
	 */
	public void setSettingReadOnly(boolean pIsSettingReadOnly) {
		isSettingReadOnly = pIsSettingReadOnly;
	}
	
	public boolean isValidating() {
		return isValidating;
	}
	
	public void setValidating(boolean pIsValidating) {
		isValidating = pIsValidating;
	}
	
	/** Creates a new GeneratorImpl */
	public GeneratorImpl() {
	}
	
	/** <p>Sets the SchemaReader.</p>
	 */
	public void setSchemaReader(SchemaReader pReader) {
		schemaReader = pReader;
	}
	
	/** <p>Returns the SchemaReader.</p>
	 */
	public SchemaReader getSchemaReader() {
		return schemaReader;
	}
	
	public void setTargetDirectory(File pDirectory) {
		targetDirectory = pDirectory;
	}
	
	public File getTargetDirectory() {
		return targetDirectory;
	}
	
	public void setResourceTargetDirectory(File pDirectory) {
		resourceTargetDirectory = pDirectory;
	}
	
	public File getResourceTargetDirectory() {
		return resourceTargetDirectory;
	}

	public SchemaSG generate(InputSource pSource) throws Exception {
		SchemaReader sr = getSchemaReader();
		sr.setGenerator(this);
		
		SchemaSG s = sr.parse(pSource);
		s.generate();
		File targetDir = getTargetDirectory();
		File resourceTargetDir = getResourceTargetDirectory();
		if (resourceTargetDir == null) {
			resourceTargetDir = targetDir;
		}
		JavaSourceFactory jsf = s.getJavaSourceFactory();
		for (Iterator iter = jsf.getJavaSources();  iter.hasNext();  ) {
			((JavaSource) iter.next()).setForcingFullyQualifiedName(true);
		}
		s.getJavaSourceFactory().write(targetDir, resourceTargetDir);
		
		return s;
	}
	
	
	/** <p>Opens the given file, calls the specified SchemaReaders
	 * () method and SourceWriters write() method.</p>
	 */
	public SchemaSG generate(File pFile) throws Exception {
		final String mName = "generate(File)";
		log.finer(mName, "->", pFile);
		String path = pFile.getAbsolutePath();
		if (!pFile.exists()) {
			throw new java.io.FileNotFoundException("File does not exist: " + path);
		}
		if (!pFile.isFile()) {
			throw new java.io.FileNotFoundException("Not a file: " + path);
		}
		
		InputSource isource = new InputSource(new FileInputStream(pFile));
		isource.setSystemId(pFile.toURL().toString());
		SchemaSG s = generate(isource);
		log.finer(mName, "<-", s);
		return s;
	}
	
	/** <p>Opens the given URL, calls the specified SchemaReaders
	 * () method and SourceWriters write() method.</p>
	 */
	public SchemaSG generate(java.net.URL pURL) throws Exception {
		final String mName = "generate(URL)";
		log.entering(mName, pURL);
		java.net.URLConnection conn = pURL.openConnection();
		
		InputSource isource = new InputSource(conn.getInputStream());
		isource.setSystemId(pURL.toString());
		SchemaSG s = generate(isource);
		log.exiting(mName, s);
		return s;
	}
	
	public String getProperty(String pName) {
		return (String) properties.get(pName);
	}
	
	public String getProperty(String pName, String pDefault) {
		String result = (String) properties.get(pName);
		return (result == null) ? pDefault : result;
	}
	
	public void setProperty(String pName, String pValue) {
		properties.put(pName, pValue);
	}
	
	public String getKey() {
		return Integer.toString(nextKey++);
	}

	public void addBindings(InputSource pSource) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = dbf.newDocumentBuilder();
		EntityResolver er = getEntityResolver();
		if (er != null) {
			db.setEntityResolver(er);
		}
		bindings.add(db.parse(pSource));
	}

	public Document[] getBindings() {
		return (Document[]) bindings.toArray(new Document[bindings.size()]);
	}
}
