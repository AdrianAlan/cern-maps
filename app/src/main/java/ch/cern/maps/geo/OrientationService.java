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

	private SensorManager sensorManager;
	private Sensor sensorAccelerometer, sensorMagneticField;
	private float[] valuesAccelerometer, valuesMagneticField;
	private float[] matrixR, matrixI, matrixValues;
	private double azimuth = -1;

	@Override
	public void onCreate() {
		super.onCreate();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];
		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];

		sensorManager.registerListener(this, sensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorMagneticField,
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
			System.arraycopy(e.values, 0, valuesAccelerometer, 0, 3);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			System.arraycopy(e.values, 0, valuesMagneticField, 0, 3);
			break;
		}

		try {
			if (SensorManager.getRotationMatrix(matrixR, matrixI,
					valuesAccelerometer, valuesMagneticField)) {
				SensorManager.getOrientation(matrixR, matrixValues);
				setAzimuth(Math.toDegrees(matrixValues[0]));
				sendOrientationIntent();
			}
		} catch (Exception ex) {
			String holdErrorInformation = "SensorEventListener error";
			Log.e(Constants.TAG, holdErrorInformation + ex.getMessage(), ex);
		}
	}

	private double getAzimuth() {
		return azimuth;
	}

	private void setAzimuth(double mAzimuth) {
		this.azimuth = mAzimuth;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (sensorManager != null) {
			sensorManager.unregisterListener(this, sensorAccelerometer);
			sensorManager.unregisterListener(this, sensorMagneticField);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}