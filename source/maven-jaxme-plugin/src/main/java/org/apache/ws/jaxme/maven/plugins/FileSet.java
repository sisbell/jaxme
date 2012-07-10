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


/**
 * Container object for specifying set of files.
 */
public class FileSet {
	private File directory;
	private String[] includes, excludes;
	private boolean skipDefaultExcludes;

	/**
	 * Returns the file sets base directory.
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * Sets the file sets base directory.
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}

	/**
	 * Returns the file sets inclusion patterns.
	 */
	public String[] getIncludes() {
		return includes;
	}

	/**
	 * Sets the file sets inclusion patterns.
	 */
	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	/**
	 * Sets the file sets inclusion patterns.
	 */
	public String[] getExcludes() {
		return excludes;
	}

	/**
	 * Returns the file sets inclusion patterns.
	 */
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	/**
	 * Returns, whether default excludes are being disabled.
	 * Defaults to false (default excludes are enabled).
	 */
	public boolean isSkipDefaultExcludes() {
		return skipDefaultExcludes;
	}

	/**
	 * Sets, whether default excludes are being disabled.
	 * Defaults to false (default excludes are enabled).
	 */
	public void setSkipDefaultExcludes(boolean skipDefaultExcludes) {
		this.skipDefaultExcludes = skipDefaultExcludes;
	}


}
