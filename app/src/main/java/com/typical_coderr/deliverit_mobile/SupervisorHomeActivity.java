package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.UserClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;
import com.typical_coderr.deliverit_mobile.util.NavHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Mon
 * Time: 12:36 AM
 */
public class SupervisorHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView mEmptyView;
    private TextView mName, mWarehouse;
    private boolean resultsRetrieved;

    private CardView mPackageRequests, mOn_Going, mInquires, mDrivers, mProfile, mSettings;

    private String jwtToken;
    private User user;

    String result;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Check if authorization token is valid
        String result = AuthHandler.validate(SupervisorHomeActivity.this, "supervisor");


        if(result!=null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_supervisor_home);


        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);



        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Setup navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setBackgroundResource(R.drawable.ic_wave__6_);
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

        mName = findViewById(R.id.supervisor_name);
        mWarehouse= findViewById(R.id.warehouse);

//        mCustomers = findViewById(R.id.customers);

        mPackageRequests = findViewById(R.id.package_requests);
        mPackageRequests.setOnClickListener(view -> startActivity(new Intent(SupervisorHomeActivity.this, PackageRequestsActivity.class)));

        mOn_Going = findViewById(R.id.on_going);
        mOn_Going.setOnClickListener(view -> startActivity(new Intent(SupervisorHomeActivity.this, OnGoingShipmentsActivity.class)));

        mInquires = findViewById(R.id.inquiries);
        mInquires.setOnClickListener(view -> startActivity(new Intent(SupervisorHomeActivity.this, InquiriesActivity.class)));

        mDrivers = findViewById(R.id.drivers);
        mDrivers.setOnClickListener(view -> startActivity(new Intent(SupervisorHomeActivity.this, AllDriversActivity.class)));

        mSettings = findViewById(R.id.settings_supervisor);
        mSettings.setOnClickListener(view -> startActivity(new Intent(SupervisorHomeActivity.this, SettingsActivity.class)));

        mProfile = findViewById(R.id.profile);
        mProfile.setOnClickListener(view -> startActivity(new Intent(SupervisorHomeActivity.this,UserProfileActivity.class)));

        userDetails();
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
//                    mCustomers.setVisibility(user.getUserRole().equals("admin") ? View.VISIBLE : View.GONE);
                }
                else{
                    Toast.makeText(SupervisorHomeActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SupervisorHomeActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleSupervisorNav(item, SupervisorHomeActivity.this);

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
        AuthHandler.validate(SupervisorHomeActivity.this, "supervisor");
    }
}
