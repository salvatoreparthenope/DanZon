package com.parthenope.salvatoresposato.danzon.System.Services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.Alert.AlertControl;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.Alert.EmailAlert;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.Alert.SmsAlert;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.ServiceGpsNotify.GpsSubject;
import com.parthenope.salvatoresposato.danzon.Database.GpsCoordinate;
import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

import static com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant.ACTION_INTENT_UPDATE_LOCATION;
import static com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant.KEY_INTENT_LOCATION;

public class ServiceSensor extends Service implements SensorEventListener {

    private SensorManager sensorManager = null;
    private GpsSubject subject = null;
    AlertControl alertControl = new AlertControl();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initSensor();
        initGps();

        return START_STICKY;
    }

    public void setSubject(GpsSubject subject) {
        this.subject = subject;
    }

    /**
     * Initilize and configure the gps library
     */
    private void initGps() {

        LocationParams parameters = (new LocationParams.Builder()).setAccuracy(LocationAccuracy.HIGH).setDistance(20.0F).setInterval(30000L).build();
        SmartLocation.with(getApplicationContext())
                .location()
                .config(parameters)
                .continuous()
                .start(new OnLocationUpdatedListener() {

                    @Override
                    public void onLocationUpdated(Location location) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_INTENT_LOCATION,location.getLatitude()+","+location.getLongitude());
                        intent.setAction(ACTION_INTENT_UPDATE_LOCATION);
                        sendBroadcast(intent);
                    }

                });
    }

    /**
     * Initilize and configure the sensor library
     */
    private void initSensor() {
        Sensey.getInstance().init(getApplicationContext());
        Sensey.getInstance().startShakeDetection(10, 2000,
                new ShakeDetector.ShakeListener() {
                    @Override
                    public void onShakeDetected() {
                        sendAlerts();
                    }

                    @Override
                    public void onShakeStopped() {
                    }
                });
    }

    /**
     * Send alerts by SMS,Email,etc
     */
    private void sendAlerts() {

        String alertSms = Variable.getValue(GlobalConstant.KEY_PHONE_NUMBERS);
        String alertEmail = Variable.getValue(GlobalConstant.KEY_ALERT_EMAIL);

        if (alertSms != null)
            alertControl.addCommand(new SmsAlert());

        if (alertEmail != null)
            alertControl.addCommand(new EmailAlert());

        alertControl.SendAlerts();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Sensey.getInstance().stop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorManager.unregisterListener(this);
        stopSelf();
    }


}