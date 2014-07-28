package org.nusco.narjillos.creature.body;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.nusco.narjillos.shared.utilities.ColorByte;

public abstract class BodyPartTest extends OrganTest {

	@Test
	public abstract void hasAParent();
	
	@Test
	public void hasAnEmptyListOfChildPartsByDefault() {
		assertEquals(Collections.EMPTY_LIST, getBodyPart().getChildren());
	}

	protected BodyPart getBodyPart() {
		return (BodyPart)organ;
	}
	
	@Test
	public void knowsItsChildren() {
		BodyPart child1 = getBodyPart().sproutOrgan(20, 10, new ColorByte(100), 0, 45);
		BodyPart child2 = getBodyPart().sproutOrgan(20, 10, new ColorByte(100), 0, -45);

		List<BodyPart> expected = new LinkedList<>();
		expected.add(child1);
		expected.add(child2);
		
		assertEquals(expected, getBodyPart().getChildren());
	}

	@Test
	public void sproutsVisibleOrgans() {
		Organ child = getBodyPart().sproutOrgan(20, 12, new ColorByte(100), 0, 45);
		assertEquals(20, child.getLength());
		assertEquals(12, child.getThickness());
	}
}
