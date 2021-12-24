package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.model.Inquiry;
import com.typical_coderr.deliverit_mobile.model.Shipment;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
import com.typical_coderr.deliverit_mobile.service.InquiryClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Fri
 * Time: 10:19 AM
 */
public class ResponseInquiryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    private EditText mResponse;
    private TextView mInquiryId;
    private Button mButton;
    private String jwtToken;

    private final InquiryClient inquiryClient = RetrofitClientInstance.getRetrofitInstance().create(InquiryClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_response_inquiry);

        //Check if authorization token is valid
        AuthHandler.validate(ResponseInquiryActivity.this, "all");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String Id = intent.getExtras().getString("InquiryId");

//        mInquiryId.setText("#"+Id);

        System.out.println(Id);


        mProgressDialog = new ProgressDialog(this);

        mResponse = findViewById(R.id.response_message);

        mButton = findViewById(R.id.send_response);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit(Id);
            }
        });


    }

    private void handleSubmit(String id) {



        if(id != null){
            String responseMsg = mResponse.getText().toString().trim();

            if (TextUtils.isEmpty(responseMsg)){
                Toast.makeText(this, "Response field is empty", Toast.LENGTH_SHORT).show();
            }else {
                //Show progress
                mProgressDialog.setMessage("Sending Response...");

                mProgressDialog.show();

                Inquiry inquiry = new Inquiry();
                inquiry.setResponse(responseMsg);
                inquiry.setInquiryId(Integer.parseInt(id));

                System.out.println("sss"+inquiry.getInquiryId());

                Call<ResponseBody> call = inquiryClient.sendInquiryResponse(jwtToken, inquiry);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 201 || response.code() == 200) {
                            Toast.makeText(ResponseInquiryActivity.this, "Successfully Response send!", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();

                            //Direct to ManageModulesActivity
                            Intent intent = new Intent(ResponseInquiryActivity.this, InquiriesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else {
                            try {

                                // Capture an display specific messages
                                JSONObject obj = new JSONObject(response.errorBody().string());
                                Toast.makeText(ResponseInquiryActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(ResponseInquiryActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ResponseInquiryActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
            }
        }else {
            Toast.makeText(this, "Form wasn't setup properly!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
