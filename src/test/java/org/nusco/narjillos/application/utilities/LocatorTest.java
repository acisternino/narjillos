package org.nusco.narjillos.application.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.nusco.narjillos.core.physics.Vector;
import org.nusco.narjillos.core.things.Energy;
import org.nusco.narjillos.core.utilities.RanGen;
import org.nusco.narjillos.creature.Narjillo;
import org.nusco.narjillos.experiment.environment.Ecosystem;
import org.nusco.narjillos.genomics.DNA;

public class LocatorTest {

	Ecosystem ecosystem;
	Locator locator;
	RanGen ranGen = new RanGen(1234);

	@Before
	public void initialize() {
		ecosystem = new Ecosystem(1000, false);
		locator = new Locator(ecosystem);
	}

	private Narjillo insertNarjillo(Vector position) {
		// the position specifies the head position; the body center will be different
		DNA dna = new DNA(1, "{145_227_116_072_163_201_077_221_217}{060_227_157_252_209_149_056_114_167}{250_253_092_189_010_247_016_214_009}{027_039_203_179_042_042_175_110_008}");
		Narjillo narjillo = new Narjillo(dna, position, 90, Energy.INFINITE);
		ecosystem.insertNarjillo(narjillo);
		return narjillo;
	}

	@Test
	public void findsThingsAtAGivenPosition() {
		Narjillo narjillo1 = insertNarjillo(Vector.cartesian(1000, 1000));
		Narjillo narjillo2 = insertNarjillo(Vector.cartesian(100, 100));

		// the narjillos have radius = 2.5
		assertTrue(narjillo1.getRadius() > 2 && narjillo1.getRadius() < 3);
		assertTrue(narjillo2.getRadius() > 2 && narjillo2.getRadius() < 3);

		// point 4.24 units away from head, 3.04 from center
		assertNull(locator.findNarjilloAt(Vector.cartesian(103, 103), 1));

		// point 2.82 units away from head, 2.06 from center (-> within the radius)
		assertEquals(narjillo2, locator.findNarjilloAt(Vector.cartesian(102, 102), 1));
		assertEquals(narjillo1, locator.findNarjilloAt(Vector.cartesian(998, 1002), 1));
	}

	@Test
	public void findsExpandedNarjillo() {
		Narjillo narjillo1 = insertNarjillo(Vector.cartesian(100, 100));

		// original narjillo -> radius = 2.5
		assertNull(locator.findNarjilloAt(Vector.cartesian(110, 110), 1));
		// expanded narjillo -> radius = 20
		assertEquals(narjillo1, locator.findNarjilloAt(Vector.cartesian(110, 110), 20));
	}

	@Test
	public void returnsNullIfNoThingIsCloseEnough() {
		insertNarjillo(Vector.cartesian(100, 10));

		assertNull(locator.findNarjilloAt(Vector.cartesian(500, 500)));
	}

	@Test
	public void returnsNullIfTheEcosystemContainsNoNarjillos() {
		Locator emptyLocator = new Locator(new Ecosystem(1000, false));

		assertNull(emptyLocator.findNarjilloAt(Vector.cartesian(150, 150)));
	}
}
