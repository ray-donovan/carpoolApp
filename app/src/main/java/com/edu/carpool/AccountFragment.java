package com.edu.carpool;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.ScrollingTabContainerView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;

public class AccountFragment extends Fragment {

    private TextView accName, accGender, accPhoneNum, badge, carNum, carModel, carColour;
    private CardView driverCV;
    private String carNumFromDB, carModelFromDB, carColourFromDB, driver_status;
    private ImageView profilePic;
    private FloatingActionButton manageFab, logoutFab, driverSignupFab, addEmergencyFab, editProfileFab, editDriverFab, appointmentFab;
    private TextView logoutActionText, driverSignupActionText, addEmergencyActionText, editProfileActionText, editDriverActionText, appointmentActionText;
    private Boolean isAllFabsVisible, genderIsEmpty, phoneNumIsEmpty;
    private ConstraintLayout acc_CL;
    private Bundle bundle;
    private FirebaseAuth mAuth;
    private ProgressBar loadingSpinner;
    private Spinner driverStatus1;
    private DatabaseReference userRef, statusRef, mStatusRef, mmStatusRef;
    private AppCompatImageView attending_view;
    private LinearLayout attending_layout;
    private boolean mSpinnerInitialized;
    private int index_of_cat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        attending_view = rootView.findViewById(R.id.attending_view);
        driverStatus1 = rootView.findViewById(R.id.spinner_driver_status);
        attending_layout = rootView.findViewById(R.id.attending_layout);

        attending_view.setVisibility(View.GONE);
        attending_layout.setVisibility(View.GONE);

        String[] driverStatus = getResources().getStringArray(R.array.driver_status);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_drop_down, driverStatus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverStatus1.setAdapter(adapter);

        // To validate user is logged-in or not
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // User is not signed in, prompt a page that allows user redirect to login
            PromptUserToLoginFragment promptUserToLoginFragment = new PromptUserToLoginFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, promptUserToLoginFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            return rootView;
        }

        accName = rootView.findViewById(R.id.accountName);
        accGender = rootView.findViewById(R.id.accountGender);
        accPhoneNum = rootView.findViewById(R.id.accountPhoneNum);
        manageFab = rootView.findViewById(R.id.manage_fab);
        loadingSpinner = rootView.findViewById(R.id.loading_spinner);
        badge = rootView.findViewById(R.id.driverBadge);
        driverCV = rootView.findViewById(R.id.driverProfileCardView);
        carNum = rootView.findViewById(R.id.userCarNum);
        carModel = rootView.findViewById(R.id.userCarModel);
        carColour = rootView.findViewById(R.id.userCarColour);
        acc_CL = rootView.findViewById(R.id.accCL);
        profilePic = rootView.findViewById(R.id.profileImg);

        genderIsEmpty = false;
        phoneNumIsEmpty = false;

        checkDriverStatus();
        GetUserDataFromDB();

        logoutFab = rootView.findViewById(R.id.logout_fab);
        driverSignupFab = rootView.findViewById(R.id.signupDriver_fab);
        addEmergencyFab = rootView.findViewById(R.id.addEmergency_fab);
        editProfileFab = rootView.findViewById(R.id.editProfile_fab);
        editDriverFab = rootView.findViewById(R.id.editDriver_fab);
        appointmentFab = rootView.findViewById(R.id.appointment_fab);

        logoutActionText = rootView.findViewById(R.id.logout_action_text);
        driverSignupActionText = rootView.findViewById(R.id.signupDriver_action_text);
        addEmergencyActionText = rootView.findViewById(R.id.addEmergency_action_text);
        editProfileActionText = rootView.findViewById(R.id.editProfile_action_text);
        editDriverActionText = rootView.findViewById(R.id.editDriver_action_text);
        appointmentActionText = rootView.findViewById(R.id.appointment_action_text);

        logoutFab.setVisibility(View.GONE);
        driverSignupFab.setVisibility(View.GONE);
        addEmergencyFab.setVisibility(View.GONE);
        editProfileFab.setVisibility(View.GONE);
        editDriverFab.setVisibility(View.GONE);
        appointmentFab.setVisibility(View.GONE);

        logoutActionText.setVisibility(View.GONE);
        driverSignupActionText.setVisibility(View.GONE);
        addEmergencyActionText.setVisibility(View.GONE);
        editProfileActionText.setVisibility(View.GONE);
        editDriverActionText.setVisibility(View.GONE);
        appointmentActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        // Floating button
        manageFab.setOnClickListener(view -> {
            acc_CL.setAlpha((float) 0.2);

            if (!isAllFabsVisible) {
                // when isAllFabsVisible becomes true
                // make all the action name texts and FABs VISIBLE
                logoutFab.show();
                addEmergencyFab.show();
                editProfileFab.show();

                // Display according to user's driver status
                if(badge.getVisibility() == View.VISIBLE){
                    editDriverFab.show();
                    editDriverActionText.setVisibility(View.VISIBLE);
                    appointmentFab.show();
                    appointmentActionText.setVisibility(View.VISIBLE);
                } else {
                    driverSignupFab.show();
                    driverSignupActionText.setVisibility(View.VISIBLE);
                }

                logoutActionText.setVisibility(View.VISIBLE);
                addEmergencyActionText.setVisibility(View.VISIBLE);
                editProfileActionText.setVisibility(View.VISIBLE);

                isAllFabsVisible = true;
            } else {
                acc_CL.setAlpha((float) 1.0);

                logoutFab.hide();
                driverSignupFab.hide();
                addEmergencyFab.hide();
                editProfileFab.hide();
                editDriverFab.hide();
                appointmentFab.hide();

                logoutActionText.setVisibility(View.GONE);
                driverSignupActionText.setVisibility(View.GONE);
                addEmergencyActionText.setVisibility(View.GONE);
                editProfileActionText.setVisibility(View.GONE);
                editDriverActionText.setVisibility(View.GONE);
                appointmentActionText.setVisibility(View.GONE);

                isAllFabsVisible = false;
            }
        });

        logoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        driverSignupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (genderIsEmpty || phoneNumIsEmpty){
                    Toast.makeText(requireContext(), "Complete profile setup first\n --> Edit profile", Toast.LENGTH_SHORT).show();
                } else {
                    driverSignupFragment driverSignupFragment = new driverSignupFragment();
                    driverSignupFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, driverSignupFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

            }
        });

        addEmergencyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), emergencyContact.class);
                startActivity(intent);
            }
        });

        editProfileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setArguments(bundle);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, editProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        appointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriverAppointment driverAppointment = new DriverAppointment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, driverAppointment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        editDriverFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDriverProfileFragment editDriverProfileFragment = new editDriverProfileFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, editDriverProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        driverStatus1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!mSpinnerInitialized){
                    mSpinnerInitialized = true;
                    return;
                }
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                String userId = User.getUid();
                mStatusRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("driverID");
                mStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot driverSnapshot : snapshot.getChildren()){
                                String mDriverStatus = driverSnapshot.child("driverStatus").getValue(String.class);
                                mmStatusRef = mStatusRef.child(driverSnapshot.getKey());
                                mmStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            if (i == 0){ // Available
                                                mmStatusRef.child("driverStatus").setValue("Available");
                                            } else if (i == 1){ // Not available
                                                mmStatusRef.child("driverStatus").setValue("Not available");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

    // Retrieve and display user data
    public void GetUserDataFromDB() {
        loadingSpinner.setVisibility(View.VISIBLE);

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null) {
            String userId = User.getUid();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    loadingSpinner.setVisibility(View.GONE);

                    if (snapshot.exists()) {
                        String nameFromDB = snapshot.child("name").getValue(String.class);
                        String genderFromDB = snapshot.child("gender").getValue(String.class);
                        String phoneNumFromDB = snapshot.child("phoneNum").getValue(String.class);

                        accName.setText(nameFromDB);

                        if(genderFromDB.isEmpty()) {
                            accGender.setText("Not set");
                            accGender.setTextColor(Color.rgb(191, 191, 191));
                            profilePic.setImageResource(R.drawable.ic_baseline_profilepic_24);
                            genderIsEmpty = true;
                        } else {
                            accGender.setText(genderFromDB);
                        }

                        if ("Female".equals(genderFromDB)){
                            profilePic.setImageResource(R.drawable.female);
                        } else if ("Male".equals(genderFromDB)){
                            profilePic.setImageResource(R.drawable.male);
                        }

                        if(phoneNumFromDB.isEmpty()) {
                            accPhoneNum.setText("Not set");
                            accPhoneNum.setTextColor(Color.rgb(191, 191, 191));
                            phoneNumIsEmpty = true;
                        } else {
                            accPhoneNum.setText(phoneNumFromDB);
                        }

                        bundle = new Bundle();
                        bundle.putString("name", nameFromDB);
                        bundle.putString("gender", genderFromDB);
                        bundle.putString("phoneNum", phoneNumFromDB);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    loadingSpinner.setVisibility(View.GONE);
                }
            });
        }
    }

    // If user is a driver, assign a driver badge
    public void checkDriverStatus() {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null) {
            String userId = User.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("driverID");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        badge.setVisibility(View.VISIBLE);
                        driverCV.setVisibility(View.VISIBLE);
                        attending_view.setVisibility(View.VISIBLE);
                        attending_layout.setVisibility(View.VISIBLE);

                        for (DataSnapshot driverSnapshot : snapshot.getChildren()) {

                            carNumFromDB = driverSnapshot.child("carPlateNum").getValue(String.class);
                            carModelFromDB = driverSnapshot.child("carModel").getValue(String.class);
                            carColourFromDB = driverSnapshot.child("carColour").getValue(String.class);
                            driver_status = driverSnapshot.child("driverStatus").getValue(String.class);

                            if (driver_status == null){
                                index_of_cat = 0;
                                statusRef = userRef.child(driverSnapshot.getKey());
                                statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            statusRef.child("driverStatus").setValue("Available");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {

                                    }
                                });
                            } else if ("Available".equals(driver_status)){
                                index_of_cat = 0;
                            } else if ("Not available".equals(driver_status)){
                                index_of_cat = 1;
                            }

                            driverStatus1.setSelection(index_of_cat);
                            carNum.setText(carNumFromDB);
                            carModel.setText(carModelFromDB);
                            carColour.setText(carColourFromDB);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }
}
