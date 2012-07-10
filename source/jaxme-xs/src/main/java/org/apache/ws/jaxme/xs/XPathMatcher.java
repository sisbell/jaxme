/*
 * Copyright 2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.xs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEField;
import org.apache.ws.jaxme.xs.xml.XsESelector;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTKeybase;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/** 
 * An XPath matcher. Implements the restricted subset of XPath as defined
 * by the XML Schema.
 *
 * @author <a href="mailto:mrck1996@yahoo.co.uk">Chris Kirk</a>
 */
public final class XPathMatcher {
  private static final XSElementOrAttrRef[] NO_MATCHES 
    = new XSElementOrAttrRef[] {};

  /**
   * The matching of elements and attributes works by walking a finite
   * state automata produced by parsing the xpath. There is one more
   * automata than |'s in the xpath, the first node of each automata is
   * stored in this array.
   */
  private final InternalNode[] _stateMachineRoots;
  private final Locator _saxLocator;

  private XPathMatcher( Locator locator, InternalNode[] roots ) {
    _saxLocator = locator;
    _stateMachineRoots = roots;
  }

  /**
   * Matches every element and attribute referenced by the specified
   * keybase.<p>
   *
   * The result is a two dimensional array, the first dimension corresponds to
   * each xs:field used to declare the constraint. The second dimension is
   * for each 'or' used within the fields xpath query. <p>
   *
   * Only tags and attributes that were matched by the xpath will be in the
   * result, any xpath that fails to match anything will not be stored
   * in this array.<p>
   */
  public static XSElementOrAttrRef[][] match( 
    XsTKeybase keybase, 
    XSElement startingNode 
  ) 
    throws SAXException
  {
    XsESelector selector = keybase.getSelector();
    XsEField[]  fields   = keybase.getFields();

    String selectorXPath = selector.getXpath().getToken();
    XPathMatcher baseMatcher = XPathMatcher.parse(
      selector.getLocator(),
      selectorXPath, 
      true
    );

    XSElementOrAttrRef[] baseElements = baseMatcher.match( startingNode );

    final int numBaseElements = baseElements.length;
    final int numFields = fields.length;
    XSElementOrAttrRef[][] results = new XSElementOrAttrRef[numFields][];


    // populate the results, the first dimension is indexed by 'field'
    for ( int i=0; i<numFields; i++ ) {
      XsEField field = fields[i];
      String fieldXPath = field.getXpath().getToken();

      XPathMatcher fieldMatcher = XPathMatcher.parse( 
        field.getLocator(),
        fieldXPath, 
        false 
      );

      Set matches = new HashSet(3);

      // run the xpath for each of the elements identified by the selector,
      // referred to as the base elements. The values saved in matches then
      // form the second dimension of the result.
      for ( 
        int baseElementIndex=0; 
        baseElementIndex<numBaseElements; 
        baseElementIndex++
      ) {
        fieldMatcher.match( 
          baseElements[baseElementIndex].getElement(), 
          matches 
        );
      }

      int numResults = matches.size();
      results[i] = (XSElementOrAttrRef[]) matches.toArray( 
        new XSElementOrAttrRef[numResults] 
      );
    }

    return results;
  }




  /**
   * Create an XPathMatcher. Parses a string holding a restricted subset
   * of XPath and returns an object that knows how to walk XSElement
   * objects based on that XPath. 
   *
   * @param elementsOnly True if the xpath is not allowed to match attributes.
   */
  public static XPathMatcher parse( 
    Locator locator, 
    String xpath, 
    boolean elementsOnly 
  ) 
    throws SAXException 
  {
    Tokenizer tokenizer = new Tokenizer( locator, xpath );
    List automatasList = new ArrayList(3);

    InternalNode automata = createNextAutomata( tokenizer, elementsOnly );
    while ( automata != null ) {
      automatasList.add( automata );
      automata = createNextAutomata( tokenizer, elementsOnly );
    }

    int numAutomatas = automatasList.size();
    InternalNode[] automataRoots = new InternalNode[numAutomatas];
    automataRoots = (InternalNode[]) automatasList.toArray( automataRoots );

    return new XPathMatcher( locator, automataRoots );
  }

  /**
   * Return the elements and attributes matched by this xpath when applied
   * from the specified starting node.
   */
  public XSElementOrAttrRef[] match( XSElement startingNode ) 
    throws SAXException
  {
    Set matches = new HashSet(5);

    match( startingNode, matches );

    int numMatches = matches.size();
    if ( numMatches == 0 ) {
      return NO_MATCHES;
    }

    XSElementOrAttrRef[] matchesArray = new XSElementOrAttrRef[numMatches];

    return (XSElementOrAttrRef[]) matches.toArray( matchesArray );
  }

  /**
   * Return the elements and attributes matched by this xpath when applied
   * from the specified starting node.
   */
  public void match(XSElement startingNode, Set matches) throws SAXException {
    InternalNode[] roots = _stateMachineRoots;
    int numRoots = roots.length;

    for ( int i=0; i<numRoots; i++ ) {
      roots[i].match( startingNode, matches );
    }
  }



//
// Below is the parser used to create the finate state automatas.
//


  /**
   * Called by #parse to create a new automata. If there are n |'s in an
   * xpath then there will be 1+n automatas generated. An automata is
   * used to walk over the XSElement objects, with the goal of creating
   * and XSElementOrAttrRef object for each match defined by the original
   * xpath.
   */
  private static InternalNode createNextAutomata( 
    Tokenizer tokenizer, 
    boolean elementsOnly
  ) 
    throws SAXException
  {
    InternalNode rootNode = createAnEntityNode( tokenizer, elementsOnly );

    if ( rootNode != null ) {
      appendASeperatorNode( tokenizer, rootNode, elementsOnly );
    } 

    return rootNode;
  }

  /**
   * Called when the next token in the xpath <b>must</b> refer to either
   * an attribute or an element. Called by #createNextAutomata and
   * #appendAnEntityNode.
   */
  private static InternalNode createAnEntityNode( 
    Tokenizer tokenizer, 
    boolean elementsOnly
  ) throws SAXException {
    Token token = tokenizer.next();
  
    switch ( token.getTokenCode() ) {
      case Tokenizer.ELEMENT_TOKEN:
        return new NamedChildElementNode(
          token.getNameSpace(), 
          token.getLabel() 
        );
      case Tokenizer.ATTR_TOKEN:
        if ( elementsOnly ) {
          tokenizer.throwException( 
            "No references to an attribute are allowed here."
          );
        } 

        return new StaticAttributeNode(token.getNameSpace(), token.getLabel());
      case Tokenizer.THIS_TOKEN:
        return new ThisNode();
      case Tokenizer.ALL_CHILDREN_TOKEN:
        return new AllChildrenNode( token.getNameSpace() );
      case Tokenizer.END_TOKEN:
        return null; 
      default:
        tokenizer.throwException(
          "Read '" + token.getImage() + "' when a reference to either an "
          + "attribute or an element was expected."
        );
    }


    //assert false : "unreachable code reached"; 
    //This return is only here to keep the compiler happy..
    return null;
  }

  /**
   * Given the current state of the automata (previousNode), append a 
   * new node that will match either an attribute or an element.
   */
  private static void appendAnEntityNode( 
    Tokenizer tokenizer, 
    InternalNode previousNode,
    boolean elementsOnly
  ) throws SAXException {
    InternalNode newNode = createAnEntityNode( tokenizer, elementsOnly );

    // has the end of the xpath been reached?
    if ( newNode != null ) {
      previousNode.setNextNode( newNode );
      appendASeperatorNode( tokenizer, newNode, elementsOnly );
    }
  }

  /**
   * The automata is made up only of nodes that match either 1 attribute
   * or 1+ elements. However the xpath string separates these references in
   * a number of ways, this function will part those separators and
   * choose between ending the automata or parsing the next attribute/element
   * reference.
   */
  private static void appendASeperatorNode( 
    Tokenizer tokenizer, 
    InternalNode previousNode,
    boolean elementsOnly
  ) throws SAXException {
    Token token = tokenizer.next();

    switch ( token.getTokenCode() ) {
      case Tokenizer.SEPARATOR_TOKEN:
        appendAnEntityNode( tokenizer, previousNode, elementsOnly );
        return;
      case Tokenizer.OR_TOKEN:
        return;    
      case Tokenizer.END_TOKEN:
        return;
      case Tokenizer.ALL_DESCENDANTS_TOKEN:
        InternalNode newNode = new AllDescendantsNode( token.getNameSpace() );
        previousNode.setNextNode( newNode );
        appendAnEntityNode( tokenizer, newNode, elementsOnly );
        return;
      default:
        tokenizer.throwException(
          "Unexpected string '" + token.getImage() + "' encountered. Expected"
          + " either | / or nothing."
        );
    }
  }




//
// Below is the lexical code. Breaks the xpath down into its component parts.
// Used by #parse to create the matcher (or finate state automata).
//


  /**
   * Breaks the input xpath down into its basic parts. 
   */
  private static final class Tokenizer {
    public static final int ELEMENT_TOKEN         = 0;
    public static final int THIS_TOKEN            = 1;
    public static final int ATTR_TOKEN            = 2;
    public static final int ALL_CHILDREN_TOKEN    = 3;
    public static final int ALL_DESCENDANTS_TOKEN = 4;
    public static final int SEPARATOR_TOKEN       = 5;
    public static final int OR_TOKEN              = 6;
    public static final int END_TOKEN             = 7;

    public static final CharHandler STARTING_HANDLER =new InitialCharHandler();
    public static final CharHandler ATTRIBUTE_HANDLER 
      = new AttributeCharHandler();
    public static final CharHandler ELEMENT_HANDLER = new ElementCharHandler();

    private final String _xpath;
    private final Locator _saxLocator;

    private int _pos;

    public Tokenizer( Locator locator, String xpath ) {
      _xpath = xpath;
      _saxLocator = locator;
    }

    public boolean hasNext() {
      return _pos < _xpath.length();
    }

    /**
     * Fetches the next token from the input xpath.
     */
    public Token next() throws SAXException {
      TokenizerState context = new TokenizerState();
      while ( context.hasNext() ) {
        CharHandler ch = context.getCharHandler();

        ch.process( context );
      }

      _pos = context.getPos();

      return context.createToken();
    }

    public void throwException( String msg ) throws SAXException {
      throw new LocSAXException( msg, _saxLocator );
    }

    private class TokenizerState {
      private final int    _startPos;

      private int    _pos;
      private int    _markedPos;
      private int    _tokenCode = -1;
      private String _nameSpace;
      private String _label;

      private CharHandler _charHandler = STARTING_HANDLER;

      public TokenizerState() {
        _startPos = Tokenizer.this._pos;
        _pos = Tokenizer.this._pos;
        _markedPos = Tokenizer.this._pos;
      }

      public int getPos() {
        return _pos;
      }

      public CharHandler getCharHandler() {
        return _charHandler;
      }

      public void setCharHandler( CharHandler ch ) {
        _charHandler = ch;
      }

      public boolean hasMatch() {
        return _tokenCode >= 0;
      }

      public boolean hasNext() {
        boolean boo = !hasMatch() && this._pos < _xpath.length();

        return boo;
      }

      public char peekAhead() {
        return Tokenizer.this._xpath.charAt( _pos );
      }

      public char scrollAhead() {
        return Tokenizer.this._xpath.charAt( _pos++ );
      }

      // TODO skipWhiteSpace and skipOverIdentifier are almost identical,
      // consider pulling the internals of them out into a reusable utility.

      public void skipWhiteSpace() {
        final String xpath = Tokenizer.this._xpath;
        final int maxPos = xpath.length();

        int pos = _pos;
        while ( pos < maxPos ) {
          char ch = xpath.charAt( pos );

          if ( Character.isWhitespace(ch) ) {
            pos++;
          } else {
            break;
          }
        }

        _pos = pos;
      }

      public void skipOverIdentifier() {
        final String xpath = Tokenizer.this._xpath;
        final int maxPos = xpath.length();

        int pos = _pos;
        while ( pos < maxPos ) {
          char ch = xpath.charAt( pos );

          if ( Character.isLetterOrDigit(ch) || ch == '_' || ch == '-' ) {
            pos++;
          } else {
            break;
          }
        }

        _pos = pos;
      }

      public void setTokenCode( int tokenCode ) {
        _tokenCode = tokenCode;
      }

      public void markPos() {
        _markedPos = _pos;
      }

      public void saveNameSpace() {
        _nameSpace = Tokenizer.this._xpath.substring( _markedPos, _pos );
        _markedPos = _pos;
      }

      public void saveLabel() {
        _label = Tokenizer.this._xpath.substring( _markedPos, _pos );
        _markedPos = _pos;
      }

      public Token createToken() {
        if ( !hasMatch() && !hasNext() ) {
          _tokenCode = END_TOKEN;
        }

        return new Token(
          _tokenCode,
          _nameSpace,
          _label,
          _xpath.substring( _startPos, _pos )
        );
      }

      public void throwException( String msg ) throws SAXException {
        Tokenizer.this.throwException( msg );
      }
    }

    /**
     * Base class for the 'state' classes that process the xpath. The main
     * loop in Tokenizer#next keeps asking the current char handler to
     * process the context (current state of the tokenizer) until either
     * the char handler decides to hand over to another char handler, the
     * end of the xpath string is reached or a match is reached inwhich case
     * a token will be generated by Tokenizer#next.
     */
    private static abstract class CharHandler {
      public abstract void process( TokenizerState context ) 
        throws SAXException;
    }

    /**
     * The first char handler invoked for each new token. Does not know what
     * token to expect, so does its best to handle the simple one or two char
     * constant tokens (eg . * / // |) but will delegate to another char 
     * handler for the more complicated composite tokens such as @ns:foo, 
     * foo, ns:foo, ns:* etc.
     */
    private static final class InitialCharHandler extends CharHandler {
      public void process( TokenizerState context ) 
        throws SAXException
      {
        // assert context.hasNext();
        context.skipWhiteSpace();

        if ( !context.hasNext() ) {
          return;
        }

        char ch = context.peekAhead();
        switch ( ch ) {
          case '*':
            context.scrollAhead();
            context.setTokenCode( Tokenizer.ALL_CHILDREN_TOKEN );
            break;
          case '.':
            context.scrollAhead();
            context.setTokenCode( Tokenizer.THIS_TOKEN );
            break;
          case '/':
            context.scrollAhead();
            if ( context.peekAhead() == '/' ) {
              context.scrollAhead();
              context.setTokenCode( Tokenizer.ALL_DESCENDANTS_TOKEN );
            } else {
              context.setTokenCode( Tokenizer.SEPARATOR_TOKEN );
            }

            break;
          case '|':
            context.scrollAhead();
            context.setTokenCode( Tokenizer.OR_TOKEN );
            break;
          case '@':
            context.scrollAhead();
            context.markPos();
            context.setCharHandler( ATTRIBUTE_HANDLER );
            break;
          default:
            context.setCharHandler( ELEMENT_HANDLER );
        }
      }
    }

    /**
     * Accepts elementName, ns:*, ns:elementName
     */
    private static final class ElementCharHandler extends CharHandler {
      public void process( TokenizerState context ) 
        throws SAXException
      {
        context.skipOverIdentifier();

        if ( context.hasNext() ) {
          if ( context.peekAhead() == ':' ) {
            context.saveNameSpace();
            context.scrollAhead();

            if ( context.hasNext() ) {
              if ( context.peekAhead() == '*' ) {
                context.scrollAhead();
                context.setTokenCode( Tokenizer.ALL_CHILDREN_TOKEN );

                return;
              }
            } 

            context.markPos();
            context.skipOverIdentifier();
          }
        } 

        context.saveLabel();
        context.setTokenCode( Tokenizer.ELEMENT_TOKEN );
      }
    }

    /**
     * Accepts fieldName, or ns:fieldName.
     */
    private static final class AttributeCharHandler extends CharHandler {
      public void process( TokenizerState context ) 
        throws SAXException
      {
        context.skipOverIdentifier();

        if ( context.hasNext() && context.peekAhead() == ':' ) {
          context.saveNameSpace();
          context.scrollAhead();
          context.markPos();
          context.skipOverIdentifier();
        } 

        context.saveLabel();
        context.setTokenCode( Tokenizer.ATTR_TOKEN );
      }
    }
  }

  /**
   * Represents a basic part of the xpath input. These tokens are
   * created by the Tokenizer.
   */
  private static final class Token {
    private final int _tokenCode;

    /**
     * The name of the name space, or null if the match does not belong to
     * a name space.
     */
    private final String _nameSpace;

    /**
     * The local name of the element/field that was matched.
     */
    private final String _label;

    /**
     * The entire matched string in its raw form.
     */
    private final String _image;

    public Token(int tokenCode, String nameSpace, String label, String image) {
      _tokenCode = tokenCode;
      _nameSpace = nameSpace;
      _label = label;
      _image = image;
    }

    public int getTokenCode() {
      return _tokenCode;
    }

    public String getNameSpace() {
      return _nameSpace;
    }

    public String getLabel() {
      return _label;
    }

    public String getImage() {
      return _image;
    }
  }



//
// Below is the finate state automata used to match the xpath with 
// XSElements and XSAttributes. Generated by the #parse method and executed
// by the #match method.
//


  /**
   * The xpath is converted into a small finite state machine. This interface
   * is the base type of each node within the generated graph.
   */
  private abstract static class InternalNode {
    private InternalNode _next;

    /**
     * Applies the action associated with this node to the current element.
     * If the node decides that the currentElement (or attribute or the
     * element) is a match then a XSElementOrAttrRef will be added to the
     * matches list. If the match is incomplete then this node will 
     * invoke match on other nodes within the automata. If there is no
     * match at all then this node will exit immediately.
     *
     * @param currentElement The element that is the focus of the match.
     * @param matches The list that collects all of the elements/attributes
     *   that reach the end of the finite state machine.
     */
    public abstract void match( XSElement currentElement, Set matches )
      throws SAXException;

    /**
     * Link this node with another node. Due to the restrictions made to
     * the valid XPath usable within the XML Schema have very simple graphs
     * where each node links to at most one other node.
     */
    public final void setNextNode( InternalNode next ) {
      _next = next;
    }

    /**
     * Carry on to the next node in the graph. If there is no other
     * node then it means that this element is a match and should be
     * added to the matches list before returning.
     */
    protected final void continueSearchFor( 
      XSElement currentElement, 
      Set matches 
    ) throws SAXException {
      InternalNode next = _next;

      if ( next == null ) {
        matches.add( new XSElementOrAttrRef(currentElement) );
      } else {
        next.match( currentElement, matches );
      }
    }

    /**
     * A utility method that checks whether the specified namespace/name
     * matches the XsQName.
     */
    protected boolean doesMatch(String nameSpace, String name, XsQName qName) {
      boolean boo = doesNSMatch(nameSpace, qName) 
        && name.equals( qName.getLocalName() );

      return boo;
    }

    /**
     * A utility method that checks whether the specified namespace/name
     * matches the XsQName.
     */
    protected boolean doesNSMatch(String nameSpace, XsQName qName) {
      if ( nameSpace == null ) {
        return qName.getPrefix() == null;
      } else {
        return nameSpace.equals( qName.getPrefix() );
      }
    }

    // TODO consider moving this functionality into the XSElement object
    protected Iterator getChildrenIteratorFor( XSElement element ) 
      throws SAXException
    {
      List children = new ArrayList(5);

      XSType type = element.getType();

      if ( !type.isSimple() ) {
        XSComplexType complexType = type.getComplexType();

        if ( !complexType.isEmpty() ) {
          XSParticle particle = complexType.getParticle();

          if ( particle.isElement() ) {
            children.add( particle.getElement() );
          } else if ( particle.isGroup() ) {
            XSGroup group = particle.getGroup();

            XSParticle[] particles = group.getParticles();
            int numParticles = particles.length;

            for ( int i=0; i<numParticles; i++ ) {
              XSParticle groupedParticle = particles[i];

              if ( groupedParticle.isElement() ) {
                children.add( groupedParticle.getElement() );
              }
            }
          }
        }
      }

      return children.iterator();
    }
  }

  /**
   * This state represents a . within the xpath. 
   */
  private static final class ThisNode extends InternalNode {
    /**
     * Use this singleton when this node appears as the last of the matching
     * criteria.
     */
    public static final ThisNode LEAF_INSTANCE = new ThisNode();

    public void match( XSElement currentElement, Set matches ) 
      throws SAXException
    {
      continueSearchFor( currentElement, matches );
    }
  }

  /**
   * Represents a constant within the xpath that matches an elements name.
   */
  private static final class NamedChildElementNode extends InternalNode {
    private final String _nameSpace;
    private final String _name;

    /**
     * @param nameSpace The XML name space required for the matching element. 
     *   Null means no name space.
     * @param name The name of the XSElement that will be matched. The name
     *   is not nullable.
     */
    public NamedChildElementNode( String nameSpace, String name ) {
      _nameSpace = nameSpace;
      _name = name;
    }

    public void match( XSElement currentElement, Set matches ) 
      throws SAXException
    {
      Iterator iterator = getChildrenIteratorFor( currentElement );

      while ( iterator.hasNext() ) {
        XSElement element = (XSElement) iterator.next();

        if ( doesMatch(_nameSpace, _name, element.getName()) ) {
          continueSearchFor( element, matches );

          // there can only be one match..
          break;
        }
      }
    }
  }

  /**
   * Represents a constant within the xpath that matches an attributes name.
   */
  private static final class StaticAttributeNode extends InternalNode {
    private final String _nameSpace;
    private final String _name;

    /**
     * @param nameSpace The XML name space required for the matching element. 
     *   Null means no name space.
     * @param name The name of the XSElement that will be matched. The name
     *   is not nullable.
     */
    public StaticAttributeNode( String nameSpace, String name ) {
      _nameSpace = nameSpace;
      _name = name;
    }

    public void match( XSElement currentElement, Set matches ) 
      throws SAXException
    {
      //assert getNextNode() == null;
      XSType type = currentElement.getType();

      if ( !type.isSimple() ) {
        XSComplexType complexType = type.getComplexType();
        XSAttributable[] attributables = complexType.getAttributes();
        int numAttribables = attributables.length;

        // TODO consider moving this functionality onto the XSElement object
        for ( int i=0; i<numAttribables; i++ ) {
          XSAttributable attributable = attributables[i];

          if ( attributable instanceof XSAttribute ) {
            XSAttribute attribute = (XSAttribute) attributable;

            if ( doesMatch(_nameSpace, _name, attribute.getName()) ) {
              matches.add( new XSElementOrAttrRef(attribute) );

              return;
            }
          }
        }
      }
    }
  }

  /**
   * This node matches all elements that are children of the current element.
   */
  private static final class AllChildrenNode extends InternalNode {
    private final String _nameSpace;

    public AllChildrenNode( String nameSpace ) {
      _nameSpace = nameSpace;
    }

    public void match( XSElement currentElement, Set matches ) 
      throws SAXException
    {
      String nameSpace = _nameSpace;
      Iterator iterator = getChildrenIteratorFor( currentElement );

      while ( iterator.hasNext() ) {
        XSElement element = (XSElement) iterator.next();

        if ( doesNSMatch(nameSpace, element.getName()) ) {
          continueSearchFor( element, matches );
        }
      }
    }
  }

  /**
   * This node matches every element that is below the current element.
   */
  private static final class AllDescendantsNode extends InternalNode {
    private final String _nameSpace;

    public AllDescendantsNode( String nameSpace ) {
      _nameSpace = nameSpace;
    }

    public void match( XSElement currentElement, Set matches ) 
      throws SAXException
    {
      String nameSpace = _nameSpace;
      Iterator iterator = getChildrenIteratorFor( currentElement );

      while ( iterator.hasNext() ) {
        XSElement element = (XSElement) iterator.next();

        if ( doesNSMatch(nameSpace, element.getName()) ) {
          continueSearchFor( element, matches );

          // go recursive
          this.match( element, matches );
        }
      }
    }
  }
}
