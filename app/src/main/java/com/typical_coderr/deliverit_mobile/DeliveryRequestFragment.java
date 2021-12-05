package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.typical_coderr.deliverit_mobile.adapter.OutForDeliveryAdapter;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DeliveryRequestFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private RecyclerView recyclerView;
    private OutForDeliveryAdapter outForDeliveryAdapter;
    private List<Shipment> outForDeliverShipments;
    private String jwtToken;
    private boolean resultsRetrieved;
    private TextView mEmptyView;

    private ProgressDialog mProgressDialog;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getContext();

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.delivering_list);
        recyclerView = view.findViewById(R.id.outForDelivery_recycler_view);

        //Check if authorization token is valid
        AuthHandler.validate(context, "driver");

        mProgressDialog = new ProgressDialog(context);

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);
        getOutForDeliveryPackages();
        outForDeliveryAdapter = new OutForDeliveryAdapter(context, outForDeliverShipments, "driver", jwtToken, mProgressDialog);
        recyclerView.setAdapter(outForDeliveryAdapter);

        mEmptyView = (TextView) getView().findViewById(R.id.empty_pick_view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getOutForDeliveryPackages();
            }
        });
    }

    private void getOutForDeliveryPackages() {

        Call<List<Shipment>> call = shipmentClient.getDeliveringPackages(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading delivering packages for today...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Shipment>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Shipment>> call, Response<List<Shipment>> response) {
                outForDeliverShipments = response.body();
                if (outForDeliverShipments != null){
                    resultsRetrieved = true;
                    outForDeliveryAdapter.setOutForDeliveryShipments(outForDeliverShipments);
                    recyclerView.setVisibility(outForDeliverShipments.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(outForDeliverShipments.isEmpty() ? View.VISIBLE : View.GONE);

                }
                else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Shipment>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }
}