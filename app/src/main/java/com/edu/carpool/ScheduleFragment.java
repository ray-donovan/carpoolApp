package com.edu.carpool;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment implements DriverRecyclerViewInterface {
    ImageView iv_schedule, profilePic;
    ConstraintLayout get_driver_layout, selected_driver_layout;
    TextView tv_driver_selected_name, tv_get_driver, tv_nan;

    ArrayList<driverModelClass> driverModels = new ArrayList<>();
    ArrayList<UserModelClass> userModels = new ArrayList<>();

    private AlertDialog globalBuilder;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, driverRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_schedule, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Check whether user is logged in or not
        if (mAuth.getCurrentUser() == null) {
            // User is not signed in, show guest layout for user
                    // When the layout is clicked, when redirect user to guest schedule page
                View viewGuest =  inflater.inflate(R.layout.fragment_schedule_guest, container, false);

            Button loginRedirect = viewGuest.findViewById(R.id.login_button);
            Button signupRedirect = viewGuest.findViewById(R.id.signup_button);

            loginRedirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                }
            });

            signupRedirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Signup.class);
                    startActivity(intent);
                }
            });
                return viewGuest;
            }

        iv_schedule = view.findViewById(R.id.iv_schedule);
        get_driver_layout = view.findViewById(R.id.get_driver_layout);
        selected_driver_layout = view.findViewById(R.id.selected_driver_layout);
        tv_driver_selected_name = view.findViewById(R.id.tv_driver_selected_name);
        profilePic = view.findViewById(R.id.profilePic);
        tv_get_driver = view.findViewById(R.id.tv_get_driver);
        tv_nan = view.findViewById(R.id.tv_nan);
        tv_nan.setVisibility(View.VISIBLE);
        iv_schedule.setVisibility(View.GONE);

        // Show list of driver
        get_driver_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show list of available driver
                clickAnim(view);
                getAllDriver();
            }
        });

        // Zoom image on click
        iv_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                AlertDialog dialog = builder.create();
                //Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                TouchImageView imageView = new TouchImageView(getContext());
                imageView.setImageResource(R.drawable.sample_schedule);
                dialog.setView(imageView, 0,0,0,0);
                dialog.show();
                dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            }
        });
        return view;
    }

    public void getAllDriver(){
        // Clear the array list after showing the alert dialog box
        // To prevent the array list from having the same value
        driverModels = new ArrayList<>();
        userModels = new ArrayList<>();
        ScheduleFragment scheduleFragment = this;
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("users");
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot driverSnapshot : snapshot.getChildren()){
                    if (driverSnapshot.exists() && driverSnapshot.hasChild("driverID")){
                        String name = driverSnapshot.child("name").getValue(String.class);
                        String gender = driverSnapshot.child("gender").getValue(String.class);
                        String phoneNum = driverSnapshot.child("phoneNum").getValue(String.class);
                        String id = driverSnapshot.child("id").getValue(String.class);
                        for (DataSnapshot driverInfo : driverSnapshot.getChildren()){
                            for (DataSnapshot driverInnerInfo : driverInfo.getChildren()){
                                String carModel = driverInnerInfo.child("carModel").getValue(String.class);
                                String carColour = driverInnerInfo.child("carColour").getValue(String.class);
                                String carPlate = driverInnerInfo.child("carPlateNum").getValue(String.class);

                                userModels.add(new UserModelClass(id, name, gender, phoneNum, null, null));
                                driverModels.add(new driverModelClass(null, null, carPlate, carModel, carColour));
                            }
                            break;
                        }
                    }
                }
                // Create the custom alert dialog box with the recycler view
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.MaterialAlertDialog_rounded2);
                View viewCustomTitle = getActivity().getLayoutInflater().inflate(R.layout.title_bar, null);
                View viewRecycler = getActivity().getLayoutInflater().inflate(R.layout.recycler_parent, null);
                builder.setCustomTitle(viewCustomTitle);


                // Attach the adapter to the recycler view
                DriverAdapter adapter = new DriverAdapter(getContext(), driverModels, userModels, scheduleFragment);
                RecyclerView recyclerView = viewRecycler.findViewById(R.id.mRecyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                builder.setView(viewRecycler);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Assign dialog box to global builder so that it can be closed programmatically in other method
                globalBuilder = alertDialog;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // error
            }
        });
    }

    public void clickAnim(View view){
        Animator scale = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.1f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.1f, 1)
        );
        scale.setDuration(200);
        scale.start();
    }

    @Override
    public void onItemClick(int position) {
        tv_get_driver.setVisibility(View.GONE);
        selected_driver_layout.setVisibility(View.VISIBLE);

        String gender = userModels.get(position).getGender();

        if ("Female".equals(gender)){
            profilePic.setImageResource(R.drawable.female);
        } else if ("Male".equals(gender)){
            profilePic.setImageResource(R.drawable.male);
        } else {
            profilePic.setImageResource(R.drawable.ic_baseline_person_24);
        }

        tv_driver_selected_name.setText(userModels.get(position).getName().toString());
        globalBuilder.dismiss();

        // Check whether the driver has timetable or not
        // If have, show timetable
        // If not, show "Not Available" text to the user
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userModels.get(position).getID()).child("driverID");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot driverSnapshot : snapshot.getChildren()){
                        String driverId = driverSnapshot.getKey();
                        driverRef = userRef.child(driverId).child("imagesDetails");
                        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot imageSnapshot : snapshot.getChildren()) {
                                        String timetableURLFromDB = imageSnapshot.child("timetableUrl").getValue(String.class);
                                        String timetableSnapURLFromDB = imageSnapshot.child("timetableByte").getValue(String.class);
                                        if (timetableURLFromDB == null){
                                            iv_schedule.setVisibility(View.GONE);
                                            tv_nan.setVisibility(View.VISIBLE);
                                        } else {
                                            iv_schedule.setVisibility(View.VISIBLE);
                                            tv_nan.setVisibility(View.GONE);
                                            Picasso.get().load(timetableURLFromDB).into(iv_schedule);
                                        }
                                    }
                                } else {
                                    iv_schedule.setVisibility(View.GONE);
                                    tv_nan.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}