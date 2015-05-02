package ch.cern.maps.utils;

import java.util.ArrayList;
import java.util.Calendar;

import ch.cern.maps.models.Trams;

public class Utils {

	private static Trams minA, minB;

	public static Trams[] getNextTrains(ArrayList<Trams> tramList, int hour,
			int minute) {
		minA = new Trams("", "24:00", "--", "");
		minB = new Trams("", "24:00", "--", "");
		minA.setWaiting(1440);
		minB.setWaiting(1440);

		Calendar rightNow = Calendar.getInstance();
		int d = rightNow.get(Calendar.DAY_OF_WEEK);
		
		String mCase = "week";
		if ((d == Calendar.SATURDAY && hour > 1)
				|| (d == Calendar.SUNDAY && hour <= 1)) {
			mCase = "sa";
		} else if (d == Calendar.SUNDAY && hour > 1 || d == Calendar.MONDAY
				&& hour <= 1) {
			mCase = "su";
		}

		for (Trams t : tramList) {
			if (mCase.equals(t.getDay())) {
				String[] time = t.getTime().split(":");
				int toGo = Integer.parseInt(time[0]) * 60
						+ Integer.parseInt(time[1]) - (hour * 60 + minute);
				if (toGo < 0) {
					toGo += 60 * 24;
				}
				t.setWaiting(toGo);
				minimumTime(minA, minB, t);
			}
		}
		Trams[] tr = { minA, minB };
		if (Math.min(minA.getWaiting(), minB.getWaiting()) == minB.getWaiting()) {
			tr[0] = minB;
			tr[1] = minA;
		}
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
}