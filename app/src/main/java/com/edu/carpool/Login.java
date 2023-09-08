package com.edu.carpool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText loginEmail, loginPassword;
    private Button loginButton, signupButton;
    private ImageButton close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
        close = findViewById(R.id.closeBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    loginEmail.setError("Required");
                    loginEmail.requestFocus();
                    Toast.makeText(Login.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();

                } else if (password.isEmpty()) {
                    loginPassword.setError("Required");
                    loginPassword.requestFocus();
                    Toast.makeText(Login.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();

                } else {
                    // Verify user using email and password
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        if (task.getException() != null) {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(Login.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}