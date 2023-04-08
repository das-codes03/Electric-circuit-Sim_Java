package utilities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class NumericUtilities {
	private static HashMap<Integer, String> unitMap = new HashMap<>(); // Map<x,suffix> which means 10^x -> prefix
	static {
		unitMap.put(12, "T");
		unitMap.put(9, "G");
		unitMap.put(6, "M");
		unitMap.put(3, "k");
		unitMap.put(0, "");
		unitMap.put(-3, "m");
		unitMap.put(-6, "μ");
		unitMap.put(-9, "n");
		unitMap.put(-12, "p");
	}
	public static double getRounded(double d, int sigDigits) {
		if(d == 0) return 0;
		int k = (int) Math.log10(Math.abs(d));
		d *= Math.pow(10,k);
		BigDecimal bd = new BigDecimal(d);
		bd = bd.round(new MathContext(sigDigits));

		return bd.doubleValue()/Math.pow(10, k);
	}
	public static String getPrefixed(double val, int sigDigits) {
		String str = "";
		int x = 0;
		if(val > 0)
			x = 3 * ((int) (Math.log10(val/(val < 1?999:1)) / 3));
		val /= Math.pow(10, x);
		val = getRounded(val,sigDigits);
		str = String.format("%.30f", val);
		var temp = str.substring(0,Math.min(str.length()-1, sigDigits+1)) ;
		for(int i = temp.length(); i <= sigDigits; ++i ) {
			temp+='0';
		}
		return temp+ " " + unitMap.get(x);
	}
}
