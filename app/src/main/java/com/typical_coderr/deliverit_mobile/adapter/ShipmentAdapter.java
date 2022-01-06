package com.typical_coderr.deliverit_mobile.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.typical_coderr.deliverit_mobile.AssignDriverActivity;
import com.typical_coderr.deliverit_mobile.ManageDeliveryRidesActivity;
import com.typical_coderr.deliverit_mobile.OnGoingShipmentsActivity;
import com.typical_coderr.deliverit_mobile.R;
import com.typical_coderr.deliverit_mobile.ResponseInquiryActivity;
import com.typical_coderr.deliverit_mobile.SupervisorHomeActivity;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.provider.ShipmentScheduleContentProvider;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShipmentAdapter extends RecyclerView.Adapter<ShipmentAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Shipment> shipments;
    private List<Shipment> filteredShipments;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    private SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

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
        if (userRole.equals("driver")) {
            view = LayoutInflater.from(context).inflate(R.layout.shipment_driver_row, parent, false);

        } else if (userRole.equals("supervisor")) {
            view = LayoutInflater.from(context).inflate(R.layout.package_requests_row, parent, false);
        }
        //customer
        else {
            view = LayoutInflater.from(context).inflate(R.layout.shipments_customer_row, parent, false);
        }
        return new ShipmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShipmentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.mShipment.setText(String.valueOf("#" + filteredShipments.get(position).getShipmentId()));
        holder.mDestination.setText(String.valueOf(filteredShipments.get(position).getDropOffLocation()));

        if (userRole.equals("driver")) {

            holder.mArrival.setText(String.valueOf(filteredShipments.get(position).getArrival()));
        }

        if (userRole.equals("customer")) {
            holder.mStatus.setText(String.valueOf(filteredShipments.get(position).getStatus()));
        }

        if (userRole.equals("supervisor")) {
            holder.mPickLocation.setText(String.valueOf(filteredShipments.get(position).getPickupLocation()));
            holder.mSenderName.setText(String.format("%s %s", filteredShipments.get(position).getSenderFirstName(), filteredShipments.get(position).getSenderLastName()));
            holder.mReceiverName.setText(String.valueOf(filteredShipments.get(position).getReceiverName()));
            if (filteredShipments.get(position).getDescription().isEmpty()) {

                holder.mDescription.setText("no description");
            } else {
                holder.mDescription.setText(String.valueOf(filteredShipments.get(position).getDescription()));
            }
            holder.mCost.setText(String.valueOf(filteredShipments.get(position).getEstimatedPrice()));
            holder.mSize.setText(String.valueOf(filteredShipments.get(position).getSize()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleOnHold(filteredShipments.get(position));
                }
            });
        }

        if (userRole.equals("driver")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    handleDriverOnHold(filteredShipments.get(position));
                    return false;
                }
            });
        }


    }

    private void handleDriverOnHold(final Shipment shipment) {


        MaterialAlertDialogBuilder option_builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme );
        option_builder.setTitle("Please select option");
        option_builder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        option_builder.setNeutralButton("Add to Delivery Schedule",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //change selected category
                dialogInterface.dismiss();
                addToDeliveryScheduler(shipment);
            }
        });
        option_builder.show();

    }

    private void addToDeliveryScheduler(Shipment shipment) {
        ContentValues values = new ContentValues();

        System.out.println(shipment.getDriverEmail());

        // Create values
        values.put(ShipmentScheduleContentProvider.SHIPMENT_ID, shipment.getShipmentId());
        values.put(ShipmentScheduleContentProvider.PICKUP_LOCATION, shipment.getPickupLocation());
        values.put(ShipmentScheduleContentProvider.PICKUP_DATE, shipment.getPickUp());
        values.put(ShipmentScheduleContentProvider.DROP_LOCATION, shipment.getDropOffLocation());
        values.put(ShipmentScheduleContentProvider.DROP_DATE, shipment.getArrival());
        values.put(ShipmentScheduleContentProvider.RECEIVER_CONTACT, shipment.getReceiverContactNumber());
        values.put(ShipmentScheduleContentProvider.SENDER_CONTACT, shipment.getSenderContactNumber());
        values.put(ShipmentScheduleContentProvider.DESCRIPTION, shipment.getDescription());
        values.put(ShipmentScheduleContentProvider.SENDER_FIRSTNAME, shipment.getSenderFirstName());
        values.put(ShipmentScheduleContentProvider.SENDER_LASTNAME, shipment.getSenderLastName());
        values.put(ShipmentScheduleContentProvider.DRIVER, shipment.getDriverEmail());
        values.put(ShipmentScheduleContentProvider.RECEIVER_NAME, shipment.getReceiverName());
        values.put(ShipmentScheduleContentProvider.PRIORITY, "normal");


        try {

            // inserting into database through content URI
            context.getContentResolver().insert(ShipmentScheduleContentProvider.CONTENT_URI, values);

            // displaying a toast message
            Toast.makeText(context, "Added to Shipment schedule!", Toast.LENGTH_LONG).show();

        }catch(SQLiteException ex){
            Toast.makeText(context, "Shipment already added!", Toast.LENGTH_LONG).show();
        }
    }

    private void handleOnHold(final Shipment shipment) {


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
//        LinearLayout layout = new LinearLayout(context);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        final DatePicker datePicker = new DatePicker(context);
//        layout.addView(datePicker);
//
//        final EditText descriptionBox = new EditText(context);
//        descriptionBox.setHint("Description");
//        layout.addView(descriptionBox);

        builder.setMessage("Package " + "#" + shipment.getShipmentId());
        builder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.setNeutralButton("Assign Driver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme );
//                builder.setTitle("select a pick up date");
////                builder.setView(layout);
//                builder.show();
                Intent in = new Intent(context, AssignDriverActivity.class);
                in.putExtra("ShipmentId", shipment.getShipmentId().toString());
                context.startActivity(in);
            }
        });
        builder.show();

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
        TextView mShipment, mDestination, mStatus, mArrival, mSenderName, mReceiverName, mPickLocation, mDescription, mCost, mSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mShipment = itemView.findViewById(R.id.shipmentId);
            mDestination = itemView.findViewById(R.id.arrival);
            mStatus = itemView.findViewById(R.id.status);
            mArrival = itemView.findViewById(R.id.drop_off);
            mPickLocation = itemView.findViewById(R.id.s_user_address);
            mSenderName = itemView.findViewById(R.id.sender_name);
            mReceiverName = itemView.findViewById(R.id.receiver_name);
            mDescription = itemView.findViewById(R.id.about_package);
            mCost = itemView.findViewById(R.id.cost);
            mSize = itemView.findViewById(R.id.package_size);

        }
    }
}
