package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;

public class Transformer extends Component {
	public static final String PRIMARY_CURRENT = "primcurrent";
	public static final String SECONDARY_CURRENT = "seccurrent";

	public static final String SECONDARY_WINDINGS = "secondary";
	public static final String PRIMARY_WINDINGS = "primary";
	public static final String PRIMARY_INDUCTANCE = "inductance";

	public static final int PRIMARY_PIN0 = 0;
	public static final int PRIMARY_PIN1 = 1;
	public static final int SECONDARY_PIN0 = 2;
	public static final int SECONDARY_PIN1 = 3;

	private double prevPrimCurrent;
	private double prevSecCurrent;

	public Transformer(Circuit c) {
		super(c);
		segments = new Circuit.Segment[2];
		segments[0] = c.addSegment();
		segments[1] = c.addSegment();
		properties.put(SECONDARY_WINDINGS, 100);
		properties.put(PRIMARY_WINDINGS, 10);
		properties.put(PRIMARY_INDUCTANCE, 10.0);
		states.put(PRIMARY_CURRENT, 0.0);
		states.put(SECONDARY_CURRENT, 0.0);
	}

	@Override
	public void updateState(double t, double dt) {
		super.updateState(t, dt);
		double prim_ind = (double) properties.get(PRIMARY_INDUCTANCE);
		int prim_turn = (int) properties.get(PRIMARY_WINDINGS);
		int sec_turn = (int) properties.get(SECONDARY_WINDINGS);
		double ratio = (double) prim_turn / (double) sec_turn;
		double sec_ind = prim_ind / (ratio * ratio);
		segments[0].setInductance(prim_ind);
		segments[1].setInductance(sec_ind);

		var primcurrent = segments[0].getCurrent();
		var seccurrent = segments[1].getCurrent();
		if (dt > 0) {
			var m = Math.sqrt(sec_ind * prim_ind) * 1.0;
			var secEmf = m * (primcurrent - prevPrimCurrent) / dt;
			var primEmf = m * (seccurrent - prevSecCurrent) / dt;
			segments[0].setEMF(-primEmf);
			segments[1].setEMF(-secEmf);
		}
		prevPrimCurrent = primcurrent;
		prevSecCurrent = seccurrent;
		states.put(PRIMARY_CURRENT, primcurrent);
		states.put(SECONDARY_CURRENT, seccurrent);
	}

	@Override
	public Circuit.Node getPin(int index) throws Exception {
		if (index < 0 || index > 4) {
			throw new Exception("Node index must be in [0-3]");
		}
		return segments[index / 2].getNode(index % 2);
	}
}
