package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.PastRidesAdapter;
import com.typical_coderr.deliverit_mobile.adapter.ShipmentAdapter;
import com.typical_coderr.deliverit_mobile.model.Shipment;
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
 * Time: 5:22 PM
 */
public class PackageRequestsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String jwtToken;
    private RecyclerView recyclerView;
    private ShipmentAdapter shipmentAdapter;
    private TextView mEmptyView;

    private List<Shipment> packageRequests;
    private boolean resultsRetrieved;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_requests);

        //Check if authorization token is valid
        AuthHandler.validate(PackageRequestsActivity.this, "supervisor");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mEmptyView = findViewById(R.id.empty_pick_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pickup_requests_swipe);
        packageRequests = new ArrayList<>();
        recyclerView = findViewById(R.id.packageRequests_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        shipmentAdapter = new ShipmentAdapter(this, packageRequests, "supervisor", jwtToken, mProgressDialog);
        recyclerView.setAdapter(shipmentAdapter);

        getPackageRequests();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getPackageRequests();
            }
        });


    }

    private void getPackageRequests() {

        Call<List<Shipment>> call = shipmentClient.getNewPackagesRequests(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading package requests...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Shipment>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Shipment>> call, Response<List<Shipment>> response) {
                packageRequests = response.body();
                if (packageRequests != null) {
                    resultsRetrieved = true;
                    shipmentAdapter.setShipments(packageRequests);
                    recyclerView.setVisibility(packageRequests.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(packageRequests.isEmpty() ? View.VISIBLE : View.GONE);
                }else {
                    Toast.makeText(PackageRequestsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Shipment>> call, Throwable t) {
                Toast.makeText(PackageRequestsActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
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
