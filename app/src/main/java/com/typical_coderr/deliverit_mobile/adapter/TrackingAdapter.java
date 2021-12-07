package com.typical_coderr.deliverit_mobile.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.typical_coderr.deliverit_mobile.PackageTrackingActivity;
import com.typical_coderr.deliverit_mobile.R;
import com.typical_coderr.deliverit_mobile.enums.TrackingStatusType;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.model.Tracking;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.TrackingClient;

import java.util.ArrayList;
import java.util.List;

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Tracking> tacking;
    private List<Tracking> filteredTracking;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    TrackingClient trackingClient = RetrofitClientInstance.getRetrofitInstance().create(TrackingClient.class);

    public TrackingAdapter(Context context, List<Tracking> tacking, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.tacking = tacking;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setTacking(List<Tracking> tacking){
        if (this.tacking ==null){
            this.tacking =tacking;
            this.filteredTracking = tacking;

            notifyItemChanged(0, filteredTracking.size());
        }else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return TrackingAdapter.this.tacking.size();
                }

                @Override
                public int getNewListSize() {
                    return tacking.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return TrackingAdapter.this.tacking.get(oldItemPosition).getTrackingId() == tacking.get(newItemPosition).getTrackingId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Tracking newTracking = TrackingAdapter.this.tacking.get(oldItemPosition);
                    Tracking oldTracking = tacking.get(newItemPosition);
                    return newTracking.getTrackingId() == oldTracking.getTrackingId();

                }
            });
            this.tacking = tacking;
            this.filteredTracking = tacking;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public TrackingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tracking_row,parent,false);
        return new  TrackingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingAdapter.ViewHolder holder, int position) {
        holder.mTrackingID.setText("#"+String.valueOf(filteredTracking.get(position).getTrackingId()));
        holder.mShipmentID.setText("#"+String.valueOf(filteredTracking.get(position).getShipmentId()));
        holder.mPick.setText(String.valueOf(filteredTracking.get(position).getPick()));
        holder.mDrop.setText(String.valueOf(filteredTracking.get(position).getDrop()));
//        if (String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.PICKUP_IN_PROGRESS.getType())){
//            holder.mShipmentStatus.setText("Pickup In Progress");
//            holder.mInProgress.setVisibility(String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.PICKUP_IN_PROGRESS.getType()) ? View.VISIBLE : View.GONE);
//
//
//        }else if (String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.IN_WAREHOUSE.getType())){
//            holder.mShipmentStatus.setText("In Warehouse");
//            holder.mInWarehouse.setVisibility(String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.IN_WAREHOUSE.getType()) ? View.VISIBLE : View.GONE);
//
//        }else if (String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.OUT_FOR_DELIVERY.getType())){
//            holder.mShipmentStatus.setText("Out for Delivery");
//            holder.mOutForDelivery.setVisibility(String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.OUT_FOR_DELIVERY.getType()) ? View.VISIBLE : View.GONE);
//
//        }else if(String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.DELIVERED.getType())){
//            holder.mShipmentStatus.setText("Delivered");
//            holder.mDelivered.setVisibility(String.valueOf(filteredTracking.get(position).getShipmentStatus()).equals(TrackingStatusType.DELIVERED.getType()) ? View.VISIBLE : View.GONE);
//        }
        holder.mStatusDesc.setText(String.valueOf(filteredTracking.get(position).getShipmentStatus())+".");
        holder.mDriver.setText(String.format("%s %s", filteredTracking.get(position).getDriverFirstName(), filteredTracking.get(position).getDriverLastName()));
        holder.mPhone.setText(String.valueOf(filteredTracking.get(position).getDriverContactNumber()));
        holder.mVehicle.setText(String.valueOf(filteredTracking.get(position).getDriverVehicleNumber()));
        holder.mUpdated.setText(String.valueOf("Updated On "+filteredTracking.get(position).getUpdatedAt()));



    }



    @Override
    public Filter getFilter() {

        return new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    filteredTracking = tacking;
                } else {


                    List<Tracking> filteredList = new ArrayList<>();
                    for (Tracking _tracking : tacking) {
                        //Search through ID and drop and pick location
                        if (String.valueOf(_tracking.getTrackingId()).contains(charString.toLowerCase())  )  {
                            filteredList.add(_tracking);

                        }
                    }
                    filteredTracking =filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredTracking;
                return filterResults;




            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredTracking = (ArrayList<Tracking>) filterResults.values;
                notifyDataSetChanged();

            }
        };

    }



    @Override
    public int getItemCount() {
        if (filteredTracking != null) return filteredTracking.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTrackingID, mShipmentID,mPick,mDrop,mShipmentStatus,mStatusDesc,mDriver,mPhone,mVehicle,mUpdated;
//        LinearLayout mInProgress, mInWarehouse, mOutForDelivery, mDelivered;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTrackingID = itemView.findViewById(R.id.track_Id);
            mShipmentID = itemView.findViewById(R.id.track_shipmentId);
            mPick = itemView.findViewById(R.id.s_user_address);
            mDrop = itemView.findViewById(R.id.r_user_address);
            mShipmentStatus = itemView.findViewById(R.id.status);
            mStatusDesc = itemView.findViewById(R.id.status_dec);
            mDriver =itemView.findViewById(R.id.driver_name);
            mPhone = itemView.findViewById(R.id.driver_phone);
            mVehicle = itemView.findViewById(R.id.driver_vehicle);
            mUpdated = itemView.findViewById(R.id.update_at);

//            mInProgress = itemView.findViewById(R.id.tracking_bar_in_progress);
//            mInWarehouse = itemView.findViewById(R.id.tracking_bar_in_warehouse);
//            mOutForDelivery = itemView.findViewById(R.id.tracking_bar_out_for_delivery);
//            mDelivered = itemView.findViewById(R.id.tracking_bar_delivered);

        }
    }
}
