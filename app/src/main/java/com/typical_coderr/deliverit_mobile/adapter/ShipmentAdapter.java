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

public class ShipmentAdapter extends RecyclerView.Adapter<ShipmentAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Shipment> shipments;
    private List<Shipment> filteredShipments;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    public ShipmentAdapter(Context context, List<Shipment> shipments, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.shipments = shipments;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setShipments(final List<Shipment> shipments) {
        if (this.shipments == null) {
            this.shipments = shipments;
            this.filteredShipments = shipments;

            notifyItemChanged(0, filteredShipments.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return ShipmentAdapter.this.shipments.size();
                }

                @Override
                public int getNewListSize() {
                    return shipments.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return ShipmentAdapter.this.shipments.get(oldItemPosition).getShipmentId() == shipments.get(newItemPosition).getShipmentId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Shipment newShipment = ShipmentAdapter.this.shipments.get(oldItemPosition);

                    Shipment oldShipment = shipments.get(newItemPosition);

                    return newShipment.getShipmentId() == oldShipment.getShipmentId();
                }
            });
            this.shipments = shipments;
            this.filteredShipments = shipments;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override

    public ShipmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        //drriver
        if (userRole.equals("driver")){
           view = LayoutInflater.from(context).inflate(R.layout.shipment_driver_row, parent, false);

        }
        //customer
        else {
            view = LayoutInflater.from(context).inflate(R.layout.shipments_customer_row, parent, false);
        }
        return new ShipmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShipmentAdapter.ViewHolder holder, final int position) {
        holder.mShipment.setText(String.valueOf(filteredShipments.get(position).getShipmentId()));
        holder.mDestination.setText(String.valueOf(filteredShipments.get(position).getDropOffLocation()));

        if(userRole.equals("driver")){

            holder.mArrival.setText(String.valueOf(filteredShipments.get(position).getArrival()));
        }

        if(userRole.equals("customer")){
            holder.mStatus.setText(String.valueOf(filteredShipments.get(position).getStatus()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    public int getItemCount() {
        if (filteredShipments != null) return filteredShipments.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredShipments = shipments;
                } else {
                    List<Shipment> filteredList = new ArrayList<>();
                    for (Shipment shipment : shipments) {
                        String searchString = charString.toLowerCase();

                    }
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredShipments = (ArrayList<Shipment>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mShipment, mDestination ,mStatus,mArrival;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mShipment = itemView.findViewById(R.id.shipmentId);
            mDestination = itemView.findViewById(R.id.arrival);
            mStatus = itemView.findViewById(R.id.status);
            mArrival = itemView.findViewById(R.id.drop_off);
        }
    }
}
