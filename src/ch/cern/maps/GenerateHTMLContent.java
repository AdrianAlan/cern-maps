package ch.cern.maps;

public class GenerateHTMLContent {

	private String HTML = "";

	public String getmHTML() {
		return HTML;
	}

	private int pngGPSWidth = 48, pngGPSHeight = 48, pngPosWidth = 20,
			pngPosHeight = 20;
	
	private String scriptPlaceGPS = "function mGPSPosition(mLeft, mTop, mScale) {" +
			"var el = document.getElementById('gps'); " +
			"el.style.display = 'block'; " +
			"el.style.left = ((mLeft-(" + pngGPSWidth + " / mScale)) + 'px'); " +
			"el.style.top = ((mTop-(" + pngGPSHeight + " * 2 / mScale)) + 'px'); " +
			"el.style.height = ((" + pngGPSHeight + " * 2 / mScale) + 'px'); " +
			"el.style.width = ((" + pngGPSWidth + " * 2 / mScale) + 'px'); " + 
			"}";
	
	private String scriptPlacePos = "function mPosPosition(mLeft, mTop, mRadius, mScale) {" +
			"var el = document.getElementById('pos'); " +
			"el.style.display = 'block'; " +
			"el.style.left = ((mLeft-(" + pngPosWidth + " / mScale)) + 'px'); " +
			"el.style.top = ((mTop-(" + pngPosHeight + " / mScale)) + 'px'); " +
			"el.style.height = ((" + pngPosHeight + " * 2 / mScale) + 'px'); " +
			"el.style.width = ((" + pngPosWidth + " * 2 / mScale) + 'px'); " + 
			"mAccuracy(mLeft, mTop, mRadius)" +
			"}";
	
	private String pageScroll = "function pageScroll(mLeft, mTop) { window.scrollTo(mLeft, mTop); }";	
	private String scriptsRotatePos = "function mRotationPos(mDegree) { var el = document.getElementById('pos'); el.style.webkitTransform = 'rotate('+mDegree+'deg)'; }";
	private String scriptsHidePos = "function mHidePos() { var el = document.getElementById('pos'); el.style.display = 'none'; var el = document.getElementById('circle'); el.style.display = 'none'; }";

	private String scriptsAccuracy = "function mAccuracy(mLeft, mTop, mRadius) {"
			+ "var el = document.getElementById('circle');"
			+ "el.style.display = 'block';"
			+ "el.style.left = ((mLeft-mRadius) + ' px');"
			+ "el.style.top = ((mTop-mRadius) + ' px');"
			+ "el.style.width = ((mRadius+mRadius).toString() + ' px');"
			+ "el.style.height = ((mRadius+mRadius) + ' px');"
			+ "el.style.WebkitBorderRadius = (mRadius + ' px'); " +
			"}";

	private String cssGPS = "#gps { background-image:url(gps.png); background-size: 100%; display: none; position: absolute; margin: 0; z-index: 10 }";
	private String cssPOS = "#pos { background-image:url(pos.png); background-size: 100%; display: none; position: absolute; margin: 0; z-index: 20 }";
	private String cssCIR = "#circle {display:none; position: absolute; background: #F08080; -webkit-opacity: 0.5; z-index: 2}";

	public String setHtml() {

		HTML += "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />";
		HTML += "<script>" + scriptsAccuracy + pageScroll + scriptPlaceGPS
				+ scriptsHidePos + scriptsRotatePos + scriptPlacePos
				+ "</script>";
		HTML += "<style type='text/css'>" + cssCIR + cssGPS + cssPOS 
				+ "</style></head>";

		HTML += "<body style='height: 3192; width: 3644px; margin: 0; padding: 0;'>"
				+ "<div style='float:left;background-image:url(MAP1A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP1A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B4.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP2A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP2A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B4.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP3A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP3A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B4.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP4A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP4A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B4.png);height:798px;width:911px;'></div>";
		HTML += "<div id='circle'></div>";
		HTML += "<div id='gps'></div>";
		HTML += "<div id='pos'></div>";
		
		HTML += "</body>";
		return HTML;
	}
}
