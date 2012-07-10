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
package org.apache.ws.jaxme.js.junit;

import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.PlaceHolder;
import junit.framework.Assert;
import junit.framework.TestCase;

/** <p>Basic test</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class BasicTest extends TestCase {
   private static final String PACKAGE_NAME = "foo";
   private static final String CLASS_NAME = "Bar";

	public BasicTest(String pName) {
     super(pName);
	}

   public JavaSource getSource(JavaSource.Protection pProtection) {
      JavaSourceFactory factory = new JavaSourceFactory();
      JavaSource js;
      JavaConstructor jcon;
      JavaMethod jm;
      JavaField jf;
      LocalJavaField lfj;
      if (pProtection == null) {
        js = factory.newJavaSource(JavaQNameImpl.getInstance(PACKAGE_NAME, CLASS_NAME));
        js.newJavaInnerClass("Bof");
        jf = js.newJavaField("someField", int.class);
        jcon = js.newJavaConstructor();
        jm = js.newJavaMethod("test", void.class);
        lfj = jm.newJavaField(String.class, "localTest");
      } else {
        js = factory.newJavaSource(JavaQNameImpl.getInstance(PACKAGE_NAME, CLASS_NAME), pProtection);
        js.newJavaInnerClass("Bof", pProtection);
        jf = js.newJavaField("someField", int.class, pProtection);
        jcon = js.newJavaConstructor(pProtection);
        jm = js.newJavaMethod("test", void.class, pProtection);
        lfj = jm.newJavaField(String.class, "localTest");
      }
      lfj.setFinal(true);
      lfj.addLine(JavaSource.getQuoted("abc"));

      jcon.addLine(jf, " = 0;");
      jm.addThrowNew(NullPointerException.class, JavaSource.getQuoted("Not implemented"));
      return js;
   }

   public String getResult(JavaSource.Protection pProtection) {
      String p = (pProtection == null  ||  pProtection.equals(JavaSource.DEFAULT_PROTECTION)) ?
        "" : (pProtection.toString() + " ");
      return "package foo;\n" +
              "\n" +
              p + "class Bar {\n" +
              "  " + p + "class Bof {\n" +
              "  }\n" +
              "\n" +
              "  " + p + "int someField;\n" +
              "\n" +
              "\n" +
              "  " + p + "Bar() {\n" +
              "    someField = 0;\n" +
              "  }\n" +
              "\n" +
              "  " + p + "void test() {\n" +
              "    final String localTest = \"abc\";\n" +
              "    throw new NullPointerException(\"Not implemented\");\n" +
              "  }\n" +
              "\n" +
              "}\n";
   }

   private String dump(String s, int offset) {
      String dump = s.substring(offset);
      if (dump.length() > 32) {
         dump = dump.substring(0, 32);
      }
      return dump;
   }

   private String hexDump(String s, int offset) {
      String dump = s.substring(offset);
      if (dump.length() > 16) {
         dump = dump.substring(0, 16);
      }
      StringBuffer sb = new StringBuffer();
      byte[] bytes = dump.getBytes();
      for (int i = 0;  i < bytes.length;  i++) {
         String h = Integer.toHexString(bytes[i]);
         while (h.length() < 2) {
            h = "0" + h;
         }
         sb.append(h);
      }
      return sb.toString();
   }

   public void assertStringEquals(String s1, String s2) {
      int l1 = s1.length();
      int l2 = s2.length();
      if (l1 != l2) {
         System.err.println("Lengths differ: " + l1 + " <=> " + l2);
      }
      int len = (l1 > l2) ? l2 : l1;
      for (int i = 0;  i < len;  i++) {
         if (s1.charAt(i) != s2.charAt(i)) {
            String s = "Strings differ at offset " + len + ": ";
            StringBuffer sb = new StringBuffer();
            for (int j = 0;  j < s.length();  j++) {
               sb.append(" ");
            }
            System.err.println(s + dump(s1, i) + " <=> " + dump(s2, i));
            System.err.println(sb + hexDump(s1, i) + " <=> " + hexDump(s2, i));
            break;
         }
      }
      Assert.assertEquals(s1, s2);
   }

   public void testNull() {
      JavaSource js = getSource(null);
      assertStringEquals(getResult(null), js.toString());
   }

   public void testDefaultProtection() {
      JavaSource js = getSource(JavaSource.DEFAULT_PROTECTION);
      assertStringEquals(getResult(JavaSource.DEFAULT_PROTECTION), js.toString());
   }

   public void testPrivate() {
      JavaSource js = getSource(JavaSource.PRIVATE);
      assertStringEquals(getResult(JavaSource.PRIVATE), js.toString());
   }

   public void testProtected() {
      JavaSource js = getSource(JavaSource.PROTECTED);
      assertStringEquals(getResult(JavaSource.PROTECTED), js.toString());
   }

   public void testPublic() {
      JavaSource js = getSource(JavaSource.PUBLIC);
      assertStringEquals(getResult(JavaSource.PUBLIC), js.toString());
   }

   public void testAbstract() {
     JavaSourceFactory factory = new JavaSourceFactory();
     JavaSource js = factory.newJavaSource(JavaQNameImpl.getInstance("com.foo", "Bar"), JavaSource.PUBLIC);
     js.setAbstract(true);
     JavaMethod jm = js.newJavaMethod("test", JavaQNameImpl.VOID, JavaSource.PUBLIC);
     jm.setAbstract(true);
     assertStringEquals("public abstract void test();\n", jm.asString());
     assertStringEquals("package com.foo;\n" +
                  "\n" +
                  "public abstract class Bar {\n" +
                  "  public abstract void test();\n" +
                  "\n" +
                  "}\n", js.asString());
   }

   private JavaSource getPlaceHolderSource(boolean pAutoMode) {
     JavaSourceFactory factory = new JavaSourceFactory();
     JavaSource js = factory.newJavaSource(JavaQNameImpl.getInstance("com.foo", "Bar"), JavaSource.PUBLIC);
     JavaMethod main = js.newJavaMethod("main", JavaQNameImpl.VOID, JavaSource.PUBLIC);
     main.addParam(String[].class, "pArgs");
     main.setStatic(true);
     main.addThrows(Exception.class);
     main.addFor(int.class, " i = 1;  i < 10;  i++");
     main.newPlaceHolder("test", pAutoMode);
     main.addEndFor();
     js.setDynamicImports(true);
     return js;
   }

   public void testPlaceHolders() {
     boolean[] autoRemovable = new boolean[]{false, true};
     for (int i = 0;  i < autoRemovable.length;  i++) {
       boolean autoMode = autoRemovable[i];
       JavaSource js = getPlaceHolderSource(autoMode);
  
       if (autoMode) {
         assertStringEquals("package com.foo;\n" +
                      "\n" +
                      "public class Bar {\n" +
                      "  public static void main(java.lang.String[] pArgs) throws java.lang.Exception {\n" +
                      "    for (int i = 1;  i < 10;  i++) {\n" +
                      "    }\n" +
                      "  }\n" +
                      "\n" +
                      "}\n", js.asString());
       } else {
         boolean gotException = false;
         try {
           js.asString();
         } catch (IllegalStateException e) {
           gotException = true;
         }
         assertTrue(gotException);
       }

       js = getPlaceHolderSource(autoMode);
       JavaMethod main = js.getMethod("main", new JavaQName[]{JavaQNameImpl.getInstance(String[].class)});
       PlaceHolder test = main.getPlaceHolder("test");
       assertNotNull(test);
       test.remove();
       main.addLine("// I am here");

       String expect = "package com.foo;\n" +
                       "\n" +
                       "public class Bar {\n" +
                       "  public static void main(java.lang.String[] pArgs) throws java.lang.Exception {\n" +
                       "    for (int i = 1;  i < 10;  i++) {\n" +
                       "      // I am here\n" +
                       "    }\n" +
                       "  }\n" +
                       "\n" +
                       "}\n";
       String got = js.asString();
       assertStringEquals(expect, got);
     }
   }
}
