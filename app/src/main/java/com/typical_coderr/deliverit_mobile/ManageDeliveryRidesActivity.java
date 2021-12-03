package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.typical_coderr.deliverit_mobile.adapter.DeliveryFragmentAdapter;
import com.typical_coderr.deliverit_mobile.adapter.PickupDeliveryAdapter;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;
import com.typical_coderr.deliverit_mobile.util.NavHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageDeliveryRidesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private TextView mEmptyView;


    private DeliveryFragmentAdapter fragmentAdapter;

    private RecyclerView recyclerView;
    private PickupDeliveryAdapter pickupDeliveryAdapter;
    private String jwtToken;

    private List<Shipment> pickShipments;
    private boolean resultsRetrieved;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_deliveries);


        //Check if authorization token is valid
        AuthHandler.validate(ManageDeliveryRidesActivity.this, "driver");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();
        fragmentAdapter = new DeliveryFragmentAdapter(fm, getLifecycle());
        mViewPager.setAdapter(fragmentAdapter);

//        mTabLayout.addTab(mTabLayout.newTab().setText("Pickup Request"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("Delivery Request"));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        getPickUpPackages();

        //setup pickup list
//        resultsRetrieved = false;
//        pickShipments = new ArrayList<>();
//        recyclerView = findViewById(R.id.pickup_recycler_view);
//        mEmptyView = findViewById(R.id.empty_pick_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        pickupDeliveryAdapter = new PickupDeliveryAdapter(this, pickShipments, "driver", jwtToken, mProgressDialog);
//        recyclerView.setAdapter(pickupDeliveryAdapter);
//
//        if (pickShipments.isEmpty()) {
//            recyclerView.setVisibility(View.GONE);
//            mEmptyView.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            mEmptyView.setVisibility(View.GONE);
//        }


//        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                mTabLayout.selectTab(mTabLayout.getTabAt(position));
//            }
//        });

//        //Setup navigation drawer
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
////        mNavigationView = findViewById(R.id.nav_view);
//
//        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(
//                this,
//                mDrawerLayout,
//                mToolbar,
//                R.string.open_nav_drawer,
//                R.string.close_nav_drawer
//        );
//
//        mProgressDialog = new ProgressDialog(this);
//        mNavigationView.setNavigationItemSelectedListener(this);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void getPickUpPackages() {
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
//                    recyclerView.setVisibility(View.VISIBLE);
//                    mEmptyView.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ManageDeliveryRidesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                    recyclerView.setVisibility(View.GONE);
//                    mEmptyView.setVisibility(View.VISIBLE);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Shipment>> call, Throwable t) {
                Toast.makeText(ManageDeliveryRidesActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavHandler.handleDriverNav(item, ManageDeliveryRidesActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }
}
