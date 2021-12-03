package com.typical_coderr.deliverit_mobile.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.typical_coderr.deliverit_mobile.R;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;

import java.util.List;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Fri
 * Time: 9:03 AM
 */
public class PickupDeliveryAdapter extends RecyclerView.Adapter<PickupDeliveryAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Shipment> pickupShipments;
    private List<Shipment> filteredPickupShipments;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    public PickupDeliveryAdapter(Context context, List<Shipment> pickupShipments, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.pickupShipments = pickupShipments;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setPickupShipments(final List<Shipment> pickupShipments){
        if (this.pickupShipments == null){
            this.pickupShipments =pickupShipments;
            this.filteredPickupShipments = pickupShipments;

            notifyItemChanged(0,filteredPickupShipments.size());
        }else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return PickupDeliveryAdapter.this.pickupShipments.size();
                }

                @Override
                public int getNewListSize() {
                    return pickupShipments.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return PickupDeliveryAdapter.this.pickupShipments.get(oldItemPosition).getShipmentId() == pickupShipments.get(newItemPosition).getShipmentId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Shipment newPickupShipment = PickupDeliveryAdapter.this.pickupShipments.get(oldItemPosition);
                    Shipment oldPickupShipment = pickupShipments.get(newItemPosition);
                    return newPickupShipment.getShipmentId() == oldPickupShipment.getShipmentId();
                }
            });
            this.pickupShipments = pickupShipments;
            this.filteredPickupShipments = pickupShipments;
            result.dispatchUpdatesTo(this);
        }
    }


    @NonNull
    @Override
    public PickupDeliveryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupDeliveryAdapter.ViewHolder holder,final int position) {
        holder.mShipment.setText("#"+String.valueOf(filteredPickupShipments.get(position).getShipmentId()));
        holder.mUserFirstname.setText(String.valueOf(filteredPickupShipments.get(position).getSenderFirstName()));
        holder.mUserLastname.setText(String.valueOf(filteredPickupShipments.get(position).getSenderLastName()));
        holder.mUserContactNumber.setText("+"+String.valueOf(filteredPickupShipments.get(position).getSenderContactNumber()));
        holder.mLocation.setText(String.valueOf(filteredPickupShipments.get(position).getPickupLocation()));
        if (filteredPickupShipments.get(position).getDescription().isEmpty()){

            holder.mDescription.setText("no description");
        }else {
            holder.mDescription.setText(String.valueOf(filteredPickupShipments.get(position).getDescription()));
        }
        holder.mPickDate.setText("Pickup: "+String.valueOf(filteredPickupShipments.get(position).getPickUp()));
        holder.mCost.setText(String.valueOf(filteredPickupShipments.get(position).getEstimatedPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        if (filteredPickupShipments != null) return filteredPickupShipments.size();
        return 0;
    }


    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mShipment, mUserFirstname, mUserLastname, mUserContactNumber, mLocation, mDescription,mCost,mPickDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mShipment = itemView.findViewById(R.id.pickup_shipmentId);
            mUserFirstname = itemView.findViewById(R.id.user_firstname);
            mUserLastname = itemView.findViewById(R.id.user_lastname);
            mUserContactNumber = itemView.findViewById(R.id.user_phone);
            mLocation = itemView.findViewById(R.id.user_address);
            mDescription = itemView.findViewById(R.id.about_package);
            mPickDate = itemView.findViewById(R.id.pick_date);
            mCost = itemView.findViewById(R.id.cost);

        }


    }
}
