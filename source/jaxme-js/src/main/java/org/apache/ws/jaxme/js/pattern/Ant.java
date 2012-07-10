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
package org.apache.ws.jaxme.js.pattern;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.apps.XmlRpcClientGenerator;
import org.apache.ws.jaxme.js.pattern.VersionGenerator.TableInfo;
import org.apache.ws.jaxme.logging.AntProjectLoggerFactory;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.logging.LoggerFactory;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.ColumnImpl;
import org.apache.ws.jaxme.sqls.impl.SQLFactoryImpl;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;


/** <p>A set of Ant tasks for running the generators in the
 * pattern package.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: Ant.java 233486 2005-08-19 14:10:04Z mac $
 */
public class Ant {
  protected abstract static class ReallyBasicAntTask extends Task {
    private File destDir;
    private boolean settingLoggerFactory = true;
    private String classpathRef;
    private Path classpath;
    /** Sets, whether the Ant task initializes its own logger factory.
     * Defaults to true.
     */
    public void setSettingLoggerFactory(boolean pSettingLoggerFactory) {
      settingLoggerFactory = pSettingLoggerFactory;
    }
    /** Returns, whether the Ant task initializes its own logger factory.
     * Defaults to true.
     */
    public boolean isSettingLoggerFactory() {
      return settingLoggerFactory;
    }
    /** Sets, the destination directory, where sources are being generated to.
     * Defaults to the current directory.
     */
    public void setDestDir(File pDir) {
      destDir = pDir;
    }
    /** Sets, the destination directory, where sources are being generated to.
     * Defaults to the current directory.
     */
    public File getDestDir() {
       return destDir;
    }
    /** Sets a classpath reference, being used to load compiled classes
     * or ressources.
     */
    public void setClasspathRef(String pRef) {
    	if(classpath != null) {
    		throw new BuildException("The 'classpathRef' attribute and the nested 'classpath' element are mutually exclusive.",
                                     getLocation());
        }
        classpathRef = pRef;
    }
    /** Returns a classpath reference, being used to load compiled classes
     * or ressources.
     */
    public String getClasspathRef() {
    	return classpathRef;
    }
    /** Sets a classpath, being used to load compiled classes
     * or ressources.
     */
    public void addClasspath(Path pClasspath) {
    	if (classpath != null) {
    		throw new BuildException("Multiple nested 'classpath' elements are forbidden.", getLocation());
        }
        if (classpathRef != null) {
            throw new BuildException("The 'classpathRef' attribute and the nested 'classpath' element are mutually exclusive.",
                    getLocation());
        }
        classpath = pClasspath;
    }
    /** Returns a classpath, being used to load compiled classes
     * or ressources.
     */
    public Path getClasspath() {
    	return classpath;
    }
    /** <p>Performs validation of the attributes and nested elements.</p>
     */
    public void finish() {
    }
    /** Abstract method, which is invoked to do the real work.
     * @throws Exception
     */
    public abstract void doExecute() throws Exception;

    public void execute() {
      if (isSettingLoggerFactory()) {
        LoggerFactory loggerFactory = LoggerAccess.getLoggerFactory();
        if (!(loggerFactory instanceof AntProjectLoggerFactory)) {
          LoggerAccess.setLoggerFactory(new AntProjectLoggerFactory(this));
        }
      }
      Path classPath = getClasspath();
      if (classPath == null) {
      	String cRef = getClasspathRef();
        if (cRef != null) {
        	classPath = (Path) getProject().getReference(cRef);
            if (classPath == null) {
            	throw new BuildException("The reference " + cRef + " is not set.", getLocation());
            }
        }
      }
      AntClassLoader acl;
      if (classPath == null) {
      	acl = null;
      } else {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
        	cl = getClass().getClassLoader();
            if (cl == null) {
            	cl = ClassLoader.getSystemClassLoader();
            }
        }
        acl = new AntClassLoader(cl, getProject(), classPath, true);
        acl.setThreadContextLoader();
      }
      try {
          finish();
          doExecute();
      } catch (BuildException e) {
      	  throw e;
      } catch (Exception e) {
      	  throw new BuildException(e, getLocation());
      } finally {
      	  if (acl != null) {
      	  	  acl.resetThreadContextLoader();
          }
      }
    }
  }

  protected abstract static class BasicAntTask extends ReallyBasicAntTask {
    private JavaQName targetClass;
    /** Sets the name of the class being generated.
     */
    public void setTargetClass(String pTargetClass) {
	   targetClass = getJavaQName(pTargetClass);
    }
    public void finish() {
		  if (targetClass == null) {
		    throw new BuildException("The attribute 'targetClass' must be set.");
		  }
	  }
      /** Abstract method, which is invoked to generate the target class.
       */
	  public abstract void generate(JavaSourceFactory pFactory, JavaQName pTargetClass)
	      throws Exception;
	  public void doExecute() {
	 	  finish();
	 	  try {
			  JavaSourceFactory factory = new JavaSourceFactory();
			  generate(factory, targetClass);
			  factory.write(getDestDir());
	 	  } catch (Exception e) {
	 		  throw new BuildException(e, getLocation());
	 	  }
	  }
  }

  protected static JavaQName getJavaQName(String pName) {
      return JavaQNameImpl.getInstance(pName);
  }

  /** The <code>AntProxyGenerator</code> is an Ant task
   * providing access to the {@link ProxyGenerator}.
   */
  public static class AntProxyGenerator extends BasicAntTask {
    private JavaQName extendedClass;
    private List implementedInterfaces = new ArrayList();

    /** Sets the class being extended, if any.
     */
    public void setExtendedClass(String pTargetClass) {
      extendedClass = getJavaQName(pTargetClass);
    }
    /** Adds a new interface being implemented.
     */
    public InterfaceDescription createImplementedInterface() {
    	ClassLoader cl = Thread.currentThread().getContextClassLoader();
    	if (cl == null) {
    		cl = ClassLoader.getSystemClassLoader();
    	}
    	InterfaceDescription result = new InterfaceDescription(cl);
    	implementedInterfaces.add(result);
    	return result;
    }
    public void finish() {
    	super.finish();
      if (implementedInterfaces.size() == 0) {
        throw new BuildException("You must specify at least one interface being implemented (child element 'implementedInterface')");
      }
    }
    public void generate(JavaSourceFactory pFactory, JavaQName pTargetClass)
        throws BuildException {
        ProxyGenerator proxyGenerator = new ProxyGenerator();
        if (extendedClass != null) {
          proxyGenerator.setExtendedClass(extendedClass);
        }
        try {
            proxyGenerator.generate(pFactory, pTargetClass,
                                    (InterfaceDescription[])
                                      implementedInterfaces.toArray(new InterfaceDescription[implementedInterfaces.size()]));
        } catch (Exception e) {
        	throw new BuildException(e, getLocation());
        }
    }
  }

  /** Ant task for generating typesafe enumerations.
   */
  public static class AntTypesafeEnumerationGenerator extends BasicAntTask {
	  private List items = new ArrayList();
	  private boolean isAddingEquals = true;

      /** Sets whether the equals and hashCode methods are being
       * generated. Defaults to true.
       */ 
	  public void setAddingEquals(boolean pAddingEquals) {
	 	  isAddingEquals = pAddingEquals;
	  }

      /** Creates a new, nested item.
       */ 
	  public TypesafeEnumerationGenerator.Item createItem() {
	 	  TypesafeEnumerationGenerator.Item item = new TypesafeEnumerationGenerator.Item();
	 	  items.add(item);
	 	  return item;
	  }

    public void finish() {
	 	  super.finish();
	 	  if (items.size() == 0) {
	 		  throw new BuildException("The generated enumeration must have at least a single item.");
	 	  }
	  }

    public void generate(JavaSourceFactory pFactory, JavaQName pTargetClass)
        throws Exception {
    	TypesafeEnumerationGenerator generator = new TypesafeEnumerationGenerator();
    	generator.setAddingEquals(isAddingEquals);
		  TypesafeEnumerationGenerator.Item[] myItems = (TypesafeEnumerationGenerator.Item[])
		  this.items.toArray(new TypesafeEnumerationGenerator.Item[this.items.size()]);
		  generator.generate(pFactory, pTargetClass, myItems);
    }
  }

  /** Ant task for the {@link org.apache.ws.jaxme.js.pattern.ChainGenerator}.
   */
  public static class AntChainGenerator extends ReallyBasicAntTask {
	  private final List chains = new ArrayList();
	  private File srcDir;
	  /** Creates a new, nested element with another chain being
	   * generated.
	   */
	  public ChainGenerator createChain() {
		  ChainGenerator chain = new ChainGenerator();
		  chains.add(chain);
		  return chain;
	  }
	  public void setSrcDir(File pSrcDir) {
		  srcDir = pSrcDir;
	  }
	  public void finish() {
		  if (chains.size() == 0) {
			  throw new BuildException("At least one nested 'chain' element must be given.",
					  getLocation());
		  }
	  }
	  public void doExecute() {
		  ClassLoader cl = Thread.currentThread().getContextClassLoader();
		  if (cl == null) {
			  cl = ClassLoader.getSystemClassLoader();
		  }
		  if (srcDir != null) {
			  Path path = (Path) getProject().createDataType("path");
			  path.createPathElement().setLocation(srcDir);
			  cl = new AntClassLoader(getProject(), path);
		  }
		  JavaSourceFactory pFactory = new JavaSourceFactory();
		  for (Iterator iter = chains.iterator();  iter.hasNext();  ) {
			  ChainGenerator chain = (ChainGenerator) iter.next();
			  try {
				  chain.generate(pFactory, cl);
			  } catch (Exception e) {
				  throw new BuildException(e, getLocation());
			  }
		  }
		  try {
			  pFactory.write(getDestDir());
		  } catch (IOException e) {
			  throw new BuildException(e, getLocation());
		  }
	  }
  }

  /** Ant task for the {@link org.apache.ws.jaxme.js.pattern.VersionGenerator}
   */
  public static class AntVersionGenerator extends BasicAntTask {
    private String driver, url, user, password, schema, verColumn;
    private List tables;
    private boolean isGeneratingLogging;

    /** Returns the JDBC driver.
     */
    public String getDriver() { return driver; }
    /** Sets the JDBC driver.
     */
    public void setDriver(String pDriver) { driver = pDriver; }
    /** Returns the JDBC password.
     */
    public String getPassword() { return password; }
    /** Sets the JDBC password.
     */
    public void setPassword(String pPassword) { password = pPassword; }
    /** Returns the JDBC URL.
     */
    public String getUrl() { return url; }
    /** Sets the JDBC URL.
     */
    public void setUrl(String pUrl) { url = pUrl; }
    /** Returns the JDBC user.
     */
    public String getUser() { return user; }
    /** Sets the JDBC user.
     */
    public void setUser(String pUser) { user = pUser; }
    /** Returns the database schema name.
     */
    public String getSchema() { return schema; }
    /** Sets the database schema name.
     */
    public void setSchema(String pSchema) { schema = pSchema; }
    /** Sets the table list; the table names are separated with white space.
     */
    public void setTables(String pTables) {
      tables = new ArrayList();
      for (StringTokenizer st = new StringTokenizer(pTables);  st.hasMoreTokens();  ) {
        String tableName = st.nextToken();
        tables.add(tableName);
      }
    }
    /** Returns the table list.
     */
    public List getTables() {
      return tables;
    }
    /** Sets the column with the version number.
     */
    public void setVerColumn(String pColumn) {
      verColumn = pColumn;
    }
    /** Returns the column with the version number.
     */
    public String getVerColumn() {
      return verColumn;
    }
    /** Sets whether logging statements are being generated.
     */
    public void setGeneratingLogging(boolean pGeneratingLogging) {
      isGeneratingLogging = pGeneratingLogging;
    }
    /** Returns whether logging statements are being generated.
     */
    public boolean isGeneratingLogging() {
      return isGeneratingLogging;
    }

    protected Connection getConnection() throws ClassNotFoundException, SQLException {
       String myUrl = getUrl();
       if (myUrl == null) {
         throw new NullPointerException("Missing 'url' attribute");
       }

       String myDriver = getDriver();
       if (myDriver != null) {
         try {
           Class.forName(myDriver);
         } catch (ClassNotFoundException ex) {
           try {
             ClassLoader cl = Thread.currentThread().getContextClassLoader();
             if (cl == null) {
               throw new ClassNotFoundException(myDriver);
             }
             cl.loadClass(myDriver);
           } catch (ClassNotFoundException ex2) {
             throw ex;
           }
         }
       }       

       return DriverManager.getConnection(myUrl, getUser(), getPassword());
    }

    private class IdIncrementer implements VersionGenerator.ColumnUpdater {
      private final List columns;
      IdIncrementer(List pColumns) {
        columns = pColumns;
      }
      public void update(JavaMethod pMethod, TableInfo pTableInfo, DirectAccessible pConnection, DirectAccessible pMap, DirectAccessible pRow) {
        for (Iterator iter = columns.iterator();  iter.hasNext();  ) {
          Integer columnNum = (Integer) iter.next();
          pMethod.addLine(pRow, "[", columnNum, "] = Long.toString(Long.parseLong((String) ",
                          pRow, "[", columnNum, "])+1);");
        }
      }
    } 

    private class VerNumIncrementer implements VersionGenerator.ColumnUpdater {
      private final int columnNumber;
      VerNumIncrementer(int pColumnNumber) {
        columnNumber = pColumnNumber;
      }
      public void update(JavaMethod pMethod, TableInfo pTableInfo, DirectAccessible pConnection, DirectAccessible pMap, DirectAccessible pRow) {
        pMethod.addLine(pRow, "[" + columnNumber + "] = new Integer(((Integer) ",
                        pRow, "[" + columnNumber + "]).intValue()+1);");
      }
    }

    public void generate(JavaSourceFactory pFactory, JavaQName pTargetClass) throws Exception {
      List myTables = getTables();
      if (myTables == null) {
        throw new NullPointerException("Missing 'tables' attribute");
      }
      if (getVerColumn() == null) {
        throw new NullPointerException("Missing 'verColumn' attribute");
      }
      Column.Name columnName = new ColumnImpl.NameImpl(getVerColumn());

      SQLFactory factory = new SQLFactoryImpl();
      Schema sch = factory.getSchema(getConnection(), getSchema());

      VersionGenerator versionGenerator = new VersionGenerator();
      versionGenerator.setGeneratingLogging(isGeneratingLogging());
      boolean isFirstTable = true;

      for (Iterator iter = myTables.iterator();  iter.hasNext();  ) {
        String tableName = (String) iter.next();
        Table table = sch.getTable(tableName);
        if (table == null) {
          throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        VersionGenerator.ColumnUpdater columnUpdater;
        if (isFirstTable) {
          Column column = null;
          int columnNum = -1;
          int i = 0;
          for (Iterator colIter = table.getColumns();  colIter.hasNext();  i++) {
            Column colIterColumn = (Column) colIter.next();
            if (colIterColumn.getName().equals(columnName)) {
              column = colIterColumn;
              columnNum = i;
              break;
            }
          }
          if (column == null) {
            throw new IllegalArgumentException("No column " + columnName +
                                               " found in table " + table.getQName());
          }
          isFirstTable = false;
          columnUpdater = new VerNumIncrementer(columnNum);
        } else {
          List pkColumns = new ArrayList();
          Index primaryKey = table.getPrimaryKey();
          if (primaryKey != null) {
            for (Iterator pkIter = primaryKey.getColumns();  pkIter.hasNext();  ) {
              Column pkColumn = (Column) pkIter.next();
              int columnNum = -1;
              int i = 0;
              for (Iterator colIter = table.getColumns();  colIter.hasNext();  i++) {
                Column colIterColumn = (Column) colIter.next();
                if (colIterColumn.getName().equals(pkColumn.getName())) {
                  columnNum = i;
                  break;
                }
              }
              if (columnNum == -1) {
                throw new IllegalStateException("Primary key column " + pkColumn.getQName() +
                                                " not found in table " + table.getQName());
              }
              pkColumns.add(new Integer(columnNum));
            }
          }
          if (pkColumns.size() == 0) {
            throw new IllegalArgumentException("The table " + table.getQName() +
                                               " doesn't have a primary key.");
          }
          columnUpdater = new IdIncrementer(pkColumns);
        }
        versionGenerator.addTable(table, columnUpdater);
      }

      JavaSource js = pFactory.newJavaSource(pTargetClass);
      versionGenerator.getCloneMethod(js);
    }
  }

    /** An ant task for the {@link org.apache.ws.jaxme.js.apps.XmlRpcClientGenerator}.
     */
    public static class XmlRpcGenerator extends ReallyBasicAntTask {
    	/** The nested child element "dispatcher".
    	 */
        public static class Dispatcher {
            private String name;
            private boolean implementingXmlRpcHandler = true;

            /** Sets the fully qualified name of the dispatcher class.
             */
            public void setName(String pName) {
                name = pName;
            }

            /** Returns the fully qualified name of the dispatcher class.
             */
            public String getName() {
                return name;
            }

            /** Returns whether the dispatcher will implement
             * XmlRpcHandler. Defaults to true.
             */
            public boolean isImplementingXmlRpcHandler() {
            	return implementingXmlRpcHandler;
            }

            /** Sets whether the dispatcher will implement
             * XmlRpcHandler. Defaults to true.
             */
            public void setImplementingXmlRpcHandler(boolean pImplementingXmlRpcHandler) {
                implementingXmlRpcHandler = pImplementingXmlRpcHandler;
            }
        }

        private final List serverClasses = new ArrayList();
        private final JavaSourceFactory jsf = new JavaSourceFactory();
        private String targetPackage;
        private Dispatcher dispatcher;

        /** Creates the dispatcher.
         */
        public Dispatcher createDispatcher() {
            if (dispatcher != null) {
                throw new BuildException("The nested 'dispatcher' element must not be used more than once.");
            }
            dispatcher = new Dispatcher();
            return dispatcher;
        }

        /** Returns the dispatcher.
         */
        public Dispatcher getDispatcher() {
        	return dispatcher;
        }

        /** Sets the target package.
         */
        public void setTargetPackage(String pPackage) {
        	targetPackage = pPackage;
        }

        /** Returns the target package.
         */
        public String getTargetPackage() {
            return targetPackage;
        }

        /** Creates a new, nested element with a {@link FileSet} of
         * server side classes, for which client stubs are being
         * generated.
         */
        public FileSet createServerClasses() {
            FileSet fs = (FileSet) getProject().createDataType("fileset");
            serverClasses.add(fs);
            return fs;
        }

        public void finish() {
            super.finish();
            if (targetPackage == null) {
            	throw new BuildException("Missing 'targetPackage' attribute",
                                         getLocation());
            }
            if (serverClasses.size() == 0) {
            	throw new BuildException("Missing nested element 'serverClasses'",
                                         getLocation());
            }
        }

		public void doExecute() throws Exception {
            JavaSourceFactory jsf = new JavaSourceFactory();
            JavaSourceFactory inputs = new JavaSourceFactory();
            XmlRpcClientGenerator gen = new XmlRpcClientGenerator(jsf, getTargetPackage());
            List sources = new ArrayList();
            for (int i = 0;  i < serverClasses.size();  i++) {
            	FileSet fs = (FileSet) serverClasses.get(i);
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                String[] files = ds.getIncludedFiles();
                for (int j = 0;  j < files.length;  j++) {
                    String s = files[j];
                    Reflector r;
                    if (s.endsWith(".class")) {
                        s = s.substring(0, s.length() - ".class".length());
                        r = new CompiledClassReflector(s.replace('/', '.'), Thread.currentThread().getContextClassLoader());
                    } else if (s.endsWith(".java")) {
                        r = new SourceReflector(new File(ds.getBasedir(), s));
                    } else {
                    	throw new BuildException("Unknown extension in file name: " + s
                                                 + ", expected .class or .java",
                                                 getLocation());
                    }
                    JavaSource js = r.getJavaSource(inputs);
                    sources.add(js);
                }
            }
            for (int i = 0;  i < sources.size();  i++) {
                JavaSource js = (JavaSource) sources.get(i);
                if (js.isAbstract()) {
                	getProject().log("Ignoring abstract class " + js.getQName(), Project.MSG_VERBOSE);
                } else {
                	getProject().log("Generating XML-RPC client for " + js.getQName(), Project.MSG_DEBUG);
                	gen.addClass(js, inputs);
                }
            }

            Dispatcher disp = getDispatcher();
            if (disp != null) {
                gen.setDispatcherImplementsXmlRpcHandler(disp.isImplementingXmlRpcHandler());
            	gen.getDispatcher(JavaQNameImpl.getInstance(disp.getName()));
            }
            jsf.write(getDestDir());
		}
    }
}
