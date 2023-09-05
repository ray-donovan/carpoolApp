package com.edu.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText signupName, signupEmail, signupPassword;
    private Button signupButton, loginButton;
    private ImageButton close;
    private FirebaseDatabase db;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginButton = findViewById(R.id.login_button);
        close = findViewById(R.id.closeBtn);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();

                if (name.isEmpty()) {
                    signupName.setError("Required");
                    signupName.requestFocus();
                    Toast.makeText(Signup.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();

                } else if (email.isEmpty()) {
                    signupEmail.setError("Required");
                    signupEmail.requestFocus();
                    Toast.makeText(Signup.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();

                } else if (password.isEmpty()) {
                    signupPassword.setError("Required");
                    signupPassword.requestFocus();
                    Toast.makeText(Signup.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();

                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        saveUserDataToDatabase(user.getUid(), name, email, password);
                                    } else {
                                        if (task.getException() != null) {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(Signup.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveUserDataToDatabase(String ID, String name, String email, String password) {
        // Write user details to database
        db = FirebaseDatabase.getInstance();
        dbReference = db.getReference("users");

        UserModelClass userModelClass = new UserModelClass(ID, name, "", "" , email, password);
        dbReference.child(ID).setValue(userModelClass);

        Toast.makeText(Signup.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
    }
}