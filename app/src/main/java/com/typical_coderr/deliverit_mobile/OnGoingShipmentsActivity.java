package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.TrackingAdapter;
import com.typical_coderr.deliverit_mobile.model.Tracking;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.TrackingClient;
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
 * Time: 8:51 PM
 */
public class OnGoingShipmentsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private TextView mEmptyView;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private TrackingAdapter trackingAdapter;
    private String jwtToken;
    private List<Tracking> trackingShipment;
    private boolean resultsRetrieved;

    private TrackingClient trackingClient = RetrofitClientInstance.getRetrofitInstance().create(TrackingClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_going_shipments);

        //Check if authorization token is valid
        String role = AuthHandler.validate(OnGoingShipmentsActivity.this, "all");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        trackingShipment = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.on_going_deliveries);
        recyclerView = findViewById(R.id.on_going_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        trackingAdapter = new TrackingAdapter(this, trackingShipment, "all", jwtToken, mProgressDialog);
        recyclerView.setAdapter(trackingAdapter);

        mEmptyView = findViewById(R.id.empty_pick_view);

        if (role.equals("admin")) {
            getAllOnGoingShipmentsForAdmin();
        } else {
            getAllOnGoingShipments();
        }



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getAllOnGoingShipments();
            }
        });
    }

    private void getAllOnGoingShipmentsForAdmin() {
        Call<List<Tracking>> call = trackingClient.getAllOnGoingShipmentsForAdmin(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading On-going Shipments...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Tracking>>() {
            @Override
            public void onResponse(Call<List<Tracking>> call, Response<List<Tracking>> response) {
                trackingShipment = response.body();
                if (trackingShipment != null) {
                    resultsRetrieved = true;
                    trackingAdapter.setTacking(trackingShipment);
                    recyclerView.setVisibility(trackingShipment.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(trackingShipment.isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    Toast.makeText(OnGoingShipmentsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Tracking>> call, Throwable t) {
                Toast.makeText(OnGoingShipmentsActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void getAllOnGoingShipments() {
        Call<List<Tracking>> call = trackingClient.getAllOnGoingShipments(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading On-going Shipments...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Tracking>>() {
            @Override
            public void onResponse(Call<List<Tracking>> call, Response<List<Tracking>> response) {
                trackingShipment = response.body();
                if (trackingShipment != null) {
                    resultsRetrieved = true;
                    trackingAdapter.setTacking(trackingShipment);
                    recyclerView.setVisibility(trackingShipment.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(trackingShipment.isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    Toast.makeText(OnGoingShipmentsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Tracking>> call, Throwable t) {
                Toast.makeText(OnGoingShipmentsActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        //changing search view text color
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {


                trackingAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                trackingAdapter.getFilter().filter(query);

                return false;
            }
        });

        return true;


    }
}
