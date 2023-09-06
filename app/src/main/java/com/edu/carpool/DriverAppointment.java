package com.edu.carpool;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DriverAppointment extends Fragment implements AppointmentRecyclerViewInterface{
    // Store instances of the appointment
    ArrayList<AppointmentModelClass> appointmentModelClasses = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_appointment, container, false);

        getDriverAppointment(view);
        // Let the driver choose either to accept or decline the appointment made by the user
        return view;
    }

    public void getDriverAppointment(View view){
        DriverAppointment driverAppointment = this;
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

        // UID is the current driver's id, only show driver's specific appointment
        String uid = User.getUid();
        DatabaseReference driverAppRef = FirebaseDatabase.getInstance().getReference("appointment");
        driverAppRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot appointmentSnapshot : snapshot.getChildren()){
                        if (appointmentSnapshot.exists() && uid.equals(appointmentSnapshot.child("driver_id").getValue(String.class))
                        && ("Pending").equals(appointmentSnapshot.child("status").getValue(String.class))) {
                            String from_address = appointmentSnapshot.child("from_address").getValue(String.class);
                            String to_address = appointmentSnapshot.child("to_address").getValue(String.class);
                            String driver_name = appointmentSnapshot.child("driver_name").getValue(String.class);
                            String driver_id = appointmentSnapshot.child("driver_id").getValue(String.class);
                            String date_time = appointmentSnapshot.child("date_time").getValue(String.class);
                            String custom_request = appointmentSnapshot.child("custom_request").getValue(String.class);
                            String user_id = appointmentSnapshot.child("user_id").getValue(String.class);
                            String status = appointmentSnapshot.child("status").getValue(String.class);
                            appointmentModelClasses.add(new AppointmentModelClass(from_address, to_address, driver_name, driver_id, date_time, custom_request, user_id, status));
                        }
                    }
                    // Show the recyclerView
                    AppointmentAdapter adapter = new AppointmentAdapter(getContext(), appointmentModelClasses, driverAppointment);
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
}