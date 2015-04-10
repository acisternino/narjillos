package org.nusco.narjillos.application.views;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.nusco.narjillos.application.utilities.Viewport;
import org.nusco.narjillos.core.utilities.VisualDebugger;
import org.nusco.narjillos.creature.Narjillo;
import org.nusco.narjillos.creature.body.Organ;

class NarjilloView extends ThingView {
	
	private final Group group = new Group();
	private final List<OrganView> organViews;
	private final MouthView mouthView;
	private final RoundObjectView eyeView;
	private final CenterOfMassView centerOfMassView;

	public NarjilloView(Narjillo narjillo) {
		super(narjillo);
		organViews = createOrganViews();
		mouthView = new MouthView(narjillo);
		eyeView = new EyeView(narjillo);
		centerOfMassView = new CenterOfMassView(narjillo);
	}

	@Override
	public Node toNode(double zoomLevel, boolean infraredOn, boolean effectsOn) {
		group.getChildren().clear();

		group.getChildren().addAll(getOrganNodes(zoomLevel, infraredOn, effectsOn));
		
		Node mouthNode = mouthView.toNode(zoomLevel, infraredOn, effectsOn);
		if (mouthNode != null)
			group.getChildren().add(mouthNode);

		Node eyeNode = eyeView.toNode(zoomLevel, infraredOn, effectsOn);
		if (eyeNode != null)
			group.getChildren().add(eyeNode);

		if (VisualDebugger.DEBUG)
			group.getChildren().add(centerOfMassView.toNode(zoomLevel, infraredOn, effectsOn));

		if (effectsOn && !group.getChildren().isEmpty())
			group.setEffect(getEffects(zoomLevel, infraredOn));

		return group;
	}

	private List<Node> getOrganNodes(double zoomLevel, boolean infraredOn, boolean effectsOn) {
		List<Node> result = new LinkedList<>();
		for (OrganView view : organViews) {
			Node node = view.toNode(zoomLevel, infraredOn, effectsOn);
			if (node != null)
				result.add(node);
		}
		return result;
	}

	private List<OrganView> createOrganViews() {
		List<OrganView> result = new LinkedList<>();
		for (Organ bodyPart : getNarjillo().getOrgans())
			result.add(new OrganView(bodyPart, getNarjillo()));
		return result;
	}

	private Narjillo getNarjillo() {
		return (Narjillo)getThing();
	}

	@Override
	public boolean isVisible(Viewport viewport) {
		for (OrganView organView : organViews)
			if (organView.isVisible(viewport))
				return true;
		OrganView organView = organViews.get(0);
		organView.isVisible(viewport);
		// ignore the mouth and eye, too small to make a visible difference
		return false;
	}
}
