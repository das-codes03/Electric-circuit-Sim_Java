package circuitlogic.solver.devices;

import circuitlogic.solver.Circuit;
import circuitlogic.solver.Component;

public class Diode extends Component {
	public static final String CURRENT = "current";
	public static final String RESISTANCE = "resistance";
	public static final String JUNCTION_CAPACITANCE = "capacitance";
	public static final String REVERSE_BREAKDOWN = "breakdown";
	public static final String KNEE_VOLTAGE = "kneevoltage";
	public static final String SATURATION_CURRENT = "saturationcurrent";
	public static final int NEG_PIN = 0;
	public static final int POS_PIN = 1;

	public Diode(Circuit c) {
		super(c);
		segments = new Circuit.Segment[2];
		segments[0] = c.addSegment(); // resistive
		segments[1] = c.addSegment(segments[0].getNode(0), segments[0].getNode(1)); // capacitive

		properties.put(JUNCTION_CAPACITANCE, 1e-15);
		properties.put(REVERSE_BREAKDOWN, 1e20);
		properties.put(KNEE_VOLTAGE, 0.0);
		properties.put(SATURATION_CURRENT, 1e-12);
		properties.put(RESISTANCE, 1.0);
		states.put(CURRENT, 0.0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);

		segments[1].setCapacitance((double) properties.get(JUNCTION_CAPACITANCE));
		segments[1].setBreakdown((double) getProperty(REVERSE_BREAKDOWN));

		var pot = -segments[1].getCharge() / segments[1].getCapacitance();
		double res = (double) getProperty(RESISTANCE);
		if (pot < 0) {// rev
			res =Math.max(res, -pot / (double) getProperty(SATURATION_CURRENT));
		} else {// forw
			var forRes = ((double) getProperty(KNEE_VOLTAGE) - pot) / (double) getProperty(KNEE_VOLTAGE);
			if (Double.isNaN(forRes))
				forRes = 0;
			res = Math.max(forRes, (double) getProperty(RESISTANCE));
		}
		segments[0].setResistance(res);
		var current = segments[0].getCurrent() + segments[1].getCurrent();
		states.put(CURRENT, current);
	}

	@Override
	public Circuit.Node getPin(int index) {
		if (index < 0 || index > 1) {
			throw new RuntimeException("Node index must be in [0-1]");
		}
		return segments[0].getNode(index);
	}
}
