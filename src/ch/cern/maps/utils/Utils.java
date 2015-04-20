package ch.cern.maps.utils;

import java.util.ArrayList;
import java.util.Date;

import ch.cern.maps.models.Trams;

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

	public static double[] getPixel(double x, double y) {
		double[] mPixels = {
				Constants.MAP_WIDTH
						* ((x - Constants.MAP_WEST) / (Constants.MAP_EAST - Constants.MAP_WEST)),
				Constants.MAP_HEIGHT
						* (1 - ((y - Constants.MAP_SOUTH) / (Constants.MAP_NORTH - Constants.MAP_SOUTH))) };
		return mPixels;
	}

	public static Date getLastTPGUpdate() {
		return null;
		// TODO Check last update date
	}
}