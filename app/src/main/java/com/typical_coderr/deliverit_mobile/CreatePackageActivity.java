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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.ShipmentClient;
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
 * Date: Mon
 * Time: 11:49 AM
 */
public class CreatePackageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;
    private AutoCompleteTextView mWarehouseDropdown;
    private Button mButton;
    private EditText mPickup, mDrop, mReceiverName, mReceiverPhone, mReceiverEmail, mWeight, mDescription;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;

    private String jwtToken;
    private User user;
    private String packageSize, mWarehouseId;


    private boolean dataLoaded;

    private ShipmentClient shipmentClient = RetrofitClientInstance.getRetrofitInstance().create(ShipmentClient.class);


    private List<String> warehouse = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_package);

        //Check if authorization token is valid
        AuthHandler.validate(CreatePackageActivity.this, "customer");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        mWarehouseDropdown = findViewById(R.id.warehouse_dropdown);
        mPickup = findViewById(R.id.input_pickup_location);
        mDrop = findViewById(R.id.input_drop_location);
        mReceiverName = findViewById(R.id.input_r_name);
        mReceiverEmail = findViewById(R.id.input_r_email);
        mReceiverPhone = findViewById(R.id.input_r_phone);
        mWeight = findViewById(R.id.input_weight);
        mDescription = findViewById(R.id.input_description);

        //Initialize list of warehouses
        setupDropdown();


        mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.small:
                        packageSize = "small";
                        break;
                    case R.id.medium:
                        packageSize = "medium";
                        break;
                    case R.id.large:
                        packageSize = "large";
                        break;
                }
            }
        });








        //Submit button
        mButton = findViewById(R.id.add_package);
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
            mWarehouseId = "col-01";
        } else if (mWarehouseDropdown.getText().toString().trim().equals("jaffna")) {
            mWarehouseId = "jaf-01";
        } else if (mWarehouseDropdown.getText().toString().trim().equals("kandy")) {
            mWarehouseId = "kan-01";
        } else {
            mWarehouseId = "gal-01";
        }
        System.out.println(mWarehouseDropdown.getText().toString().trim());


        if (dataLoaded) {
            //Get packages inputs
            String pickupLocation = mPickup.getText().toString().trim();
            String dropLocation = mDrop.getText().toString().trim();
            String name = mReceiverName.getText().toString().trim();
            String email = mReceiverEmail.getText().toString().trim();
            String phone = mReceiverPhone.getText().toString().trim();
            String size = packageSize;
            String warehouseSelection = mWarehouseId;
            Double weight = Double.parseDouble(mWeight.getText().toString());
            String mDesc = mDescription.getText().toString().trim();
            Double estimate = weight * 1000;

            //Validate user input
            if (TextUtils.isEmpty(pickupLocation) || TextUtils.isEmpty(dropLocation) || TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(size) || TextUtils.isEmpty(warehouseSelection)) {
                Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
            } else {



                //If user input is valid

                //Show progress
                mProgressDialog.setMessage("Adding package...");
                mProgressDialog.show();

                Shipment shipment = new Shipment();
                shipment.setPickupLocation(pickupLocation);
                shipment.setDropOffLocation(dropLocation);
                shipment.setReceiverName(name);
                shipment.setReceiverEmail(email);
                shipment.setReceiverContactNumber(phone);
                shipment.setSize(size);
                shipment.setWarehouseNumber(warehouseSelection);
                shipment.setWeight(weight);
                shipment.setDescription(mDesc);
                shipment.setEstimatedPrice(estimate);

                Call<ResponseBody> call = shipmentClient.createPackage(jwtToken, shipment);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //Successfully added
                        if (response.code() == 201) {
                            Toast.makeText(CreatePackageActivity.this, "Successfully added Package!", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();

                            //Direct to ManageStudentsActivity
                            Intent intent = new Intent(CreatePackageActivity.this, CustomerHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            try {

                                // Capture an display specific messages
                                JSONObject obj = new JSONObject(response.errorBody().string());
                                Toast.makeText(CreatePackageActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(CreatePackageActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(CreatePackageActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });

            }
        } else {
            Toast.makeText(CreatePackageActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }

    }

    private void setupDropdown() {

        warehouse.add("colombo");
        warehouse.add("jaffna");
        warehouse.add("kandy");
        warehouse.add("galle");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                CreatePackageActivity.this,
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
