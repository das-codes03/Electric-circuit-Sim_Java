package simulatorgui.rendering;

import java.awt.Font;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import utilities.NumericUtilities;

public class LogarithmicSlider extends JSlider {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3552928829448308293L;
	private int sigNum;
	private int minPow;
	@SuppressWarnings("unused")
	private int maxPow;

	public LogarithmicSlider(int minPow, int maxPow, int sigDigits) {
		this(minPow, maxPow, sigDigits, "");
	}

	public LogarithmicSlider(int minPow, int maxPow, int sigDigits, String tickSuffix) {
		this.minPow = minPow;
		this.maxPow = maxPow;
		this.sigNum = sigDigits;
		this.setMinimum(9 * (int) Math.pow(10, sigDigits - 1));
		this.setMaximum(getMinimum() + (int) (9 * Math.pow(10, sigNum - 1) * (maxPow - minPow)));
		this.setMajorTickSpacing(9 * (int) Math.pow(10, sigNum - 1));
		this.setMinorTickSpacing(this.getMajorTickSpacing() / 4);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		for (int i = this.getMinimum(); i <= this.getMaximum(); i += 3 * this.getMajorTickSpacing()) {
			var lbl = new JLabel(NumericUtilities.getPrefixed(lVal(i), 1) + tickSuffix);
			lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
			labelTable.put(i, lbl);
		}
		this.setLabelTable(labelTable);
		this.setPaintLabels(true);
		this.setPaintTicks(true);
	}

	public void setLogValue(double x) {
		int p = (int) Math.log10(x);

		int k1 = (int) Math.pow(10, sigNum);
		int k2 = (int) Math.pow(10, sigNum - 1);
		double currstep = 0;
		if (p >= 0)
			currstep = ((x - Math.pow(10, p)) / Math.pow(10, p + 1)) / 0.9 * (double) (k1 - k2);
		else
			currstep = ((x - Math.pow(10, p)) / Math.pow(10, p)) / 0.9 * (double) (k1 - k2);
		int b = getMinimum() + (k1 - k2) * (p - minPow);
		super.setValue((int) (b + currstep));
	}

	private double lVal(int d) {
		int base = d;
		int k1 = (int) Math.pow(10, sigNum);
		int k2 = (int) Math.pow(10, sigNum - 1);
		int divisions = k1 - k2;
		int p = base / divisions;
		double currStep = (base % divisions + k2) / (double) (divisions + k2);
		double val = Math.pow(10, p + minPow) * (currStep);
		return val;
	}

	public double getLogValue() {
		return NumericUtilities.getRounded(lVal(super.getValue()),sigNum);
	}
}
