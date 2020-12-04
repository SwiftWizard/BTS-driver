package com.rk.bts_busdriversapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rk.bts_busdriversapp.model.Bus;

public class TrackingService extends Service implements LocationListener {

    private static final long UPDATE_INTERVAL_MINIMUM = 1000; // 1s

    Bus myBus;

    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRefBus = fbDatabase.getReference("Bus");


    public TrackingService() {
    }


    private LocationManager locationManager;

    private FusedLocationProviderClient mFusedLocationClient;
    Location location;




    private void buildNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String NOTIFICATION_CHANNEL_ID = "bts_busdriversapp";
            String channelName = "Location Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.tracking_img)
                    .setContentTitle(getText(R.string.app_name))
                    .setContentText(getText(R.string.tracking_active_notification))
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .build();
            startForeground(2, notification);
        }
        else {

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification.Builder notification = new Notification.Builder(this)
                    .setContentTitle(getText(R.string.app_name))
                    .setContentText(getText(R.string.tracking_active_notification))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.tracking_img);

            startForeground(1, notification.build());
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("LOCATION: ", location.toString());

        myBus.setLat(String.format("%f", location.getLatitude()));
        myBus.setLon(String.format("%f", location.getLongitude()));

        dbRefBus.child(myBus.getRegistrationPlate()).setValue(myBus);

    }

    //Location enabled ... yay
    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    //Location not enabled on users phone, intent to enable it
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getApplicationContext(), "Please enable GPS", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Deprecated
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permission == PackageManager.PERMISSION_GRANTED){

            myBus = (Bus) intent.getSerializableExtra("bus");

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL_MINIMUM, 0, this);

            buildNotification();

        }
        else{

            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            locationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(locationIntent);

        }

        return START_STICKY;

    }

}
