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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sat
 * Time: 8:06 PM
 */
public class PastRidesAdapter extends RecyclerView.Adapter<PastRidesAdapter.ViewHolder> implements Filterable {


    private Context context;
    private List<Shipment> pastShipments;
    private List<Shipment> filteredPastShipments;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    public PastRidesAdapter(Context context, List<Shipment> pastShipments, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.pastShipments = pastShipments;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setPastShipments(List<Shipment> pastShipments) {
        if (this.pastShipments == null){
            this.pastShipments = pastShipments;
            this.filteredPastShipments = pastShipments;

            notifyItemChanged(0, filteredPastShipments.size());
        }else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return PastRidesAdapter.this.pastShipments.size();
                }

                @Override
                public int getNewListSize() {
                    return pastShipments.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return PastRidesAdapter.this.pastShipments.get(oldItemPosition).getShipmentId() == pastShipments.get(newItemPosition).getShipmentId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Shipment newPastShipment = PastRidesAdapter.this.pastShipments.get(oldItemPosition);
                    Shipment oldPastShipment = pastShipments.get(newItemPosition);
                    return newPastShipment.getShipmentId() == oldPastShipment.getShipmentId();

                }
            });
            this.pastShipments = pastShipments;
            this.filteredPastShipments = pastShipments;
            result.dispatchUpdatesTo(this);
        }

    }

    @NonNull
    @Override
    public PastRidesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.past_rides_row,parent,false);
        return new PastRidesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastRidesAdapter.ViewHolder holder, int position) {

        holder.mShipment.setText("#" + String.valueOf(filteredPastShipments.get(position).getShipmentId()));
        holder.mSenderName.setText(String.format("%s %s",filteredPastShipments.get(position).getSenderFirstName(), filteredPastShipments.get(position).getSenderFirstName()));
        holder.mName.setText(String.valueOf(filteredPastShipments.get(position).getReceiverName()));
        holder.mDropLocation.setText(String.valueOf(filteredPastShipments.get(position).getDropOffLocation()));
        holder.mPickLocation.setText(String.valueOf(filteredPastShipments.get(position).getPickupLocation()));
        if (filteredPastShipments.get(position).getDescription().isEmpty()) {

            holder.mDescription.setText("no description");
        } else {
            holder.mDescription.setText(String.valueOf(filteredPastShipments.get(position).getDescription()));
        }
        holder.mDropDate.setText("Dropped on: " + String.valueOf(filteredPastShipments.get(position).getCreatedAt()));
        holder.mCost.setText(String.valueOf(filteredPastShipments.get(position).getEstimatedPrice()));

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleOnHold(filteredPastShipments.get(position));
//
//            }
//        });
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredPastShipments = pastShipments;
                } else {
                    List<Shipment> filteredList = new ArrayList<>();
                    for (Shipment _shipment : pastShipments) {
                        //Search through ID and drop and pick location
                        if (String.valueOf(_shipment.getShipmentId()).contains(charString.toLowerCase()) || _shipment.getPickupLocation().toLowerCase().contains(charString.toLowerCase()) || _shipment.getDropOffLocation().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(_shipment);
                        }
                    }
                    filteredPastShipments = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredPastShipments;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredPastShipments = (ArrayList<Shipment>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        if (filteredPastShipments != null) return filteredPastShipments.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mShipment,mSenderName,mName,mPickLocation, mDropLocation,mDescription, mCost, mDropDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mShipment = itemView.findViewById(R.id.delivery_shipmentId);
            mSenderName = itemView.findViewById(R.id.sender_name);
            mName = itemView.findViewById(R.id.receiver_name);
            mPickLocation = itemView.findViewById(R.id.s_user_address);
            mDropLocation = itemView.findViewById(R.id.r_user_address);
            mDescription = itemView.findViewById(R.id.about_package);
            mCost = itemView.findViewById(R.id.cost);
            mDropDate = itemView.findViewById(R.id.drop_date);
        }
    }
}
