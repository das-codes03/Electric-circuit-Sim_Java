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
		updateProperties();
		updateState(t, 0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		boolean closed = (boolean) properties.get(CLOSED);
		if(closed) {
			segments[0].setResistance(0);
			segments[0].setCapacitance(0);
		}else {
			segments[0].setCapacitance(10e-15);
		}
//		segments[0].setResistance( (double) properties.get(CLOSED));
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
