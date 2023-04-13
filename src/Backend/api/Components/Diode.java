package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;
import Backend.api.Circuit.Node;
import Backend.api.Circuit.Segment;

public class Diode extends Component {
	public static final String CURRENT = "current";
	public static final String RESISTANCE = "resistance";

	public Diode(Circuit c) {
		super(c);
		segments = new Circuit.Segment[2];
		segments[0] = c.addSegment();
		segments[1] = c.addSegment(segments[0].getNode(0), segments[0].getNode(1));
		
		properties.put(RESISTANCE, 1.0);
		updateProperties();
		updateState(t, 0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		segments[0].setResistance( (double) properties.get(RESISTANCE));
//		var current = segments[0].current;
//		if (Node.compareDepth(segments[0].getNode(0), segments[0].getNode(1)) < 0)
//			current *= -1;
		segments[0].setCapacitance(1e-10);
		segments[0].setBreakdown(10000);
		if(segments[0].getCharge() > 0) {
			segments[1].setResistance(1e10);
		}else {
			segments[1].setResistance(0.00001);
		}
		var current = segments[0].getCurrent() + segments[1].getCurrent();
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
