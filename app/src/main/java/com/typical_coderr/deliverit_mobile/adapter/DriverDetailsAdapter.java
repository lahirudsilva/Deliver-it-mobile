package com.typical_coderr.deliverit_mobile.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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
import com.typical_coderr.deliverit_mobile.enums.DriverStatusType;
import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;

import java.util.List;

public class DriverDetailsAdapter extends RecyclerView.Adapter<DriverDetailsAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<DriverDetails> drivers;
    private List<DriverDetails> filteredDrivers;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    DriverDetailsClient driverDetailsClient = RetrofitClientInstance.getRetrofitInstance().create(DriverDetailsClient.class);

    public DriverDetailsAdapter(Context context, List<DriverDetails> drivers, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.drivers = drivers;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setDrivers(List<DriverDetails> drivers){
        if (this.drivers ==null){
            this.drivers =drivers;
            this.filteredDrivers = drivers;

            notifyItemChanged(0, filteredDrivers.size());
        }else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return DriverDetailsAdapter.this.drivers.size();
                }

                @Override
                public int getNewListSize() {
                    return drivers.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return DriverDetailsAdapter.this.drivers.get(oldItemPosition).getDriverId() == drivers.get(newItemPosition).getDriverId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    DriverDetails newDriver = DriverDetailsAdapter.this.drivers.get(oldItemPosition);
                    DriverDetails oldDriver = drivers.get(newItemPosition);
                    return newDriver.getDriverId() == oldDriver.getDriverId();

                }
            });
            this.drivers = drivers;
            this.filteredDrivers = drivers;
            result.dispatchUpdatesTo(this);
        }
    }



    @NonNull
    @Override
    public DriverDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drivers_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverDetailsAdapter.ViewHolder holder, int position) {

        holder.mName.setText(String.format("%s %s", filteredDrivers.get(position).getDriverFirstName(), filteredDrivers.get(position).getDriverLastName()));
        holder.mEmail.setText(String.valueOf(filteredDrivers.get(position).getDriverEmail()));
        holder.mContact.setText(String.valueOf(filteredDrivers.get(position).getContactNumber()));
        holder.mTown.setText(String.valueOf(filteredDrivers.get(position).getTown()));
        holder.mDriverID.setText(String.valueOf(filteredDrivers.get(position).getDriverId()));

        if(filteredDrivers.get(position).getStatus().contains(DriverStatusType.AVAILABLE.getType())){
            holder.mDriverStatus.setText(String.valueOf(filteredDrivers.get(position).getStatus()));
        }else if(filteredDrivers.get(position).getStatus().contains(DriverStatusType.ASSIGN_SLOTS_FULL.getType())){
            holder.mDriverStatus.setTextColor(Color.BLUE);
            holder.mDriverStatus.setText(String.valueOf(filteredDrivers.get(position).getStatus()));
        }else {
            holder.mDriverStatus.setTextColor(Color.RED);
            holder.mDriverStatus.setText(String.valueOf(filteredDrivers.get(position).getStatus()));
        }
        holder.mWarehouse.setText(String.valueOf(filteredDrivers.get(position).getWarehouseLocation()));
        holder.mNIC.setText(String.valueOf(filteredDrivers.get(position).getIdNumber()));
        holder.mVehicle.setText(String.valueOf(filteredDrivers.get(position).getVehicleNumber()));
        holder.mRegisterDate.setText("Registered On "+String.valueOf(filteredDrivers.get(position).getRegisteredOn()));


    }


    @Override
    public int getItemCount() {
        if (filteredDrivers != null) return filteredDrivers.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mDriverID, mDriverStatus, mName, mEmail, mContact, mTown,mWarehouse, mNIC, mVehicle, mRegisterDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mDriverID = itemView.findViewById(R.id.driver_id);
            mDriverStatus = itemView.findViewById(R.id.driverStatus);
            mName = itemView.findViewById(R.id.driver_name);
            mEmail = itemView.findViewById(R.id.driver_email);
            mContact = itemView.findViewById(R.id.driver_contact);
            mTown = itemView.findViewById(R.id.driver_town);
            mWarehouse = itemView.findViewById(R.id.driver_warehouse);
            mNIC = itemView.findViewById(R.id.driver_nic);
            mVehicle = itemView.findViewById(R.id.driver_vehicle);
            mRegisterDate = itemView.findViewById(R.id.registeredOn);
        }
    }
}
