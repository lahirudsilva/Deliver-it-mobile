package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.ShipmentAdapter;
import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;
import com.typical_coderr.deliverit_mobile.util.NavHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    private RecyclerView recyclerView;
    private CardView mDeliveryRides;
    private ShipmentAdapter shipmentAdapter;
    private String jwtToken;

    private TextView mName;
    private TextView mNoOfRides;


    private List<Shipment> shipments;
    private DriverDetails driverDetails;
    private boolean resultsRetrieved;
    private boolean driverResultsRetrieved;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);
    private DriverDetailsClient driverDetailsClient = RetrofitClientInstance.getRetrofitInstance().create(DriverDetailsClient.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        //Check if authorization token is valid
        AuthHandler.validate(DriverHomeActivity.this, "driver");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);


        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Setup navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );

        mProgressDialog = new ProgressDialog(this);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);



        //Setup shipment list

        resultsRetrieved = false;
        driverResultsRetrieved = false;
        shipments = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        shipmentAdapter = new ShipmentAdapter(this, shipments, "driver", jwtToken, mProgressDialog);
        recyclerView.setAdapter(shipmentAdapter);
        mName = findViewById(R.id.driver_name);
        mNoOfRides = findViewById(R.id.no_Of_Rides);


        getDriverDetails();
        getAllShipmentsForDelivery();

        mDeliveryRides = findViewById(R.id.my_rides);
        mDeliveryRides.setOnClickListener(view -> startActivity(new Intent(DriverHomeActivity.this, ManageDeliveryRidesActivity.class)));


    }

    private void getDriverDetails() {
        Call<DriverDetails> call = driverDetailsClient.getDriverDetails(jwtToken);

        //Show progress
        mProgressDialog.setMessage("setting user");
        mProgressDialog.show();

        call.enqueue(new Callback<DriverDetails>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<DriverDetails> call, Response<DriverDetails> response) {
                driverDetails = response.body();
                if (driverDetails != null){
                    driverResultsRetrieved = true;
                    mName.setText(String.valueOf("Hello "+driverDetails.getDriverFirstName())+"!");
                    mNoOfRides.setText(String.valueOf(driverDetails.getNoOfRidesToGo()));
                }else{
                    Toast.makeText(DriverHomeActivity.this, "Name not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DriverDetails> call, Throwable t) {

            }
        });
    }

    public void getAllShipmentsForDelivery() {
        Call<List<Shipment>> call = shipmentClient.getAcceptedShipmentsForDriver(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading shipments for today...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Shipment>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Shipment>> call, Response<List<Shipment>> response) {
                shipments = response.body();
                System.out.println(response);
                System.out.println(response.body());
                if (shipments != null) {
                    resultsRetrieved = true;
                    shipmentAdapter.setShipments(shipments);


                } else {
                    Toast.makeText(DriverHomeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Shipment>> call, Throwable t) {
                Toast.makeText(DriverHomeActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, DriverHomeActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if authorization token is valid
        AuthHandler.validate(DriverHomeActivity.this, "driver");
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }
}
