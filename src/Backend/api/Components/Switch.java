package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;
import Backend.api.Circuit.Node;
import Backend.api.Circuit.Segment;

public class Switch extends Component {
	public static final String CURRENT = "current";
	public static final String CLOSED = "closed";

	public Switch(Circuit c) {
		super(c);
		segments = new Circuit.Segment[1];
		segments[0] = c.addSegment();
		properties.put(CLOSED, true);
		states.put(CURRENT, 0.0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		boolean closed = (boolean) properties.get(CLOSED);
		if(closed) {
			segments[0].setBreakdown(0);
			segments[0].setCapacitance(Double.POSITIVE_INFINITY);
		}else {
			segments[0].setBreakdown(1e18);
			segments[0].setCapacitance(10e-15);
		}
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

	@Override
	public void updateProperties() {

	}
}
