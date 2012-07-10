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
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;


/**
 * Runs the various integration tests.
 */
public class BaseTest extends AbstractMojoTestCase {
	private JaxMeMojo getMojo(final File pBaseDir) throws Exception {
		 File testPom = new File(pBaseDir, "pom.xml" );
		 JaxMeMojo mojo = (JaxMeMojo) lookupMojo("jaxme", testPom);
		 final Build build = new Build();
		 build.setDirectory("target");
		 build.setOutputDirectory("target/classes");
		 build.setResources(new ArrayList());
		 MavenProjectStub project = new MavenProjectStub(){
			public File getBasedir() {
				return pBaseDir;
			}
			public Build getBuild() {
				return build;
			}
		 };
		 project.setDependencyArtifacts(new HashSet());
		 setVariableValueToObject(mojo, "project", project);
		 setVariableValueToObject(mojo, "srcTarget", "target/classes/generated-sources/jaxme");
		 setVariableValueToObject(mojo, "resourceTarget", "target/classes/generated-resources/jaxme");
		 setVariableValueToObject(mojo, "classpathElements", new ArrayList());
		 setVariableValueToObject(mojo, "pluginArtifacts", new ArrayList());
		 ArtifactFactory factory = (ArtifactFactory) lookup(ArtifactFactory.class.getName());
		 if (factory == null) {
			 throw new NullPointerException("Missing ArtifactFactory");
		 }
		 setVariableValueToObject(mojo, "artifactFactory", factory);
		 
		 return mojo;
	}

	private void runTest(String pDir) throws Exception {
		File baseDir = new File(getBasedir(), pDir);
		JaxMeMojo mojo = getMojo(baseDir);
		mojo.execute();
	}

	/**
	 * Runs the integration test in the it1 directory.
	 */
	public void testIt1() throws Exception {
		runTest("src/test/it1");
	}

	/**
	 * Runs the integration test in the it2 directory.
	 */
	public void testIt2() throws Exception {
		runTest("src/test/it2");
	}
}
