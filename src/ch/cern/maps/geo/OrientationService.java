package ch.cern.maps.geo;

import ch.cern.maps.utils.Constants;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class OrientationService extends Service implements SensorEventListener {

	private String holdErrorInformation = "SensorEventListener error";
	private SensorManager mSensorManager;
	private Sensor mSensorAccelerometer, mSensorMagneticField;
	private float[] mValuesAccelerometer, mValuesMagneticField;
	private float[] mMatrixR, mMatrixI, mMatrixValues;
	private double mAzimuth = -1;

	@Override
	public void onCreate() {
		super.onCreate();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		mSensorAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorMagneticField = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		mValuesAccelerometer = new float[3];
		mValuesMagneticField = new float[3];
		mMatrixR = new float[9];
		mMatrixI = new float[9];
		mMatrixValues = new float[3];

		mSensorManager.registerListener(this, mSensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void sendOrientationIntent() {
		Intent OrientationIntent = new Intent();
		OrientationIntent.setAction(Constants.OrientationActionTag);
		OrientationIntent.putExtra(Constants.OrientationFlagAzimuth,
				getAzimuth());
		sendBroadcast(OrientationIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sendOrientationIntent();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onAccuracyChanged(Sensor s, int i) {
	}

	@Override
	public void onSensorChanged(SensorEvent e) {

		switch (e.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			for (int i = 0; i < 3; i++) {
				mValuesAccelerometer[i] = e.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++) {
				mValuesMagneticField[i] = e.values[i];
			}
			break;
		}

		try {
			if (SensorManager.getRotationMatrix(mMatrixR, mMatrixI,
					mValuesAccelerometer, mValuesMagneticField)) {
				SensorManager.getOrientation(mMatrixR, mMatrixValues);
				setAzimuth(Math.toDegrees(mMatrixValues[0]));
				sendOrientationIntent();
			}
		} catch (Exception ex) {
			Log.e(Constants.TAG, holdErrorInformation + ex);
		}
	}

	private double getAzimuth() {
		return mAzimuth;
	}

	private void setAzimuth(double mAzimuth) {
		this.mAzimuth = mAzimuth;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this, mSensorAccelerometer);
			mSensorManager.unregisterListener(this, mSensorMagneticField);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}