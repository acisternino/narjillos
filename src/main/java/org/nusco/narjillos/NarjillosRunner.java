package org.nusco.narjillos;

import org.nusco.narjillos.application.CommandLineOptions;
import org.nusco.narjillos.application.MainNarjillosApplication;
import org.nusco.narjillos.application.PetriDish;
import org.nusco.narjillos.core.utilities.Configuration;
import org.nusco.narjillos.serializer.Persistence;

/**
 * The entry point to the "narjillos" program.
 */
public class NarjillosRunner {
	public static void main(String... args) throws Exception {
		CommandLineOptions options = CommandLineOptions.parse(args);
		if (options == null)
			System.exit(1);
		
		if (options.isFast())
			runWithoutGraphics(options);
		else
			MainNarjillosApplication.main(args);
	}

	private static void runWithoutGraphics(CommandLineOptions options) {
		String applicationVersion = Persistence.readApplicationVersion();
		final PetriDish dish = new PetriDish(applicationVersion, options, Configuration.ECOSYSTEM_BLOCKS_PER_EDGE_IN_EXPERIMENT * 1000);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				dish.terminate();
			}
		});

		while (dish.tick())
			;
		System.exit(0);
	}
}
