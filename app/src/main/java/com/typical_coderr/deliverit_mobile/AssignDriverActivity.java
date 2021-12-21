package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.model.DriverDetails;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Tue
 * Time: 10:24 AM
 */
public class AssignDriverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    private AutoCompleteTextView mDriversDropDown;
    private EditText mPickupDateEditText, mDropDateEditText;
    private Button mButton;
    private String jwtToken;
    private boolean updateFlag;

    //Dropdown attributes
    private List<String> drivers = new ArrayList<>();
    private List<String> driver_ids = new ArrayList<>();

    private boolean driversLoaded;
    private boolean pickDateSelected, dropDateSelected;

    private String pick_date;
    private String drop_date;

    private DriverDetails driverDetailsObj;

    private final DriverDetailsClient driverDetailsClient = RetrofitClientInstance.getRetrofitInstance().create(DriverDetailsClient.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assign_driver);

        //Check if authorization token is valid
        AuthHandler.validate(AssignDriverActivity.this, "supervisor");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String Id = intent.getExtras().getString("ShipmentId");

        mDriversDropDown = findViewById(R.id.drivers_dropdown);

        mProgressDialog = new ProgressDialog(this);

        //Initialize edit texts
        mPickupDateEditText = findViewById(R.id.input_pickup_date);
        mDropDateEditText = findViewById(R.id.input_drop_date);
        mDriversDropDown = findViewById(R.id.drivers_dropdown);
        mPickupDateEditText = findViewById(R.id.input_pickup_date);
        mDropDateEditText = findViewById(R.id.input_drop_date);

        mPickupDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePickupDatePicker();
            }
        });

        mDropDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDropDatePicker();
            }
        });


        setupDriversDropdown();


        mButton = findViewById(R.id.assign_driver);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit(Id);
            }
        });
    }

    private void handleSubmit(String Id) {
        String driverSelection = mDriversDropDown.getText().toString();
        System.out.println(driverSelection);
//        String pick = mPickupDateEditText.getText().toString();
//        String drop = mDropDateEditText.getText().toString();

        if (driversLoaded) {


            //Validate input
            if (TextUtils.isEmpty(driverSelection)) {
                Toast.makeText(this, "Please select a driver", Toast.LENGTH_SHORT).show();
            } else if (!pickDateSelected) {
                Toast.makeText(this, "Please select valid pickup date", Toast.LENGTH_SHORT).show();
            } else if (!dropDateSelected) {
                Toast.makeText(this, "Please select valid drop off date", Toast.LENGTH_SHORT).show();
            }

            //Valid user input
            else {
                //Show progress
                mProgressDialog.setMessage("Assigning driver...");

                if (updateFlag) mProgressDialog.setMessage("Updating drivers...");

                mProgressDialog.show();

                Shipment shipment = new Shipment();
                shipment.setPickUp(pick_date);
                shipment.setArrival(drop_date);
                shipment.setShipmentId(Integer.parseInt(Id));
                shipment.setDriverID(driver_ids.get(drivers.indexOf(driverSelection)));

                System.out.println("ssssssssssssssssssssssssssss"+shipment + pick_date + drop_date);

                Call<ResponseBody> call = driverDetailsClient.assignDriver(jwtToken, shipment);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 201 || response.code() == 200) {
                            Toast.makeText(AssignDriverActivity.this, "Successfully Driver Assigned!", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();

                            //Direct to ManageModulesActivity
                            Intent intent = new Intent(AssignDriverActivity.this, PackageRequestsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {

                            try {

                                // Capture an display specific messages
                                JSONObject obj = new JSONObject(response.errorBody().string());
                                Toast.makeText(AssignDriverActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(AssignDriverActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AssignDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();

                    }
                });


            }
        }else{
            Toast.makeText(this, "Form wasn't setup properly!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDriversDropdown() {
        Call<List<DriverDetails>> call = driverDetailsClient.getAvailableDrivers(jwtToken);

        call.enqueue(new Callback<List<DriverDetails>>() {
            @Override
            public void onResponse(Call<List<DriverDetails>> call, Response<List<DriverDetails>> response) {
                List<DriverDetails> driverList = response.body();
                if (driverList != null) {
                    //Configure drop down
                    for (DriverDetails d : driverList) {
                        drivers.add(d.getDriverFirstName() + " " + d.getDriverLastName() + " - " + d.getDriverId().toUpperCase(Locale.ROOT));
                        driver_ids.add(d.getDriverId());
                    }

                    //Set adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AssignDriverActivity.this,
                            R.layout.drop_down_menu_popup,
                            drivers);

                    mDriversDropDown.setAdapter(adapter);
                    driversLoaded = true;

                    //If updating lecture set existing module
                    if (updateFlag) {
                        mDriversDropDown.setText(drivers.get(driver_ids.indexOf(driverDetailsObj.getDriverId())));
                    }
                    mProgressDialog.dismiss();
                } else {
                    Toast.makeText(AssignDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<DriverDetails>> call, Throwable t) {
                Toast.makeText(AssignDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    //Method to handle date picker
    private void handlePickupDatePicker() {

        //Setup material date picker
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Pickup Date");
        MaterialDatePicker picker = builder.build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        //When submitted
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick(Object selection) {
                Long dateLong = (Long) selection;
                LocalDate local_date = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                pick_date = date_formatter.format(local_date);

                pickDateSelected = true;
                mPickupDateEditText.setText(pick_date);
            }
        });
    }

    //Method to handle date picker
    private void handleDropDatePicker() {

        //Setup material date picker
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Drop Off date");
        MaterialDatePicker picker = builder.build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        //When submitted
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick(Object selection) {
                Long dateLong = (Long) selection;
                LocalDate local_date = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                drop_date = date_formatter.format(local_date);

                dropDateSelected = true;
                mDropDateEditText.setText(drop_date);
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
