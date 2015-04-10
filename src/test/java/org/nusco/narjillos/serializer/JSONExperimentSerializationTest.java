package org.nusco.narjillos.serializer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nusco.narjillos.ecosystem.Ecosystem;
import org.nusco.narjillos.experiment.Experiment;
import org.nusco.narjillos.genomics.GenePool;

public class JSONExperimentSerializationTest {

	@Test
	public void serializesAndDeserializesExperiment() {
		Experiment experiment = new Experiment(1234, new Ecosystem(10000, false), "experiment_serialization_test", true);
		assertTrue(experiment.getGenePool().hasAncestralMemory());
		
		for (int i = 0; i < 10; i++)
			experiment.tick();
		
		String json = JSON.toJson(experiment, Experiment.class);
		Experiment deserialized = JSON.fromJson(json, Experiment.class);

		assertEquals(experiment.getId(), deserialized.getId());
		assertEquals(experiment.getTotalRunningTimeInSeconds(), deserialized.getTotalRunningTimeInSeconds());
		assertEquals(experiment.getEcosystem().getNumberOfEggs(), deserialized.getEcosystem().getNumberOfEggs());
		assertEquals(experiment.getEcosystem().getNumberOfFoodPieces(), deserialized.getEcosystem().getNumberOfFoodPieces());
		assertEquals(experiment.getEcosystem().getNumberOfNarjillos(), deserialized.getEcosystem().getNumberOfNarjillos());
		assertEquals(10, deserialized.getTicksChronometer().getTotalTicks());
		
		GenePool genePool = experiment.getGenePool();
		GenePool deserializedGenePool = deserialized.getGenePool();
		assertArrayEquals(deserializedGenePool.getAncestry(deserializedGenePool.getMostSuccessfulDNA()).toArray(), genePool.getAncestry(genePool.getMostSuccessfulDNA()).toArray());
	}
}
