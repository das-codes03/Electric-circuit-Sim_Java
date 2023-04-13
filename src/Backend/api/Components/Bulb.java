package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;
import Backend.api.Circuit.Node;
import Backend.api.Circuit.Segment;

public class Bulb extends Component {
	public static final String CURRENT = "current";
	public static final String INTENSITY = "intensity";
	public static final String RATED_VOLTAGE = "ratedvoltage";
	public static final String RATED_WATTAGE = "ratedwattage";

	public Bulb(Circuit c) {
		super(c);
		segments = new Circuit.Segment[1];
		segments[0] = c.addSegment();
		properties.put(RATED_VOLTAGE, 1.0);
		properties.put(RATED_WATTAGE, 1.0);
		updateProperties();
		updateState(t, 0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		states.put(CURRENT, segments[0].getCurrent());
		segments[0].setResistance( Math.pow((double) properties.get(RATED_VOLTAGE), 2)
				/ (double) properties.get(RATED_WATTAGE));
		var intensity =Math.abs(segments[0].getCurrent()) / (double) properties.get(RATED_WATTAGE)
				/ (double) properties.get(RATED_VOLTAGE);
		states.put(INTENSITY, intensity);
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
