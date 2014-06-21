package ch.cern.maps.services;

import ch.cern.maps.utils.Constants;

public class MapScroller {

	private double[] correctedScroll = { 0, 0 };
	private int[] mMap = { Constants.MAP_WIDTH, Constants.MAP_HEIGHT };

	public double[] getScroll() {
		return correctedScroll;
	}

	public MapScroller(double[] scrollLocation, double[] screenDimentions) {
		for (int i = 0; i < 2; i++) {
			correctedScroll[i] = correctScroll(scrollLocation[i]
					- (screenDimentions[i] / 2), screenDimentions[i], mMap[i]);
		}
	}

	private double correctScroll(double mScroll, double mDimention, int mMapMax) {
		if (mScroll + mDimention > mMapMax) {
			mScroll = (mMapMax) - mDimention;
		}
		if (mScroll < 0) {
			return 0;
		}
		return mScroll;
	}
}