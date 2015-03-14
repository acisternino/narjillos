package org.nusco.narjillos.creature.body;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nusco.narjillos.creature.body.pns.Nerve;
import org.nusco.narjillos.creature.body.pns.WaveNerve;
import org.nusco.narjillos.shared.physics.Vector;

public class HeadTest extends ConnectedOrganTest {

	@Override
	public Head createConcreteOrgan(int length, int thickness) {
		return new Head(length, thickness, 100, 101, 102, 1, 0.5, 1);
	}

	@Override
	public void hasAParent() {
		// ...only it's null
		assertEquals(null, getOrgan().getParent());
	}

	@Override
	public void hasAnEndPoint() {
		fullyGrow(organ);
		
		assertEquals(Vector.cartesian(50, 0), organ.getEndPoint());
	}

	@Test
	public void startsAtPointZeroByDefault() {
		assertEquals(Vector.ZERO, organ.getStartPoint());
	}

	@Test
	public void hasAWaveNerve() {
		Nerve nerve = new Head(0, 0, 0, 0, 0, 1, 0.5, 30).getNerve();
				
		assertEquals(WaveNerve.class, nerve.getClass());
	}
	
	@Test
	public void hasACenterOfMass() {
		Head head = new Head(10, 6, 0, 0, 0, 1, 0.5, 1);
		head.setAngleToParent(90);
		head.updateGeometry();
		
		fullyGrow(head);
		
		assertTrue(head.getCenterOfMass().almostEquals(Vector.cartesian(0, 5)));
	}
	
	@Test
	public void hasAPercentOfEnergyThatItPassesOnToChildren() {
		Head head = new Head(10, 6, 0, 0, 0, 1, 0.42, 1);
		
		assertEquals(0.42, head.getPercentEnergyToChildren(), 0.0);
	}
	
	@Test
	public void hasAVelocityOfEggs() {
		Head head = new Head(10, 6, 0, 0, 0, 1, 0.42, 42);
		
		assertEquals(0.42, head.getEggVelocity(), 42);
	}
	
	@Test
	public void hasAFiber() {
		Head head = new Head(10, 6, 10, 20, 30, 1, 0.42, 30);
		
		assertEquals(new Fiber(10, 20, 30), head.getFiber());
	}
}
