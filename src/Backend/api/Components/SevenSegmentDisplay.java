package Backend.api.Components;

import java.util.ArrayList;

import Backend.api.Circuit;
import Backend.api.Component;
import Backend.api.Circuit.Node;

public class SevenSegmentDisplay extends Component {
	public static final String CURRENT = "current";
	public static final String[] INTENSITIES = new String[] { "A", "B", "C", "D", "E", "F", "G", "DP" };
	public static final int PIN_A = 0;
	public static final int PIN_B = 1;
	public static final int PIN_C = 2;
	public static final int PIN_D = 3;
	public static final int PIN_E = 4;
	public static final int PIN_F = 5;
	public static final int PIN_G = 6;
	public static final int PIN_DP = 7;
	public static final int PIN_GND = 8;
	private Led[] lights = new Led[8];

	public SevenSegmentDisplay(Circuit c) {
		super(c);
		var data = new ArrayList<Node>();
		// add lights and node connections
		for (int i = 0; i < lights.length; ++i) {
			lights[i] = new Led(c);
			data.add(lights[i].getPin(Led.NEG_PIN));
		}
		// connect to common ground
		c.mergeNodes(data);
		for (var i : INTENSITIES) {
			states.put(i, 0.0);
		}
		states.put(CURRENT, 0.0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		double curr = 0;
		for(int i = 0; i < lights.length; ++i) {
			lights[i].updateState(t, dt);
			states.put(INTENSITIES[i], (double)lights[i].getState(Led.INTENSITY));
			curr+=(double)lights[i].getState(Led.CURRENT);
		}
		states.put(CURRENT, curr);
	}

	@Override
	public Circuit.Node getPin(int index) throws Exception {
		if (index < 0 || index > 8) {
			throw new Exception("Node index must be in [0-8]");
		}
		if (index == 8)
			return lights[0].getPin(Led.NEG_PIN);
		else
			return lights[index].getPin(Led.POS_PIN);
	}
}
