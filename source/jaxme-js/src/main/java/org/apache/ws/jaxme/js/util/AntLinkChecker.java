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
package org.apache.ws.jaxme.js.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.logging.AntProjectLoggerFactory;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.logging.LoggerFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.TaskAdapter;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class AntLinkChecker extends TaskAdapter {
  public class CheckedURL {
    private URL name;
    public void setName(URL pName) {
      if (name != null) {
        throw new IllegalStateException("An 'URL' element must have exactly one 'name' or 'file' attribute. These attributes are mutually exclusive.");
      }
      name = pName;
    }
    public void setFile(File pName) throws MalformedURLException {
      if (name != null) {
        throw new IllegalStateException("An 'URL' element must have exactly one 'name' or 'file' attribute. These attributes are mutually exclusive.");
      }
      name = pName.toURL();
    }
    public URL getName() {
      return name;
    }
    public void finish() {
      if (name == null) {
        throw new IllegalStateException("An 'URL' element must have exactly one 'name' or 'file' attribute.");
      }
    }
  }

  public class MyLinkChecker extends LinkChecker {
    private boolean isSettingLoggerFactory = true;
    public CheckedURL createURL() {
      CheckedURL url = new CheckedURL();
      AntLinkChecker.this.addURL(url);
      return url;
    }
    public boolean isSettingLoggerFactory() {
      return isSettingLoggerFactory;
    }
    public void setSettingLoggerFactory(boolean pSettingLoggerFactory) {
      isSettingLoggerFactory = pSettingLoggerFactory;
    }
  }

  private MyLinkChecker checker = new MyLinkChecker();
  private List urls = new ArrayList();

  void addURL(CheckedURL pURL) {
     urls.add(pURL);
  }

  public AntLinkChecker() {
    setProxy(checker);
  }

  protected LoggerFactory initLogging() {
    if (!checker.isSettingLoggerFactory()) {
      return null;
    }
    LoggerFactory loggerFactory = LoggerAccess.getLoggerFactory();
    if (loggerFactory instanceof AntProjectLoggerFactory) {
      return null;
    }
    LoggerAccess.setLoggerFactory(new AntProjectLoggerFactory(this));
    return loggerFactory;
  }

  protected void stopLogging(LoggerFactory pLoggerFactory) {
    if (pLoggerFactory != null) {
      LoggerAccess.setLoggerFactory(pLoggerFactory);
    }
  }

  public void execute() {
    log("execute: ->", Project.MSG_VERBOSE);
    LoggerFactory loggerFactory = initLogging();
    checker.setLogger(LoggerAccess.getLogger(LinkChecker.class));
    try {
      for (Iterator iter = urls.iterator();  iter.hasNext();  ) {
        CheckedURL url = (CheckedURL) iter.next();
        checker.addURL(url.getName());
      }
      checker.parse();
    } finally {
      stopLogging(loggerFactory);
    }
    log("execute: <-", Project.MSG_VERBOSE);
  }
}
