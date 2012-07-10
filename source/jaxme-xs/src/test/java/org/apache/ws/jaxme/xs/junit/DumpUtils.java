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
package org.apache.ws.jaxme.xs.junit;

import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSElementOrAttrRef;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSIdentityConstraint;
import org.apache.ws.jaxme.xs.XSKeyRef;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.SAXException;


/** 
 * Debug tool. Prints an XS data object to standard out.
 *
 * @author <a href="mailto:mrck1996@yahoo.co.uk">Chris Kirk</a>
 */
public class DumpUtils  {
 private static void dumpElements( XSElement[] elements, String prefix ) 
    throws SAXException 
  {
    int numElements = elements.length;

    for ( int i=0; i<numElements; i++ ) {
      dumpElement( elements[i], prefix );
    }
  }

  private static void dumpElement( XSElement element, String prefix ) 
    throws SAXException 
  {
    String indented = "  " + prefix;
    System.out.println( 
      prefix + "Element "+System.identityHashCode(element)
      + ": " + element.getName() 
    );

    dumpType( element.getType(), indented );
    dumpIdentityConstraints( element.getIdentityConstraints(), indented );
    dumpKeyRefs( element.getKeyRefs(), indented );
   }

  private static void dumpType( XSType type, String prefix ) 
    throws SAXException 
  {
    String indented = "  " + prefix;
    System.out.print( prefix + "Type: " );

    if ( type.isSimple() ) {
      System.out.println( "simple - " + type.getName() );
    } else {
      System.out.println( "complex - " + type.getName() );

      dumpComplexType( type.getComplexType(), indented );
    }
  }

  private static void dumpComplexType( XSComplexType type, String prefix ) 
    throws SAXException 
  {
    String indented = prefix;
    XSAttributable[] attributables = type.getAttributes();
    int numAttribables = attributables.length;

    for ( int i=0; i<numAttribables; i++ ) {
      dumpAttributable( attributables[i], indented );
    }

    if ( !type.isEmpty() ) {
      dumpParticle( type.getParticle(), indented ); 
    }
  }

  private static void dumpIdentityConstraints( 
    XSIdentityConstraint[] constraints,
    String prefix 
  ) {
    int numConstraints = constraints.length;

    for ( int i=0; i<numConstraints; i++ ) {
      dumpIdentityConstraint( constraints[i], prefix );
    }
  }

  private static void dumpKeyRefs( XSKeyRef[] keyRefs, String prefix ) {
    int numKeyRefs = keyRefs == null ? 0 : keyRefs.length;

    for ( int i=0; i<numKeyRefs; i++ ) {
      dumpKeyRef( keyRefs[i], prefix );
    }
  }

  private static void dumpIdentityConstraint(
    XSIdentityConstraint constraint,
    String prefix
  ) {
    System.out.println( 
      prefix + "constraint: " + constraint.getName() 
      + (constraint.isUnique() ? " (unique)" : "")
    );

    dumpMatchCriteria( constraint.getMatchCriteria(), prefix + "  " );
  }

  private static void dumpKeyRef( XSKeyRef keyRef, String prefix ) {
    System.out.println( 
      prefix + "keyref: " + keyRef.getName() + ": refers " 
      + keyRef.getIdentityConstraint()
    );

    dumpMatchCriteria( keyRef.getMatchCriteria(), prefix + "  " );
  }

  private static void dumpMatchCriteria( 
    XSElementOrAttrRef[][] criteria,
    String prefix
  ) {
    int numKeyParts = criteria.length;

    for ( int i=0; i<numKeyParts; i++ ) {
      XSElementOrAttrRef[] keys = criteria[i];

      int numOptions = keys.length;
      for ( int j=0; j<numOptions; j++ ) {
        dumpElementOrAttrRef( keys[j], i + ": " );
      }
    }
  }

  private static void dumpElementOrAttrRef(
    XSElementOrAttrRef ref, String prefix
  ) {
    if ( ref.isAttributeRef() ) {
      System.out.println( prefix + ref.getAttribute().getName() + " (attr) " );
    } else {
      System.out.println( prefix + ref.getElement().getName() + " (ele) " );
    }
  }

  private static void dumpAttributable( 
    XSAttributable attributable, 
    String prefix
  ) throws SAXException {
    if ( attributable instanceof XSAttribute ) {
      XSAttribute attr = (XSAttribute) attributable;
      System.out.println( 
        prefix + "attribute " + System.identityHashCode(attr) + ": " 
        + attr.getName() + " " 
        + (attr.getType().isSimple() ? "simple" : "complex!!!")
        + (attr.isOptional() ? " optional" : " required")
      );
    } else {
      System.out.println( prefix + "??? attrributable " + attributable );
    }
  }

  private static void dumpParticle( XSParticle particle, String prefix ) 
    throws SAXException 
  {
    String indented = "  " + prefix;
    System.out.print( 
      prefix + " particle: min=" + particle.getMinOccurs()
      + " max=" + particle.getMaxOccurs() + " particle_type=" 
      + particle.getType()
    );

    if ( particle.isElement() ) {
      System.out.println( " element" );
      dumpElement( particle.getElement(), indented );
    } else if ( particle.isGroup() ) {
      System.out.println( " group" );
      dumpGroup( particle.getGroup(), indented );
    } else if ( particle.isWildcard() ) {
      System.out.println( " wildcard" );
    }
  }

  private static void dumpGroup( XSGroup group, String prefix ) throws SAXException {
    String indented = "  " + prefix;
    System.out.println( prefix + "group: name=" + group.getName() );

    XSParticle[] particles = group.getParticles();
    int numParticles = particles.length;
    for ( int i=0; i<numParticles; i++ ) {
      dumpParticle( particles[i], indented );
    }
  }
}
