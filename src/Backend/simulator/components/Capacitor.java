package Backend.simulator.components;

import Backend.simulator.Circuit;
import Backend.simulator.Component;

public class Capacitor extends Component {
	
	public static final String CAPACITANCE = "capacitance";
	public static final String CURRENT = "current";
	public Capacitor(Circuit c) {
		super(c);

		segments = new Circuit.Segment[1];
		segments[0] = c.AddSegment();
		properties.put(CAPACITANCE, 1.0);
		updateProperties();
		updateState(t, 0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		states.put(CURRENT, segments[0].current);
	}

	@Override
	public Circuit.Node getExternalNode(int index) throws Exception {
		if (index < 0 || index > 1) {
			throw new Exception("Node index must be in [0-1]");
		}
		return segments[0].nodes[index];
	}

	@Override
	public void updateProperties() {
		segments[0].capacitance = (double) properties.get(CAPACITANCE);
	}
}
