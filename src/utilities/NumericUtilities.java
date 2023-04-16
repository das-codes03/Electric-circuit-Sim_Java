package utilities;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.HashMap;

public class NumericUtilities {
	private static HashMap<Integer, String> unitMap = new HashMap<>(); // Map<x,suffix> which means 10^x -> prefix
	static {
		unitMap.put(24, "Y");
		unitMap.put(21, "Z");
		unitMap.put(18, "E");
		unitMap.put(15, "P");
		unitMap.put(12, "T");
		unitMap.put(9, "G");
		unitMap.put(6, "M");
		unitMap.put(3, "k");
		unitMap.put(0, "");
		unitMap.put(-3, "m");
		unitMap.put(-6, "μ");
		unitMap.put(-9, "n");
		unitMap.put(-12, "p");
		unitMap.put(-15, "f");
		unitMap.put(-18, "a");
		unitMap.put(-21, "z");
		unitMap.put(-24, "y");
	}
	private static final int MIN_EXP = -12;
	private static final int MAX_EXP = 24;
	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(val, max));
	}
	public static double clamp01(double val) {
		return clamp(val,0,1);
	}
	public static double getRounded(double d, int sigDigits) {
		if (d == 0 || Double.isNaN(d) || Double.isInfinite(d))
			return 0;
		BigDecimal bd = new BigDecimal(d);
		bd = bd.round(new MathContext(sigDigits+1));
		return bd.doubleValue();
	}

	public static String getPrefixed(double val, int sigDigits) {
		return getPrefixed(val, sigDigits, false);
	}

	public static String getPrefixed(double val, int sigDigits, boolean clipZeros) {
		if (Math.abs(val) > Math.pow(10, MAX_EXP))
			return "OVERLOAD ";

		String str = "";
		int x = 0;
		val = getRounded(val, sigDigits);
		if (Math.abs(val) < Math.pow(10, MIN_EXP))
			val = 0;

		var sign = Math.signum(val);
		val = Math.abs(val);
		if (val != 0)
			x = 3 * ((int) (Math.log10(val / (val < 1 ? 999 : 1)) / 3));
		val /= Math.pow(10, x);

		str = String.format("%.30f", val);
		var temp = str.substring(0, Math.min(str.length(), sigDigits + 1));
//		if (!clipZeros)
//			for (int i = temp.length(); i <= sigDigits; ++i) {
//				temp += '0';
//			}

		if (temp.endsWith(".")) {
			temp = temp.substring(0, temp.length() - 1);
		}
		return (sign < 0 ? "-" : "") + temp + " " + unitMap.get(x);
	}

	public static Point addPoint(Point a, Point b) {
		return new Point(a.x + b.x, a.y + b.y);
	}
}
