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
package org.apache.ws.jaxme.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.impl.GeneratorImpl;
import org.apache.ws.jaxme.generator.sg.SGFactoryChain;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.xml.sax.InputSource;


/** <p>A command line interface for the JaxMe source generator.</p>
 * <p><em>Implementation note</em>: If you update this class, you
 * should consider updating the following files and classes as
 * well:
 * <ul>
 *   <li>{@link Generator}</li>
 *   <li>{@link XJCTask}</li>
 *   <li>docs/Reference.html</li>
 * </ul></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class Main {
  /** <p>Prints the Usage message and exits with error status.</p>
   */
  public static void Usage(String msg) {
    if (msg != null) {
      System.err.println(msg);
      System.err.println();
    }
    java.io.PrintStream ps = System.err;
    ps.println("Usage: " + Main.class.getName() + " <inputfile> [options]");
    ps.println();
    ps.println("Reads a schema definition from the given <inputfile>");
    ps.println("Possible options are:");
    ps.println("  --bindingFile=<filename> Adds an external binding file.");
    ps.println("                           Multiple external binding files may");
    ps.println("                           be used by repeating this option.");
    ps.println("  --force                  Force overwriting files");
    ps.println("  --schemaReader=<class>   Sets the SchemaReader class; defaults to");
    ps.println("                           " + JAXBSchemaReader.class.getName());
    ps.println("  --sgFactoryChain=<class> Adds an instance of <class> to the");
    ps.println("                           generation process.");
    ps.println("  --logFile=<filename>     Sets a logfile for debugging purposes.");
    ps.println("                           By default System.err is used.");
    ps.println("  --logLevel=<level>       Sets the default logging level.");
    ps.println("                           Possible levels are fatal, error (default),");
    ps.println("                           warn, info and debug");
    ps.println("  --option=<name=value>    Sets the option <name> to the given <value>.");
    ps.println("  --package=<name>         Sets the package of the generated sources to");
    ps.println("                           <name>.");
    ps.println("  --resourceTarget=<dir>   Sets the directory where to generate");
    ps.println("                           resource files. By default, the same directory");
    ps.println("                           is used for Java sources and resource files.");
    ps.println("  --target=<dir>           Sets the directory where to generate Java");
    ps.println("                           sources. By default the current directory");
    ps.println("                           is used.");
    ps.println("  --validating             Turns the XML parsers validation on.");
    System.exit(1);
  }

  /**
   * @param args the command line arguments
   */
  public static void main (String args[]) throws Exception {
    java.io.File schemaFile = null;
    Generator g = new GeneratorImpl();
    String schemaReaderClass = JAXBSchemaReader.class.getName();
    String target = null, resourceTarget = null;
    String logFile = null;
    String logLevel = null;
    String packageName = null;
    List sgFactoryChains = new ArrayList();

    for (int i = 0;  i < args.length;  i++) {
      String arg = args[i];
      if (arg.equals("--")) {
        for (int j = i;  j < args.length;  j++) {
          if (schemaFile != null) {
            Usage("Only one input file may be specified.");
          }
          schemaFile = new java.io.File(args[j]);
        }
        break;
      }
      if (arg.startsWith("--")) {
        arg = arg.substring(1);
      }
      if (arg.startsWith("-")) {
        arg = arg.substring(1);
        int optIndex = arg.indexOf('=');
        String opt = null;
        if (optIndex > 0) {
          opt = arg.substring(optIndex+1);
          arg = arg.substring(0, optIndex);
        }
        if (arg.equalsIgnoreCase("bindingFile")) {
          if (opt == null) {
        	if (i == args.length) {
        	  Usage("Missing argument for option " + arg);
        	}
        	opt = args[++i];
          }
          File f = new File(opt);
          if (f.isFile()) {
        	  try {
        		  g.addBindings(new InputSource(f.toURL().toExternalForm()));
        	  } catch (Exception e) {
        		  System.err.println("The external binding file "
        				  + f.getAbsolutePath() + " could not be read: "
        				  + e.getMessage());
        		  e.printStackTrace();
        		  System.exit(1);
        	  }
          } else {
        	  System.err.println("The external binding file "
        			  + f.getAbsolutePath() + " does not exist.");
        	  System.exit(1);
          }
        } else if (arg.equalsIgnoreCase("force")) {
        	g.setForcingOverwrite(true);
        } else if (arg.equalsIgnoreCase("schemaReader")) {
          if (logLevel != null) {
            Usage("The option " + arg + " may be used only once.");
          }
          if (opt == null) {
            if (i == args.length) {
              Usage("Missing argument for option " + arg);
            }
            opt = args[++i];
          }
          schemaReaderClass = opt;
        } else if (arg.equalsIgnoreCase("logFile")) {
          if (logFile != null) {
            Usage("The option " + arg + " may be used only once.");
          }
          if (opt == null) {
            if (i == args.length) {
              Usage("Missing argument for option " + arg);
            }
            opt = args[++i];
          }
          logFile = opt;
        } else if (arg.equalsIgnoreCase("package")) {
          if (packageName != null) {
            Usage("The option " + arg + " may be used only once.");
          }
          if (opt == null) {
            if (i == args.length) {
              Usage("Missing argument for option " + arg);
            }
            opt = args[++i];
          }
          packageName = opt;
          g.setProperty("jaxme.package.name", packageName);
        } else if (arg.equalsIgnoreCase("logLevel")) {
          if (logLevel != null) {
            Usage("The option " + arg + " may be used only once.");
          }
          if (opt == null) {
            if (i == args.length) {
              Usage("Missing argument for option " + arg);
            }
            opt = args[++i];
          }
          logLevel = opt;
        } else if (arg.equalsIgnoreCase("target")) {
          if (target != null) {
            Usage("The option " + arg + " may be used only once.");
          }
          if (opt == null) {
            if (i == args.length) {
              Usage("Missing argument for option " + arg);
            }
            opt = args[++i];
          }
          target = opt;
        } else if (arg.equalsIgnoreCase("resourceTarget")) {
            if (resourceTarget != null) {
              Usage("The option " + arg + " may be used only once.");
            }
            if (opt == null) {
              if (i == args.length) {
                Usage("Missing argument for option " + arg);
              }
              opt = args[++i];
            }
            resourceTarget = opt;
        } else if (arg.equalsIgnoreCase("option")) {
          if (opt == null) {
            if (i == args.length) {
              Usage("Missing argument for option " + arg);
            }
            opt = args[++i];
          }
          int offset = opt.indexOf('=');
          if (offset < 1) {
            System.err.println("Failed to parse option definition " + opt);
            System.err.println("Must be like --option=name=value or");
            System.err.println("--option=name=target=value");
            System.err.println();
            Usage(null);
          }
          String optName = opt.substring(0, offset);
          String optValue = opt.substring(offset+1);
          g.setProperty(optName, optValue);
        } else if (arg.equalsIgnoreCase("validating")) {
          g.setValidating(true);
        } else if ("sgFactoryChain".equals(arg)) {
          if (opt == null) {
            if (i == args.length) {
              Usage("Missing argument for option " + arg);
            }
            opt = args[++i];
          }
          Class c = null;
          try {
            c = Class.forName(opt);
          } catch (ClassNotFoundException e) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
              try {
                c = cl.loadClass(opt);
              } catch (ClassNotFoundException f) {
              }
            }
            if (c != null) {
              System.err.println("Failed to load class " + opt);
              System.exit(1);
            }
          }
          if (!SGFactoryChain.class.isAssignableFrom(c)) {
            System.err.println("The SGFactoryChain implementation " + c.getName() +
                               " is not implementing " + SGFactoryChain.class.getName());
          }
          sgFactoryChains.add(c);
        } else if (arg.equalsIgnoreCase("h")  ||  arg.equalsIgnoreCase("help")  ||
                   arg.equalsIgnoreCase("?")) {
          Usage(null);
        } else {
          Usage("Unknown option name: " + arg);
        }
      } else if (schemaFile != null) {
        Usage("Only one input file may be specified.");
       } else {
         schemaFile = new java.io.File(args[i]);
       }
    }

    if (schemaFile == null) {
      Usage("A Schema file must be specified");
    }

    SchemaReader sr = null;
    try {
      Class c = Class.forName(schemaReaderClass);
      sr = (SchemaReader) c.newInstance();
      g.setSchemaReader(sr);
      sr.setGenerator(g);
    } catch (ClassNotFoundException e) {
      System.err.println("Could not find SchemaReader class " + schemaReaderClass);
      System.exit(1);
    } catch (ClassCastException e) {
      System.err.println("Class " + schemaReaderClass +
                         " is not implementing " + SchemaReader.class.getName());
      System.exit(1);
    } catch (InstantiationException e) {
      System.err.println("Failed to instantiate SchemaReader class " + schemaReaderClass);
      System.exit(1);
    } catch (IllegalAccessException e) {
      System.err.println("Illegal access to SchemaReader class " + schemaReaderClass);
      System.exit(1);
    }

    if (sgFactoryChains.size() > 0) {
      if (!(sr instanceof JAXBSchemaReader)) {
        System.err.println("Additional instances of " + SGFactoryChain.class.getName()
                           + " may be specified only, if the schema reader is an instance of "
                           + JAXBSchemaReader.class.getName());
        System.exit(1);
      }
      JAXBSchemaReader jsr = (JAXBSchemaReader) sr;
      for (int i = 0;  i < sgFactoryChains.size();  i++) {
        jsr.addSGFactoryChain((Class) sgFactoryChains.get(i));
      }
    }

    if (target != null) {
      g.setTargetDirectory(new File(target));
    }
    if (resourceTarget != null) {
      g.setResourceTargetDirectory(new File(target));
    }

    try {
      g.generate(schemaFile);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.exit(0);     // Explicit System.exit, so that hsqldb closes nicely
  }
}
