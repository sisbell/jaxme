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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/** A wrapper for the Ant logger.
 */
public class AntProjectLogger implements Logger {
    private final String cName;
    private final Task task;
    
    /** Creates a new instance for the given task.
     */
    public AntProjectLogger(String pName, Task pTask) {
        cName = pName;
        task = pTask;
    }
    
    public boolean isFinestEnabled() { return true; }
    public boolean isFinerEnabled() { return true; }
    public boolean isFineEnabled() { return true; }
    public boolean isInfoEnabled() { return true; }
    public boolean isWarnEnabled() { return true; }
    public boolean isErrorEnabled() { return true; }
    
    protected String asString(Object[] pDetails) {
        if (pDetails == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0;  i < pDetails.length;  i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(pDetails[i]);
        }
        return sb.toString();
    }
    
    protected void log(int pLevel, String mName, String pMsg) {
        task.log(cName + "." + mName + ": " + pMsg, pLevel);
    }
    
    public void entering(String mName, Object[] pDetails) {
        log(Project.MSG_VERBOSE, mName, "-> " + asString(pDetails));
    }
    
    public void entering(String mName) {
        log(Project.MSG_VERBOSE, mName, "->");
    }
    
    public void entering(String mName, Object pDetails) {
        log(Project.MSG_VERBOSE, mName, "-> " + pDetails);
    }
    
    public void exiting(String mName, Object[] pDetails) {
        log(Project.MSG_VERBOSE, mName, "<- " + asString(pDetails));
    }
    
    public void exiting(String mName) {
        log(Project.MSG_VERBOSE, mName, "<-");
    }
    
    public void exiting(String mName, Object pDetails) {
        log(Project.MSG_VERBOSE, mName, "<- " + pDetails);
    }
    
    public void throwing(String mName, Throwable pThrowable) {
        StringWriter sw = new StringWriter();
        pThrowable.printStackTrace(new PrintWriter(sw));
        error(mName, sw.toString());
    }
    
    public void finest(String mName, String pMsg, Object[] pDetails) {
        finest(mName, pMsg + ", " + asString(pDetails));
    }
    
    public void finest(String mName, String pMsg) {
        log(Project.MSG_DEBUG, mName, pMsg);
    }
    
    public void finest(String mName, String pMsg, Object pDetails) {
        finest(mName, pMsg + ", " + pDetails);
    }
    
    public void finer(String mName, String pMsg, Object[] pDetails) {
        finer(mName, pMsg + ", " + asString(pDetails));
    }
    
    public void finer(String mName, String pMsg) {
        log(Project.MSG_DEBUG, mName, pMsg);
    }
    
    public void finer(String mName, String pMsg, Object pDetails) {
        finer(mName, pMsg + ", " + pDetails);
    }
    
    public void fine(String mName, String pMsg, Object[] pDetails) {
        fine(mName, pMsg + ", " + asString(pDetails));
    }
    
    public void fine(String mName, String pMsg) {
        log(Project.MSG_VERBOSE, mName, pMsg);
    }
    
    public void fine(String mName, String pMsg, Object pDetails) {
        fine(mName, pMsg + ", " + pDetails);
    }
    
    public void info(String mName, String pMsg, Object[] pDetails) {
        info(mName, pMsg + ", " + asString(pDetails));
    }
    
    public void info(String mName, String pMsg) {
        log(Project.MSG_INFO, mName, pMsg);
    }
    
    public void info(String mName, String pMsg, Object pDetails) {
        info(mName, pMsg + ", " + pDetails);
    }
    
    public void warn(String mName, String pMsg, Object[] pDetails) {
        warn(mName, pMsg + ", " + asString(pDetails));
    }
    
    public void warn(String mName, String pMsg) {
        log(Project.MSG_WARN, mName, pMsg);
    }
    
    public void warn(String mName, String pMsg, Object pDetails) {
        warn(mName, pMsg + ", " + pDetails);
    }
    
    public void error(String mName, String pMsg, Object[] pDetails) {
        error(mName, pMsg + ", " + asString(pDetails));
    }
    
    public void error(String mName, String pMsg) {
        log(Project.MSG_ERR, mName, pMsg);
    }
    
    public void error(String mName, String pMsg, Object pDetails) {
        error(mName, pMsg + ", " + pDetails);
    }
}
