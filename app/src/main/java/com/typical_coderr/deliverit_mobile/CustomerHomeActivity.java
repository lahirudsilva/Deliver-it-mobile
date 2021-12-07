package com.typical_coderr.deliverit_mobile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.ShipmentAdapter;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.service.UserClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;
import com.typical_coderr.deliverit_mobile.util.NavHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShipmentAdapter shipmentAdapter;
    private TextView mEmptyView;
    private TextView mName;
    private CardView mCreatePackage;
    private CardView mDeliveryHistory;
    private CardView mTracking;
    private CardView mProfile;

    private String jwtToken;
    private User user;

    private List<Shipment> shipments;
    private boolean resultsRetrieved;
    private boolean shipmentsRetrieved;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);
    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Check if authorization token is valid
        String result = AuthHandler.validate(CustomerHomeActivity.this, "customer");


        if(result!=null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_customer_home);


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

        mName = findViewById(R.id.customer_name);
        mEmptyView = findViewById(R.id.empty_pick_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.my_deliveries);

        resultsRetrieved = false;
        shipmentsRetrieved = false;
        shipments = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        shipmentAdapter = new ShipmentAdapter(this, shipments, "customer", jwtToken, mProgressDialog);
        recyclerView.setAdapter(shipmentAdapter);




        userDetails();
        recentPackages();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                recentPackages();

            }
        });

        mCreatePackage = findViewById(R.id.create_package);
        mCreatePackage.setOnClickListener(view -> startActivity(new Intent(CustomerHomeActivity.this, CreatePackageActivity.class)));

        mDeliveryHistory = findViewById(R.id.my_packages);
        mDeliveryHistory.setOnClickListener(view -> startActivity(new Intent(CustomerHomeActivity.this, CustomerDeliveryHistoryActivity.class)));

        mProfile = findViewById(R.id.view_profile);
        mProfile.setOnClickListener(view -> startActivity(new Intent(CustomerHomeActivity.this, UserProfileActivity.class)));

        mTracking = findViewById(R.id.search_tracking);
        mTracking.setOnClickListener(view -> startActivity(new Intent(CustomerHomeActivity.this,PackageTrackingActivity.class)));


    }



    private void userDetails() {
        Call<User> call = userClient.loggedInUser(jwtToken);

        //Show progress
        mProgressDialog.setMessage("setting user");
        mProgressDialog.show();

        call.enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                if (user != null){
                    resultsRetrieved = true;
                    mName.setText(String.valueOf("Hello "+user.getFirstName()+"!"));
                }
                else{
                    Toast.makeText(CustomerHomeActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });


    }

    private void recentPackages() {
        Call<List<Shipment>> call = shipmentClient.getMyShipments(jwtToken);


        //Show progress
        mProgressDialog.setMessage("Loading recent shipments...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Shipment>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Shipment>> call, Response<List<Shipment>> response) {
                shipments = response.body();
                if (shipments != null) {
                    resultsRetrieved = true;
                    shipmentAdapter.setShipments(shipments);
                    recyclerView.setVisibility(shipments.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(shipments.isEmpty() ? View.VISIBLE : View.GONE);



                }else {
                    Toast.makeText(CustomerHomeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Shipment>> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Handle side drawer navigation
        NavHandler.handleCustomerNav(item, CustomerHomeActivity.this);

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
        AuthHandler.validate(CustomerHomeActivity.this, "customer");
    }
}