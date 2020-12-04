package com.rk.bts_busdriversapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rk.bts_busdriversapp.model.Bus;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = AppCompatActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final int REQUEST_CODE_LINE_AND_BUS = 101;

    ImageView ivBusStatus;
    TextView tvBusStatus;
    TextView tvBusLine;
    Button chooseLine;
    Button toggleMode;

    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRefBus = fbDatabase.getReference("Bus");

    Bus myBus; // Bus chosen by driver
    boolean isInDrivingMode = false;

    private boolean networkConnectionAvailable(){
        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMan.getActiveNetworkInfo();

        boolean networkAvailable =  (networkInfo != null && networkInfo.isConnected());

        if(!networkAvailable){
            CharSequence msg = getString(R.string.internet_access_warning);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return networkAvailable;

    }

    private boolean runtimePermissions(){
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_CODE_PERMISSIONS);
            return true;

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            enableButtons();
        }
        else{
            runtimePermissions();
        }
    }

    private void enableButtons(){
        toggleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myBus == null){
                    Toast.makeText(getApplicationContext(), R.string.no_line_chosen_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    isInDrivingMode = !isInDrivingMode;

                    if(isInDrivingMode){
                        ivBusStatus.setImageResource(R.drawable.bus_moving);
                        tvBusStatus.setText(R.string.bus_driving_text);
                        toggleMode.setText(R.string.btn_driving_mode_on);

                        myBus.setActive(true);
                        dbRefBus.child(myBus.getRegistrationPlate()).setValue(myBus);

                        Toast.makeText(getApplicationContext(), R.string.tracking_enabled_toast, Toast.LENGTH_LONG).show();

                        Intent startTrackingIntent = new Intent(getApplicationContext(), TrackingService.class);
                        startTrackingIntent.putExtra("bus", myBus);
                        startService(startTrackingIntent);


                    }
                    else{
                        ivBusStatus.setImageResource(R.drawable.bus_idle);
                        tvBusStatus.setText(R.string.bus_idle_text);
                        toggleMode.setText(R.string.btn_driving_mode_off);

                        Intent stopTrackingIntent = new Intent(getApplicationContext(), TrackingService.class);
                        stopService(stopTrackingIntent);

                        myBus.setActive(false);
                            dbRefBus.child(myBus.getRegistrationPlate()).setValue(myBus);

                        Toast.makeText(getApplicationContext(), R.string.tracking_disabled_toast, Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        chooseLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ChooseLineActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LINE_AND_BUS);

                /*
                DatabaseReference dbRefBus = fbDatabase.getReference("Bus");

                Bus bus1 = new Bus();
                bus1.setRegistrationPlate("NS123AB");
                bus1.setLine("");
                bus1.setLat("34.13212");
                bus1.setLon("89.90900");
                bus1.setActive(false);

                dbRefBus.child(bus1.getRegistrationPlate()).setValue(bus1);
                */
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_LINE_AND_BUS){
            if(resultCode == RESULT_OK){
                myBus = (Bus) data.getSerializableExtra("bus");
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.bus_selection_failed, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivBusStatus = findViewById(R.id.ivBusStatus);
        tvBusStatus = findViewById(R.id.tvBusStatus);
        tvBusLine = findViewById(R.id.tvBusLine);
        chooseLine = findViewById(R.id.btnLine);
        toggleMode = findViewById(R.id.btnToggleMode);

        if(savedInstanceState != null){
            myBus = (Bus) savedInstanceState.getSerializable("myBus");
            isInDrivingMode = savedInstanceState.getBoolean("isInDrivingMode");
        }

        if(!runtimePermissions() && networkConnectionAvailable()){
            enableButtons();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean("isInDrivingMode", isInDrivingMode);
        outState.putSerializable("myBus", myBus);

    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        myBus = (Bus) savedInstanceState.getSerializable("myBus");
        isInDrivingMode = savedInstanceState.getBoolean("isInDrivingMode");

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        Intent stopTrackingIntent = new Intent(getApplicationContext(), TrackingService.class);

        //Check if user is logged in

        FirebaseAuth fbAuth = FirebaseAuth.getInstance();

        if(fbAuth.getCurrentUser() == null){

            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();

        }
        else{
            //Fail-safe
            stopService(stopTrackingIntent);
        }

        if(myBus == null){
            //Another fail-safe
            stopService(stopTrackingIntent);
        }

        //If bus and line is chosen display it
        if(myBus != null){
            tvBusLine.setText("(" + myBus.getRegistrationPlate() + ")   " + myBus.getLine());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        fbAuth.signOut();

    }
}