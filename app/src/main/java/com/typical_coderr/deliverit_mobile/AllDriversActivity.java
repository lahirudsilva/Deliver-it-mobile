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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.DriverDetailsAdapter;
import com.typical_coderr.deliverit_mobile.adapter.PastRidesAdapter;
import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
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
 * Time: 9:06 PM
 */
public class AllDriversActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private TextView mEmptyView;
    private SearchView searchView;

    private RecyclerView recyclerView;
    private DriverDetailsAdapter driverDetailsAdapter;
    private String jwtToken;
    private List<DriverDetails> driverDetails;
    private boolean resultsRetrieved;

    private DriverDetailsClient driverDetailsClient = RetrofitClientInstance.getRetrofitInstance().create(DriverDetailsClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_drivers);

        //Check if authorization token is valid
        String role = AuthHandler.validate(AllDriversActivity.this, "all");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        driverDetails = new ArrayList<>();
        recyclerView = findViewById(R.id.drivers_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        driverDetailsAdapter = new DriverDetailsAdapter(this, driverDetails, "all", jwtToken, mProgressDialog);
        recyclerView.setAdapter(driverDetailsAdapter);

        mEmptyView = findViewById(R.id.empty_pick_view);


        if(role.equals("admin")){
            getAllDrivers();
        }else {
            getAllDriversForWarehouse();
        }

    }

    private void getAllDriversForWarehouse() {
        Call<List<DriverDetails>> call = driverDetailsClient.getAllDriversForWarehouse(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading Drivers...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<DriverDetails>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<DriverDetails>> call, Response<List<DriverDetails>> response) {
                driverDetails = response.body();
                if (driverDetails != null) {
                    resultsRetrieved = true;
                    driverDetailsAdapter.setDrivers(driverDetails);
                    recyclerView.setVisibility(driverDetails.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(driverDetails.isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    Toast.makeText(AllDriversActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<DriverDetails>> call, Throwable t) {
                Toast.makeText(AllDriversActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

    }

    private void getAllDrivers() {
        Call<List<DriverDetails>> call = driverDetailsClient.getAllDrivers(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading Drivers...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<DriverDetails>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<DriverDetails>> call, Response<List<DriverDetails>> response) {
                driverDetails = response.body();
                if (driverDetails != null) {
                    resultsRetrieved = true;
                    driverDetailsAdapter.setDrivers(driverDetails);
                    recyclerView.setVisibility(driverDetails.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(driverDetails.isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    Toast.makeText(AllDriversActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<DriverDetails>> call, Throwable t) {
                Toast.makeText(AllDriversActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
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
