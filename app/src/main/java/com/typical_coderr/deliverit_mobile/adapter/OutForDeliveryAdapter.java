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
 * Date: Sat
 * Time: 2:46 PM
 */
public class OutForDeliveryAdapter extends RecyclerView.Adapter<OutForDeliveryAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Shipment> outForDeliveryShipments;
    private List<Shipment> filteredOutForDeliveryShipments;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    public OutForDeliveryAdapter(Context context, List<Shipment> outForDeliveryShipments, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.outForDeliveryShipments = outForDeliveryShipments;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setOutForDeliveryShipments(final List<Shipment> outForDeliveryShipments){
        if (this.outForDeliveryShipments == null){
            this.outForDeliveryShipments = outForDeliveryShipments;
            this.filteredOutForDeliveryShipments = outForDeliveryShipments;

            notifyItemChanged(0, filteredOutForDeliveryShipments.size());
        }else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return OutForDeliveryAdapter.this.outForDeliveryShipments.size();
                }

                @Override
                public int getNewListSize() {
                    return outForDeliveryShipments.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return OutForDeliveryAdapter.this.outForDeliveryShipments.get(oldItemPosition).getShipmentId() == outForDeliveryShipments.get(newItemPosition).getShipmentId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Shipment newOutForDeliveryShipment = OutForDeliveryAdapter.this.outForDeliveryShipments.get(oldItemPosition);
                    Shipment oldOutForDeliveryShipment = outForDeliveryShipments.get(newItemPosition);
                    return newOutForDeliveryShipment.getShipmentId() == oldOutForDeliveryShipment.getShipmentId();
                }
            });
            this.outForDeliveryShipments = outForDeliveryShipments;
            this.filteredOutForDeliveryShipments = outForDeliveryShipments;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public OutForDeliveryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.out_for_delivery_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutForDeliveryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mShipment.setText("#" + String.valueOf(filteredOutForDeliveryShipments.get(position).getShipmentId()));
        holder.mName.setText(String.valueOf(filteredOutForDeliveryShipments.get(position).getReceiverName()));
        holder.mContact.setText(String.valueOf(filteredOutForDeliveryShipments.get(position).getReceiverContactNumber()));
        holder.mLocation.setText(String.valueOf(filteredOutForDeliveryShipments.get(position).getDropOffLocation()));
        if (filteredOutForDeliveryShipments.get(position).getDescription().isEmpty()) {

            holder.mDescription.setText("no description");
        } else {
            holder.mDescription.setText(String.valueOf(filteredOutForDeliveryShipments.get(position).getDescription()));
        }
        holder.mDropDate.setText("Pickup: " + String.valueOf(filteredOutForDeliveryShipments.get(position).getArrival()));
        holder.mCost.setText(String.valueOf(filteredOutForDeliveryShipments.get(position).getEstimatedPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOnHold(filteredOutForDeliveryShipments.get(position));

            }
        });


    }

    private void handleOnHold(Shipment shipment) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
        builder.setMessage("Package " + "#" + shipment.getShipmentId());
        builder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.setNeutralButton("Delivered", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Please confirm delivered shipment ID is " + shipment.getShipmentId());
                builder.setNeutralButton("Confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<ResponseBody> call = shipmentClient.confirmPackageDelivered(jwtToken, shipment.getShipmentId());

                        //Show progress
                        mProgressDialog.setMessage("Package Delivered...");
                        mProgressDialog.show();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(context, "Successfully delivered package!", Toast.LENGTH_SHORT).show();


//                                    Intent intent = new Intent(context, ManageDeliveryRidesActivity.class);
//                                    context.startActivity(intent);
                                }else{
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mShipment,mName,mContact,mLocation,mDescription, mCost, mDropDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mShipment = itemView.findViewById(R.id.delivery_shipmentId);
            mName = itemView.findViewById(R.id.user_fullName);
            mLocation = itemView.findViewById(R.id.user_address);
            mContact =itemView.findViewById(R.id.user_phone);
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
        if (filteredOutForDeliveryShipments != null) return filteredOutForDeliveryShipments.size();
        return 0;
    }


}
