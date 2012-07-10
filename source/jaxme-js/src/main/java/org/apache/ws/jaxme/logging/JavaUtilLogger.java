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
package org.apache.ws.jaxme.logging;

import java.util.logging.Level;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JavaUtilLogger implements Logger {
   private final java.util.logging.Logger myLogger;
   private final String cName;

   public JavaUtilLogger(String pCName) {
      cName = pCName;
      myLogger = java.util.logging.Logger.getLogger(cName);
   }

   public void entering(String mName, Object[] pDetails) {
      myLogger.entering(cName, mName, pDetails);
   }

   public void entering(String mName) {
      myLogger.entering(cName, mName);
   }

   public void entering(String mName, Object pDetails) {
      myLogger.entering(cName, mName, pDetails);
   }

   public void exiting(String mName, Object[] pDetails) {
      myLogger.exiting(cName, mName, pDetails);
   }

   public void exiting(String mName) {
      myLogger.exiting(cName, mName);
   }

   public void exiting(String mName, Object pDetails) {
      myLogger.exiting(cName, mName, pDetails);
   }

   public void throwing(String mName, Throwable t) {
      myLogger.throwing(cName, mName, t);
   }

   public void finest(String mName, String pMsg, Object[] pDetails) {
      myLogger.logp(Level.FINEST, cName, mName, pMsg, pDetails);
   }

   public void finest(String mName, String pMsg) {
      myLogger.logp(Level.FINEST, cName, mName, pMsg);
   }

   public void finest(String mName, String pMsg, Object pDetails) {
      myLogger.logp(Level.FINEST, cName, mName, pMsg, pDetails);
   }

   public void finer(String mName, String pMsg, Object[] pDetails) {
      myLogger.logp(Level.FINER, cName, mName, pMsg, pDetails);
   }

   public void finer(String mName, String pMsg) {
      myLogger.logp(Level.FINER, cName, mName, pMsg);
   }

   public void finer(String mName, String pMsg, Object pDetails) {
      myLogger.logp(Level.FINER, cName, mName, pMsg, pDetails);
   }

   public void fine(String mName, String pMsg, Object[] pDetails) {
      myLogger.logp(Level.FINE, cName, mName, pMsg, pDetails);
   }

   public void fine(String mName, String pMsg) {
      myLogger.logp(Level.FINE, cName, mName, pMsg);
   }

   public void fine(String mName, String pMsg, Object pDetails) {
      myLogger.logp(Level.FINE, cName, mName, pMsg, pDetails);
   }

   public void info(String mName, String pMsg, Object[] pDetails) {
      myLogger.logp(Level.INFO, cName, mName, pMsg, pDetails);
   }

   public void info(String mName, String pMsg) {
      myLogger.logp(Level.INFO, cName, mName, pMsg);
   }

   public void info(String mName, String pMsg, Object pDetails) {
      myLogger.logp(Level.INFO, cName, mName, pMsg, pDetails);
   }

   public void warn(String mName, String pMsg, Object[] pDetails) {
      myLogger.logp(Level.WARNING, cName, mName, pMsg, pDetails);
   }

   public void warn(String mName, String pMsg) {
      myLogger.logp(Level.WARNING, cName, mName, pMsg);
   }

   public void warn(String mName, String pMsg, Object pDetails) {
      myLogger.logp(Level.WARNING, cName, mName, pMsg, pDetails);
   }

   public void error(String mName, String pMsg, Object[] pDetails) {
      myLogger.logp(Level.SEVERE, cName, mName, pMsg, pDetails);
   }

   public void error(String mName, String pMsg) {
      myLogger.logp(Level.SEVERE, cName, mName, pMsg);
   }

   public void error(String mName, String pMsg, Object pDetails) {
      myLogger.logp(Level.SEVERE, cName, mName, pMsg, pDetails);
   }

	public boolean isFinestEnabled() {
      return myLogger.isLoggable(Level.FINEST);
	}

	public boolean isFinerEnabled() {
      return myLogger.isLoggable(Level.FINER);
	}

	public boolean isFineEnabled() {
      return myLogger.isLoggable(Level.FINE);
	}

	public boolean isInfoEnabled() {
      return myLogger.isLoggable(Level.INFO);
	}

	public boolean isWarnEnabled() {
      return myLogger.isLoggable(Level.WARNING);
	}

	public boolean isErrorEnabled() {
      return myLogger.isLoggable(Level.SEVERE);
	}
}
