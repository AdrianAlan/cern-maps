package ch.cern.maps.services;

import ch.cern.maps.utils.Constants;

public class MapScroller {

	private int webViewWidth = 0, webViewHeight = 0, scrollToX = 0,
			scrollToY = 0;
	private double currentScale = 1;

	public int getScrollToX() {
		return scrollToX;
	}

	public int getScrollToY() {
		return scrollToY;
	}

	public MapScroller(int scrollLocationX, int scrollLocationY,
			int measuredWidth, int measuredHeight, double theScale) {

		webViewWidth = measuredWidth;
		webViewHeight = measuredHeight;
		currentScale = theScale;
		setScroll(scrollLocationX, scrollLocationY);
	}

	private void setScroll(int X, int Y) {
		scrollToX = (int) (X * currentScale - webViewWidth/2);
		scrollToY = (int) (Y * currentScale - webViewHeight/2);
		correctX(scrollToX);
		correctY(scrollToY);
	}

	private void correctY(int Y) {
		if (Y < 0) {
			scrollToY = 0;
		} else if (Y + webViewHeight > Constants.MAP_HEIGHT * currentScale) {
			scrollToY = (int) ((Constants.MAP_HEIGHT * currentScale) - webViewHeight);
			if (scrollToY < 0) {
				scrollToY = 0;
			}
		}
	}

	private void correctX(int X) {
		if (X < 0) {
			scrollToX = 0;
		} else if (X + webViewWidth > Constants.MAP_WIDTH * currentScale) {
			scrollToX = (int) ((Constants.MAP_WIDTH * currentScale) - webViewWidth);
			if (scrollToY < 0) {
				scrollToY = 0;
			}
		}
	}
}