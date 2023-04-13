package utilities;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.HashMap;

public class NumericUtilities {
	private static HashMap<Integer, String> unitMap = new HashMap<>(); // Map<x,suffix> which means 10^x -> prefix
	static {
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
	}
	private static final int MIN_EXP = -15;
	private static final int MAX_EXP = 15;

	public static double getRounded(double d, int sigDigits) {
		if (d == 0)
			return 0;
		var sign = Math.signum(d);
		d = Math.abs(d);
		int k = (int) Math.log10(d);
		d *= Math.pow(10, k);
		BigDecimal bd = new BigDecimal(d);
		bd = bd.round(new MathContext(sigDigits));
		return sign * bd.doubleValue() / Math.pow(10, k);
	}

	public static String getPrefixed(double val, int sigDigits) {
		return getPrefixed(val, sigDigits, false);
	}

	public static String getPrefixed(double val, int sigDigits, boolean clipZeros) {
		String str = "";
		int x = 0;
		val = getRounded(val, sigDigits);
		if (Math.abs(val) < Math.pow(10, MIN_EXP))
			val = 0;
		if (Math.abs(val) > Math.pow(10, MAX_EXP))
			val = (val < 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);

		var sign = Math.signum(val);
		val = Math.abs(val);
		if (val != 0)
			x = 3 * ((int) (Math.log10(val / (val < 1 ? 999 : 1)) / 3));
		val /= Math.pow(10, x);

		str = new DecimalFormat("0.######").format(val);
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
