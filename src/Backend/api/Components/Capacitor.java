package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;
import Backend.api.Circuit.Node;
import Backend.api.Circuit.Segment;

public class Capacitor extends Component {
	
	public static final String CAPACITANCE = "capacitance";
	public static final String BREAKDOWN_VOLTAGE = "breakdown"; 
	public static final String CURRENT = "current";
	public Capacitor(Circuit c) {
		super(c);

		segments = new Circuit.Segment[1];
		segments[0] = c.addSegment();
		properties.put(CAPACITANCE, 1.0);
		properties.put(BREAKDOWN_VOLTAGE, 1.0);
		updateProperties();
		updateState(t, 0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		segments[0].setCapacitance((double) properties.get(CAPACITANCE));
		segments[0].setBreakdown((double) properties.get(BREAKDOWN_VOLTAGE));
		var current = segments[0].getCurrent();
		states.put(CURRENT, current);
	}

	@Override
	public Circuit.Node getExternalNode(int index) throws Exception {
		if (index < 0 || index > 1) {
			throw new Exception("Node index must be in [0-1]");
		}
		return segments[0].getNode(index);
	}

	@Override
	public void updateProperties() {
		
	}
}
