package com.typical_coderr.deliverit_mobile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.adapter.ShipmentScheduleAdapter;
import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.ShipmentSchedule;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
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
 * Date: Wed
 * Time: 9:55 PM
 */
public class ShipmentScheduleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ShipmentScheduleAdapter shipmentScheduleAdapter;

    private List<ShipmentSchedule> shipmentSchedules;
    private TextView mEmptyView;

    private String username;


    private DriverDetailsClient driverDetailsClient = RetrofitClientInstance.getRetrofitInstance().create(DriverDetailsClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_schedule);

        //Check if authorization token is valid
        AuthHandler.validate(ShipmentScheduleActivity.this, "driver");

        //Retrieve username
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);


        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        mEmptyView = findViewById(R.id.empty_pick_view);


        //Setup classes list
        shipmentSchedules = new ArrayList<>();
        recyclerView = findViewById(R.id.shipmentSchedule_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        shipmentScheduleAdapter = new ShipmentScheduleAdapter(this, shipmentSchedules, mProgressDialog);
        recyclerView.setAdapter(shipmentScheduleAdapter);

        getShipmentScheduleList();

    }



    @SuppressLint("Range")
    private void getShipmentScheduleList() {
        //Show progress
        mProgressDialog.setMessage("Loading Shipment schedule...");
        mProgressDialog.show();

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.typical_coderr.deliverit_mobile.provider/shipmentSchedule"), null, null, null, null);

        // iteration of the cursor
        // to print whole table
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if(cursor.getString(cursor.getColumnIndex("driver")).equals(username)) {
                    ShipmentSchedule shipmentSchedule = new ShipmentSchedule();
                    shipmentSchedule.setShipmentId(cursor.getInt(cursor.getColumnIndex("shipmentId")));
                    shipmentSchedule.setPickupLocation(cursor.getString(cursor.getColumnIndex("pickupLocation")));
                    shipmentSchedule.setDropOffLocation(cursor.getString(cursor.getColumnIndex("dropOffLocation")));
                    shipmentSchedule.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    shipmentSchedule.setSenderFirstName(cursor.getString(cursor.getColumnIndex("senderFirstName")));
                    shipmentSchedule.setSenderLastName(cursor.getString(cursor.getColumnIndex("senderLastName")));
                    shipmentSchedule.setReceiverName(cursor.getString(cursor.getColumnIndex("receiverName")));
                    shipmentSchedule.setPriority(cursor.getString(cursor.getColumnIndex("priority")));
                    shipmentSchedule.setArrival(cursor.getString(cursor.getColumnIndex("arrival")));
                    shipmentSchedules.add(shipmentSchedule);

                }
                cursor.moveToNext();
            }
            shipmentScheduleAdapter.setShipmentSchedules(shipmentSchedules);
        }
        else {
            mEmptyView.setVisibility(shipmentSchedules.isEmpty() ? View.VISIBLE : View.GONE);
            Toast.makeText(this, "You have no Shipments scheduled!", Toast.LENGTH_SHORT).show();
        }

        if (shipmentSchedules.size()==0) Toast.makeText(this, "You have no Shipments scheduled!", Toast.LENGTH_SHORT).show();
        mProgressDialog.dismiss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
