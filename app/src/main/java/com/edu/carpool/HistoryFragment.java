package com.edu.carpool;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements AppointmentRecyclerViewInterface{

    // Store instances of the appointment
    ArrayList<AppointmentModelClass> appointmentModelClasses = new ArrayList<>();
    TextView tv_emp_hist, tv_your, tv_customer;
    Switch modeSwitch;
    TableLayout tb1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history, container, false);
        tv_emp_hist = view.findViewById(R.id.tv_emp_hist);
        modeSwitch = view.findViewById(R.id.switch1);
        tb1 = view.findViewById(R.id.tb1);
        tv_your = view.findViewById(R.id.tv_your);
        tv_customer = view.findViewById(R.id.tv_customer);
        // Retrieve appointment only relevant to the current user

        // Guest history view and Driver history view should be different
        // Guest can only see the view they booked
        // Driver can see both the appointment they themself book and also the appointment from other user

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // If user is not logged in, no history to show
        if (mAuth.getCurrentUser() == null){
            View viewGuest = inflater.inflate(R.layout.history_guest, container, false);
            return viewGuest;
        }
        getUserHistory(view, null);

        // Display mode switch only for driver
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String uid = User.getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() &&  snapshot.hasChild("driverID")){
                    tb1.setVisibility(View.VISIBLE);
                } else {
                    tb1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Show driver's customer past booking
                    appointmentModelClasses = new ArrayList<>();
                    tv_customer.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
                    tv_your.setTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_grey, null));
                    tv_customer.setTypeface(null, Typeface.BOLD);
                    tv_your.setTypeface(null, Typeface.NORMAL);
                    getUserHistory(view, "customer");

                } else{
                    //Show driver's own past booking
                    appointmentModelClasses = new ArrayList<>();
                    tv_your.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
                    tv_customer.setTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_grey, null));
                    tv_your.setTypeface(null, Typeface.BOLD);
                    tv_customer.setTypeface(null, Typeface.NORMAL);
                    getUserHistory(view, "your");
                }
            }
        });

        return view;
    }

    public void getUserHistory(View view, String mode){
        HistoryFragment historyFragment = this;
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        String uid = User.getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("appointment");
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String driver_name = null, from_address = null, to_address = null, driver_id = null, date_time = null,
                        custom_request = null, user_id = null, status = null, username = null;
                if (snapshot.exists()){
                    for (DataSnapshot appointmentSnapshot : snapshot.getChildren()){
                        if (appointmentSnapshot.exists() && uid.equals(appointmentSnapshot.child("user_id").getValue(String.class)) && ("your".equals(mode) || mode == null)) {
                            driver_name = appointmentSnapshot.child("driver_name").getValue(String.class);
                            from_address = appointmentSnapshot.child("from_address").getValue(String.class);
                            to_address = appointmentSnapshot.child("to_address").getValue(String.class);
                            driver_id = appointmentSnapshot.child("driver_id").getValue(String.class);
                            date_time = appointmentSnapshot.child("date_time").getValue(String.class);
                            custom_request = appointmentSnapshot.child("custom_request").getValue(String.class);
                            user_id = appointmentSnapshot.child("user_id").getValue(String.class);
                            status = appointmentSnapshot.child("status").getValue(String.class);
                            username = appointmentSnapshot.child("username").getValue(String.class);
                            appointmentModelClasses.add(new AppointmentModelClass(from_address, to_address, driver_name, driver_id, date_time, custom_request, user_id, status, username));
                        } else if (appointmentSnapshot.exists() && uid.equals(appointmentSnapshot.child("driver_id").getValue(String.class)) &&
                                "customer".equals(mode) && (!appointmentSnapshot.child("status").getValue(String.class).equals("Pending"))){
                            driver_name = appointmentSnapshot.child("username").getValue(String.class);
                            from_address = appointmentSnapshot.child("from_address").getValue(String.class);
                            to_address = appointmentSnapshot.child("to_address").getValue(String.class);
                            driver_id = appointmentSnapshot.child("driver_id").getValue(String.class);
                            date_time = appointmentSnapshot.child("date_time").getValue(String.class);
                            custom_request = appointmentSnapshot.child("custom_request").getValue(String.class);
                            user_id = appointmentSnapshot.child("user_id").getValue(String.class);
                            status = appointmentSnapshot.child("status").getValue(String.class);
                            username = appointmentSnapshot.child("username").getValue(String.class);
                            appointmentModelClasses.add(new AppointmentModelClass(from_address, to_address, driver_name, driver_id, date_time, custom_request, user_id, status, username));
                        }

                    }

                    if (appointmentModelClasses.size() == 0){
                        tv_emp_hist.setVisibility(View.VISIBLE);
                    } else {
                        tv_emp_hist.setVisibility(View.GONE);
                    }

                    // Show the recyclerView
                    AppointmentAdapter adapter = new AppointmentAdapter(getContext(), appointmentModelClasses, null, historyFragment);
                    RecyclerView mRecyclerView = view.findViewById(R.id.recyclerViewHistory);
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // error
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onDeclineClick(int position) {

    }

    @Override
    public void onAcceptClick(int position) {

    }
}