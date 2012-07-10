package org.apache.ws.jaxme.generator.sg.impl.ccsg;

import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.util.JavaNamer;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.xml.sax.SAXException;


/** Utility methods for handling groups.
 */
public class GroupUtil {
	/** Returns a suggested name for the given group.
	 */
	public static String getGroupName(GroupSG pGroup) throws SAXException {
		if (pGroup.isGlobal()) {
			return JavaNamer.convert(pGroup.getName().getLocalName(), pGroup.getSchema());
		} else {
			String sep;
			StringBuffer sb = new StringBuffer();
			if (pGroup.isSequence()  ||  pGroup.isAll()) {
				sep = "And";
			} else {
				sep = "Choice";
			}
			int num = 0;
			ParticleSG[] groupParticles = pGroup.getParticles();
			for (int i = 0;  i < groupParticles.length;  i++) {
				ParticleSG particle = groupParticles[i];
				if (particle.isElement()) {
					if (num++ > 0) {
						sb.append(sep);
					}
					String f = particle.getPropertySG().getPropertyName();
					sb.append(Character.toUpperCase(f.charAt(0)) + f.substring(1));
					if (num == 3) {
						break;
					}
				}
			}
			if (sb.length() == 0) {
				return "Group";
			} else {
				return sb.toString();
			}
		}
	}

	static JavaQName getContentClass(GroupSG pGroup, ParticleSG pParticle,
									 JavaQName pQName) throws SAXException {
		String name = pParticle.getPropertySG().getPropertyName();
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		if (pGroup.isGlobal()) {
			return JavaQNameImpl.getInstance(pQName.getPackageName(),
											 getGroupName(pGroup) + name);
		} else {
			return JavaQNameImpl.getInnerInstance(pQName, name);
		}
	}
}
