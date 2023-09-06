package com.edu.carpool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>{
    private final AppointmentRecyclerViewInterface appointmentRecyclerViewInterface;

    Context context;
    ArrayList<AppointmentModelClass> appointmentModelClass;
    List<String> keyArr = new ArrayList<>();

    public AppointmentAdapter(Context context, ArrayList<AppointmentModelClass> appointmentModelClass, List<String> keyArr, AppointmentRecyclerViewInterface appointmentRecyclerViewInterface){
        this.context = context;
        this.appointmentModelClass = appointmentModelClass;
        this.appointmentRecyclerViewInterface = appointmentRecyclerViewInterface;
        this.keyArr = keyArr;
    }

    @Override
    public AppointmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This is where you inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.history_cardview, parent, false);
        return new AppointmentAdapter.MyViewHolder(view, appointmentRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(AppointmentAdapter.MyViewHolder holder, int position) {
        // assigning values to the views we created in the recycler view layout file
        // based on the position of the recycler view
        int padding_in_dp1 = 28;
        int padding_in_dp2 = 35;
        final float scale = context.getResources().getDisplayMetrics().density;
        int padding_in_px1 = (int) (padding_in_dp1 * scale + 0.5f);
        int padding_in_px2 = (int) (padding_in_dp2 * scale + 0.5f);

        String curContext = appointmentRecyclerViewInterface.getClass().toString();

        if (curContext.contains("DriverAppointment")){
            // If driver is looking at the appointment page, it will show the user's name
            // Driver appointment page
            holder.cc_history.setPadding(0, 0, 0, padding_in_px2);
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.tv_history_status_input.setVisibility(View.GONE);
            holder.tv_history_driver_name.setText(appointmentModelClass.get(holder.getAdapterPosition()).getUsername());
        } else {
            // If the user is looking at the history page, it will show the driver's name
            // User history page
            holder.cc_history.setPadding(0, 0, 0, padding_in_px1);
            holder.linearLayout.setVisibility(View.GONE);
            holder.tv_history_status_input.setVisibility(View.VISIBLE);
            holder.tv_history_driver_name.setText(appointmentModelClass.get(holder.getAdapterPosition()).getDriver_name());
        }
        holder.tv_history_from_input.setText(appointmentModelClass.get(holder.getAdapterPosition()).getFrom_address());
        holder.tv_history_to_input.setText(appointmentModelClass.get(holder.getAdapterPosition()).getTo_address());
        holder.tv_history_to_schedule_input.setText(appointmentModelClass.get(holder.getAdapterPosition()).getDate_time());
        if (appointmentModelClass.get(holder.getAdapterPosition()).getCustom_request() == null){
            holder.tr_custom_request.setVisibility(View.GONE);
        } else{
            holder.tr_custom_request.setVisibility(View.VISIBLE);
            holder.tv_history_custom_input.setText(appointmentModelClass.get(holder.getAdapterPosition()).getCustom_request());
        }
        holder.tv_history_status_input.setText(appointmentModelClass.get(holder.getAdapterPosition()).getStatus());
        if (appointmentModelClass.get(holder.getAdapterPosition()).getStatus().equals("Accepted")){
            // Status = Accepted - set background to green color
            holder.tv_history_status_input.setBackgroundColor(ContextCompat.getColor(context, R.color.ok_green));
            holder.tv_history_status_input.setTextColor(Color.WHITE);
        } else if (appointmentModelClass.get(holder.getAdapterPosition()).getStatus().equals("Declined")){
            // Status = Declined - set background to red color
            holder.tv_history_status_input.setBackgroundColor(ContextCompat.getColor(context, R.color.decline_red));
            holder.tv_history_status_input.setTextColor(Color.WHITE);
        } else{
            // Status = Pending - leave default background color
        }

        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus("Declined", keyArr.get(holder.getAdapterPosition()));
                Toast.makeText(context, "Appointment declined", Toast.LENGTH_SHORT).show();
                appointmentModelClass.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), appointmentModelClass.size());
                if (appointmentModelClass.size() == 0){
                    DriverAppointment driverAppointment = new DriverAppointment();
                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, driverAppointment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus("Accepted", keyArr.get(holder.getAdapterPosition()));
                Toast.makeText(context, "Appointment accepted", Toast.LENGTH_SHORT).show();
                appointmentModelClass.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), appointmentModelClass.size());
                if (appointmentModelClass.size() == 0){
                    DriverAppointment driverAppointment = new DriverAppointment();
                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, driverAppointment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // How many items we have in total
        return appointmentModelClass.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardview_container;
        Button btn_decline, btn_accept;
        TableRow tr_custom_request;
        TextView tv_history_driver_name, tv_history_from_input, tv_history_to_input, tv_history_to_schedule_input,
                tv_history_custom_input, tv_history_status_input;
        LinearLayout linearLayout;
        ConstraintLayout cc_history;
        public MyViewHolder(View itemView,AppointmentRecyclerViewInterface appointmentRecyclerViewInterface) {
            super(itemView);
            String context = appointmentRecyclerViewInterface.getClass().toString();
            tv_history_driver_name = itemView.findViewById(R.id.tv_history_driver_name);
            tv_history_from_input = itemView.findViewById(R.id.tv_history_from_input);
            tv_history_to_input = itemView.findViewById(R.id.tv_history_to_input);
            tv_history_to_schedule_input = itemView.findViewById(R.id.tv_history_to_schedule_input);
            tv_history_custom_input = itemView.findViewById(R.id.tv_history_custom_input);
            tv_history_status_input = itemView.findViewById(R.id.tv_history_status_input);
            tr_custom_request = itemView.findViewById(R.id.tr_custom_request);
            linearLayout = itemView.findViewById(R.id.ll_app_dec);
            cc_history = itemView.findViewById(R.id.cc_history);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_decline = itemView.findViewById(R.id.btn_decline);
            cardview_container = itemView.findViewById(R.id.cardview_container);

            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (appointmentRecyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            appointmentRecyclerViewInterface.onAcceptClick(pos);
                        }
                    }
                }
            });

            btn_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (appointmentRecyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            appointmentRecyclerViewInterface.onDeclineClick(pos);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (appointmentRecyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            appointmentRecyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }

    public void updateStatus(String status, String key){
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("appointment");
        DatabaseReference userRef = statusRef.child(key);
        userRef.child("status").setValue(status);

    }

}
