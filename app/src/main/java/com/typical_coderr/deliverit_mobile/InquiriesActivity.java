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

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.InquiryAdapter;
import com.typical_coderr.deliverit_mobile.adapter.PastRidesAdapter;
import com.typical_coderr.deliverit_mobile.model.Inquiry;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.InquiryClient;
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
 * Time: 8:58 PM
 */
public class InquiriesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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


    private InquiryClient inquiryClient = RetrofitClientInstance.getRetrofitInstance().create(InquiryClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiries);

        //Check if authorization token is valid
        String role =AuthHandler.validate(InquiriesActivity.this, "all");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        inquiries = new ArrayList<>();
        recyclerView = findViewById(R.id.inquiries_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        inquiryAdapter = new InquiryAdapter(this, inquiries, "all", jwtToken, mProgressDialog);
        recyclerView.setAdapter(inquiryAdapter);

        mEmptyView = findViewById(R.id.empty_pick_view);

        if (role.equals("admin")){
            getAllInquiriesForAdmin();
        }else {
            getAllInquiries();
        }




    }

    private void getAllInquiriesForAdmin() {

        Call<List<Inquiry>> call = inquiryClient.getAllInquiries(jwtToken);

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
                    Toast.makeText(InquiriesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Inquiry>> call, Throwable t) {
                Toast.makeText(InquiriesActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void getAllInquiries() {
        Call<List<Inquiry>> call = inquiryClient.getAllInquiriesForWarehouse(jwtToken);

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
                    Toast.makeText(InquiriesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Inquiry>> call, Throwable t) {
                Toast.makeText(InquiriesActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
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
