package com.typical_coderr.deliverit_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.typical_coderr.deliverit_mobile.model.User;
import com.typical_coderr.deliverit_mobile.service.RetrofitClientInstance;
import com.typical_coderr.deliverit_mobile.service.UserClient;

import org.json.JSONObject;

import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Sun
 * Time: 12:40 PM
 */
public class RegisterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextInputEditText mFirstNameEditText, mLastNameEditText, mPasswordEditText, mConfirmPasswordEditText, mEmailEditText, mContactEditText, mCityEditText;

    TextView mLogin;

    private MaterialButton mRegisterButton;
    private ProgressDialog mProgressDialog;

    private SharedPreferences sharedPrefs;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);




        mFirstNameEditText = findViewById(R.id.input_firstName);
        mLastNameEditText = findViewById(R.id.otf_lastName);
        mPasswordEditText = findViewById(R.id.otf_password);
        mConfirmPasswordEditText = findViewById(R.id.otf_c_password);
        mEmailEditText = findViewById(R.id.otf_email);
        mContactEditText = findViewById(R.id.otf_contact);
        mCityEditText = findViewById(R.id.otf_city);

        mProgressDialog = new ProgressDialog(this);

        mRegisterButton = findViewById(R.id.signup_button);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

        mLogin = findViewById(R.id.link_login);
        mLogin.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void RegisterUser() {

        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String contact = mContactEditText.getText().toString();
        String city = mCityEditText.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||  TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(contact) || TextUtils.isEmpty(city)){
            Toast.makeText(this, "Please enter valid Credentials", Toast.LENGTH_SHORT).show();
        }else if(!Pattern.matches("\\S+@\\S+\\.\\S+", email)){
            Toast.makeText(this, "Please enter Email address", Toast.LENGTH_SHORT).show();
        }else if(!Pattern.matches(".{5,}", password)){
            Toast.makeText(this, "Password should contain at least 5 characters", Toast.LENGTH_SHORT).show();
        }else if(!TextUtils.equals(password, confirmPassword)){
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show();
        }else if (!Pattern.matches("^(?:0|94|\\+94)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|912)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$", contact)){
            Toast.makeText(this, "Please enter valid contact Number", Toast.LENGTH_SHORT).show();
        }else {



            //Show progress
            mProgressDialog.setMessage("Registering user....");
            mProgressDialog.show();

            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
            user.setEmail(email);
            user.setContactNumber(contact);
            user.setCity(city);


            System.out.println(user.getEmail());

            Call<ResponseBody> call = userClient.register(user);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //Successfully added
                    if (response.code() == 201) {
                        Toast.makeText(RegisterActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();

                        mProgressDialog.dismiss();

                        //Direct to ManageStudentsActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else {
                        try {

                            // Capture an display specific messages
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }

                        mProgressDialog.dismiss();
                    }

            }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
