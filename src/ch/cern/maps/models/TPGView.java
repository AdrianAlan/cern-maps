package ch.cern.maps.models;

import android.graphics.drawable.Drawable;

public class TPGView {

	private Drawable lineColor;
	private Trams[] next;
	private String lineNumber;

	public TPGView(String lineNumber, Drawable drawable, Trams[] nextTrams) {
		setLineNumber(lineNumber);
		setLineColor(drawable);
		setNext(nextTrams);
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

	public Trams[] getNext() {
		return next;
	}

	public void setNext(Trams[] next) {
		this.next = next;
	}
}
