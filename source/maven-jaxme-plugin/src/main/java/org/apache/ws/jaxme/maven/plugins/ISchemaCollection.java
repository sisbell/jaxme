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
 * Specifies the details of one schema collection, which is being
 * compiled.
 */
public interface ISchemaCollection {
	/**
	 * Returns the sets of external binding files, which are
	 * being applied.
	 */
	FileSet[] getBindings();

	/**
	 * Specifies the sets of files, which are referenced from within
	 * the XML schema files, but aren't processed as XML schema
	 * files for themselves. Specifying files in the "depends" set
	 * allows to include them into the uptodate check.
	 */
	FileSet[] getDepends();

	/**
	 * Setting this property to true enables JaxMe's extension
	 * mode. Several vendor extensions are available, including
	 * some extensions, which are originally specified by the
	 * JAXB reference implementation.
	 */
	boolean isExtension();

	/**
	 * Specifies the Java package name.
	 */
	String getPackageName();

	/**
	 * Specifies the sets of files, which are being produced by the
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
	FileSet[] getProduces();

	/**
	 * If this property is set to true, and one or more
	 * produces "produces" elements are specified, then the plugin
	 * will remove all files matching the "produces" elements
	 * before running the source generator.
	 * @parameter expression="false"
	 */
	boolean isRemoveOldOutput();

	/**
	 * Specifies the schema reader being used. The default
	 * schema reader is an instance of {@link JAXBSchemaReader}.
	 * The default changes, if "extension" is set to true,
	 * in which case the {@link JaxMeSchemaReader} is being
	 * used.
	 */
	String getSchemaReader();
	
	/**
	 * Specifies the set of schemas being compiled.
	 */
	FileSet[] getSchemas();

	/**
	 * The set of factory chains, which are being added to the
	 * generator. Factory chains are modifying the generators
	 * behaviour. A good example is the
	 * <code>org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG</code>.
	 */
	String[] getSgFactoryChains();

	/**
	 * The target directory for source files. Defaults to
     * ${project.build.directory}/generated-sources/jaxme.
	 */
	String getSrcTarget();

	/**
	 * The target directory for resource files. Defaults to
	 * ${project.build.directory}/generated-resources/jaxme.
	 */
	String getResourceTarget();

	/**
	 * Setting this property to true advices the plugin to use
	 * a validating XML parser for reading the schema files.
	 * By default, validation is disabled.
	 */
	boolean isValidating();

	/**
	 * Returns a set of properties, which are being set on the
	 * generator.
	 */
	Map getProperties();

	/**
	 * Returns a prefix, where to add the schema files to the generated jar
	 * file, if you want that. Defaults to null, in which case no schema
	 * files are being added. Use "" or "/" to add schema files to the
	 * jar files root directory.
	 */
	String getSchemaTargetPrefix();

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
	String getEntityResolverMode();
}
