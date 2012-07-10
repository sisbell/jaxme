/*
 * Copyright 2005  The Apache Software Foundation
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

import org.apache.maven.plugin.logging.Log;
import org.apache.ws.jaxme.logging.Logger;


/** A Logger implementation, which uses the Maven
 * projects logger.
 */
class MavenProjectLogger implements Logger {
	private final Log log;
	private final String name;

	MavenProjectLogger(Log pLog, String pName) {
		log = pLog;
		name = pName;
	}

	private String asName(String pName) {
		return name + "." + pName;
	}

	private String asString(Object[] pArgs) {
		StringBuffer sb = new StringBuffer();
		if (pArgs != null) {
			for (int i = 0;  i < pArgs.length;  i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(pArgs[i]);
			}
		}
		return sb.toString();
	}

	public void entering(String pName, Object[] pArgs) {
		entering(pName, asString(pArgs));
	}

	public void entering(String pName) {
		entering(pName, "");
	}

	public void entering(String pName, Object pArg) {
		log.debug(asName(pName) + " -> " + pArg);
	}

	public void exiting(String pName, Object[] pArgs) {
		exiting(pName, asString(pArgs));
	}

	public void exiting(String pName) {
		exiting(pName, "");
	}

	public void exiting(String pName, Object pArg) {
		log.debug(asName(pName) + " <- " + pArg);
	}

	public void throwing(String pName, Throwable pError) {
		log.error(asName(pName), pError);
	}

	public boolean isFinestEnabled() { return true; }

	public void finest(String pName, String pArg, Object[] pArgs) {
		finest(pName, pArg + ", " + asString(pArgs));
	}

	public void finest(String pName, String pArg) {
		log.debug(asName(pName) + ": " + pArg);
	}

	public void finest(String pName, String pArg1, Object pArg2) {
		finest(pName, pArg1 + ", " + pArg2);
	}

	public void finer(String pName, String pArg, Object[] pArgs) {
		finer(pName, pArg + ", " + asString(pArgs));
	}

	public boolean isFinerEnabled() { return true; }

	public void finer(String pName, String pArg) {
		log.debug(asName(pName) + ": " + pArg);
	}

	public void finer(String pName, String pArg1, Object pArg2) {
		finer(pName, pArg1 + ", " + pArg2);
	}

	public boolean isFineEnabled() { return true; }

	public void fine(String pName, String pArg, Object[] pArgs) {
		fine(pName, pArg + ", " + asString(pArgs));
	}

	public void fine(String pName, String pArg) {
		log.debug(asName(pName) + ": " + pArg);
	}

	public void fine(String pName, String pArg1, Object pArg2) {
		fine(pName, pArg1 + ", " + pArg2);
	}

	public boolean isInfoEnabled() { return true; }

	public void info(String pName, String pArg, Object[] pArgs) {
		info(pName, pArg + ", " + asString(pArgs));
	}

	public void info(String pName, String pArg) {
		log.info(asName(pName) + ": " + pArg);
	}

	public void info(String pName, String pArg1, Object pArg2) {
		info(pName, pArg1 + ", " + pArg2);
	}

	public boolean isWarnEnabled() { return true; }

	public void warn(String pName, String pArg, Object[] pArgs) {
		warn(pName, pArg + ", " + asString(pArgs));
	}

	public void warn(String pName, String pArg) {
		log.warn(pName + ": " + pArg);
	}

	public void warn(String pName, String pArg1, Object pArg2) {
		warn(pName, pArg1 + ", " + pArg2);
	}

	public boolean isErrorEnabled() { return true; }

	public void error(String pName, String pArg, Object[] pArgs) {
		error(pName, pArg + ", " + asString(pArgs));
	}

	public void error(String pName, String pArg) {
		log.error(asName(pName) + ": " + pArg);
	}

	public void error(String pName, String pArg1, Object pArg2) {
		error(pName, pArg1 + ", " + pArg2);
	}
}
