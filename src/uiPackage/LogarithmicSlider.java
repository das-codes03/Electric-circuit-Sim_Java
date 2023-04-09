package uiPackage;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class LogarithmicSlider extends JSlider {
	private int sigNum;
	private int minPow;
	private int maxPow;

	public LogarithmicSlider(int minPow, int maxPow, int sigDigits) {
		this.minPow = minPow;
		this.maxPow = maxPow;
		this.sigNum = sigDigits;
		this.setMaximum((int) Math.pow(10, sigNum) * (maxPow - minPow) - (int) Math.pow(10, sigDigits));
		this.setMinimum((int) Math.pow(10, sigDigits) - (int) Math.pow(10, sigDigits - 1));
		this.setMajorTickSpacing((int) Math.pow(10, sigNum) - (int) Math.pow(10, sigNum - 1));
		this.setMinorTickSpacing(this.getMajorTickSpacing()/4);

		this.setPaintTicks(true);
	}
	public void setLogValue(double x) {
		int p = (int) Math.log10(x);
		
		int k1 = (int) Math.pow(10, sigNum);
		int k2 = (int) Math.pow(10, sigNum - 1);
		double currstep = 0;
		if(p >= 0)
			currstep=((x-Math.pow(10, p))/Math.pow(10, p+1))/0.9 * (double)(k1-k2);
		else
			currstep =((x-Math.pow(10, p))/Math.pow(10, p))/0.9 * (double)(k1-k2);
		int b = getMinimum()+(k1-k2) * (p - minPow);
		super.setValue((int)(b + currstep));
	} 
	public double getLogValue() {
		int base = super.getValue();
		int k1 = (int) Math.pow(10, sigNum);
		int k2 = (int) Math.pow(10, sigNum - 1);
		int divisions = k1 - k2;
		int p = base / divisions;
		double currStep = (base % divisions + k2) / (double) (divisions + k2);
		double val = Math.pow(10, p + minPow) * (currStep);
		return val;
	}
}
