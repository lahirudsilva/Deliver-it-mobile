package com.typical_coderr.deliverit_mobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.typical_coderr.deliverit_mobile.adapter.PickupDeliveryAdapter;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupRequestFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private PickupDeliveryAdapter pickupDeliveryAdapter;
    private List<Shipment> pickShipments;
    private String jwtToken;
    private boolean resultsRetrieved;
    private TextView mEmptyView;

    private ProgressDialog mProgressDialog;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pickup_request, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getContext();
        recyclerView = view.findViewById(R.id.pickup_recycler_view);
        //Check if authorization token is valid
        AuthHandler.validate(context, "driver");

        mProgressDialog = new ProgressDialog(context);

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);
        getPickUpPackages();
        pickupDeliveryAdapter = new PickupDeliveryAdapter(context, pickShipments, "driver", jwtToken, mProgressDialog);
        recyclerView.setAdapter(pickupDeliveryAdapter);
//        mEmptyView = mEmptyView.findViewById(R.id.empty_pick_view);
//

        mEmptyView = (TextView) getView().findViewById(R.id.empty_pick_view);

    }

    public void getPickUpPackages() {
        Call<List<Shipment>> call = shipmentClient.getPickupDeliveries(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading pickup requests for today...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Shipment>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Shipment>> call, Response<List<Shipment>> response) {
                pickShipments = response.body();
                System.out.println(response);
                System.out.println(response.body());
                if (pickShipments != null) {
                    resultsRetrieved = true;
                    pickupDeliveryAdapter.setPickupShipments(pickShipments);
                    recyclerView.setVisibility(pickShipments.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(pickShipments.isEmpty() ? View.VISIBLE : View.GONE);


                } else {
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