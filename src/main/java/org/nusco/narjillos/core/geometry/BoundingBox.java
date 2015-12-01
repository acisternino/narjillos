package org.nusco.narjillos.core.geometry;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BoundingBox {

	public final double left;
	public final double right;
	public final double bottom;
	public final double top;

	public BoundingBox(SegmentShape segmentShape) {
		Segment segment = segmentShape.toSegment();
		left = Math.min(segment.getStartPoint().x, segment.getEndPoint().x);
		right = Math.max(segment.getStartPoint().x, segment.getEndPoint().x);
		top = Math.max(segment.getStartPoint().y, segment.getEndPoint().y);
		bottom = Math.min(segment.getStartPoint().y, segment.getEndPoint().y);
	}

	public BoundingBox(Set<SegmentShape> segmentShapes) {
		if (segmentShapes.isEmpty())
			throw new RuntimeException("Empty bounding box");
		
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

		List<Vector> points = collectPoints(segmentShapes);
		for (Vector point : points) {
			minX = Math.min(minX, point.x);
			maxX = Math.max(maxX, point.x);
			minY = Math.min(minY, point.y);
			maxY = Math.max(maxY, point.y);
		}
		
		left = minX;
		right = maxX;
		bottom = minY;
		top = maxY;
	}

	private List<Vector> collectPoints(Set<SegmentShape> shapes) {
		List<Vector> result = new LinkedList<>();
		
		for (SegmentShape segmentShape : shapes) {
			Segment segment = segmentShape.toSegment();
			result.add(segment.getStartPoint());
			result.add(segment.getEndPoint());
		}
		return result;
	}

	public boolean overlaps(BoundingBox other) {
		return !(top <= other.bottom || bottom >= other.top || right <= other.left || left >= other.right);
	}
}
