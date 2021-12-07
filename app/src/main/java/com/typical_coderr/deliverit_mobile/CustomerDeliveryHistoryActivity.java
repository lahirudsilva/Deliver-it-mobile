package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.PastRidesAdapter;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Mon
 * Time: 12:10 PM
 */
public class CustomerDeliveryHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private TextView mEmptyView;
    private SearchView searchView;

    private RecyclerView recyclerView;
    private PastRidesAdapter pastRidesAdapter;
    private String jwtToken;
    private User user;

    private List<Shipment> pastShipment;
    private boolean resultsRetrieved;


    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history);

        //Check if authorization token is valid
        AuthHandler.validate(CustomerDeliveryHistoryActivity.this, "customer");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        pastShipment = new ArrayList<>();
        recyclerView = findViewById(R.id.pastRides_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        pastRidesAdapter = new PastRidesAdapter(this, pastShipment, "customer", jwtToken, mProgressDialog);
        recyclerView.setAdapter(pastRidesAdapter);

        mEmptyView = findViewById(R.id.empty_pick_view);

        getAllCustomerHistory();
    }

    private void getAllCustomerHistory() {

        Call<List<Shipment>> call = shipmentClient.getCustomerShipmentHistory(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading past packages...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Shipment>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Shipment>> call, Response<List<Shipment>> response) {
                pastShipment = response.body();
                if (pastShipment != null) {
                    resultsRetrieved = true;
                    pastRidesAdapter.setPastShipments(pastShipment);
                    recyclerView.setVisibility(pastShipment.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(pastShipment.isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    Toast.makeText(CustomerDeliveryHistoryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Shipment>> call, Throwable t) {
                Toast.makeText(CustomerDeliveryHistoryActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
