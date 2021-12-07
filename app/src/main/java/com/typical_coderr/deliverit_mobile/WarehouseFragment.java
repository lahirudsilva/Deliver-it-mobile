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

import com.typical_coderr.deliverit_mobile.adapter.WarehouseDeliveryAdapter;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WarehouseFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private RecyclerView recyclerView;
    private WarehouseDeliveryAdapter warehouseDeliveryAdapter;
    private List<Shipment> warehouseShipment;
    private String jwtToken;
    private boolean resultsRetrieved;
    private TextView mEmptyView;

    private ProgressDialog mProgressDialog;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_warehouse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = getContext();

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.warehouse_list);
        recyclerView = view.findViewById(R.id.warehouse_recycler_view);
        //Check if authorization token is valid
        AuthHandler.validate(context, "driver");

        mProgressDialog = new ProgressDialog(context);

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);
        getWarehousePackages();
        warehouseDeliveryAdapter = new WarehouseDeliveryAdapter(context, warehouseShipment, "driver", jwtToken , mProgressDialog);
        recyclerView.setAdapter(warehouseDeliveryAdapter);


        mEmptyView = (TextView) getView().findViewById(R.id.empty_pick_view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getWarehousePackages();
            }
        });
    }

    private void getWarehousePackages() {
        Call<List<Shipment>> call = shipmentClient.getInWarehouseDeliveries(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading packages in warehouse...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Shipment>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Shipment>> call, Response<List<Shipment>> response) {
                warehouseShipment = response.body();
                if (warehouseShipment != null){
                    resultsRetrieved = true;
                    warehouseDeliveryAdapter.setWarehouseShipments(warehouseShipment);
                    recyclerView.setVisibility(warehouseShipment.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(warehouseShipment.isEmpty() ? View.VISIBLE : View.GONE);
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