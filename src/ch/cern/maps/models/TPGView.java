package ch.cern.maps.models;

import android.graphics.drawable.Drawable;

public class TPGView {

	private Drawable lineColor;
	private int nextIn;
	private String lineNumber, directionTo;

	public TPGView(String lineNumber, Drawable drawable, int j,
			String directionTo) {
		setLineNumber(lineNumber);
		setLineColor(drawable);
		setNextIn(j);
		setDirectionTo(directionTo);
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Drawable getLineColor() {
		return lineColor;
	}

	public void setLineColor(Drawable lineColor) {
		this.lineColor = lineColor;
	}

	public int getNextIn() {
		return nextIn;
	}

	public void setNextIn(int nextIn) {
		this.nextIn = nextIn;
	}

	public String getDirectionTo() {
		return directionTo;
	}

	public void setDirectionTo(String directionTo) {
		this.directionTo = directionTo;
	}
}
