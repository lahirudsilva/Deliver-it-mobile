package com.typical_coderr.deliverit_mobile.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.typical_coderr.deliverit_mobile.AssignDriverActivity;
import com.typical_coderr.deliverit_mobile.R;
import com.typical_coderr.deliverit_mobile.ResponseInquiryActivity;
import com.typical_coderr.deliverit_mobile.model.Inquiry;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.InquiryClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Thu
 * Time: 10:33 AM
 */
public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Inquiry> inquiries;
    private List<Inquiry> filteredInquiries;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    InquiryClient inquiryClient = RetrofitClientInstance.getRetrofitInstance().create(InquiryClient.class);


    public InquiryAdapter(Context context, List<Inquiry> inquiries, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.inquiries = inquiries;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setInquiries(List<Inquiry> inquiries){
        if (this.inquiries == null){
            this.inquiries = inquiries;
            this.filteredInquiries = inquiries;

            notifyItemChanged(0, filteredInquiries.size());
        }else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return InquiryAdapter.this.inquiries.size();
                }

                @Override
                public int getNewListSize() {
                    return inquiries.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return InquiryAdapter.this.inquiries.get(oldItemPosition).getInquiryId() == inquiries.get(newItemPosition).getInquiryId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Inquiry newInquiry = InquiryAdapter.this.inquiries.get(oldItemPosition);
                    Inquiry oldInquiry = InquiryAdapter.this.inquiries.get(newItemPosition);
                    return newInquiry.getInquiryId() == oldInquiry.getInquiryId();
                }
            });
            this.inquiries = inquiries;
            this.filteredInquiries = inquiries;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public InquiryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (userRole.equals("customer")){
            view = LayoutInflater.from(context).inflate(R.layout.my_inquiries_row, parent, false);

        }else {
            view = LayoutInflater.from(context).inflate(R.layout.inquiries_row, parent, false);

        }
        return new InquiryAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull InquiryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mInquiryId.setText("#"+String.valueOf(filteredInquiries.get(position).getInquiryId()));
        holder.mShipmentId.setText("#"+String.valueOf(filteredInquiries.get(position).getShipmentId()));
        holder.mMessage.setText(String.valueOf(filteredInquiries.get(position).getDescription()));
        if (userRole.equals("customer")){
            if(filteredInquiries.get(position).getResponse() == null){
                holder.mResponse.setText("Waiting for Response....");
            }else {
                holder.mResponse.setText(String.valueOf(filteredInquiries.get(position).getResponse()));
            }

        }

        holder.mCreatedOn.setText("Created on "+String.valueOf(filteredInquiries.get(position).getCreatedAt()));
        if (userRole.equals("supervisor") || userRole.equals("admin")){
            holder.mFrom.setText(String.valueOf(filteredInquiries.get(position).getUserId()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleOnClick(filteredInquiries.get(position));
                }
            });

        }





    }

    private void handleOnClick(Inquiry inquiry) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme );
        builder.setMessage("Inquiry " +"#"+ inquiry.getInquiryId());
        builder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, null));
        builder.setNeutralButton("Response now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent in = new Intent(context, ResponseInquiryActivity.class);
                in.putExtra("InquiryId", inquiry.getInquiryId().toString());
                context.startActivity(in);
            }
        });
        builder.show();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredInquiries = inquiries;
                } else {
                    List<Inquiry> filteredList = new ArrayList<>();
                    for (Inquiry inquiry : inquiries) {
                        String searchString = charString.toLowerCase();

                    }
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredInquiries = (ArrayList<Inquiry>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }




    @Override
    public int getItemCount() {
        if (filteredInquiries != null) return filteredInquiries.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mInquiryId, mShipmentId,mFrom, mMessage, mResponse, mCreatedOn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mInquiryId = itemView.findViewById(R.id.inquiry_Id);
            mShipmentId = itemView.findViewById(R.id.inquiry_shipmentId);
            mFrom = itemView.findViewById(R.id.inquiry_userId);
            mMessage = itemView.findViewById(R.id.message);
            mResponse = itemView.findViewById(R.id.response);
            mCreatedOn = itemView.findViewById(R.id.created_at);
        }
    }
}
