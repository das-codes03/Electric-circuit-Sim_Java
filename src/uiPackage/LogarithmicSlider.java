package uiPackage;

import javax.swing.JComponent;
import javax.swing.JSlider;

public class LogarithmicSlider extends JSlider {
	private int sigNum = 2;
	private int minPow = -5;
	private int maxPow = 5;

	public LogarithmicSlider(int minPow, int maxPow, int sigDigits) {
		this.minPow = minPow;
		this.maxPow = maxPow;
		this.sigNum = sigDigits;
		this.setMaximum((int) Math.pow(10, sigNum) * (maxPow -minPow));
		this.setMinimum(0);
	}

	public double getLogValue() {
		int base = super.getValue();
		int k1 = (int) Math.pow(10, sigNum);
		int k2 = (int) Math.pow(10, sigNum - 1);
		int divisions = k1 - k2;
		int p = base / divisions;
		double currStep =(base % divisions + k2) / (double) (divisions + k2);
		double val = Math.pow(10, p + minPow) * (currStep);
		System.out.println("Val = " + val);
		return val;
	}
}
