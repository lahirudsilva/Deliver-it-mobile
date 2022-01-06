package com.typical_coderr.deliverit_mobile.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.typical_coderr.deliverit_mobile.R;
import com.typical_coderr.deliverit_mobile.ShipmentScheduleActivity;
import com.typical_coderr.deliverit_mobile.model.ShipmentSchedule;
import com.typical_coderr.deliverit_mobile.provider.ShipmentScheduleContentProvider;

import java.util.List;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Wed
 * Time: 9:59 PM
 */
public class ShipmentScheduleAdapter extends RecyclerView.Adapter<ShipmentScheduleAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<ShipmentSchedule> shipmentSchedules;
    private List<ShipmentSchedule> filteredShipmentSchedules;
    private ProgressDialog mProgressDialog;

    public ShipmentScheduleAdapter(Context context, List<ShipmentSchedule> shipmentSchedules, ProgressDialog mProgressDialog) {
        this.context = context;
        this.shipmentSchedules = shipmentSchedules;
        this.mProgressDialog = mProgressDialog;
    }

    public void setShipmentSchedules(final List<ShipmentSchedule> shipmentSchedules) {
        if (this.shipmentSchedules == null) {
            this.shipmentSchedules = shipmentSchedules;
            this.filteredShipmentSchedules = shipmentSchedules;
            //Alert a change in items
            notifyItemChanged(0, filteredShipmentSchedules.size());
        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return ShipmentScheduleAdapter.this.shipmentSchedules.size();
                }

                @Override
                public int getNewListSize() {
                    return shipmentSchedules.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return ShipmentScheduleAdapter.this.shipmentSchedules.get(oldItemPosition).getShipmentId() == shipmentSchedules.get(newItemPosition).getShipmentId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ShipmentSchedule newShipmentSchedule = ShipmentScheduleAdapter.this.shipmentSchedules.get(oldItemPosition);

                    ShipmentSchedule oldShipmentSchedule = shipmentSchedules.get(newItemPosition);

                    return newShipmentSchedule.getShipmentId() == oldShipmentSchedule.getShipmentId();
                }
            });
            this.shipmentSchedules = shipmentSchedules;
            this.filteredShipmentSchedules = shipmentSchedules;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public ShipmentScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shipment_schedule_row, parent, false);
        return new ShipmentScheduleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShipmentScheduleAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mShipment.setText("#" + String.valueOf(filteredShipmentSchedules.get(position).getShipmentId()));
        holder.mSenderName.setText(String.format("%s %s", filteredShipmentSchedules.get(position).getSenderFirstName(), filteredShipmentSchedules.get(position).getSenderLastName()));
        holder.mName.setText(String.valueOf(filteredShipmentSchedules.get(position).getReceiverName()));
        holder.mPickLocation.setText(String.valueOf(filteredShipmentSchedules.get(position).getPickupLocation()));
        holder.mDropLocation.setText(String.valueOf(filteredShipmentSchedules.get(position).getDropOffLocation()));
        holder.mDropDate.setText("Expected on "+String.valueOf(filteredShipmentSchedules.get(position).getArrival()));
        if (filteredShipmentSchedules.get(position).getDescription().isEmpty()) {

            holder.mDescription.setText("no description");
        } else {
            holder.mDescription.setText(String.valueOf(filteredShipmentSchedules.get(position).getDescription()));
        }
        //Change icon color on priority
        if (filteredShipmentSchedules.get(position).getPriority().equals("high")){
            holder.mImageView.setColorFilter(ContextCompat.getColor(context, R.color.buttonRed), android.graphics.PorterDuff.Mode.SRC_IN);
        }


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                handleOnHold(filteredShipmentSchedules.get(position));
                return false;
            }
        });
    }

    private void handleOnHold(ShipmentSchedule shipmentSchedule) {

        MaterialAlertDialogBuilder option_builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
        option_builder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        option_builder.setTitle("Please select option");

        CharSequence[] options = new CharSequence[]{"Set as important", "Remove From Schedule"};
        String settablePriority = "high";

        //Change options with priority
        if (shipmentSchedule.getPriority().equals("high")) {
            options = new CharSequence[] {"Set as normal", "Remove bookmark"};
            settablePriority = "normal";
        }

        final String finalSettablePriority = settablePriority;
        option_builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //change selected category
                dialogInterface.dismiss();

                //Change priority
                if (i == 0) {
                    try {
                        changePriority(shipmentSchedule, finalSettablePriority);
                    } catch (Exception e) {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                //Remove shipment
                else {
                    try {
                        removeFromSchedule(shipmentSchedule);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        option_builder.show();
    }

    private void removeFromSchedule(ShipmentSchedule shipmentSchedule) {
        //Delete shipment
        context.getContentResolver().delete(ShipmentScheduleContentProvider.CONTENT_URI, "shipmentId=?", new String[]{String.valueOf(shipmentSchedule.getShipmentId())});
        Toast.makeText(context, "shipment deleted successfully!", Toast.LENGTH_SHORT).show();

        //Redirect to my schedule activity
        Intent intent = new Intent(context, ShipmentScheduleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void changePriority(ShipmentSchedule shipmentSchedule, String priority) {

        // class to add values in the database
        ContentValues values = new ContentValues();
        values.put(ShipmentScheduleContentProvider.PRIORITY, priority);

        //Update schedule
        context.getContentResolver().update(ShipmentScheduleContentProvider.CONTENT_URI, values, "shipmentId=?", new String[]{String.valueOf(shipmentSchedule.getShipmentId())});
        Toast.makeText(context, "Priority changed successfully!", Toast.LENGTH_SHORT).show();

        //Redirect to schedules activity
        Intent intent = new Intent(context, ShipmentScheduleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    @Override
    public int getItemCount() {
        if (filteredShipmentSchedules != null) return filteredShipmentSchedules.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mShipment, mSenderName, mName, mPickLocation, mDropLocation, mDescription, mDropDate;
        ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.icon);
            mShipment = itemView.findViewById(R.id.ss_shipmentId);
            mDropLocation = itemView.findViewById(R.id.ss_r_user_address);
            mPickLocation = itemView.findViewById(R.id.ss_s_user_address);
            mSenderName = itemView.findViewById(R.id.ss_sender_name);
            mName = itemView.findViewById(R.id.ss_receiver_name);
            mDescription = itemView.findViewById(R.id.ss_about_package);
            mDropDate = itemView.findViewById(R.id.ss_drop_date);
        }
    }
}
