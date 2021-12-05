package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.typical_coderr.deliverit_mobile.model.ChangePasswordRequest;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.UserClient;
import com.typical_coderr.deliverit_mobile.util.AuthHandler;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sun
 * Time: 12:38 AM
 */
public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;


    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button button;

    private String jwtToken;
    private boolean resultsRetrieved;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Check if authorization token is valid
        AuthHandler.validate(SettingsActivity.this, "driver");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        jwtToken = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        oldPasswordEditText = findViewById(R.id.input_old_password);
        newPasswordEditText = findViewById(R.id.input_new_password);
        confirmPasswordEditText = findViewById(R.id.input_confirm_password);
        button = findViewById(R.id.change_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleChangePassword();
            }
        });


    }

    private void handleChangePassword() {

        //Show progress
        mProgressDialog.setMessage("Changing password...");


        //Get user input
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        final String confirmPassword = confirmPasswordEditText.getText().toString();

        //Validate user input
        if(TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
        //Valid user input
        else {
            ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);

            Call<ResponseBody> call = userClient.changePassword(jwtToken, request);

            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //Successfully added
                    if (response.code()==200) {
                        Toast.makeText(SettingsActivity.this, "Successfully changed password!", Toast.LENGTH_SHORT).show();

                        mProgressDialog.dismiss();

                        //Clear fields
                        oldPasswordEditText.setText("");
                        newPasswordEditText.setText("");
                        confirmPasswordEditText.setText("");
                    }
                    else {

                        try {

                            // Capture an display specific messages
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            Toast.makeText(SettingsActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }catch(Exception e) {
                            Toast.makeText(SettingsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }

                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SettingsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
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
