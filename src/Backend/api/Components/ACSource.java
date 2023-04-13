package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;
import Backend.api.Circuit.Node;
import Backend.api.Circuit.Segment;

public class ACSource extends Component {
	public static final String CURRENT = "current";
	public static final String AMPLITUDE = "amplitude";
	public static final String PHASE = "phase";
	public static final String FREQUENCY = "frequency";

	public ACSource(Circuit c) {
		super(c);
		properties.put(AMPLITUDE, 1.0);
		properties.put(PHASE, 0.0); // in degree
		properties.put(FREQUENCY, 5000000.0);
		states.put(CURRENT, 0.0);
		segments = new Circuit.Segment[1];
		segments[0] = c.addSegment();
		updateProperties();
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		var current = segments[0].getCurrent();
		states.put(CURRENT, current);
		segments[0].setEMF(getEmf((double) properties.get(AMPLITUDE), (double) properties.get(PHASE),
				(double) properties.get(FREQUENCY), t));
//		System.out.println("EMF: " + segments[0].emf);
	}

	@Override
	public void updateProperties() {

	}

	private static double getEmf(double amplitude, double phase, double hz, double t) {
		return amplitude * Math.sin(2 * t * Math.PI * hz + Math.toRadians(phase));
	}

	@Override
	public Node getExternalNode(int index) throws Exception {
		if (index < 0 || index > 1) {
			throw new Exception("Node index must be in [0-1]");
		}
		return segments[0].getNode(index);
	}
}
