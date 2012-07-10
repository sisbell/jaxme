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
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.impl.GeneratorImpl;
import org.apache.ws.jaxme.generator.sg.SGFactoryChain;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.JaxMeSchemaReader;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * An instance of this class is responsible for processing a single
 * schema collection.
 */
class SchemaCollectionProcessor {
	private static class FileSpec {
		private final File baseDir;
		private final String path;
		private final File file;
		FileSpec(File pBaseDir, String pPath) {
			baseDir = pBaseDir;
			path = pPath;
			file = new File(baseDir, path);
		}
		File getFile() { return file; }
		String getPath() { return path; }
	}

	private final Log log;
	private final MavenProject project;
	private final boolean forceCreation, readOnly;
	private final Map sourceDirectories = new HashMap(), resourcecDirectories = new HashMap();

	SchemaCollectionProcessor(MavenProject pProject, Log pLog, boolean pForceCreation,
							  boolean pReadOnly) {
		project = pProject;
		log = pLog;
		forceCreation = pForceCreation;
		readOnly = pReadOnly;
	}

	private FileSpec[] getFiles(FileSet[] pSpec) {
		if (pSpec == null  ||  pSpec.length == 0) {
			return new FileSpec[0];
		}
		final List list = new ArrayList();
		for (int i = 0;  i < pSpec.length;  i++) {
			FileSet set = pSpec[i];
			File dir = set.getDirectory();
			if (!dir.isAbsolute()) {
				dir = new File(project.getBasedir(), dir.getPath());
			}
			DirectoryScanner ds = new DirectoryScanner();
			ds.setBasedir(dir);
			ds.setIncludes(set.getIncludes());
			ds.setExcludes(set.getExcludes());
			if (!set.isSkipDefaultExcludes()) {
				ds.addDefaultExcludes();
			}
			ds.scan();
			String[] files = ds.getIncludedFiles();
			for (int j = 0;  j < files.length;  j++) {
				list.add(new FileSpec(dir, files[j]));
			}
		}
		final FileSpec[] result = (FileSpec[]) list.toArray(new FileSpec[list.size()]);
		/*
		 *  Unfortunately, JaxMe has a bug which changes the
		 *  generated code somewhat, depending on the order
		 *  of the processed schemas. It is hard to fix that
		 *  bug and impossible to do it without loosing
		 *  upwards compatibility. Therefore, we don't fix
		 *  that branch for the 0.5 branch, but at least
		 *  make the order deterministic.
		 */
		Arrays.sort(result, new Comparator(){
			final Collator coll = Collator.getInstance(Locale.US);
			private String pathOf(File pFile) {
				if (pFile == null) {
					return "";
				}
				final String path = pFile.getPath();
				return path == null ? "" : path;
			}
			public int compare(Object pArg0, Object pArg1) {
				final FileSpec fs1 = (FileSpec) pArg0;
				final FileSpec fs2 = (FileSpec) pArg1;
				return coll.compare(pathOf(fs1.getFile()), pathOf(fs2.getFile()));
			}
		});
		return result;
	}

	private FileSpec[] getSchemaFiles(ISchemaCollection pCollection)
			throws MojoFailureException {
		final FileSet[] schemas = pCollection.getSchemas();
		if (schemas == null  ||  schemas.length == 0) {
			throw new MojoFailureException("A SchemaCollection must have a set of schema files specified.");
		}
		FileSpec[] schemaFiles = getFiles(schemas);
		if (schemaFiles.length == 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("Schema specification returns no result: ");
			for (int i = 0;  i < schemas.length;  i++) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(schemas[i]);
			}
			log.warn(sb);
		}
		return schemaFiles;
	}

	private FileSpec[] getProducedFiles(ISchemaCollection pCollection) {
		List producesList = new ArrayList();
		FileSet[] prdcs = pCollection.getProduces();
		if (prdcs == null) {
			FileSet f1 = new FileSet();
			f1.setDirectory(new File(pCollection.getSrcTarget()));
			producesList.add(f1);
			FileSet f2 = new FileSet();
			f2.setDirectory(new File(pCollection.getResourceTarget()));
			producesList.add(f2);
		} else {
			producesList.addAll(Arrays.asList(prdcs));
		}
		for (Iterator iter = producesList.iterator();  iter.hasNext();  ) {
			FileSet fileSet = (FileSet) iter.next();
			if (!fileSet.getDirectory().exists()) {
				iter.remove();
			}
		}
		
		return getFiles((FileSet[]) producesList.toArray(new FileSet[producesList.size()]));
	}

	private FileSpec[] getBindingFiles(ISchemaCollection pCollection) {
		return getFiles(pCollection.getBindings());
	}

	private FileSpec[] getDependencies(ISchemaCollection pCollection) {
		return getFiles(pCollection.getDepends());
	}

	private FileSpec[] concat(FileSpec[] pFiles1, FileSpec[] pFiles2) {
		final FileSpec[] deps = new FileSpec[pFiles1.length + pFiles2.length];
		System.arraycopy(pFiles1, 0, deps, 0, pFiles1.length);
		System.arraycopy(pFiles2, 0, deps, pFiles1.length, pFiles2.length);
		return deps;
	}

	private boolean isUptodate(FileSpec[] pSourceFiles, FileSpec[] pProducedFiles) {
		if (forceCreation) {
			log.debug("ForceCreation flag set, disabling uptodate check.");
			return false;
		}
		if (pProducedFiles.length == 0) {
			log.debug("No produced files found, disabling uptodate check.");
			return false;
		}
		File minProducedFile = null;
		long minProducedTime = 0;
		for (int i = 0;  i < pProducedFiles.length;  i++) {
			File f = pProducedFiles[i].getFile();
			long l = f.lastModified();
			if (l == 0) {
				log.debug("Produced file " + f + " has unknown timestamp, disabling uptodate check.");
				return false;
			}
			if (minProducedTime == 0  ||  minProducedTime > l) {
				minProducedTime = l;
				minProducedFile = f;
			}
		}

		final FileSpec[] deps = pSourceFiles;
		long maxDepTime = 0;
		File maxDepFile = null;
		for (int i = 0;  i < deps.length;  i++) {
			File f = deps[i].getFile();
			long l = f.lastModified();
			if (l == 0) {
				log.debug("Dependency file " + f + " has unknown timestamp, disabling uptodate check.");
				return false;
			}
			if (maxDepTime == 0  ||  maxDepTime < l) {
				maxDepTime = l;
				maxDepFile = f;
			}
		}

		if (maxDepTime >= minProducedTime) {
			log.debug("Dependency file " + maxDepFile + " is more recent than produced file " + minProducedFile);
			return true;
		} else {
			log.debug("All produced files are uptodate.");
			return false;
		}
	}

	void process(ISchemaCollection pCollection) throws MojoExecutionException, MojoFailureException {
		final FileSpec[] schemaFiles = getSchemaFiles(pCollection);
		if (schemaFiles.length == 0) {
			return;
		}
		final FileSpec[] producedFiles = getProducedFiles(pCollection);
		final FileSpec[] dependencies = getDependencies(pCollection);
		final FileSpec[] bindingFiles = getBindingFiles(pCollection);
		final boolean uptodate = isUptodate(concat(concat(schemaFiles, dependencies), bindingFiles), producedFiles);
		if (uptodate) {
			log.info("Generated files are uptodate.");
		} else {
			runGenerator(pCollection, schemaFiles, producedFiles, bindingFiles, dependencies);
		}

		project.addCompileSourceRoot(pCollection.getSrcTarget());
		Resource resource = new Resource();
		resource.setDirectory(pCollection.getResourceTarget());
		project.addResource(resource);
	}

	private void removeOldOutput(ISchemaCollection pCollection, FileSpec[] pProducedFiles) throws MojoExecutionException {
		if (pCollection.isRemoveOldOutput()) {
			for (int i = 0;  i < pProducedFiles.length;  i++) {
				File f = pProducedFiles[i].getFile();
				if (f.isFile()  &&  !f.delete()) {
					throw new MojoExecutionException("Unable to delete file: " + f.getAbsolutePath());
				}
			}
		}
	}

	private SchemaReader newSchemaReaderInstance(ISchemaCollection pCollection)
			throws MojoFailureException, MojoExecutionException {
		final SchemaReader result;
		final String s = pCollection.getSchemaReader();
		if (s == null  ||  s.length() == 0) {
			if (pCollection.isExtension()) {
				result = new JaxMeSchemaReader();
			} else {
				result = new JAXBSchemaReader();
			}
		} else {
			Class c;
			try {
				c = Thread.currentThread().getContextClassLoader().loadClass(s);
			} catch (ClassNotFoundException e) {
				throw new MojoFailureException("The schema reader class " + s + " was not found.");
			}
			Object o;
			try {
				o = c.newInstance();
			} catch (InstantiationException e) {
				throw new MojoExecutionException("Failed to instantiate schema reader class " + c.getName(), e);
			} catch (IllegalAccessException e) {
				throw new MojoExecutionException("Illegal access to schema reader class " + c.getName(), e);
			}
			try {
				result = (SchemaReader) o;
			} catch (ClassCastException e) {
				throw new MojoFailureException("The configured schema reader class " + c.getName()
						+ " is not implementing " + SchemaReader.class.getName());
			}
		}
		return result;
	}

	private Class getSgFactoryChainClass(String pClass)
			throws MojoFailureException {
		Class c;
		try {
			c = Thread.currentThread().getContextClassLoader().loadClass(pClass);
		} catch (ClassNotFoundException e) {
			throw new MojoFailureException("The factory chain class " + pClass + " was not found.");
		}
		if (!SGFactoryChain.class.isAssignableFrom(c)) {
			throw new MojoFailureException("The factory chain class " + c.getName()
					+ " is not implementing " + SGFactoryChain.class);
		}
		return c;
	}

	private SchemaReader getSchemaReaderInstance(ISchemaCollection pCollection)
			throws MojoFailureException, MojoExecutionException {
		final SchemaReader result = newSchemaReaderInstance(pCollection);
		log.debug("Schema reader class: " + result.getClass().getName());

		String[] chains = pCollection.getSgFactoryChains();
		if (chains != null) {
			for (int i = 0;  i < chains.length;  i++) {
				Class c = getSgFactoryChainClass(chains[i]);
				log.debug("Adding SG Factory chain: " + c.getName());
				result.addSGFactoryChain(c);
			}
		}

		return result;
	}

	private File getSrcTargetDirectory(ISchemaCollection pCollection) {
		final String prefix = pCollection.getSrcTarget();
		final File f = getTargetDir(prefix);
		sourceDirectories.put(prefix, f);
		return f;
	}

	private File getTargetDir(String pDir) {
		File f = new File(pDir);
		if (!f.isAbsolute()) {
			f = new File(project.getBasedir(), pDir);
		}
		return f;
	}

	private File getResourceTargetDirectory(ISchemaCollection pCollection) {
		final String prefix = pCollection.getResourceTarget();
		final File f = getTargetDir(prefix);
		resourcecDirectories.put(prefix, f);
		return f;
	}

	private void runGenerator(ISchemaCollection pCollection, final FileSpec[] pSchemaFiles,
							  final FileSpec[] pProducedFiles, final FileSpec[] pBindingFiles,
							  final FileSpec[] pDependencies)
			throws MojoExecutionException, MojoFailureException {
		removeOldOutput(pCollection, pProducedFiles);

		Generator g = new GeneratorImpl();
		g.setEntityResolver(EntityResolverFactory.getEntityResolver(pCollection.getEntityResolverMode()));
		for (int i = 0;  i < pBindingFiles.length;  i++) {
			File f = pBindingFiles[i].getFile();
			try {
				g.addBindings(new InputSource(f.toURI().toURL().toExternalForm()));
			} catch (ParserConfigurationException e) {
				throw new MojoExecutionException("Failed to add binding file "
						+ f.getPath() + ": " + e.getMessage(), e);
			} catch (SAXException e) {
				throw new MojoExecutionException("Failed to add binding file "
						+ f.getPath() + ": " + e.getMessage(), e);
			} catch (IOException e) {
				throw new MojoExecutionException("Failed to add binding file "
						+ f.getPath() + ": " + e.getMessage(), e);
			}
		}
		File resourceTargetDir = getResourceTargetDirectory(pCollection);
		for (int i = 0;  i < pSchemaFiles.length;  i++) {
			final SchemaReader reader = getSchemaReaderInstance(pCollection);
			g.setSchemaReader(reader);
			g.setForcingOverwrite(forceCreation);
			g.setSettingReadOnly(readOnly);
			g.setTargetDirectory(getSrcTargetDirectory(pCollection));
			g.setResourceTargetDirectory(resourceTargetDir);
			g.setValidating(pCollection.isValidating());
			if (pCollection.getPackageName() != null) {
				g.setProperty("jaxme.package.name", pCollection.getPackageName());
			}
			Map props = pCollection.getProperties();
			if (props != null) {
				for (Iterator iter = props.entrySet().iterator();  iter.hasNext();  ) {
					Map.Entry entry = (Map.Entry) iter.next();
					g.setProperty((String) entry.getKey(), (String) entry.getValue());
				}
			}
			try {
				g.generate(pSchemaFiles[i].getFile());
			} catch (Exception e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
		}

		copySchemaFiles(pCollection, pSchemaFiles, pBindingFiles,
				pDependencies, resourceTargetDir);
	}

	private void copySchemaFiles(ISchemaCollection pCollection,
			final FileSpec[] pSchemaFiles, final FileSpec[] pBindingFiles,
			final FileSpec[] pDependencies, File resourceTargetDir)
			throws MojoExecutionException {
		final String schemaTargetPrefix = pCollection.getSchemaTargetPrefix();
		if (schemaTargetPrefix != null) {
			copySchemaFiles(resourceTargetDir, pSchemaFiles, schemaTargetPrefix);
			copySchemaFiles(resourceTargetDir, pBindingFiles, schemaTargetPrefix);
			copySchemaFiles(resourceTargetDir, pDependencies, schemaTargetPrefix);
		}
	}

	private void copySchemaFiles(File pTarget, FileSpec[] pFiles, String pPrefix)
			throws MojoExecutionException {
		final File dir;
		if ("".equals(pPrefix)  ||  "/".equals(pPrefix)  ||  File.separator.equals(pPrefix)) {
			dir = pTarget;
		} else {
			dir = new File(pTarget, pPrefix);
		}
		for (int i = 0;  i < pFiles.length;  i++) {
			final File src = pFiles[i].getFile();
			final File target = new File(dir, pFiles[i].getPath());
			try {
				FileUtils.copyFile(src, target);
			} catch (IOException e) {
				throw new MojoExecutionException("Failed to copy file " + src + " to " +
						target + ": " + e.getMessage(), e);
			}
		}
	}

	void fixProjectSettings() {
		fixCompileSourceRoots();
		fixResourceDirectories();
	}

	private void fixCompileSourceRoots() {
		for (Iterator iter = sourceDirectories.entrySet().iterator();  iter.hasNext();  ) {
			final Map.Entry entry = (Map.Entry) iter.next();
			final String dir = (String) entry.getKey();
			boolean found = false;
			for (Iterator iter2 = project.getCompileSourceRoots().iterator();  iter2.hasNext();  ) {
				String s = (String) iter2.next();
				if (dir.equals(s)) {
					found = true;
					break;
				}
			}
			if (!found) {
				project.addCompileSourceRoot(dir);
			}
		}
	}

	private void fixResourceDirectories() {
		for (Iterator iter = resourcecDirectories.entrySet().iterator();  iter.hasNext();  ) {
			final Map.Entry entry = (Map.Entry) iter.next();
			final String dir = (String) entry.getKey();
			boolean found = false;
			for (Iterator iter2 = project.getBuild().getResources().iterator();  iter2.hasNext();  ) {
				Resource resource = (Resource) iter2.next();
				if (dir.equals(resource.getDirectory())) {
					found = true;
					break;
				}
			}
			if (!found) {
				Resource resource = new Resource();
				resource.setDirectory(dir);
				resource.setFiltering(false);
				project.getBuild().addResource(resource);
			}
		}
	}
}
