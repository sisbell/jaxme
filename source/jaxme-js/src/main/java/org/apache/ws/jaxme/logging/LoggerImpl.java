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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;


/** <p>Base implementation of a Logger.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class LoggerImpl implements Logger {
   private final String cName;
   private boolean loggingTime, loggingThread;

   public LoggerImpl(String pCName) {
      cName = pCName;
   }

   public boolean isLoggingTime() {
      return loggingTime;
   }

   public boolean isLoggingThread() {
      return loggingThread;
   }

   public void setLoggingTime(boolean pLoggingTime) {
      loggingTime = pLoggingTime;
   }

   public void setLoggingThread(boolean pLoggingThread) {
      loggingThread = pLoggingThread;
   }

   protected void log(String pMsg) {
      System.err.println(pMsg);
   }

   protected void log(String pLevel, String pCName, String pMName, String pMsg) {
      log(pLevel, pCName, pMName, new Object[]{pMsg});
   }

   protected void log(String pLevel, String pCName, String pMName,
                       String pMsg, Object[] pDetails) {
      if (pDetails == null) {
         log(pLevel, pCName, pMName, new Object[]{pMsg});
      } else {
         Object[] o = new Object[pDetails.length+1];
         o[0] = pMsg;
         for (int i = 0;  i < pDetails.length;  i++) {
            o[i+1] = pDetails[i];
         }
         log(pLevel, pCName, pMName, o);
      }
   }

   protected void log(String pLevel, String pCName, String pMName,
                       Object[] pDetails) {
      StringBuffer sb = new StringBuffer();
      if (loggingTime) {
         sb.append(new Date());
         sb.append(", ");
      }
      if (loggingThread) {
         sb.append(Thread.currentThread().getName());
         sb.append(", ");
      }
      sb.append(": ");
      sb.append(pLevel).append(", ").append(pMName).append(", ");
      if (pDetails != null) {
         for (int i = 0;  i < pDetails.length;  i++) {
            sb.append(", ").append(pDetails[i]);
         }
      }
      log(sb.toString());
   }

   public boolean isFinestEnabled() { return true; }
   public boolean isFinerEnabled() { return true; }
   public boolean isFineEnabled() { return true; }
   public boolean isInfoEnabled() { return true; }
   public boolean isWarnEnabled() { return true; }
   public boolean isErrorEnabled() { return true; }

   public void finest(String mName, String pMsg, Object[] pDetails) {
      log("FINEST", cName, mName, pMsg, pDetails);
   }

   public void finest(String mName, String pMsg) {
      log("FINEST", cName, mName, pMsg);
   }

   public void finest(String mName, String pMsg, Object pDetails) {
      log("FINEST", cName, mName, new Object[]{pMsg, pDetails});
   }

   public void finer(String mName, String pMsg, Object[] pDetails) {
      log("FINER", cName, mName, pMsg, pDetails);
   }

   public void finer(String mName, String pMsg) {
      log("FINER", cName, mName, pMsg);
   }

   public void finer(String mName, String pMsg, Object pDetails) {
      log("FINER", cName, mName, new Object[]{pMsg, pDetails});
   }

   public void fine(String mName, String pMsg, Object[] pDetails) {
      log("FINE", cName, mName, pMsg, pDetails);
   }

   public void fine(String mName, String pMsg) {
      log("FINE", cName, mName, pMsg);
   }

   public void fine(String mName, String pMsg, Object pDetails) {
      log("FINE", cName, mName, new Object[]{pMsg, pDetails});
   }

   public void info(String mName, String pMsg, Object[] pDetails) {
      log("INFO", cName, mName, pMsg, pDetails);
   }

   public void info(String mName, String pMsg) {
      log("INFO", cName, mName, pMsg);
   }

   public void info(String mName, String pMsg, Object pDetails) {
      log("INFO", cName, mName, new Object[]{pMsg, pDetails});
   }

   public void warn(String mName, String pMsg, Object[] pDetails) {
      log("WARN", cName, mName, pMsg, pDetails);
   }

   public void warn(String mName, String pMsg) {
      log("WARN", cName, mName, pMsg);
   }

   public void warn(String mName, String pMsg, Object pDetails) {
      log("WARN", cName, mName, new Object[]{pMsg, pDetails});
   }

   public void error(String mName, String pMsg, Object[] pDetails) {
      log("ERROR", cName, mName, pMsg, pDetails);
   }

   public void error(String mName, String pMsg) {
      log("ERROR", cName, mName, pMsg);
   }

   public void error(String mName, String pMsg, Object pDetails) {
      log("ERROR", cName, mName, new Object[]{pMsg, pDetails});
   }

	public void entering(String mName, Object[] pDetails) {
      log("->", cName, mName, pDetails);
	}

	public void entering(String mName) {
      log("->", cName, mName, (Object[]) null);
	}

	public void entering(String mName, Object pDetails) {
      log("->", cName, mName, new Object[]{pDetails});
	}

	public void exiting(String mName, Object[] pDetails) {
      log("<-", cName, mName, pDetails);
	}

	public void exiting(String mName) {
      log("<-", cName, mName, (Object[]) null);
	}

	public void exiting(String mName, Object pDetails) {
      log("<-", cName, mName, new Object[]{pDetails});
	}

	public void throwing(String mName, Throwable pThrowable) {
      StringWriter sw = new StringWriter();
      pThrowable.printStackTrace(new PrintWriter(sw));
      log("THROWABLE", cName, mName, sw.toString());
	}
}
