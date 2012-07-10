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
package org.apache.ws.jaxme.generator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/** <p>Helper class, that filters a file by replacing certain
 * patterns. Used by the test suite.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: DateFilter.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class DateFilter {
  private File fromFile, toFile;
  private boolean force;

  public void setFromFile(File pFile) { fromFile = pFile; }
  public File getFromFile() { return fromFile; }
  public void setToFile(File pFile) { toFile = pFile; }
  public File getToFile() { return toFile; }
  public void setForce(boolean pForce) { force = pForce; }
  public boolean getForce() { return force; }

  public String validate() {
    StringBuffer result = new StringBuffer();
    if (fromFile == null) {
      result.append("Input file (option fromFile) is not set.\n");
    } else if (!fromFile.exists()) {
      result.append("Input file " + fromFile + " doesn't exist.");
    }
    if (toFile == null) {
      result.append("toFile is not set.\n");
    }

    if (result.length() > 0) {
      return result.toString();
    } else {
      return null;
    }
  }

  public boolean isRunning() {
    if (getForce()) {
      return true;
    }

    long l1 = fromFile.lastModified();
    if (l1 == -1) {
      return true;
    }
    long l2 = toFile.lastModified();
    if (l2 == -1) {
      return true;
    }
    return l2 <= l1;
  }

  public List getLines() throws IOException {
    List result = new ArrayList();
    BufferedReader br = new BufferedReader(new FileReader(getFromFile()));
    for (;;) {
      String s = br.readLine();
      if (s == null) {
        break;
      }
      result.add(s);
    }
    return result;
  }

  public Calendar getCalendar(String pDateTime, String pPattern)
      throws ParseException {
    DateFormat simpleFormat = new SimpleDateFormat(pPattern);
    Calendar cal = Calendar.getInstance();
    cal.setTime(simpleFormat.parse(pDateTime));
    return cal;
  }

  public String replacePattern(String pPattern, String pSourceFormat,
                                DateFormat pTargetFormat) throws ParseException {
    return pTargetFormat.format(getCalendar(pPattern, pSourceFormat).getTime());
  }

  public String getDateTime(String pDateTime) throws ParseException {
    return replacePattern(pDateTime, "yyyy-MM-dd HH:mm:ss",
                           DateFormat.getDateTimeInstance());
  }

  public String getDate(String pDateTime) throws ParseException {
    return replacePattern(pDateTime, "yyyy-MM-dd",
                           DateFormat.getDateInstance());
  }

  public String getTime(String pDateTime) throws ParseException {
    return replacePattern(pDateTime, "HH:mm:ss",
                           DateFormat.getTimeInstance());
  }

  public void replace(List pLines, List fromStrings, List toStrings) {
    for (ListIterator iter = pLines.listIterator();  iter.hasNext();  ) {
      String s = (String) iter.next();
      StringBuffer result = new StringBuffer();

      while (s.length() > 0) {
        boolean done = false;
        for (int i = 0;  i < fromStrings.size();  i++) {
          String from = (String) fromStrings.get(i);
          if (s.startsWith(from)) {
            result.append((String) toStrings.get(i));
            s = s.substring(from.length());
            done = true;
            break;
          }
        }
        if (!done) {
          result.append(s.charAt(0));
          s = s.substring(1);
        }
      }

      iter.set(result.toString());
    }
  }

  public void putLines(List pLines) throws IOException {
	Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getToFile()), "UTF-8"));
    for (Iterator iter = pLines.iterator();  iter.hasNext();  ) {
      String s = (String) iter.next();
      fw.write(s + "\n");
    }
    fw.close();
  }

  public void execute() throws IOException, ParseException {
    if (!isRunning()) {
      return;
    }
    List lines = getLines();

    List fromStrings = new ArrayList();
    List toStrings = new ArrayList();

    fromStrings.add("2002-12-17 12:23:11");
    toStrings.add(getDateTime("2002-12-17 12:23:11"));

    fromStrings.add("2002-12-16 12:00:11");
    toStrings.add(getDateTime("2002-12-16 12:00:11"));

    fromStrings.add("2002-12-17");
    toStrings.add(getDate("2002-12-17"));

    fromStrings.add("2002-12-16");
    toStrings.add(getDate("2002-12-16"));

    fromStrings.add("12:23:11");
    toStrings.add(getTime("12:23:11"));

    fromStrings.add("12:00:11");
    toStrings.add(getTime("12:00:11"));

    replace(lines, fromStrings, toStrings);
    putLines(lines);
  }

  public static void Usage(String pMsg) {
    PrintStream ps = System.err;
    if (pMsg != null) {
      ps.println(pMsg);
      ps.println();
    }
    ps.println("Usage: java " + DateFilter.class + " <options>");
    ps.println();
    ps.println("Possible options are:");
    ps.println(" --fromFile <file>    Sets the input file");
    ps.println(" --toFile <file>      Sets the output file");
    ps.println(" --force              Forces overwriting of the output file");
    System.exit(1);
  }

  public static void main(String[] args) throws Exception {
    DateFilter filter = new DateFilter();
    List argList = new ArrayList(Arrays.asList(args));
    while (!argList.isEmpty()) {
      String arg = (String) argList.remove(0);
      String opt = null;
      if (arg.startsWith("--")) {
        opt = arg.substring(2);
      } else if (arg.startsWith("-")) {
        opt = arg.substring(1);
      } else {
        Usage("Unknown argument: " + arg);
      }

      if ("fromFile".equals(opt)) {
        if (argList.isEmpty()) {
          Usage("Option " + arg + " requires an argument: Input file");
        }
        filter.setFromFile(new File((String) argList.remove(0)));
      } else if ("toFile".equals(opt)) {
        if (argList.isEmpty()) {
          Usage("Option " + arg + " requires an argument: Output file");
        }
        filter.setToFile(new File((String) argList.remove(0)));
      } else if ("force".equals(opt)) {
        filter.setForce(true);
      } else {
        Usage("Unknown option: " + opt);
      }
    }

    String msg = filter.validate();
    if (msg != null) {
      Usage(msg);
    } else {
      filter.execute();
    }
  }
}
