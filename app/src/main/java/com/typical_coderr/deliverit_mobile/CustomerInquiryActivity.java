package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.typical_coderr.deliverit_mobile.adapter.InquiryAdapter;
import com.typical_coderr.deliverit_mobile.model.Inquiry;
import com.typical_coderr.deliverit_mobile.service.InquiryClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Thu
 * Time: 12:47 PM
 */
public class CustomerInquiryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private TextView mEmptyView;
    private SearchView searchView;

    private String jwtToken;
    private RecyclerView recyclerView;
    private List<Inquiry> inquiries;

    private InquiryAdapter inquiryAdapter;
    private boolean resultsRetrieved;
    private SwipeRefreshLayout swipeRefreshLayout;

    private InquiryClient inquiryClient = RetrofitClientInstance.getRetrofitInstance().create(InquiryClient.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiries);

        //Check if authorization token is valid
        AuthHandler.validate(CustomerInquiryActivity.this, "customer");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.inquiries);

        inquiries = new ArrayList<>();
        recyclerView = findViewById(R.id.inquiries_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        inquiryAdapter = new InquiryAdapter(this, inquiries, "customer", jwtToken, mProgressDialog);
        recyclerView.setAdapter(inquiryAdapter);

        mEmptyView = findViewById(R.id.empty_pick_view);

        getAllInquiries();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getAllInquiries();

            }
        });

    }

    private void getAllInquiries() {
        Call<List<Inquiry>> call = inquiryClient.getAllMyInquiries(jwtToken);

        //Show progress
        mProgressDialog.setMessage("Loading inquiries...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Inquiry>>() {
            @Override
            public void onResponse(Call<List<Inquiry>> call, Response<List<Inquiry>> response) {
                inquiries = response.body();
                if (inquiries != null) {
                    resultsRetrieved = true;
                    inquiryAdapter.setInquiries(inquiries);
                    recyclerView.setVisibility(inquiries.isEmpty() ? View.GONE : View.VISIBLE);
                    mEmptyView.setVisibility(inquiries.isEmpty() ? View.VISIBLE : View.GONE);

                }else {
                    Toast.makeText(CustomerInquiryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Inquiry>> call, Throwable t) {
                Toast.makeText(CustomerInquiryActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
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
