package ch.cern.maps.utils;

import java.util.ArrayList;
import java.util.Date;

import ch.cern.maps.models.Trams;

public class Utils {

	private static Trams minA, minB;

	public static Trams[] getNextTrains(ArrayList<Trams> tramList) {
		minA = new Trams("", "24:00", "--");
		minB = new Trams("", "24:00", "--");
		minA.setWaiting(1440);
		minB.setWaiting(1440);
		Date nowTime = new Date();

		for (Trams t : tramList) {
			String[] time = t.getTime().split(":");
			int toGo = Integer.parseInt(time[0]) * 60
					+ Integer.parseInt(time[1])
					- (nowTime.getHours() * 60 + nowTime.getMinutes());
			if (toGo < 0) {
				toGo += 60 * 24;
			}
			t.setWaiting(toGo);
			minimumTime(minA, minB, t);
		}
		Trams[] tr = { minA, minB };
		return tr;
	}

	public static void minimumTime(Trams A, Trams B, Trams C) {
		Trams left;
		int max = Math.max(A.getWaiting(), B.getWaiting());
		if (Math.min(A.getWaiting(), B.getWaiting()) == A.getWaiting()) {
			minA = A;
			left = B;
		} else {
			minA = B;
			left = A;
		}
		if (Math.min(max, C.getWaiting()) == C.getWaiting()) {
			minB = C;
		} else {
			minB = left;
		}
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