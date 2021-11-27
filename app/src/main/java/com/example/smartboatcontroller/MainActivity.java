package com.example.smartboatcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
        private SeekBar sBar;
        private Switch sMode;
        private TextView power;

        FirebaseDatabase firebaseDatabase;
        TextView powerText;
        TextView directionText;
        TextView typeText;

        String[] dataType = {"Direction", "Type", "Power"};
        DatabaseReference databaseReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            getSupportActionBar().hide();
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            sBar = (SeekBar) findViewById(R.id.seekBar);
            sMode = (Switch) findViewById(R.id.switch_mode);
            power = findViewById(R.id.txt_power);
            power.setText(sBar.getProgress() + "/" + sBar.getMax());
            sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int pval = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pval = progress;
                    databaseReference = firebaseDatabase.getReference("Power");
                    databaseReference.setValue(pval+"");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //write custom code to on start progress
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    power.setText(pval + "/" + seekBar.getMax());
                }
            });

            sMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    databaseReference = firebaseDatabase.getReference("Type");
                    if(isChecked == true) databaseReference.setValue("Auto");
                    if(isChecked == false) databaseReference.setValue("Manual");
                }
            });

            powerText = findViewById(R.id.txt_power);
            directionText = findViewById(R.id.txt_direction);
            typeText = findViewById(R.id.txt_type);
            firebaseDatabase = FirebaseDatabase.getInstance();
            for(int i=0; i<3;i++){
                databaseReference = firebaseDatabase.getReference(dataType[i]);
                getData(i);
            }
        }

        public void onClick_stop(View view){
            sBar.setProgress(0);
            power.setText("0/100");
            databaseReference = firebaseDatabase.getReference("Direction");
            databaseReference.setValue("Stop");
        }
        public void onClick_Left(View view){
            databaseReference = firebaseDatabase.getReference("Direction");
            databaseReference.setValue("Left");
        }
        public void onClick_Right(View view){
            databaseReference = firebaseDatabase.getReference("Direction");
            databaseReference.setValue("Right");
        }
        public void onClick_Backward(View view){
            databaseReference = firebaseDatabase.getReference("Direction");
            databaseReference.setValue("Backward");
        }
        public void onClick_Forward(View view){
            databaseReference = firebaseDatabase.getReference("Direction");
            databaseReference.setValue("Forward");
        }

    private void getData(int k) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (k==0) directionText.setText(value);
                if (k==1) typeText.setText(value);
                if (k==2) powerText.setText(value+"/100");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    }
