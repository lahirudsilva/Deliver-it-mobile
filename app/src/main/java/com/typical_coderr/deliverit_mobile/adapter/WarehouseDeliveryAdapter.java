package com.typical_coderr.deliverit_mobile.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.typical_coderr.deliverit_mobile.ManageDeliveryRidesActivity;
import com.typical_coderr.deliverit_mobile.R;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sat
 * Time: 2:45 PM
 */
public class WarehouseDeliveryAdapter extends RecyclerView.Adapter<WarehouseDeliveryAdapter.ViewHolder> implements Filterable {


    private Context context;
    private List<Shipment> warehouseShipments;
    private List<Shipment> filteredWarehouseShipments;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    public WarehouseDeliveryAdapter(Context context, List<Shipment> filteredWarehouseShipments, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.filteredWarehouseShipments = filteredWarehouseShipments;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setWarehouseShipments(final List<Shipment> warehouseShipments) {
        if (this.warehouseShipments == null) {
            this.warehouseShipments = warehouseShipments;
            this.filteredWarehouseShipments = warehouseShipments;

            notifyItemChanged(0, filteredWarehouseShipments.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return WarehouseDeliveryAdapter.this.warehouseShipments.size();
                }

                @Override
                public int getNewListSize() {
                    return warehouseShipments.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return WarehouseDeliveryAdapter.this.warehouseShipments.get(oldItemPosition).getShipmentId() == warehouseShipments.get(newItemPosition).getShipmentId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Shipment newWarehouseShipment = WarehouseDeliveryAdapter.this.warehouseShipments.get(oldItemPosition);
                    Shipment oldWarehouseShipment = warehouseShipments.get(newItemPosition);
                    return newWarehouseShipment.getShipmentId() == oldWarehouseShipment.getShipmentId();


                }
            });
            this.warehouseShipments = warehouseShipments;
            this.filteredWarehouseShipments = warehouseShipments;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public WarehouseDeliveryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.warehouse_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarehouseDeliveryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.mShipment.setText("#" + String.valueOf(filteredWarehouseShipments.get(position).getShipmentId()));
        holder.mWarehouse.setText(String.valueOf(filteredWarehouseShipments.get(position).getWarehouseLocation()));
        holder.mLocation.setText(String.valueOf(filteredWarehouseShipments.get(position).getDropOffLocation()));
        if (filteredWarehouseShipments.get(position).getDescription().isEmpty()) {

            holder.mDescription.setText("no description");
        } else {
            holder.mDescription.setText(String.valueOf(filteredWarehouseShipments.get(position).getDescription()));
        }
        holder.mDropDate.setText("Pickup: " + String.valueOf(filteredWarehouseShipments.get(position).getArrival()));
        holder.mCost.setText(String.valueOf(filteredWarehouseShipments.get(position).getEstimatedPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOnHold(filteredWarehouseShipments.get(position));

            }
        });
    }

    private void handleOnHold(Shipment shipment) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
        builder.setMessage("Package " + "#" + shipment.getShipmentId());
        builder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.setPositiveButton("Take to Deliver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Please confirm shipment ID is " + shipment.getShipmentId());
                builder.setNeutralButton("Drive", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<ResponseBody> call = shipmentClient.confirmOutForDelivery(jwtToken, shipment.getShipmentId());

                        //Show progress
                        mProgressDialog.setMessage("Taking for Delivery...");
                        mProgressDialog.show();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(context, "Successfully Updated pickup!", Toast.LENGTH_SHORT).show();


//                                    Intent intent = new Intent(context, ManageDeliveryRidesActivity.class);
//                                    context.startActivity(intent);
                                } else {
                                    try {

                                        // Capture an display specific messages
                                        JSONObject obj = new JSONObject(response.errorBody().string());
                                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                mProgressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                //When cancel button is clicked
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        builder.show();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mShipment, mWarehouse, mLocation, mDescription, mCost, mDropDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mShipment = itemView.findViewById(R.id.warehouse_shipmentId);
            mWarehouse = itemView.findViewById(R.id.warehouse);
            mLocation = itemView.findViewById(R.id.user_address);
            mDescription = itemView.findViewById(R.id.about_package);
            mCost = itemView.findViewById(R.id.cost);
            mDropDate = itemView.findViewById(R.id.drop_date);


        }
    }


    @Override
    public Filter getFilter() {
        return null;
    }


    @Override
    public int getItemCount() {
        if (filteredWarehouseShipments != null) return filteredWarehouseShipments.size();
        return 0;
    }


}
