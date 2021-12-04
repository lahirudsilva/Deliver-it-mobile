package com.typical_coderr.deliverit_mobile.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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


    public void setPickupShipments(final List<Shipment> pickupShipments) {
        if (this.pickupShipments == null) {
            this.pickupShipments = pickupShipments;
            this.filteredPickupShipments = pickupShipments;

            notifyItemChanged(0, filteredPickupShipments.size());
        } else {
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
        View view = LayoutInflater.from(context).inflate(R.layout.pickup_delivery_row, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PickupDeliveryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.mShipment.setText("#" + String.valueOf(filteredPickupShipments.get(position).getShipmentId()));
        holder.mSender.setText(String.format("%s %s", filteredPickupShipments.get(position).getSenderFirstName(), filteredPickupShipments.get(position).getSenderLastName()));
        holder.mUserContactNumber.setText("+" + String.valueOf(filteredPickupShipments.get(position).getSenderContactNumber()));
        holder.mLocation.setText(String.valueOf(filteredPickupShipments.get(position).getPickupLocation()));
        if (filteredPickupShipments.get(position).getDescription().isEmpty()) {

            holder.mDescription.setText("no description");
        } else {
            holder.mDescription.setText(String.valueOf(filteredPickupShipments.get(position).getDescription()));
        }
        holder.mPickDate.setText("Pickup: " + String.valueOf(filteredPickupShipments.get(position).getPickUp()));
        holder.mCost.setText(String.valueOf(filteredPickupShipments.get(position).getEstimatedPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOnHold(filteredPickupShipments.get(position));

            }
        });
    }


    private void handleOnHold(final Shipment shipment) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme );
        builder.setMessage("Package " +"#"+ shipment.getShipmentId());
        builder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.setNeutralButton("Confirm Pickup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context );
                builder.setTitle("Pickup Confirmation");
                builder.setMessage("Please confirm picked up shipment ID is " + shipment.getShipmentId());

                //When "Confirm" button is clicked
                builder.setNeutralButton("Confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<ResponseBody> call = shipmentClient.confirmPickup(jwtToken, shipment.getShipmentId());

                        //Show progress
                        mProgressDialog.setMessage("Pickup Confirming...");
                        mProgressDialog.show();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                //Successfully pickup updated
                                if (response.code() == 200) {
                                    Toast.makeText(context, "Successfully Updated pickup!", Toast.LENGTH_SHORT).show();


                                    Intent intent = new Intent(context, ManageDeliveryRidesActivity.class);
                                    context.startActivity(intent);

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
        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + shipment.getSenderContactNumber()));
                context.startActivity(intent);
            }
        });
        builder.show();


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
        TextView mShipment, mSender, mUserContactNumber, mLocation, mDescription, mCost, mPickDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mShipment = itemView.findViewById(R.id.pickup_shipmentId);
            mSender = itemView.findViewById(R.id.user_fullName);
            mUserContactNumber = itemView.findViewById(R.id.user_phone);
            mLocation = itemView.findViewById(R.id.user_address);
            mDescription = itemView.findViewById(R.id.about_package);
            mPickDate = itemView.findViewById(R.id.pick_date);
            mCost = itemView.findViewById(R.id.cost);

        }


    }


}
