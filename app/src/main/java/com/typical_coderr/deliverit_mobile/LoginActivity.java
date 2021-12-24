package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.typical_coderr.deliverit_mobile.model.LoginCredentials;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mUsernameEditText, mPasswordEditText;
    private MaterialButton mLoginButton;
    private ProgressDialog mProgressDialog;

    private SharedPreferences sharedPrefs;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkRole();

        //Initialize views
        mUsernameEditText = findViewById(R.id.input_username);
        mPasswordEditText = findViewById(R.id.input_password);
        mLoginButton = findViewById(R.id.login_button);
        mProgressDialog = new ProgressDialog(this);

        //When login button is clicked
        mLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loginUser();

            }
        });



    }

    private void loginUser(){

        String email = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        //If there are empty fields
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter valid Credentials", Toast.LENGTH_SHORT).show();
        }else {

            //Create login credentials
            LoginCredentials loginCredentials = new LoginCredentials(email, password);
            Call<User> call = userClient.login(loginCredentials);

            //Show progress
            mProgressDialog.setMessage("Getting things ready....");
            mProgressDialog.show();

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    mProgressDialog.dismiss();

                    //200 status code
                    if (response.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();

                        //Save authorization token and role
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("auth_token", response.body().getJwtToken());
                        editor.putString("role", response.body().getUserRole());
                        editor.putString("username", response.body().getEmail());
                        editor.apply();

                        //Direct user to respective home page
                        String role = response.body().getUserRole();

                        Intent homePageIntent;

                        if(role.equals("customer")){
                            homePageIntent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                        }else if(role.equals("driver")) {
                            homePageIntent = new Intent(LoginActivity.this, DriverHomeActivity.class);
                        }else if(role.equals("supervisor")){
                            homePageIntent = new Intent(LoginActivity.this,SupervisorHomeActivity.class);
                        }else {
                            homePageIntent = new Intent(LoginActivity.this,AdminHomeActivity.class);
                        }
                        homePageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homePageIntent);
                        finish();

                    }else if(response.code() == 403){
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    System.out.println(t.toString());
                }
            });
        }
    }

    private void checkRole(){
        sharedPrefs = LoginActivity.this.getSharedPreferences("auth_preferences",Context.MODE_PRIVATE);
        String role = sharedPrefs.getString("role", null);

        if(role!=null){
            if(role.equals("customer")){
                Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }else if (role.equals("driver")){
                Intent intent = new Intent(LoginActivity.this, DriverHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }else if (role.equals("supervisor")){
                Intent intent = new Intent(LoginActivity.this, SupervisorHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }else if(role.equals("admin")){
                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }


}
