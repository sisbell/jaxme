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
package org.apache.ws.jaxme.js;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.ws.jaxme.js.apps.JavaSourceResolver;
import org.apache.ws.jaxme.js.impl.TextFileImpl;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;

/** <p>Factory for generating JavaSource objects.</p>
 * <p>
 * The factory properties:
 * </p>
 * <ul>
 *  <li>{@link #setOverwriteForced}</li>
 *  <li>{@link #setSettingReadOnly}</li>
 * </ul>
 * <p>
 * are used for finely grained controlled over the generated 
 * source files. Methods are provided to access abstract descriptions
 * of the files to be created:
 * </p>
 * <ul>
 *   <li>{@link #getTextFiles} and {@link #getTextFile}</li>
 *   <li>{@link #getJavaSources} and {@link #getJavaSource}</li>
 * </ul>
 * <p>
 * Concrete files (source and otherwise) are created by calling 
 * the following construction methods:
 * </p>
 * <ul>
 *   <li>{@link #newJavaSource(JavaQName)}, {@link #newJavaSource(JavaQName, String)}
 *  and {@link #newJavaSource(JavaQName, JavaSource.Protection)}</li>
 *   <li>{@link #newTextFile}</li>
 * </ul>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JavaSourceFactory.java 358953 2005-12-24 21:49:14Z jochen $
 */
public class JavaSourceFactory implements JavaSourceResolver {
	private Logger logger = LoggerAccess.getLogger(JavaSourceFactory.class);
	private boolean overwriteForced;
	private boolean settingReadOnly;
	private Map sources = new HashMap();
	private List files = new ArrayList();
	
	/** <p>Sets the {@link Logger} to use.</p>
	 */
	public void setLogger(Logger pLogger) {
		logger = pLogger;
	}
	
	/** <p>Returns the {@link Logger}.</p>
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/** <p>Sets whether the generated files are created in read-only mode.</p>
	 */
	public void setSettingReadOnly(boolean pSettingReadOnly) {
		settingReadOnly = pSettingReadOnly;
	}
	
	/** <p>Returns whether the generated files are created in read-only mode.</p>
	 */
	public boolean isSettingReadOnly() {
		return settingReadOnly;
	}
	
	/** <p>By default the JavaSourceFactory will only overwrite existing
	 * files only, if they have different content. If the overwriteForced property is
	 * set to true, existing files will always be overwritten.</p>
	 * @see #isOverwriteForced()
	 */
	public void setOverwriteForced(boolean pOverwriteForced) {
		overwriteForced = pOverwriteForced;
	}
	
	/** <p>By default the JavaSourceFactory will only overwrite existing
	 * files, if they have different content. If the overwriteForced property is
	 * set to true, existing files will always be overwritten.</p>
	 * @see #setOverwriteForced(boolean)
	 */
	public boolean isOverwriteForced() {
		return overwriteForced;
	}
	
	/** <p>Creates a new instance of JavaSource with the given
	 * name and default protection.</p>
	 */
	public JavaSource newJavaSource(JavaQName pName) {
		return newJavaSource(pName, JavaSource.DEFAULT_PROTECTION);
	}
	
	/** <p>Creates a new instance of JavaSource with the given
	 * name and protection.</p>
	 */
	public JavaSource newJavaSource(JavaQName pName, JavaSource.Protection pProtection) {
		if (sources.containsKey(pName)) {
			throw new IllegalStateException("The class " + pName + " has already been created.");
		}
		JavaSource result = new JavaSource(this, pName, pProtection);
		sources.put(pName, result);
		return result;
	}
	
	/** <p>Creates a new instance of JavaSource with the given
	 * name and protection. Shortcut for
	 * <code>newJavaSource(pName, JavaSource.Protection.valueOf(pProtection))</code>.</p>
	 */
	public JavaSource newJavaSource(JavaQName pName, String pProtection) {
		return newJavaSource(pName, JavaSource.Protection.valueOf(pProtection));
	}
	
	/** <p>Returns an iterator to the generated classes.</p>
	 */
	public Iterator getJavaSources() {
		return sources.values().iterator();
	}
	
	/** <p>Returns an iterator to the generated text files.</p>
	 */
	public Iterator getTextFiles() {
		return files.iterator();
	}
	
	/** <p>Returns the generated class with the given name or null, if
	 * no such class has been generated.</p>
	 */
	public JavaSource getJavaSource(JavaQName pName) {
		return (JavaSource) sources.get(pName);
	}
	
	/** <p>Returns the text file with the given name or null, if no such
	 * text file has been generated.</p>
	 */
	public TextFile getTextFile(String pPackageName, String pFileName) {
		for (Iterator iter = files.iterator();  iter.hasNext();  ) {
			TextFile textFile = (TextFile) iter.next();
			if (textFile.getPackageName().equals(pPackageName)  &&  textFile.getFileName().equals(pFileName)) {
				return textFile;
			}
		}
		return null;
	}
	
	/** <p>Given a package name and a base directory, returns the package
	 * directory.</p>
	 *
	 * @param pBaseDir The base directory, where to create sources;
	 *    may be null (current directory).
	 * @return Package directory; null is a valid value and indicates
	 *    the current directory.
	 */
	public File getPackageDirectory(File pBaseDir, String pPackageName) {
		if (pPackageName != null) {
			for (StringTokenizer st = new StringTokenizer(pPackageName, ".");
			st.hasMoreTokens();  ) {
				String dir = st.nextToken();
				pBaseDir = new File(pBaseDir, dir);
			}
		}
		return pBaseDir;
	}
	
	/** <p>Given a fully qualified name, returns the name of the
	 * corresponding Java file.</p>
	 */
	public File getSourceFile(File pBaseDir, JavaQName pQName) {
		if (pQName == null) {
			throw new NullPointerException("The class name must not be null.");
		}
		File packageDirectory = getPackageDirectory(pBaseDir, pQName.getPackageName());
		String name = pQName.getClassName();
		int offset = name.indexOf('.');
		if (offset != -1) {
			throw new IllegalArgumentException("Source files must not be generated for inner classes: " + name);
		}
		offset = name.indexOf('.');
		if (offset != -1) {
			throw new IllegalArgumentException("Source files must not be generated for inner classes: " + name);
		}
		return new File(packageDirectory, name + ".java");
	}
	
	/** <p>Returns a location the given package, relative to the given
	 * base directory. For example, if you have the base
	 * directory <code>c:\temp</code> and the package
	 * <code>com.mycompany.demo</code>, this would yield
	 * new File("c:\temp\com\mycompany\demo").</p>
	 *
	 * @param pBaseDir The base directory or null for the current directory.
	 * @param pPackage The JavaSource being stored; null or the empty
	 *   string for the root package
	 * @return Directory related to the package; this may be null, if
	 *   the base directory was null and the package was the root
	 *   package
	 */
	public File getLocation(File pBaseDir, String pPackage) {
		for (StringTokenizer st = new StringTokenizer(pPackage, ".");  st.hasMoreTokens();  ) {
			pBaseDir = new File(pBaseDir, st.nextToken());
		}
		return pBaseDir;
	}
	
	/** <p>Returns a location for storing the JavaSource class, relative
	 * to the given base directory. For example, if you have the base
	 * directory <code>c:\temp</code> and the class <code>Sample</code>
	 * in package <code>com.mycompany.demo</code>, this would yield
	 * new File("c:\temp\com\mycompany\demo\Sample.java").</p>
	 *
	 * @param pBaseDir The base directory or null for the current directory.
	 * @param pJs The JavaSource being stored.
	 */
	public File getLocation(File pBaseDir, JavaSource pJs) {
		if (pJs.isInnerClass()) {
			throw new IllegalArgumentException("Inner classes have no assigned location in the file system.");
		}
		return new File(getLocation(pBaseDir, pJs.getPackageName()),
				pJs.getClassName() + ".java");
	}
	
	/** <p>Returns a location for storing the TextFile, relative
	 * to the given base directory. For example, if you have the base
	 * directory <code>c:\temp</code> and the class <code>Sample</code>
	 * in package <code>com.mycompany.demo</code>, this would yield
	 * new File("c:\temp\com\mycompany\demo\Sample.java").</p>
	 *
	 * @param pBaseDir The base directory or null for the current directory.
	 * @param pTextFile The text file being created.
	 */
	public File getLocation(File pBaseDir, TextFile pTextFile) {
		return new File(getLocation(pBaseDir, pTextFile.getPackageName()), pTextFile.getFileName());
	}
	
	
	/** <p>Verifies whether the given Java source and the contents of the
	 * given file are identical.</p>
	 */
	protected boolean isSameFile(JavaSource pJs, File pFile) throws IOException {
		StringWriter sw = new StringWriter();
		pJs.write(sw);
		sw.close();
		return isSameFile(sw.toString(), pFile);
	}
	
	/** <p>Verifies whether the given string and the contents of the given
	 * file are identical.</p>
	 */
	protected boolean isSameFile(String pContents, File pFile) throws IOException {
		Reader r = new FileReader(pFile);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[4096];
		for (;;) {
			int len = r.read(buffer);
			if (len == -1) {
				break;
			} else if (len > 0) {
				sw.write(buffer, 0, len);
			}
		}
		return isSameFile(pContents, sw.toString());
	}
	
	/** <p>Verifies whether the given strings are identical.</p>
	 */
	protected boolean isSameFile(String pContents1, String pContents2) {
		return pContents1.equals(pContents2);
	}
	
	/** <p>Actually creates a file with the given name.</p>
	 */
	protected void writeFile(File pFile, JavaSource pJs) throws IOException {
		final String mName = "writeFile(File,JavaSource)";
		File p = pFile.getParentFile();
		if (p != null  &&  !p.exists()) {
			getLogger().fine(mName, "Creating directory " + p);
			p.mkdirs();
		}      
		
		if (isOverwriteForced()  ||  !pFile.exists()  ||  !isSameFile(pJs, pFile)) {
			getLogger().fine(mName, "Creating " + pFile);
			Writer w = new BufferedWriter(new FileWriter(pFile), 4096);
			pJs.write(w);
			w.close();
			if (isSettingReadOnly()) {
				pFile.setReadOnly();
			}
		} else {
			getLogger().fine(mName, "Uptodate: " + pFile);
		}
	}
	
	/** <p>Actually creates a file with the given name.</p>
	 */
	protected void writeFile(File pFile, String pContents) throws IOException {
		final String mName = "writeFile(File,String)";
		File p = pFile.getParentFile();
		if (p != null  &&  !p.exists()) {
			getLogger().fine(mName, "Creating directory " + p);
			p.mkdirs();
		}
		
		if (isOverwriteForced()  ||  !pFile.exists()  ||  !isSameFile(pContents, pFile)) {
			getLogger().fine(mName, "Creating " + pFile);
			Writer w = new BufferedWriter(new FileWriter(pFile), 4096);
			w.write(pContents);
			w.close();
			if (isSettingReadOnly()) {
				pFile.setReadOnly();
			}
		} else {
			getLogger().fine(mName, "Uptodate: " + pFile);
		}
	}
	
	/** <p>Writes the given JavaSource class to the file system,
	 * relative to the given base directory.</p>
	 * @param pBaseDir The base directory or null for the current directory.
	 * @param pJs The JavaSource being stored.
	 * @see JavaSource#write(Writer)
	 */
	public void write(File pBaseDir, JavaSource pJs) throws IOException {
		writeFile(getLocation(pBaseDir, pJs), pJs);
	}
	
	/** <p>Writes the given text file to the file system, relative to the
	 * given base directory.</p>
	 * @param pBaseDir The base directory or null for the current directory.
	 * @param pFile The text file being stored.
	 * @see JavaSource#write(Writer)
	 */
	public void write(File pBaseDir, TextFile pFile) throws IOException {
		writeFile(getLocation(pBaseDir, pFile), pFile.getContents());
	}
	
	/** Writes all Source files to the FileSystem, relative to the
	 * given base directory.
	 * @param pBaseDir The base directory or null for the current directory.
	 */
	public void write(File pBaseDir) throws IOException {
		write(pBaseDir, pBaseDir);
	}
	
	/** Writes all Source files to the file system, relative to the
	 * respective base directory.
	 * @param pJavaSourceDir The base directory for Java source files.
	 * @param pResourceDir The base directory for resource files.
	 */
	public void write(File pJavaSourceDir, File pResourceDir) throws IOException {
		for (Iterator iter = getJavaSources();  iter.hasNext();  ) {
			write(pJavaSourceDir, (JavaSource) iter.next());
		}
		for (Iterator iter = getTextFiles();  iter.hasNext();  ) {
			write(pResourceDir, (TextFile) iter.next());
		}
	}
	
	/** <p>Creates a new text file.</p>
	 */
	public TextFile newTextFile(String pPackageName, String pFileName) {
		for (Iterator iter = files.iterator();  iter.hasNext();  ) {
			TextFile f = (TextFile) iter.next();
			if (f.getPackageName().equals(pPackageName)  &&  f.getFileName().equals(pFileName)) {
				throw new IllegalStateException("A file named " + pFileName + " in package " + pPackageName + " already exists.");
			}
		}
		for (Iterator iter = sources.keySet().iterator();  iter.hasNext();  ) {
			JavaQName qName = (JavaQName) iter.next();
			if (qName.getPackageName().equals(pPackageName)  &&  (qName.getClassName() + ".java").equals(pFileName)) {
				throw new IllegalStateException("A Java source file names " + pFileName + " in package " + pPackageName + " already exists.");
			}
		}
		
		TextFile result = new TextFileImpl(pPackageName, pFileName);
		files.add(result);
		return result;
	}
}
