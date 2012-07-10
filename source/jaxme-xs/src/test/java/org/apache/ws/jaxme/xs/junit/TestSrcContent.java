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
 
package org.apache.ws.jaxme.xs.junit;

import junit.framework.TestCase;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.ws.jaxme.xs.XSParser;
import org.xml.sax.InputSource;


public class TestSrcContent extends TestCase {
  public TestSrcContent(String name) {
    super(name);
  }

  public void testSourceFiles() throws Exception {
    String path = System.getProperty("xstc.zip.file");
    if (path == null  ||  "".equals(path)) {
      fail("The property xstc.zip.file is not set.");
    }
    File f = new File(path);
    if (!f.exists()  ||  !f.isFile()) {
      fail("The file " + f.getAbsolutePath() +
           ", given by property xstc.zip.file, does not exist ");
    }

    int passXSDFiles = 0;
    int failXSDFiles = 0;

    ZipFile zipFile = new ZipFile(f);
    for (Enumeration en = zipFile.entries();  en.hasMoreElements();  ) {
      ZipEntry entry = (ZipEntry) en.nextElement();
      if (entry.isDirectory()) {
        continue;
      }
      String name = entry.getName();
      if (!name.endsWith(".xsd")) {
        continue;
      }

      InputSource iSource = new InputSource(zipFile.getInputStream(entry));
      iSource.setSystemId(name);
      System.out.print(name);
      XSParser parser = new XSParser();
      parser.setValidating(false);
      try {
        parser.parseSyntax(iSource);
        passXSDFiles++;
        System.out.println(": PASS");
      } catch (Exception e) {
        failXSDFiles++;
        System.out.println(": FAIL");
        e.printStackTrace(System.out);
      }
    }

    System.out.println();
    System.out.println();
    System.out.println("Total files = " + (passXSDFiles + failXSDFiles));
    System.out.println("Passed      = " + passXSDFiles);
    System.out.println("Failed      = " + failXSDFiles);
  }

  public static void main(String[] args) throws Exception {
    TestSrcContent testSrcContent = new TestSrcContent(TestSrcContent.class.getName());
    testSrcContent.testSourceFiles();
  }
}
