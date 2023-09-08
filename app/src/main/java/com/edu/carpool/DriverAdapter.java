package com.edu.carpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.MyViewHolder> {
    private final DriverRecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<driverModelClass> driverModels;
    ArrayList<UserModelClass> userModels;

    public DriverAdapter(Context context, ArrayList<driverModelClass> driverModels, ArrayList<UserModelClass> userModels, DriverRecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.driverModels = driverModels;
        this.userModels = userModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    public DriverAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This is where you inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.driver_info, parent, false);
        return new DriverAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(DriverAdapter.MyViewHolder holder, int position) {
        // assigning values to the views we created in the recycler view layout file
        // based on the position of the recycler view

        String gender = userModels.get(position).getGender();
        String curContext = recyclerViewInterface.getClass().toString();
        if ("Female".equals(gender)){
            holder.profilePic.setImageResource(R.drawable.female);
            holder.profilePic2.setImageResource(R.drawable.female);
        } else if ("Male".equals(gender)){
            holder.profilePic.setImageResource(R.drawable.male);
            holder.profilePic2.setImageResource(R.drawable.male);
        } else {
            holder.profilePic.setImageResource(R.drawable.ic_baseline_person_24);
        }

        holder.tv_driver_name.setText(userModels.get(position).getName());
        holder.tv_driver_name_sche.setText(userModels.get(position).getName());
        holder.tv_car_model.setText(driverModels.get(position).getCarModel());
        holder.tv_car_colour.setText(driverModels.get(position).getCarColour());
        holder.tv_car_plate.setText(driverModels.get(position).getCarPlateNum());

        if (curContext.contains("ScheduleFragment")){
            holder.cardview2.setVisibility(View.VISIBLE);
            holder.cardview1.setVisibility(View.GONE);

        } else {
            holder.cardview2.setVisibility(View.GONE);
            holder.cardview1.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        // How many items we have in total
        return driverModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // ImageView profilePic;
        // profilePic not implemented yet, might be done in future

        TextView tv_driver_name, tv_car_model, tv_car_colour, tv_car_plate, tv_driver_name_sche;
        ImageView profilePic, profilePic2;
        CardView cardview1, cardview2;

        public MyViewHolder(View itemView, DriverRecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            tv_driver_name = itemView.findViewById(R.id.tv_driver_name);
            tv_car_model = itemView.findViewById(R.id.input_car_model);
            tv_car_colour = itemView.findViewById(R.id.input_car_colour);
            tv_car_plate = itemView.findViewById(R.id.input_car_plate);
            profilePic = itemView.findViewById(R.id.profilePic);
            cardview1 = itemView.findViewById(R.id.cardview1);
            cardview2 = itemView.findViewById(R.id.cardview2);
            tv_driver_name_sche = itemView.findViewById(R.id.tv_driver_name_sche);
            profilePic2 = itemView.findViewById(R.id.profilePic2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
