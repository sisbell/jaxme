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
 
package org.apache.ws.jaxme.generator.sg.impl;

import java.io.Serializable;

import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SerializableSG {
    private static final JavaQName SERIALIZABLE_TYPE = JavaQNameImpl.getInstance(Serializable.class);

    /** Makes the class <code>pSource</code> implementing the
     * {@link Serializable} interface.
     */
    public static void makeSerializable(SchemaSG pSchema, JavaSource pSource) {
        SchemaReader sr = pSchema.getFactory().getGenerator().getSchemaReader();
        if (sr instanceof JaxMeSchemaReader) {
            JaxMeSchemaReader jsr = (JaxMeSchemaReader) sr;
            XjcSerializable xjcSerializable = jsr.getXjcSerializable();
            if (xjcSerializable != null) {
                if (!pSource.isImplementing(SERIALIZABLE_TYPE)) {
                    pSource.addImplements(SERIALIZABLE_TYPE);
                }
                if (xjcSerializable.getUid() != null) {
                    JavaField uid = pSource.newJavaField("serialVersionUID", JavaQNameImpl.LONG, JavaSource.PRIVATE);
                    uid.setStatic(true);
                    uid.setFinal(true);
                    uid.addLine(xjcSerializable.getUid());
                }
            }
        }
    }
}
