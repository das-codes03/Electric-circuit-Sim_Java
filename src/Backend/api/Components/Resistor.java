package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;

public class Resistor extends Component {
	public static final String CURRENT = "current";
	public static final String RESISTANCE = "resistance";

	public Resistor(Circuit c) {
		super(c);
		segments = new Circuit.Segment[1];
		segments[0] = c.addSegment();
		properties.put(RESISTANCE, 1.0);
		states.put(CURRENT, 0.0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		segments[0].setResistance( (double) properties.get(RESISTANCE));
		var current = segments[0].getCurrent();
		states.put(CURRENT, current);
	}

	@Override
	public Circuit.Node getPin(int index) throws Exception {
		if (index < 0 || index > 1) {
			throw new Exception("Node index must be in [0-1]");
		}
		return segments[0].getNode(index);
	}
}
