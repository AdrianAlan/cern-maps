package ch.cern.maps;

public class GenerateHTMLContent {

	private String HTML = "";

	public String getmHTML() {
		return HTML;
	}

	private int pngGPSWidth = 48, pngGPSHeight = 48, pngPosWidth = 20,
			pngPosHeight = 20;

	private String scriptPlaceGPS = "function mGPSPosition(mLeft, mTop, mScale) {"
			+ "var el = document.getElementById('gps'); "
			+ "el.style.display = 'block'; "
			+ "el.style.left = ((mLeft-("
			+ pngGPSWidth
			+ " / mScale)) + 'px'); "
			+ "el.style.top = ((mTop-("
			+ pngGPSHeight
			+ " * 2 / mScale)) + 'px'); "
			+ "el.style.height = (("
			+ pngGPSHeight
			+ " * 2 / mScale) + 'px'); "
			+ "el.style.width = (("
			+ pngGPSWidth
			+ " * 2 / mScale) + 'px'); " + "}";

	private String scriptPlacePos = "function mPosPosition(mLeft, mTop, mRadius, mScale) {"
			+ "var el = document.getElementById('pos'); "
			+ "el.style.display = 'block'; "
			+ "el.style.left = ((mLeft-("
			+ pngPosWidth
			+ " / mScale)) + 'px'); "
			+ "el.style.top = ((mTop-("
			+ pngPosHeight
			+ " / mScale)) + 'px'); "
			+ "el.style.height = (("
			+ pngPosHeight
			+ " * 2 / mScale) + 'px'); "
			+ "el.style.width = (("
			+ pngPosWidth
			+ " * 2 / mScale) + 'px'); "
			+ "mAccuracy(mLeft, mTop, mRadius)" + "}";

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
			+ "el.style.WebkitBorderRadius = (mRadius + ' px'); " + "}";

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

		HTML += "<body style='height: 5376px; width: 4096px; margin: 0; padding: 0;'>";

		for (int j = 46490; j < 46511; j++) {
			for (int i = 67730; i < 67746; i++) {
				HTML += "<div style='float:left;background-image:url(map/"
						+ i + "-" + j
						+ ".png);height:256px;width:256px;'></div>";
			}
		}
		HTML += "<div id='circle'></div><div id='gps'></div><div id='pos'></div></body>";
		return HTML;
	}
}
