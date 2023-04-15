package Backend.api.Components;

import Backend.api.Circuit;
import Backend.api.Component;
import Backend.api.Circuit.Node;
import Backend.api.Circuit.Segment;

public class Led extends Diode {
	public static final String RATED_VOLTAGE = "voltage";
	public static final String RATED_POWER = "power";
	public static final String WAVELENGTH_NM = "wavelength";
	public static final String INTENSITY = "intensity";

	public Led(Circuit c) {
		super(c);
		properties.put(WAVELENGTH_NM, 660.0);
		properties.put(RATED_VOLTAGE, 3.0);
		properties.put(RATED_POWER, 0.030);
		states.put(CURRENT, 0.0);
		states.put(INTENSITY, 0.0);
	}

	@Override
	public void updateState(double t, double dt) {
		double res = Math.pow((double) properties.get(RATED_VOLTAGE), 2) / (double) properties.get(RATED_POWER);
		super.setProperty(RESISTANCE, res);
		super.updateState(t, dt);
		double current = (double) super.getState(CURRENT);
		double power = Math.pow((double) super.getState(CURRENT), 2) * (double) super.getProperty(RESISTANCE);
		var intensity = power / (double) properties.get(RATED_POWER);
		states.put(CURRENT, current);
		states.put(INTENSITY, intensity);
	}

	@Override
	public Circuit.Node getPin(int index) {
		if (index < 0 || index > 1) {
			throw new RuntimeException("Node index must be in [0-1]");
		}
		return super.getPin(index);
	}

	@Override
	public void updateProperties() {

	}
}
