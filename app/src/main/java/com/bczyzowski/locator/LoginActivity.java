package com.bczyzowski.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bczyzowski.locator.model.User;
import com.bczyzowski.locator.utils.HttpUtils;
import com.bczyzowski.locator.utils.SharedPrefReadWrite;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private Button submit;
    private TextView gotToSignup;
    private EditText emailText, passwordText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submit = (Button) findViewById(R.id.btn_login);
        gotToSignup = (TextView) findViewById(R.id.link_signup);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        gotToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (SharedPrefReadWrite.getUserFromSharedPref(getApplicationContext()) != null) {
            Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void login() {

        Log.d(TAG, "Login");

        //checking input data
        if (!validateInputData()) {
            signupFailed();
            return;
        }

        submit.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating ...");
        progressDialog.setCancelable(false);


        // invoking auth
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                authorization(emailText.getText().toString(), passwordText.getText().toString());
            }
        };
        runnable.run();
    }

    private void signupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        submit.setEnabled(true);
    }

    private void authorization(final String email, final String password) {
        progressDialog.show();
        HttpUtils.postLogin(getApplicationContext(), email, password, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                try {
                    String token = jsonObject.getString("token");
                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
                    User user = new User(email, password, token);
                    SharedPrefReadWrite.saveUserToSharedPref(user, getApplicationContext(), new String[]{firstName, lastName});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                dismissProgressDialog();
                signupFailed();
            }
        });

    }

    private boolean validateInputData() {
        boolean result = true;
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            result = false;
        } else {
            emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 5) {
            passwordText.setError("minimum 5 alphanumeric characters");
            result = false;
        } else {
            passwordText.setError(null);
        }
        return result;
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
