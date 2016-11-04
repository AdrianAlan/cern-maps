package ch.cern.maps.services;

import ch.cern.maps.utils.Constants;

public class MapScroller {

	private double[] correctedScroll = { 0, 0 };
	private static final int[] MAP_DIMENSIONS = { Constants.MAP_WIDTH, Constants.MAP_HEIGHT };

	public double[] getScroll() {
		return correctedScroll;
	}

	public MapScroller(double[] scrollLocation, double[] screenDimensions) {
		for (int i = 0; i < 2; i++) {
			correctedScroll[i] = correctScroll(scrollLocation[i]
					- (screenDimensions[i] / 2), screenDimensions[i], MAP_DIMENSIONS[i]);
		}
	}

	private double correctScroll(double mScroll, double mDimension, int mMapMax) {
		if (mScroll + mDimension > mMapMax) {
			mScroll = (mMapMax) - mDimension;
		}
		if (mScroll < 0) {
			return 0;
		}
		return mScroll;
	}
}