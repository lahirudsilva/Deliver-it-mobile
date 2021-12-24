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
import com.typical_coderr.deliverit_mobile.adapter.UserAdapter;
import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.UserClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Fri
 * Time: 4:56 PM
 */
public class AllCustomersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private TextView mEmptyView;
    private SearchView searchView;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private String jwtToken;
    private List<User> customers;
    private boolean resultsRetrieved;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_customers);

        //Check if authorization token is valid
        AuthHandler.validate(AllCustomersActivity.this, "admin");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        customers = new ArrayList<>();
        recyclerView = findViewById(R.id.customers_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(this, customers, "admin", jwtToken, mProgressDialog);
        recyclerView.setAdapter(userAdapter);

        mEmptyView = findViewById(R.id.empty_pick_view);

        getAllCustomers();
    }

    private void getAllCustomers() {

        Call<List<User>> call = userClient.getAllUsers(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading Customers...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<User>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                customers = response.body();
                if (customers != null) {
                    resultsRetrieved = true;
                    userAdapter.setUsers(customers);
                    recyclerView.setVisibility(customers.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(customers.isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    Toast.makeText(AllCustomersActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(AllCustomersActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
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
