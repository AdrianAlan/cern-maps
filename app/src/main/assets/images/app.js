var pngGPSWidth = 48;
var pngGPSHeight = 48;
var pngPosWidth = 20;
var pngPosHeight = 20;

function mGPSPosition(mLeft, mTop, mScale) {
    var el = document.getElementById('gps');
    el.style.display = 'block';
    el.style.left = ((mLeft - (pngGPSWidth / mScale)) + 'px');
    el.style.top = ((mTop - (pngGPSHeight * 2 / mScale)) + 'px');
    el.style.height = ((pngGPSHeight * 2 / mScale) + 'px');
    el.style.width = ((pngGPSWidth * 2 / mScale) + 'px');
}

function mPosPosition(mLeft, mTop, mRadius, mScale) {
    var el = document.getElementById('pos');
    el.style.display = 'block';
    el.style.left = ((mLeft - (pngPosWidth / mScale)) + 'px');
    el.style.top = ((mTop - (pngPosHeight / mScale)) + 'px');
    el.style.height = ((pngPosHeight * 2 / mScale) + 'px');
    el.style.width = ((pngPosWidth * 2 / mScale) + 'px');
    mAccuracy(mLeft, mTop, mRadius);
}

function pageScroll(mLeft, mTop) {
    $('html, body').animate({
        scrollTop: mTop,
        scrollLeft: mLeft
    }, 500);
}

function mRotationPos(mDegree) {
    var el = document.getElementById('pos');
    el.style.webkitTransform = 'rotate(' + mDegree + 'deg)';
}

function mHidePos() {
    var el = document.getElementById('pos');
    el.style.display = 'none';
    var el = document.getElementById('circle');
    el.style.display = 'none';
}

function mAccuracy(mLeft, mTop, mRadius) {
    var el = document.getElementById('circle');
    el.style.display = 'block';
    el.style.left = ((mLeft - mRadius) + ' px');
    el.style.top = ((mTop - mRadius) + ' px');
    el.style.width = ((mRadius + mRadius).toString() + ' px');
    el.style.height = ((mRadius + mRadius) + ' px');
    el.style.WebkitBorderRadius = (mRadius + ' px');
}

$(document).ready(function() {
    loadMap();
});

function loadMap() {
    var type = getParameterByName('type');
    if (!type) {
        type = 'standard';
    }

    var bodyElement = document.getElementsByTagName('body')[0];
    for (var j = 46490; j < 46511; j++) {
        for (var i = 67730; i < 67751; i++) {
            var areaElement = document.createElement('div');
            areaElement.style.float = 'left';
            areaElement.style.backgroundImage = 'url(map/'+ type + '/' + i + '-' + j + '.png)';
            areaElement.style.height = '256px';
            areaElement.style.width = '256px';

            bodyElement.appendChild(areaElement);
        }
    }
}

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

