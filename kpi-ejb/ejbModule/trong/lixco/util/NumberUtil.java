package trong.lixco.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

	public static double round(double number, int lamtron) {
		BigDecimal bd = new BigDecimal(Double.toString(number));
		bd = bd.setScale(lamtron, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double myMath(double number1, double number2, MOpe math, int round) {
		try {
			BigDecimal n1 = new BigDecimal(Double.toString(number1));
			BigDecimal n2 = new BigDecimal(Double.toString(number2));
			BigDecimal ketqua =new BigDecimal("0");
			if (math.equals(MOpe.ADD)) {
				ketqua = n1.add(n2);
			} else if (math.equals(MOpe.SUBTRACT)) {
				ketqua = n1.subtract(n2);
			} else if (math.equals(MOpe.MULTIPLY)) {
				ketqua = n1.multiply(n2);
			} else if (math.equals(MOpe.DIVIDE)) {
				ketqua = n1.divide(n2,10, RoundingMode.HALF_UP);
			}
			ketqua = ketqua.setScale(round, RoundingMode.HALF_UP);
			return ketqua.doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static double myMath(double[] number, MOpe math, int round) {
		try {
			BigDecimal ketqua = new BigDecimal("0");
			if (math.equals(MOpe.ADD)) {
				for (int i = 0; i < number.length; i++) {
					ketqua = ketqua.add(new BigDecimal(Double.toString(number[i])));
				}
			} else if (math.equals(MOpe.SUBTRACT)) {
				for (int i = 0; i < number.length; i++) {
					ketqua = ketqua.subtract(new BigDecimal(Double.toString(number[i])));
				}
			} else if (math.equals(MOpe.MULTIPLY)) {
				for (int i = 0; i < number.length; i++) {
					ketqua = ketqua.multiply(new BigDecimal(Double.toString(number[i])));
				}
			} else if (math.equals(MOpe.DIVIDE)) {
				for (int i = 0; i < number.length; i++) {
					if (i == 0) {
						ketqua = new BigDecimal(Double.toString(number[i]));
					} else {
						ketqua = ketqua.divide(new BigDecimal(Double.toString(number[i])),10, RoundingMode.HALF_UP);
					}
				}
			}
			ketqua = ketqua.setScale(round, RoundingMode.HALF_UP);
			return ketqua.doubleValue();
		} catch (Exception e) {
			return 0;
		}
	}

	public static void main(String[] args) {
		double so[] = { 97.3, 100 };
		System.out.println(myMath(1081.0, 12.0, MOpe.DIVIDE, 10));
	}

}
