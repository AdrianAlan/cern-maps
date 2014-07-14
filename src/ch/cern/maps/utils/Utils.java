package ch.cern.maps.utils;

import java.util.ArrayList;
import java.util.Date;

public class Utils {

	private static int minA, minB;

	public static String getNextTrains(ArrayList<Trams> tramList) {
		minA = 10080;
		minB = 10080;
		Date nowTime = new Date();

		for (Trams t : tramList) {
			String[] time = t.getTime().split(":");
			@SuppressWarnings("deprecation")
			int toGo = (60 * 24 * Integer.parseInt(time[0])
					+ Integer.parseInt(time[1]) * 60 + Integer
						.parseInt(time[2]))
					- (24 * 60 * (nowTime.getDay() - 1) + nowTime.getHours()
							* 60 + nowTime.getMinutes());
			if (toGo < 0) {
				toGo += 60 * 24 * 7;
			}
			minimumTime(minA, minB, toGo);
		}
		return " " + Math.min(minA, minB) + "min; " + Math.max(minA, minB)
				+ "min.";
	}

	public static void minimumTime(int A, int B, int C) {
		int max = Math.max(A, B);
		minA = Math.min(A, B);
		minB = Math.min(max, C);
	}

	public static double[] getPixel(double vertical, double horizontal) {
		double maxTop = Constants.MAP_NORTH;
		double maxBottom = Constants.MAP_SOUTH;
		double maxLeft = Constants.MAP_WEST;
		double maxRight = Constants.MAP_EAST;
		double picHeight = Constants.MAP_HEIGHT;
		double picWidth = Constants.MAP_WIDTH;

		double picV = picHeight
				* (1 - ((vertical - maxBottom) / (maxTop - maxBottom)));
		double picH = picWidth
				* ((horizontal - maxLeft) / (maxRight - maxLeft));

		double[] returnMe = { picH, picV };
		return returnMe;
	}
	
	public static Date getLastTPGUpdate() {
		return null;
		// TODO Check last update date
	}
}