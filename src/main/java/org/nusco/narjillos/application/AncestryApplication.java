package org.nusco.narjillos.application;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.nusco.narjillos.application.utilities.AncestryAppState;
import org.nusco.narjillos.application.utilities.AppState;
import org.nusco.narjillos.application.utilities.Speed;
import org.nusco.narjillos.application.utilities.StoppableThread;
import org.nusco.narjillos.application.views.AncestryStatusView;
import org.nusco.narjillos.application.views.EnvirommentView;
import org.nusco.narjillos.application.views.MicroscopeView;
import org.nusco.narjillos.core.physics.Vector;

/**
 * This is work in progress for an application that will load the ancestry out
 * of an experiment and show the evolution of the most successfull genome.
 */
public class AncestryApplication extends NarjillosApplication {

	private static String[] programArguments = new String[0];

	private AppState state = new AncestryAppState();

	@Override
	protected StoppableThread createModelThread(final String[] arguments, final boolean[] isModelInitialized) {
		return new StoppableThread() {
			@Override
			public void run() {
				CommandLineOptions options = CommandLineOptions.parse(arguments);
				if (options == null)
					System.exit(1);

				setDish(new IsolationDish(options));

				isModelInitialized[0] = true;

				while (!hasBeenAskedToStop()) {
					long startTime = System.currentTimeMillis();
					if (state.getSpeed() != Speed.PAUSED)
						if (!tick())
							Platform.exit();
					waitFor(state.getSpeed().getTicksPeriod(), startTime);
				}
			}
		};
	}

	@Override
	protected StoppableThread createViewThread(final Group root) {
		return new StoppableThread() {
			private volatile boolean renderingFinished = false;

			private final MicroscopeView foregroundView = new MicroscopeView(getViewport());
			private final EnvirommentView ecosystemView = new EnvirommentView(getEcosystem(), getViewport(), state);
			private AncestryStatusView statusBarView = new AncestryStatusView();

			public void run() {
				double size = getEcosystem().getSize();
				getTracker().startTracking(Vector.cartesian(size, size).by(0.5));

				while (!hasBeenAskedToStop()) {
					long startTime = System.currentTimeMillis();
					renderingFinished = false;

					getTracker().tick();
					ecosystemView.tick();

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							update(root);
							renderingFinished = true;
						}
					});

					waitFor(state.getFramesPeriod(), startTime);
					while (!renderingFinished && !hasBeenAskedToStop())
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
				}
			};

			private void update(final Group root) {
				root.getChildren().clear();
				root.getChildren().add(ecosystemView.toNode());
				root.getChildren().add(foregroundView.toNode());

				Node statusInfo = statusBarView.toNode(getGeneration(), getNumberOfGenerations());
				root.getChildren().add(statusInfo);
			}

			private long getGeneration() {
				return 1;
			}

			private long getNumberOfGenerations() {
				return 1000;
			}
		};
	}

	@Override
	protected String getName() {
		return "Ancestry Browser";
	}

	@Override
	protected void registerInteractionHandlers(final Scene scene) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(final KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.RIGHT)
					moveToNext();
				else if (keyEvent.getCode() == KeyCode.RIGHT)
					moveToPrevious();
			}

			private void moveToPrevious() {
				// TODO Auto-generated method stub

			}

			private void moveToNext() {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected String[] getProgramArguments() {
		return AncestryApplication.programArguments;
	}

	public static void main(String... args) throws Exception {
		AncestryApplication.programArguments = args;
		launch(args);
	}
}
