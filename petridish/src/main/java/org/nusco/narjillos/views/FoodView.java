package org.nusco.narjillos.views;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;

import org.nusco.narjillos.ecosystem.FoodPiece;
import org.nusco.narjillos.shared.physics.Vector;

class FoodView extends RoundObjectView {

	private static final double MINIMUM_ZOOM_LEVEL = 0.035;

	private final Shape circle;

	public FoodView(FoodPiece food) {
		super(food, 7);
		circle = new Circle(getRadius());
	}

	public Node toNode(double zoomLevel, boolean infraredOn) {
		if (zoomLevel < MINIMUM_ZOOM_LEVEL)
			return null;

		circle.setFill(getColor(infraredOn));
		
		circle.getTransforms().clear();
		circle.getTransforms().add(moveToStartPoint());
		circle.setEffect(getEffects(zoomLevel, infraredOn));
		return circle;
	}

	private Color getColor(boolean infraredOn) {
		if (infraredOn)
			return Color.RED;
		return Color.BLUE;
	}

	private Translate moveToStartPoint() {
		Vector position = getThing().getPosition();
		return new Translate(position.x, position.y);
	}
}