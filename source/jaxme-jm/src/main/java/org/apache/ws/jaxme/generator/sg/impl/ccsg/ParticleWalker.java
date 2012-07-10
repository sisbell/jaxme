package org.apache.ws.jaxme.generator.sg.impl.ccsg;

import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.xml.sax.SAXException;


/** A <code>ParticleWalker</code> walks over a complex
 * elements particles, invoking the given
 * {@link org.apache.ws.jaxme.generator.sg.impl.ccsg.ParticleVisitor Particle Visitors}
 * methods for any particle.
 */
public class ParticleWalker {
	private final ParticleVisitor visitor;

	/** Creates a new instance, walking over the given type.
	 */
	public ParticleWalker(ParticleVisitor pVisitor) {
		visitor = pVisitor;
	}

	/** Walks over the given types particles.
	 * @throws SAXException Walking over the particles failed.
	 */
	public void walk(ComplexTypeSG pType) throws SAXException {
		if (pType.hasSimpleContent()) {
			visitor.simpleContent(pType);
		} else {
			ComplexContentSG ccSG = pType.getComplexContentSG();
			if (ccSG.isEmpty()) {
				visitor.emptyType(pType);
			} else {
				visitor.startComplexContent(pType);
				walkGroup(ccSG.getRootParticle().getGroupSG());
				visitor.endComplexContent(pType);
			}
		}
	}

	/** Walks over the group.
	 * @throws SAXException Walking over the group failed.
	 */
	private void walkGroup(GroupSG pGroup) throws SAXException {
		if (pGroup.isSequence()) {
			visitor.startSequence(pGroup);
			walkParticles(pGroup);
			visitor.endSequence(pGroup);
		} else if (pGroup.isChoice()) {
			visitor.startChoice(pGroup);
			walkParticles(pGroup);
			visitor.endChoice(pGroup);
		} else if (pGroup.isAll()) {
			visitor.startAll(pGroup);
			walkParticles(pGroup);
			visitor.endAll(pGroup);
		} else {
			throw new IllegalStateException("Invalid group type");
		}
	}

	/** Walks over the groups particles.
	 * @throws SAXException Walking over the groups particles failed.
	 */
	private void walkParticles(GroupSG pGroup) throws SAXException {
		ParticleSG[] particles = pGroup.getParticles();
		for (int i = 0;  i < particles.length;  i++) {
			ParticleSG particle = particles[i];
			if (particle.isElement()) {
				TypeSG type = particle.getObjectSG().getTypeSG();
				if (type.isComplex()) {
					visitor.complexElementParticle(pGroup, particle);
				} else {
					visitor.simpleElementParticle(pGroup, particle);
				}
			} else if (particle.isGroup()) {
				walkGroup(particle.getGroupSG());
			} else if (particle.isWildcard()) {
				visitor.wildcardParticle(particle);
			} else {
				throw new IllegalStateException("Invalid particle type");
			}
		}
	}
}
