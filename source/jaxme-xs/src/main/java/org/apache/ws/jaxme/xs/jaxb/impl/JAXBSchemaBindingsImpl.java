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
package org.apache.ws.jaxme.xs.jaxb.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.xs.jaxb.JAXBJavadoc;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectImpl;


/** <p>Implementation of the SchemaBindings interface.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBSchemaBindingsImpl.java 231789 2004-02-22 00:52:34Z jochen $
 */
public class JAXBSchemaBindingsImpl extends JAXBXsObjectImpl implements JAXBSchemaBindings {
  private Package myPackage;
  private List nameXmlTransforms;

  public static class PackageImpl extends JAXBXsObjectImpl implements JAXBSchemaBindings.Package {
    /** <p>Creates a new instance of JAXBSchemaBindingsImpl.</p>
     */
    protected PackageImpl(XsObject pParent) {
      super(pParent);
    }

    private String name;
    private JAXBJavadoc javadoc;

    public void setName(String pName) {
      name = pName;
    }

    public String getName() {
      return name;
    }

    public JAXBJavadoc createJavadoc() {
      if (javadoc != null) {
        throw new IllegalStateException("Multiple javadoc elements are not supported.");
      }
      javadoc = getJAXBXsObjectFactory().newJAXBJavadoc(this);
      return javadoc;
    }

    public JAXBJavadoc getJavadoc() {
      return javadoc;
    }
  }

  public static class NameXmlTransformImpl extends XsObjectImpl
      implements JAXBSchemaBindings.NameXmlTransform {
    public static class NameTransformationImpl extends XsObjectImpl
        implements JAXBSchemaBindings.NameTransformation {
      /** <p>Creates a new instance of JAXBSchemaBindingsImpl.</p>
       */
      protected NameTransformationImpl(XsObject pParent) {
        super(pParent);
      }

      private String suffix, prefix;

      public void setSuffix(String pSuffix) {
        suffix = pSuffix;
      }

      public String getSuffix() {
        return suffix;
      }

      public void setPrefix(String pPrefix) {
        prefix = pPrefix;
      }

      public String getPrefix() {
        return prefix;
      }
    }

    /** <p>Creates a new instance of JAXBSchemaBindingsImpl.</p>
     */
    protected NameXmlTransformImpl(XsObject pParent) {
      super(pParent);
    }

    private NameTransformation typeName, elementName, modelGroupName, anonymousTypeName;

    public NameTransformation createTypeName() {
      if (typeName != null) {
        throw new IllegalStateException("Multiple instances of typeName are not supported.");
      }
      typeName = ((JAXBXsObjectFactory) getObjectFactory()).newNameTransformation(this);
      return typeName;
    }

    public NameTransformation getTypeName() {
      return typeName;
    }

    public NameTransformation createElementName() {
      if (elementName != null) {
        throw new IllegalStateException("Multiple instances of elementName are not supported.");
      }
      elementName = ((JAXBXsObjectFactory) getObjectFactory()).newNameTransformation(this);
      return elementName;
    }

    public NameTransformation getElementName() {
      return elementName;
    }

    public NameTransformation createModelGroupName() {
      if (modelGroupName != null) {
        throw new IllegalStateException("Multiple instances of modelGroupName are not supported.");
      }
      modelGroupName = ((JAXBXsObjectFactory) getObjectFactory()).newNameTransformation(this);
      return modelGroupName;
    }

    public NameTransformation getModelGroupName() {
      return modelGroupName;
    }

    public NameTransformation createAnonymousTypeName() {
      if (anonymousTypeName != null) {
        throw new IllegalStateException("Multiple instances of anonymousTypeName are not supported.");
      }
      anonymousTypeName = ((JAXBXsObjectFactory) getObjectFactory()).newNameTransformation(this);
      return anonymousTypeName;
    }

    public NameTransformation getAnonymousTypeName() {
      return anonymousTypeName;
    }
  }

  /** <p>Creates a new instance of SchemaBindings with the given
   * GlobalBindings.</p>
   */
  protected JAXBSchemaBindingsImpl(XsObject pParent) {
    super(pParent);
  }

  /** <p>Creates a new Package implementation.</p>
   */
  public Package createPackage() {
    if (myPackage != null) {
      throw new IllegalStateException("Multiple package declarations are not supported.");
    }
    myPackage = ((JAXBXsObjectFactory) getObjectFactory()).newPackage(this);
    return myPackage;
  }

  public Package getPackage() {
    return myPackage;
  }

  /** <p>Creates a new NameXmlTransform implementation.</p>
   */
  public NameXmlTransform createNameXmlTransform() {
    if (nameXmlTransforms == null) {
      nameXmlTransforms = new ArrayList();
    }
    NameXmlTransform result = ((JAXBXsObjectFactory) getObjectFactory()).newNameXmlTransform(this);
    nameXmlTransforms.add(result);
    return result;
  }

  private static final NameXmlTransform[] DEFAULT_NAME_XML_TRANSFORMATION = new NameXmlTransform[0];
  public NameXmlTransform[] getNameXmlTransform() {
    if (nameXmlTransforms == null) {
      return DEFAULT_NAME_XML_TRANSFORMATION;
    } else {
      return (NameXmlTransform[]) nameXmlTransforms.toArray(new NameXmlTransform[nameXmlTransforms.size()]);
    }
  }
}
