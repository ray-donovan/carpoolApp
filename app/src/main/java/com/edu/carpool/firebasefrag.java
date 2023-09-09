package com.edu.carpool;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class firebasefrag extends Fragment {

    View view;
    private EditText edName;
    private EditText edRelationship;
    private EditText edPhoneNumber;

    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_firebasefrag, container, false);


        final Button btUpload = view.findViewById(R.id.btUpload);

        final Animation moveUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.move_up);
        final Animation fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out);

        // Set an animation listener to start the fadeOutAnimation when moveUpAnimation ends
        moveUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the fade out animation
                btUpload.startAnimation(fadeOutAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://carpool-fe2e1-default-rtdb.firebaseio.com/");

        edName = view.findViewById(R.id.name);
        edRelationship = view.findViewById(R.id.relationship);
        edPhoneNumber = view.findViewById(R.id.phone);

        Button btClear = view.findViewById(R.id.btnclear);
        Button btStorage = view.findViewById(R.id.btnfirebaseHist);
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                String phone = edPhoneNumber.getText().toString();
                String relationship = edRelationship.getText().toString();


                if (name.isEmpty() || phone.isEmpty() || relationship.isEmpty()) {
                    Toast.makeText(requireContext(), "Opps!! Please fill in all fields!", Toast.LENGTH_SHORT).show();
                } else if (!isValidPhoneNumber(phone)) {
                    // Check if the phone number is valid
                    Toast.makeText(requireContext(), "Please enter a valid 10-digit phone number!", Toast.LENGTH_SHORT).show(); }
                else {

                    String uid = currentUser.getUid();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                            .child(uid)
                            .child("emergencyContacts");

                    String contactKey = databaseReference.push().getKey();

                    UserData userData = new UserData(name, phone, relationship);

                    databaseReference.child(contactKey).setValue(userData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(requireContext(), "Contact Successfully updated!", Toast.LENGTH_SHORT).show();
                                        btUpload.startAnimation(moveUpAnimation);
                                        clearText();
                                    } else {

                                        Toast.makeText(requireContext(), "Failed to save emergency contact: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    // Handle the failure
                                    Toast.makeText(requireContext(), "Failed to save emergency contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }


        });

        btStorage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), firebaseStorage.class);
                startActivity(intent);
            }
        });


        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearText();
            }
        });
        return view;
    }
    private void clearText() {
        edName.getText().clear();
        edRelationship.getText().clear();
        edPhoneNumber.getText().clear();

    }

        private boolean isValidPhoneNumber(String phoneNumber) {
            String phonePattern = "^[0-9]{10}$";

            // Check if the phone number matches the pattern
            return phoneNumber.matches(phonePattern);
        }


}