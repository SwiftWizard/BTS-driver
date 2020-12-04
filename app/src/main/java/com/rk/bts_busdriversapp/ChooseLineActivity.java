package com.rk.bts_busdriversapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rk.bts_busdriversapp.model.Bus;
import com.rk.bts_busdriversapp.model.BusLine;

import java.util.ArrayList;
import java.util.List;

public class ChooseLineActivity extends AppCompatActivity {

    private static final String TAG = ChooseLineActivity.class.getSimpleName();

    Spinner spinnerCl;
    EditText etRegPlate;
    Button btnFinish;

    FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRefBusLine = fbDatabase.getReference("BusLine");
    DatabaseReference dbRefBus = fbDatabase.getReference("Bus");

    String selectedLine;
    Bus myBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_line);

        spinnerCl = (Spinner) findViewById(R.id.spinnerCl);
        etRegPlate = findViewById(R.id.etRegPlate);
        btnFinish = findViewById(R.id.btnFinish);

        dbRefBusLine.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> busLines = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()){
                    String listEntry = ds.getValue(BusLine.class).getName();
                    busLines.add(listEntry);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChooseLineActivity.this, android.R.layout.simple_spinner_item, busLines);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCl.setAdapter(arrayAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedLine = spinnerCl.getSelectedItem().toString();

                final String regPlate = etRegPlate.getText().toString().trim().toUpperCase();

                if(regPlate.isEmpty()){
                    etRegPlate.setError(getString(R.string.et_not_filled_in_warning));
                    etRegPlate.requestFocus();
                }
                else{

                    //Try to find bus with supplied registration plate
                    dbRefBus.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(regPlate)){
                                myBus = snapshot.getValue(Bus.class);
                                myBus.setLine(selectedLine);
                                myBus.setLon("");
                                myBus.setLat("");
                                myBus.setRegistrationPlate(regPlate);
                                myBus.setActive(false);

                                dbRefBus.child(regPlate).setValue(myBus);

                                Toast.makeText(ChooseLineActivity.this, R.string.bus_found_toast, Toast.LENGTH_LONG).show();

                                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                                resultIntent.putExtra("bus", myBus);
                                setResult(RESULT_OK, resultIntent);

                                finish();

                            }else{
                                Toast.makeText(ChooseLineActivity.this, R.string.bus_not_found_toast, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }



            }
        });
    }
}