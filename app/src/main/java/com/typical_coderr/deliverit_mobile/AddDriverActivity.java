package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
import com.typical_coderr.deliverit_mobile.service.UserClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Fri
 * Time: 3:33 PM
 */
public class AddDriverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private AutoCompleteTextView mWarehouseDropdown;
    private Button mButton;
    private EditText mFirstName, mLastName, mEmail, mContactNumber, mDriverId, mHomeTown, mNIC, mVehicleNumber;

    private String jwtToken;
    private User user;
    private String warehouseId;

    private boolean dataLoaded;

    private DriverDetailsClient driverDetailsClient = RetrofitClientInstance.getRetrofitInstance().create(DriverDetailsClient.class);

    private List<String> warehouse = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        //Check if authorization token is valid
        AuthHandler.validate(AddDriverActivity.this, "admin");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        mWarehouseDropdown = findViewById(R.id.warehouse_dropdown);
        mFirstName = findViewById(R.id.input_driver_firstName);
        mLastName = findViewById(R.id.input_driver_lastName);
        mEmail = findViewById(R.id.input_driver_email);
        mContactNumber = findViewById(R.id.input_driver_contact);
        mDriverId = findViewById(R.id.input_driver_driverID);
        mHomeTown = findViewById(R.id.input_driver_town);
        mNIC = findViewById(R.id.input_driver_nic);
        mVehicleNumber = findViewById(R.id.input_driver_vehicle);




        //Initialize list of warehouses
        setupDropdown();

        //Submit button
        mButton = findViewById(R.id.add_driver);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });
    }

    private void handleSubmit() {

        //return warehouse id according to the selection
        if (mWarehouseDropdown.getText().toString().trim().equals("colombo")) {
            warehouseId = "col-01";
        } else if (mWarehouseDropdown.getText().toString().trim().equals("jaffna")) {
            warehouseId = "jaf-01";
        } else if (mWarehouseDropdown.getText().toString().trim().equals("kandy")) {
            warehouseId = "kan-01";
        } else {
            warehouseId = "gal-01";
        }

        if (dataLoaded) {
            //Get user inputs
            String firstName = mFirstName.getText().toString().trim();
            String lastName = mLastName.getText().toString().trim();
            String email = mEmail.getText().toString().trim();
            String contactNumber = mContactNumber.getText().toString().trim();
            String driverID = mDriverId.getText().toString().trim();
            String hometown = mHomeTown.getText().toString().trim();
            String warehouseSelection = warehouseId;
            String nIC = mNIC.getText().toString().trim();
            String vehicleNo = mVehicleNumber.getText().toString().trim();


            //Validate user input
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(contactNumber) || TextUtils.isEmpty(driverID) || TextUtils.isEmpty(hometown) || TextUtils.isEmpty(warehouseSelection) || TextUtils.isEmpty(nIC) || TextUtils.isEmpty(vehicleNo)) {
                Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
            } else {

                //If user input is valid

                //Show progress
                mProgressDialog.setMessage("Adding driver...");
                mProgressDialog.show();

                DriverDetails driverDetails = new DriverDetails();
                driverDetails.setDriverFirstName(firstName);
                driverDetails.setDriverLastName(lastName);
                driverDetails.setDriverEmail(email);
                driverDetails.setContactNumber(contactNumber);
                driverDetails.setDriverId(driverID);
                driverDetails.setTown(hometown);
                driverDetails.setWarehouseId(warehouseSelection);
                driverDetails.setIdNumber(nIC);
                driverDetails.setVehicleNumber(vehicleNo);

                System.out.println("dsddsds"+driverDetails.getIdNumber());

                Call<ResponseBody> call = driverDetailsClient.addDriver(jwtToken, driverDetails);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //Successfully added
                        if (response.code() == 201) {
                            Toast.makeText(AddDriverActivity.this, "Successfully added driver!", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();

                            //Direct to ManageStudentsActivity
                            Intent intent = new Intent(AddDriverActivity.this, AllDriversActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else {

                            try {

                                // Capture an display specific messages
                                JSONObject obj = new JSONObject(response.errorBody().string());
                                Toast.makeText(AddDriverActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(AddDriverActivity    .this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AddDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });

            }
        }else{
            Toast.makeText(AddDriverActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

    private void setupDropdown() {

        warehouse.add("colombo");
        warehouse.add("jaffna");
        warehouse.add("kandy");
        warehouse.add("galle");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                AddDriverActivity.this,
                R.layout.drop_down_menu_popup, warehouse);
        mWarehouseDropdown.setAdapter(adapter);

        dataLoaded = true;



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
