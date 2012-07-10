/*
 * Copyright 2006  The Apache Software Foundation
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
package org.apache.ws.jaxme.impl;

import java.util.List;


/** A subclass of {@link org.apache.ws.jaxme.impl.JMSAXElementParser}
 * for parsing complex elements.
 */
public abstract class JMSAXMixedElementParser extends JMSAXElementParser {
    protected abstract List getContentList();

    protected void normalize() {
        List list = getContentList();
        boolean previousIsString = false;
        for (int i = list.size()-1;  i >= 0;  i--) {
            Object o = list.get(i);
            if (o instanceof StringBuffer) {
                if (previousIsString) {
                    StringBuffer sb = (StringBuffer) o;
                    sb.append((String) list.remove(i+1));
                }
                list.set(i, o.toString());
                previousIsString = true;
            } else if (o instanceof String) {
                if (previousIsString) {
                    list.set(i, (String) o + (String) list.remove(i+1));
                }
                previousIsString = true;
            } else {
                previousIsString = false;
            }
        }
    }

    public void addText(char[] pBuffer, int pOffset, int pLength) {
        java.util.List list = getContentList();
        StringBuffer sb = null;
        if (list.size() > 0) {
            java.lang.Object _3 = list.get(list.size()-1);
            if (_3 instanceof StringBuffer) {
                sb = (StringBuffer) _3;
            }
        }
        if (sb == null) {
            sb = new StringBuffer();
            list.add(sb);
        }
        sb.append(pBuffer, pOffset, pLength);
    }
}
