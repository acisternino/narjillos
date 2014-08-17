package org.nusco.narjillos.creature.body;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nusco.narjillos.creature.body.physics.Acceleration;
import org.nusco.narjillos.creature.body.physics.ForceField;
import org.nusco.narjillos.creature.body.pns.WaveNerve;
import org.nusco.narjillos.shared.physics.Vector;

public class Body {

	private static final double WAVE_SIGNAL_FREQUENCY = 0.01;
	private static final double MAX_SKEWING = 45;
	private static final double MAX_SKEWING_VELOCITY = 1;

	private final Head head;
	private final List<BodyPart> parts;
	private final double mass;
	private final WaveNerve tickerNerve;
	private Vector position = Vector.ZERO;
	private double skewing = 0;
	private Vector centerOfMass = null;
	
	public Body(Head head) {
		this.head = head;
		this.parts = getOrgans(head);
		this.mass = calculateTotalMass();
		this.tickerNerve = new WaveNerve(WAVE_SIGNAL_FREQUENCY * getMetabolicRate());
	}

	public Acceleration tick(Vector targetDirection) {
		centerOfMass = null; // reset cached value

		double angleToTarget = getMainAxis().getAngleWith(targetDirection);

		double currentSkewing = updateSkewing(angleToTarget);
		
		double targetAmplitudePercent = tickerNerve.tick(0);
		head.updateAngleToParent(targetAmplitudePercent, currentSkewing);

		ForceField forceField = new ForceField();
		head.recordForce(forceField);

		double rotation = forceField.calculateRotation(getMass(), getCenterOfMass());
		Vector translation = forceField.calculateTranslation(getMass());
		
		double energySpent = forceField.getTotalEnergySpent() * getMetabolicRate();
		
		return new Acceleration(translation, rotation, energySpent);
	}

	private double updateSkewing(double angleToTarget) {
		double updatedSkewing = (angleToTarget % 180) / 180 * MAX_SKEWING;
		double skewingVelocity = updatedSkewing - skewing;
		if (Math.abs(skewingVelocity) > MAX_SKEWING_VELOCITY)
			skewingVelocity = Math.signum(skewingVelocity) * MAX_SKEWING_VELOCITY;
		return skewing += skewingVelocity;
	}

	public Vector getMainAxis() {
		return head.getMainAxis();
	}

	public double getMass() {
		return mass;
	}

	public List<BodyPart> getBodyParts() {
		return parts;
	}

	private double getMetabolicRate() {
		return head.getMetabolicRate();
	}

	private double calculateTotalMass() {
		double result = 0;
		List<BodyPart> allOrgans = getBodyParts();
		for (BodyPart organ : allOrgans)
			result += organ.getMass();
		return result;
	}

	private List<BodyPart> getOrgans(Head head) {
		List<BodyPart> result = new LinkedList<>();
		addWithChildren(head, result);
		return result;
	}

	private void addWithChildren(Organ organ, List<BodyPart> result) {
		result.add(organ);
		for (Organ child : organ.getChildren())
			addWithChildren(child, result);
	}
	
	public void forceBend(double bendAngle) {
		forceBendWithChildren(head, bendAngle);
	}
	
	private void forceBendWithChildren(Organ bodyPart, double bendAngle) {
		bodyPart.forceBend(bendAngle);
		for (Organ child : bodyPart.getChildren())
			forceBendWithChildren(child, bendAngle);
	}
	
	public Vector getCenterOfMass() {
		if (centerOfMass == null)
			centerOfMass = calculateCenterOfMass();
		
		return centerOfMass;
	}

	private Vector calculateCenterOfMass() {
		List<BodyPart> organs = getBodyParts();
		Vector[] weightedCentersOfMass = new Vector[organs.size()];
		Iterator<BodyPart> iterator = getBodyParts().iterator();
		for (int i = 0; i < weightedCentersOfMass.length; i++) {
			BodyPart organ = iterator.next();
			weightedCentersOfMass[i] = organ.getCenterOfMass().by(organ.getMass());
		}
		
		// do it in one swoop instead of calling Vector#plus() a lot
		// TODO: does this have any visible effect on performance?
		int totalX = 0;
		int totalY = 0;
		for (int i = 0; i < weightedCentersOfMass.length; i++) {
			totalX += weightedCentersOfMass[i].x;
			totalY += weightedCentersOfMass[i].y;
		}
		double totalMass = getMass();
		
		if (totalMass <= 0)
			return Vector.ZERO;
		
		return Vector.cartesian(totalX / totalMass, totalY / totalMass);
	}

	public double getAngle() {
		return head.getAbsoluteAngle();
	}

	public void setAngle(double angle) {
		centerOfMass = null; // clear cache
		head.setAngleToParent(angle);
	}

	public Vector getPosition() {
		return position;
	}
	
	public void setPosition(Vector position) {
		centerOfMass = null; // clear cache
		this.position = position;
	}
}
