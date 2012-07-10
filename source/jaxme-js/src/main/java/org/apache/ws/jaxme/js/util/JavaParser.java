package org.apache.ws.jaxme.js.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ws.jaxme.js.AbstractJavaMethod;
import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.JavaSourceObject;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.jparser.JavaLexer;
import org.apache.ws.jaxme.js.jparser.JavaRecognizer;
import org.apache.ws.jaxme.js.jparser.JavaTokenTypes;

import antlr.RecognitionException;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.AST;


/** <p>The <code>JavaParser</code> is a utility class, that
 * reads Java sources and converts them into instances of
 * {@link org.apache.ws.jaxme.js.JavaSource}.</p>
 */
public class JavaParser {
	private final JavaSourceFactory factory;
    private final List importStatements = new ArrayList();
    private final List generatedClasses = new ArrayList();
    private final List currentClasses = new ArrayList();
    private String packageName;

	/** <p>Creates a new instance of <code>JavaParser</code>,
     * that will use the given {@link JavaSourceFactory} for
     * creating instances of {@link JavaSource}.</p>
	 */
    public JavaParser(JavaSourceFactory pFactory) {
    	factory = pFactory;
    }

    /** Returns the factory.
     */
    public JavaSourceFactory getFactory() {
        return factory;
    }

    /** Returns the package name.
     */
    public String getPackageName() {
        return packageName;
    }

    /** Sets the package name.
     */
    public void setPackageName(String pPackageName) {
        packageName = pPackageName;
    }

    /** <p>Parses the given file.</p>
     * @return List of classes, that have been read.
     */
    public List parse(File pFile) throws RecognitionException, TokenStreamException, FileNotFoundException {
    	return parse(new FileReader(pFile));
    }

    /** <p>Parses the input read from the given
     * {@link Reader} <code>pReader</code>.</p>
     * @return List of classes, that have been read.
     */
    public List parse(Reader pReader) throws RecognitionException, TokenStreamException {
    	return parse(new JavaLexer(pReader));
    }

    private void showAST(int pLevel, AST pAST) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0;  i < pLevel;  i++) {
        	sb.append("  ");
        }
        System.out.println(sb.toString() + pAST.getType() + " " + pAST.getText());
        for (AST child = pAST.getFirstChild();  child != null;  child = child.getNextSibling()) {
        	showAST(pLevel+1, child);
        }
    }

    private void reset() {
        packageName = null;
        importStatements.clear();
    }

    /** Parses the given {@link TokenStream} <code>pStream</code>.
     * @return List of classes, that have been read.
     */
    public List parse(TokenStream pStream) throws RecognitionException, TokenStreamException {
    	reset();
        JavaRecognizer parser = new JavaRecognizer(pStream);
    	parser.compilationUnit();
    	for (AST ast = parser.getAST();  ast != null;  ast = ast.getNextSibling()) {
    		if (false) {
    			showAST(0, ast);
    		}
            parseAST(ast);
        }
        return generatedClasses;
    }

    private void parsePackageName(AST pAST) {
        setPackageName(parseIdentifier(pAST));
    }

    private void addImportStatement(JavaQName pQName) {
        importStatements.add(pQName);
    }

    private void parseImportStatement(AST pAST) {
        addImportStatement(JavaQNameImpl.getInstance(parseIdentifier(pAST)));
    }

    private String parseIdentifier(AST pAST) {
    	StringBuffer sb = new StringBuffer();
        for (AST child = pAST.getFirstChild();  child != null;  child = child.getNextSibling()) {
        	parseIdentifier(child, sb);
        }
        return sb.toString();
    }

    private String parseSimpleIdentifier(AST pAST) {
        StringBuffer sb = new StringBuffer();
        parseIdentifier(pAST, sb);
        return sb.toString();
    }

    private void parseIdentifier(AST pAST, StringBuffer sb) {
    	switch (pAST.getType()) {
	        case JavaTokenTypes.ANNOTATIONS:
	        	// Ignore this
	        	break;
	        case JavaTokenTypes.LITERAL_void:
            case JavaTokenTypes.LITERAL_boolean:
	        case JavaTokenTypes.LITERAL_byte:
	        case JavaTokenTypes.LITERAL_char:
	        case JavaTokenTypes.LITERAL_short:
	        case JavaTokenTypes.LITERAL_int:
	        case JavaTokenTypes.LITERAL_long:
	        case JavaTokenTypes.LITERAL_float:
	        case JavaTokenTypes.LITERAL_double:
	        	sb.append(pAST.getText());
	            break;
	        case JavaTokenTypes.IDENT:
            case JavaTokenTypes.STAR:
	        	sb.append(pAST.getText());
	            break;
	        case JavaTokenTypes.DOT:
	        	boolean first = true;
                for (AST child = pAST.getFirstChild();  child != null;  child = child.getNextSibling()) {
	        		if (first) {
	        			first = false;
                    } else {
                    	sb.append('.');
                    }
                    parseIdentifier(child, sb);
                }
                break;
            case JavaTokenTypes.ARRAY_DECLARATOR:
                for (AST child = pAST.getFirstChild();  child != null;
                	 child = child.getNextSibling()) {
                	parseIdentifier(child, sb);
                }
                sb.append("[]");
                break;
            default:
                throw new IllegalStateException("Unknown token: " + pAST.getType());
        }
    }

    private AST findChild(AST pAST, int pType) {
    	for (AST child = pAST.getFirstChild();  child != null;  child = child.getNextSibling()) {
    		if (child.getType() == pType) {
    			return child;
            }
        }
        return null;
    }

    private JavaSource getJavaSource(JavaSource pOuterClass, AST pAST) {
        AST classNameAST = findChild(pAST, JavaRecognizer.IDENT);
        if (classNameAST == null) {
            throw new IllegalStateException("Missing class name");
        }
        String className = classNameAST.getText();
        if (pOuterClass == null) {
            String packageName = getPackageName();
            JavaQName qName = JavaQNameImpl.getInstance(packageName, className);
            JavaSource js = factory.newJavaSource(qName, JavaSource.DEFAULT_PROTECTION);
            for (Iterator it = importStatements.iterator();  it.hasNext();  ) {
                js.addImport((JavaQName) it.next());
            }
            generatedClasses.add(js);
            return js;
        } else {
            return pOuterClass.newJavaInnerClass(className, JavaSource.DEFAULT_PROTECTION);
        }
    }

    private void parseModifiers(JavaSourceObject pObject, AST pAST) {
        AST modifiers = findChild(pAST, JavaRecognizer.MODIFIERS);
        if (modifiers == null) {
        	throw new IllegalStateException("Missing MODIFIERS");
        }
        for (AST child = modifiers.getFirstChild();  child != null;  child = child.getNextSibling()) {
            switch (child.getType()) {
                case JavaTokenTypes.LITERAL_public:
                    pObject.setProtection(JavaSource.PUBLIC);
                    break;
                case JavaTokenTypes.LITERAL_protected:
                    pObject.setProtection(JavaSource.PROTECTED);
                    break;
                case JavaTokenTypes.LITERAL_private:
                    pObject.setProtection(JavaSource.PRIVATE);
                    break;
                case JavaTokenTypes.LITERAL_static:
                    pObject.setStatic(true);
                    break;
                case JavaTokenTypes.ABSTRACT:
                    pObject.setAbstract(true);
                    break;
                case JavaTokenTypes.FINAL:
                	pObject.setFinal(true);
                    break;
            }
        }
    }

    private void parseModifiers(AST pAST) {
        AST modifiers = findChild(pAST, JavaRecognizer.MODIFIERS);
        if (modifiers == null) {
            throw new IllegalStateException("Missing MODIFIERS");
        }
    }

    private void parseModifiers(JavaSource pSource, AST pAST) {
        AST modifiers = findChild(pAST, JavaRecognizer.MODIFIERS);
        if (modifiers == null) {
            throw new IllegalStateException("Missing MODIFIERS");
        }
        for (AST child = modifiers.getFirstChild();  child != null;  child = child.getNextSibling()) {
            switch (child.getType()) {
                case JavaTokenTypes.LITERAL_public:
                    pSource.setProtection(JavaSource.PUBLIC);
                    break;
                case JavaTokenTypes.LITERAL_protected:
                    pSource.setProtection(JavaSource.PROTECTED);
                    break;
                case JavaTokenTypes.LITERAL_private:
                    pSource.setProtection(JavaSource.PRIVATE);
                    break;
                case JavaTokenTypes.LITERAL_static:
                    pSource.setStatic(true);
                    break;
                case JavaTokenTypes.ABSTRACT:
                    pSource.setAbstract(true);
                    break;
                case JavaTokenTypes.FINAL:
                	//pSource.setFinal(true);
                    break;
            }
        }
    }

    private JavaQName getQName(String pName) {
    	JavaQName result = asQName(pName);
    	return result;
    }

    private JavaQName asQName(String pName) {
        if (pName.endsWith("[]")) {
        	return JavaQNameImpl.getArray(getQName(pName.substring(0, pName.length()-2)));
        }
    	int offset = pName.indexOf('.');
    	String firstIdent;
    	String suffix;
        if (offset > 0) {
            firstIdent = pName.substring(0, offset);
            suffix = pName.substring(offset+1);
        } else {
        	firstIdent = pName;
            suffix = "";
        }
        for (int i = 0;  i < importStatements.size();  i++) {
        	JavaQName qName = (JavaQName) importStatements.get(i);
            if (qName.getClassName().equals(firstIdent)) {
            	for (StringTokenizer st = new StringTokenizer(suffix, ".");  st.hasMoreTokens();  ) {
            		String s = st.nextToken();
                    qName = JavaQNameImpl.getInnerInstance(qName, s);
                }
                return qName;
            }
        }

        if (currentClasses.size() > 0) {
        	JavaSource currentClass = (JavaSource) currentClasses.get(currentClasses.size()-1);
        	JavaInnerClass[] innerClasses = currentClass.getInnerClasses();
        	for (int i = 0;  i < innerClasses.length;  i++) {
        		if (innerClasses[i].getQName().getInnerClassName().equals(pName)) {
        			return innerClasses[i].getQName();
        		}
        	}
        }

        if ("void".equals(pName)
            ||  "byte".equals(pName)
            ||  "char".equals(pName)
            ||  "short".equals(pName)
            ||  "int".equals(pName)
            ||  "long".equals(pName)
            ||  "float".equals(pName)
            ||  "double".equals(pName)
            ||  "boolean".equals(pName)) {
        	return JavaQNameImpl.getInstance(pName);
        } else {
        	try {
        		Class c = Class.forName("java.lang." + pName);
        		return JavaQNameImpl.getInstance(c);
        	} catch (ClassNotFoundException e) {
        	}
        	if (packageName == null  ||  packageName.length() == 0) {
        		return JavaQNameImpl.getInstance(pName);
        	} else {
        		return JavaQNameImpl.getInstance(packageName, pName);
        	}
        }
    }

    private void parseImplementsOrExtends(JavaSource pSource, AST pAST, int pType) {
        AST implementsAST = findChild(pAST, pType);
        if (implementsAST == null) {
                throw new IllegalStateException("AST implements not found");
        }
        for (AST child = implementsAST.getFirstChild();  child != null;  child = child.getNextSibling()) {
            String ident = parseSimpleIdentifier(child);
            JavaQName qName = getQName(ident);
            if (pType == JavaRecognizer.IMPLEMENTS_CLAUSE) {
                pSource.addImplements(qName);
            } else {
                pSource.addExtends(qName);
            }
        }
    }

    private JavaField getJavaField(JavaSource pSource, AST pAST) {
        AST fieldNameAST = findChild(pAST, JavaRecognizer.IDENT);
        if (fieldNameAST == null) {
            throw new IllegalStateException("Missing IDENT AST");
        }
        String fieldName = fieldNameAST.getText();
        AST type = findChild(pAST, JavaRecognizer.TYPE);
        if (type == null) {
            throw new IllegalStateException("Missing TYPE AST");
        }
        String typeName = parseIdentifier(type);
        JavaQName typeQName = getQName(typeName);
        return pSource.newJavaField(fieldName, typeQName, JavaSource.DEFAULT_PROTECTION);
    }

    private void parseFieldDefinition(JavaSource pSource, AST pAST) {
    	JavaField jf = getJavaField(pSource, pAST);
        parseModifiers(jf, pAST);
    }


    private JavaMethod getJavaMethod(JavaSource pSource, AST pAST) {
        AST fieldNameAST = findChild(pAST, JavaRecognizer.IDENT);
        if (fieldNameAST == null) {
            throw new IllegalStateException("Missing IDENT AST");
        }
        String fieldName = fieldNameAST.getText();
        AST type = findChild(pAST, JavaRecognizer.TYPE);
        if (type == null) {
            throw new IllegalStateException("Missing TYPE AST");
        }
        String typeName = parseIdentifier(type);
        if (typeName == null) {
            throw new IllegalStateException("Missing identifier for " + fieldName);
        }
        JavaQName typeQName = getQName(typeName);
        return pSource.newJavaMethod(fieldName, typeQName, JavaSource.DEFAULT_PROTECTION);
    }

    private Parameter getParameter(AbstractJavaMethod pMethod, AST pAST) {
    	AST paramNameAST = findChild(pAST, JavaRecognizer.IDENT);
        if (paramNameAST == null) {
            throw new IllegalStateException("Missing IDENT AST");
        }
        String paramName = paramNameAST.getText();
        AST type = findChild(pAST, JavaRecognizer.TYPE);
        if (type == null) {
            throw new IllegalStateException("Missing TYPE AST");
        }
        String typeName = parseIdentifier(type);
        JavaQName typeQName = getQName(typeName);
        return pMethod.addParam(typeQName, paramName);
    }

    private void parseParameter(AbstractJavaMethod pMethod, AST pAST) {
        getParameter(pMethod, pAST);
        parseModifiers(pAST);
    }

    private void parseParameters(AbstractJavaMethod pMethod, AST pAST) {
        AST params = findChild(pAST, JavaRecognizer.PARAMETERS);
        if (params == null) {
        	throw new IllegalStateException("Missing PARAMETERS AST");
        }
        for (AST child = params.getFirstChild();  child != null;  child = child.getNextSibling()) {
        	switch (child.getType()) {
        	    case JavaRecognizer.PARAMETER_DEF:
                    parseParameter(pMethod, child);
                    break;
            }
        }
    }

    private void parseExceptions(AbstractJavaMethod pMethod, AST pAST) {
        AST throwsClause = findChild(pAST, JavaTokenTypes.LITERAL_throws);
        if (throwsClause != null) {
        	for (AST child = throwsClause.getFirstChild();  child != null;  child = child.getNextSibling()) {
        		String ident = parseSimpleIdentifier(child);
                JavaQName qName = getQName(ident);
                pMethod.addThrows(qName);
            }
        }
    }

    private void parseMethodDefinition(JavaSource pSource, AST pAST) {
    	JavaMethod jm = getJavaMethod(pSource, pAST);
        parseModifiers(jm, pAST);
        parseParameters(jm, pAST);
        parseExceptions(jm, pAST);
    }

    private void parseConstructorDefinition(JavaSource pSource, AST pAST) {
    	JavaConstructor jc = pSource.newJavaConstructor(JavaSource.DEFAULT_PROTECTION);
        parseModifiers(jc, pAST);
        parseParameters(jc, pAST);
        parseExceptions(jc, pAST);
    }

    private void parseObjects(JavaSource pSource, AST pAST) {
    	AST objBlock = findChild(pAST, JavaRecognizer.OBJBLOCK);
        if (objBlock == null) {
        	throw new IllegalStateException("Missing OBKBLOCK");
        }
        for (AST child = objBlock.getFirstChild();  child != null;  child = child.getNextSibling()) {
            switch (child.getType()) {
                case JavaTokenTypes.VARIABLE_DEF:
                	parseFieldDefinition(pSource, child);
                    break;
                case JavaTokenTypes.METHOD_DEF:
                	parseMethodDefinition(pSource, child);
                    break;
                case JavaTokenTypes.CTOR_DEF:
                    parseConstructorDefinition(pSource, child);
                    break;
                case JavaTokenTypes.CLASS_DEF:
                	parseClassDefinition(pSource, JavaSource.CLASS, child);
                    break;
                case JavaTokenTypes.INTERFACE_DEF:
                    parseClassDefinition(pSource, JavaSource.INTERFACE, child);
                    break;
            }
        }
    }

    private void parseClassDefinition(JavaSource pOuterClass,
                                      JavaSource.Type pType, AST pAST) {
    	JavaSource currentClass = getJavaSource(pOuterClass, pAST);
    	currentClasses.add(currentClass);
    	try {
	        currentClass.setType(pType);
	        parseModifiers(currentClass, pAST);
	        parseImplementsOrExtends(currentClass, pAST, JavaRecognizer.EXTENDS_CLAUSE);
	        if (!JavaSource.INTERFACE.equals(pType)) {
	        	parseImplementsOrExtends(currentClass, pAST, JavaRecognizer.IMPLEMENTS_CLAUSE);
	        }
	        parseObjects(currentClass, pAST);
    	} finally {
    		currentClasses.remove(currentClasses.size()-1);
    	}
    }

    private void parseAST(AST pAST) {
        switch (pAST.getType()) {
            case JavaRecognizer.PACKAGE_DEF:
                parsePackageName(pAST);
            	break;
            case JavaRecognizer.IMPORT:
                parseImportStatement(pAST);
                break;
            case JavaRecognizer.CLASS_DEF:
            	parseClassDefinition(null, JavaSource.CLASS, pAST);
                break;
            case JavaRecognizer.INTERFACE_DEF:
            	parseClassDefinition(null, JavaSource.INTERFACE, pAST);
                break;
        }
    }

    /** For tests
     */
    public static void main(String[] args) throws Exception {
    	for (int i = 0;  i < args.length;  i++) {
    		new JavaParser(new JavaSourceFactory()).parse(new File(args[i]));
        }
    }
}
