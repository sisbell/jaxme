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

import java.util.Map;

import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.JaxMeSchemaReader;


/**
 * Default implementation of {@link ISchemaCollection}.
 */
public class SchemaCollection implements ISchemaCollection {
	private FileSet[] bindings, depends, produces, schemas;
	private String[] sgFactoryChain;
	private String packageName, srcTarget, resourceTarget, schemaReader;
	private String schemaTargetPrefix;
	private boolean extension, removeOldOutput, validating;
	private Map properties;
	private String entityResolverMode;

	/**
	 * Sets a set of external binding files, which are
	 * being applied.
	 */
	public void setBindings(FileSet[] pBindings) {
		bindings = pBindings;
	}

	public FileSet[] getBindings() {
		return bindings;
	}

	/**
	 * Sets a set of files, which are referenced from within
	 * the XML schema files, but aren't processed as XML schema
	 * files for themselves. Specifying files in the "depends" set
	 * allows to include them into the uptodate check.
	 */
	public void setDepends(FileSet[] pDepends) {
		depends = pDepends;
	}

	public FileSet[] getDepends() {
		return depends;
	}

	/**
	 * Sets the Java package name.
	 */
	public void setPackageName(String pPackageName) {
		packageName = pPackageName;
	}

	public String getPackageName() {
		return packageName;
	}

	/**
	 * Specifies a set of files, which are being produced by the
	 * source generator. Any element in the set may contain wildcards.
	 * The set of produced files is used for checking,
	 * whether the generated files are uptodate. If that is
	 * the case, then the generator will omit recreating
	 * them. If the uptodate check fails, or if the "forceCreation"
	 * property is turned on, then the source generator
	 * will actually be invoked. In that case, the set of
	 * produces files will also be used for removing old
	 * files, if the "removeOldOutput" feature is turned
	 * on.
	 */
	public void setProduces(FileSet[] pProduces) {
		produces = pProduces;
	}

	public FileSet[] getProduces() {
		return produces;
	}

	/**
	 * The target directory for resource files. Defaults to
	 * ${project.build.directory}/generated-sources/jaxme/resources.
	 */
	public void setResourceTarget(String pResourceTarget) {
		resourceTarget = pResourceTarget;
	}

	public String getResourceTarget() {
		return resourceTarget;
	}

	/**
	 * Specifies the schema reader being used. The default
	 * schema reader is an instance of {@link JAXBSchemaReader}.
	 * The default changes, if "extension" is set to true,
	 * in which case the {@link JaxMeSchemaReader} is being
	 * used.
	 */
	public void setSchemaReader(String pSchemaReader) {
		schemaReader = pSchemaReader;
	}

	public String getSchemaReader() {
		return schemaReader;
	}

	/**
	 * Sets the set of schemas being compiled. Schema names may
	 * include wildcards.
	 */
	public void setSchemas(FileSet[] pSchemas) {
		schemas = pSchemas;
	}

	public FileSet[] getSchemas() {
		return schemas;
	}

	/**
	 * The set of factory chains, which are being added to the
	 * generator. Factory chains are modifying the generators
	 * behaviour. A good example is the
	 * <code>org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG</code>.
	 */
	public void setSgFactoryChains(String[] pSgFactoryChain) {
		sgFactoryChain = pSgFactoryChain;
	}

	public String[] getSgFactoryChains() {
		return sgFactoryChain;
	}

	/**
	 * The target directory for resource files. Defaults to
	 * ${project.build.directory}/generated-sources/jaxme/java.
	 */
	public void setSrcTarget(String pSrcTarget) {
		srcTarget = pSrcTarget;
	}

	public String getSrcTarget() {
		return srcTarget;
	}

	/**
	 * Setting this property to true enables JaxMe's extension
	 * mode. Several vendor extensions are available, including
	 * some extensions, which are originally specified by the
	 * JAXB reference implementation.
	 */
	public void setExtension(boolean pExtension) {
		extension = pExtension;
	}

	public boolean isExtension() {
		return extension;
	}

	/**
	 * If this property is set to true, and one or more
	 * produces "produces" elements are specified, then the plugin
	 * will remove all files matching the "produces" elements
	 * before running the source generator.
	 * @parameter expression="false"
	 */
	public void setRemoveOldOutput(boolean pRemoveOldOutput) {
		removeOldOutput = pRemoveOldOutput;
	}

	public boolean isRemoveOldOutput() {
		return removeOldOutput;
	}

	/**
	 * Setting this property to true advices the plugin to use
	 * a validating XML parser for reading the schema files.
	 * By default, validation is disabled.
	 */
	public void setValidating(boolean pValidating) {
		validating = pValidating;
	}

	public boolean isValidating() {
		return validating;
	}

	/**
	 * Sets a set of properties, which are being set on the
	 * generator.
	 */
	public void setProperties(Map pProperties) {
		properties = pProperties;
	}

	public Map getProperties() {
		return properties;
	}

	/**
	 * Sets a prefix, where to add the schema files to the generated jar
	 * file, if you want that. Defaults to null, in which case no schema
	 * files are being added. Use "" or "/" to add schema files to the
	 * jar files root directory.
	 */
	public void setSchemaTargetPrefix(String schemaTargetPrefix) {
		this.schemaTargetPrefix = schemaTargetPrefix;
	}

	public String getSchemaTargetPrefix() {
		return schemaTargetPrefix;
	}

	public String getEntityResolverMode() {
		return entityResolverMode;
	}

	/**
	 * Returns a mechanism for entity resolving. The following modes
	 * are available:
	 * <table>
	 *   <tr><td>files</td><td>Entities are resolved in the local file system.
	 *     This is the default</td></tr>
	 *   <tr><td>classpath</td><td>Entities are resolved in the classpath.</td></tr>
	 *   <tr><td>class:&lt;classname&gt;</td><td>
	 *     Entities are resolved through a SAX entity resolver, which is an instance
	 *     of the given class</td></tr>
	 * </table>
	 */
	public void setEntityResolverMode(String entityResolverMode) {
		this.entityResolverMode = entityResolverMode;
	}
}
