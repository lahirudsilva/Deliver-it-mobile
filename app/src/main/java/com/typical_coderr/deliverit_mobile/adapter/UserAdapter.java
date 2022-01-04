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
import com.typical_coderr.deliverit_mobile.model.Tracking;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.UserClient;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<User> users;
    private List<User> filteredUsers;
    private String userRole;
    private String jwtToken;
    private ProgressDialog mProgressDialog;

    UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    public UserAdapter(Context context, List<User> users, String userRole, String jwtToken, ProgressDialog mProgressDialog) {
        this.context = context;
        this.users = users;
        this.userRole = userRole;
        this.jwtToken = jwtToken;
        this.mProgressDialog = mProgressDialog;
    }

    public void setUsers(List<User> users){
        if (this.users ==null){
            this.users =users;
            this.filteredUsers = users;

            notifyItemChanged(0, filteredUsers.size());
        }else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return UserAdapter.this.users.size();
                }

                @Override
                public int getNewListSize() {
                    return users.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return UserAdapter.this.users.get(oldItemPosition).getEmail() == users.get(newItemPosition).getEmail();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    User newUser = UserAdapter.this.users.get(oldItemPosition);
                    User oldUser = users.get(newItemPosition);
                    return newUser.getEmail() == oldUser.getEmail();

                }
            });
            this.users = users;
            this.filteredUsers = users;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.customers_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.mName.setText(String.format("%s %s", filteredUsers.get(position).getFirstName(), filteredUsers.get(position).getLastName()));
        holder.mEmail.setText(String.valueOf(filteredUsers.get(position).getEmail()));
        holder.mContact.setText(String.valueOf(filteredUsers.get(position).getContactNumber()));
        holder.mTown.setText(String.valueOf(filteredUsers.get(position).getCity()));
        holder.mRegisterDate.setText("Joined on "+String.valueOf(filteredUsers.get(position).getJoinedOn()));


    }

    @Override
    public Filter getFilter() {
        return null;
    }





    @Override
    public int getItemCount() {
        if (filteredUsers != null) return filteredUsers.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mEmail, mContact, mTown, mRegisterDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            mName = itemView.findViewById(R.id.customer_name);
            mEmail = itemView.findViewById(R.id.customer_email);
            mContact = itemView.findViewById(R.id.customer_contact);
            mTown = itemView.findViewById(R.id.customer_town);
            mRegisterDate = itemView.findViewById(R.id.registeredOn);

        }
    }
}
