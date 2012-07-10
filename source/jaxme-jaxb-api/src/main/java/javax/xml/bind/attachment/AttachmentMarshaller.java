/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind.attachment;

import javax.activation.DataHandler;
import javax.xml.bind.Marshaller;

/**
 * The attachment marshaller allows to supply some parts
 * of the generated XML as binary attachments (MTOM
 * or SWA attachments).
 * @since JAXB 2.0
 * @see Marshaller#setAttachmentMarshaller(AttachmentMarshaller)
 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">XML-binary Optimized Packaging</a>
 * @see <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html">WS-I Attachments Profile Version 1.0.</a>
 */
public abstract class AttachmentMarshaller {
    /**
     * Asks, whether the given element is being attached via
     * MTOM or marshalled inline.
     * @return Null, if the element is being marshalled inline.
     *   Otherwise, it returns the content ID of the attachment.
     */
    public abstract String addMtomAttachment(DataHandler data,
            String elementNamespace, String elementLocalName);

    /**
     * Asks, whether the given element is being attached via
     * MTOM or marshalled inline.
     * @return Null, if the element is being marshalled inline.
     *   Otherwise, it returns the content ID of the attachment.
     */
    public abstract String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName);

    /**
     * Returns, whether the marshaller ought to support XOP
     * processing.
     */
    public boolean isXOPPackage() { return false; }

   /**
     * Asks, whether the given element is being attached via
     * SWA or marshalled inline.
     * @return Null, if the element is being marshalled inline.
     *   Otherwise, it returns the content ID of the attachment.
    */
    public abstract String addSwaRefAttachment(DataHandler data);
}

