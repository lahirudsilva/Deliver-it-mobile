package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.DriverDetailsClient;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.UserClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sat
 * Time: 11:41 PM
 */
public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    private Button mSettingsBtn;
    private TextView mFirstName,mLastName,mEmail,mContact,mCity,mJoined;

    private String jwtToken;
    private boolean resultsRetrieved;

    private User user;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Check if authorization token is valid
        AuthHandler.validate(UserProfileActivity.this, "driver");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);


        mFirstName = findViewById(R.id.user_firstName);
        mLastName =findViewById(R.id.user_lastName);
        mEmail =findViewById(R.id.user_email);
        mContact =findViewById(R.id.user_contact);
        mCity = findViewById(R.id.user_city);
        mJoined = findViewById(R.id.user_joined);

        getProfile();

        mSettingsBtn = findViewById(R.id.settings);
        mSettingsBtn.setOnClickListener(view -> startActivity(new Intent(UserProfileActivity.this, SettingsActivity.class)));



    }

    private void getProfile() {
        Call<User> call = userClient.getProfileDetails(jwtToken);

        //Show progress
        mProgressDialog.setMessage("loading user details");
        mProgressDialog.show();

        call.enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                if (user != null){
                    resultsRetrieved = true;
                    mFirstName.setText(String.valueOf(user.getFirstName()));
                    mLastName.setText(String.valueOf(user.getLastName()));
                    mEmail.setText(String.valueOf(user.getEmail()));
                    mContact.setText("+"+String.valueOf(user.getContactNumber()));
                    mCity.setText(String.valueOf(user.getCity()));
                    mJoined.setText(String.valueOf(user.getJoinedOn()));
                }else{
                    Toast.makeText(UserProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
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
