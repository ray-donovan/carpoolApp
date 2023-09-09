package com.edu.carpool;


import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


public class emergencyContact extends AppCompatActivity {


    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencycontact);

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString("Phone", "");

        Button sosButton = findViewById(R.id.btnsos);
        sosButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btnsosanim));

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the phone number is not empty
                if (!phoneNumber.isEmpty()) {
                    // Create an Intent to open the dial pad with the phone number
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(dialIntent);
                } else {
                    // Handle the case where the phone number is empty
                    // You can show a message to the user
                    Toast.makeText(emergencyContact.this, "Phone number not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnContact = findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(emergencyContact.this, manageContact.class);
                startActivity(intent);
            }
        });


        Button btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String defaultMessage = "I am in danger, please help me! I am in the car with the car plate of:";

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));

                sendIntent.putExtra("sms_body", defaultMessage);

                startActivity(sendIntent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop any ongoing animations to prevent memory leaks
        findViewById(R.id.btnsos).clearAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop any ongoing animations to prevent memory leaks
        findViewById(R.id.btnsos).clearAnimation();
    }

}
