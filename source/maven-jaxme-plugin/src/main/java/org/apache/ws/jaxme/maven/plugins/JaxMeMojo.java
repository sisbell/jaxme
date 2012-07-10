/*
 * Copyright 2005-2006  The Apache Software Foundation
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.JaxMeSchemaReader;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.logging.LoggerFactory;


/**
 * @goal jaxme
 * @phase generate-sources
 * @description Runs the JaxMe binding compiler and generates the source files.
 * @requiresDependencyResolution compile
 */
public class JaxMeMojo extends AbstractMojo implements ISchemaCollection {
	/** This property specifies a set of external
	 * binding files, which are being applied.
	 * @parameter
	 */
	private FileSet[] bindings;

	/** Specifies classpath elements, which should be added to
	 * the plugins classpath.
	 * @parameter expression="${project.compileClasspathElements}"
	 * @required
	 * @readonly
	 */
	private List classpathElements;

	/** This property specifies a set of files, which
	 * are referenced from within the XML schema files,
	 * but aren't processed as XML schema files for
	 * themselves. Specifying files in the "depends" set
	 * allows to include them into the uptodate check.
	 * @parameter
	 */
	private FileSet[] depends;

	/** Setting this property to true enables JaxMe's extension
	 * mode. Several vendor extensions are available, including
	 * some extensions, which are originally specified by the
	 * JAXB reference implementation.
	 * @parameter default-value="false"
	 */
	private boolean extension;

	/** Setting this property to suppresses the builtin
	 * uptodate check.
	 * @parameter expression="${jaxme.forceCreation}" default-value="false"
	 */
	private boolean forceCreation;

	/** Sets the Java package name.
	 * @parameter
	 */
	private String packageName;

	/** This property specifies a set of files, which are
	 * being produced by the source generator. Any element
	 * in the set may contain wildcards.
	 * The set of produced files is used for checking,
	 * whether the generated files are uptodate. If that is
	 * the case, then the generator will omit recreating
	 * them. If the uptodate check fails, or if the "forceCreation"
	 * property is turned on, then the source generator
	 * will actually be invoked. In that case, the set of
	 * produces files will also be used for removing old
	 * files, if the "removeOldOutput" feature is turned
	 * on.
	 * @parameter
	 */
	private FileSet[] produces;

    /** The properties being set on the generator.
	 * @parameter
	 */
	private Map properties;

	/** Setting this property to true will make the generated
	 * files read-only.
	 * @parameter default-value="false"
	 */
	private boolean readOnly;

	/** If this property is set to true, and one or more
	 * produces "produces" elements are specified, then the plugin
	 * will remove all files matching the "produces" elements
	 * before running the source generator.
	 * @parameter default-value="false"
	 */
	private boolean removeOldOutput;

	/** Sets the schema reader being used. The default
	 * schema reader is an instance of {@link JAXBSchemaReader}.
	 * The default changes, if "extension" is set to true,
	 * in which case the {@link JaxMeSchemaReader} is being
	 * used.
	 * @parameter
	 */
	private String schemaReader;
	
	/** The set of schemas being compiled. Schema names may
	 * include wildcards. By default the fileset "src/main/jaxme/*"
	 * is used.
	 * @parameter
	 */
	private FileSet[] schemas;

	/** The set of factory chains, which are being added to the
	 * generator. Factory chains are modifying the generators
	 * behaviour. A good example is the
	 * <code>org.apache.ws.jaxme.pm.generator.jdbc.JaxMeJdbcSG</code>.
	 * @parameter
	 */
	private String[] sgFactoryChains;

	/** The target directory for source files. Defaults to
	 * @parameter expression="${project.build.directory}/generated-sources/jaxme"
	 */
	private String srcTarget;

	/** The target directory for resource files. Defaults to
	 * @parameter expression="${project.build.directory}/generated-resources/jaxme"
	 */
	private String resourceTarget;

	/** Setting this property to true advices the plugin to use
	 * a validating XML parser for reading the schema files.
	 * By default, validation is disabled.
	 * @parameter default-value="false"
	 */
	private boolean validating;

	/**
	 * Specifies additional sets of schema collections.
	 * @parameter
	 */
	private SchemaCollection[] schemaCollections;

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
	 * @parameter default-value="files"
	 */
	private String entityResolverMode;

	/**
	 * Returns a prefix, where to add the schema files to the generated jar
	 * file, if you want that. Defaults to null, in which case no schema
	 * files are being added. Use "" or "/" to add schema files to the
	 * jar files root directory.
	 * @parameter
	 */
	private String schemaTargetPrefix;

	/** The Maven project.
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

	/**
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @read-only
     */
    private List pluginArtifacts;

	public FileSet[] getBindings() {
		return bindings;
	}

	protected List getClasspathElements() {
		return classpathElements;
	}

	public FileSet[] getDepends() {
		return depends;
	}

	public boolean isExtension() {
		return extension;
	}


	protected boolean isForceCreation() {
		return forceCreation;
	}

	public String getPackageName() {
		return packageName;
	}

	public FileSet[] getProduces() {
		return produces;
	}

	protected MavenProject getProject() {
		return project;
	}

	public Map getProperties() {
		return properties;
	}

	protected boolean isReadOnly() {
		return readOnly;
	}

	public boolean isRemoveOldOutput() {
		return removeOldOutput;
	}

	public String getSchemaReader() {
		return schemaReader;
	}

	public String[] getSgFactoryChains() {
		return sgFactoryChains;
	}

	public String getSrcTarget() {
		return srcTarget;
	}

	public String getResourceTarget() {
		return resourceTarget;
	}

	public boolean isValidating() {
		return validating;
	}

	public String getSchemaTargetPrefix() {
		return schemaTargetPrefix;
	}

	public String getEntityResolverMode() {
		return entityResolverMode;
	}

	private ClassLoader getClassLoader(ClassLoader pParent) throws MojoFailureException {
		List clElements = getClasspathElements();
		if (clElements == null  &&  clElements.size() == 0) {
			return pParent;
		}
		URL[] urls = new URL[clElements.size()];
		StringBuffer sb = new StringBuffer();
		for (int i = 0;  i < clElements.size();  i++) {
			final String elem = (String) clElements.get(i);
			File f = new File(elem);
			if (!f.isAbsolute()) {
				f = new File(getProject().getBasedir(), elem);
			}
			try {
				urls[i] = f.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new MojoFailureException("Invalid classpath element: " + elem);
			}
			if (i > 0) {
				sb.append(File.pathSeparator);
			}
			sb.append(urls[i]);
		}
		getLog().debug("Using classpath " + sb);
		return new URLClassLoader(urls, pParent);
	}

	public FileSet[] getSchemas() {
		return schemas;
	}

	private boolean isEmpty(String pValue) {
		return pValue == null  ||  pValue.length() == 0;
	}

	private boolean isEmpty(Object[] pArray) {
		return pArray == null  ||  pArray.length == 0;
	}

	private void configure(ISchemaCollection pCollection) {
		if (isEmpty(pCollection.getSrcTarget())) {
			((SchemaCollection) pCollection).setSrcTarget(new File(project.getBuild().getDirectory(), "generated-sources/jaxme").getPath());
		}
		if (isEmpty(pCollection.getResourceTarget())) {
			((SchemaCollection) pCollection).setResourceTarget(new File(project.getBuild().getDirectory(), "generated-resources/jaxme").getPath());
		}
	}

	private void addPluginArtifacts() {
		Set projectArtifacts = new HashSet(project.getDependencyArtifacts());
		for (Iterator iter = pluginArtifacts.iterator();  iter.hasNext();  ) {
			Artifact pluginArtifact = (Artifact) iter.next();
			if (pluginArtifact.getGroupId().startsWith("org.apache.ws.jaxme")) {
				boolean found = false;
				for (Iterator iter2 = projectArtifacts.iterator();  iter2.hasNext();  ) {
					Artifact projectArtifact = (Artifact) iter2.next();
					if (pluginArtifact.getGroupId().equals(projectArtifact.getGroupId())  &&
						pluginArtifact.getArtifactId().equals(projectArtifact.getArtifactId())) {
						found = true;
						break;
					}
				}
				if (!found) {
				    getLog().warn("No jaxme2 artifact is on your classpath. Please check your POM.");
				}
			}
		}
		project.setDependencyArtifacts(projectArtifacts);
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClassLoader(SchemaReader.class.getClassLoader()));
		LoggerFactory lf = LoggerAccess.getLoggerFactory();
		try {
			LoggerAccess.setLoggerFactory(new LoggerFactory(){
				public Logger getLogger(String pName) {
					return new MavenProjectLogger(getLog(), pName);
				}
			});

			FileSet[] schemaArray = getSchemas();
			final ISchemaCollection[] collections = schemaCollections;
			SchemaCollectionProcessor processor = new SchemaCollectionProcessor(project, getLog(), isForceCreation(), isReadOnly());
			if (isEmpty(schemaArray)) {
				if (schemaCollections == null  ||  schemaCollections.length == 0) {
					FileSet fileSet = new FileSet();
					fileSet.setDirectory(new File(project.getBasedir(), "src/main/jaxme"));
					schemaArray = schemas = new FileSet[]{fileSet};
				}
			}
			if (!isEmpty(schemaArray)) {
				getLog().debug("Processing implicit schemaCollection");
				configure(this);
				processor.process(this);
			}
			if (collections != null) {
				for (int i = 0;  i < collections.length;  i++) {
					getLog().debug("Processing schemaCollection " + i);
					configure(collections[i]);
					processor.process(collections[i]);
				}
			}
			processor.fixProjectSettings();
			addPluginArtifacts();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCl);
			LoggerAccess.setLoggerFactory(lf);
		}
	}
}
