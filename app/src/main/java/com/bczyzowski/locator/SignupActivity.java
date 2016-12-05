package com.bczyzowski.locator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private TextView goBackToLogin;
    private EditText firstNameText, lastNameText, emailText, passwordText1, passwordText2;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstNameText = (EditText) findViewById(R.id.input_first_name);
        lastNameText = (EditText) findViewById(R.id.input_last_name);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText1 = (EditText) findViewById(R.id.input_password);
        passwordText2 = (EditText) findViewById(R.id.input_reEnterPassword);
        goBackToLogin = (TextView) findViewById(R.id.link_login);
        submit = (Button) findViewById(R.id.btn_signup);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        goBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signup(){
        Log.d(TAG,"Signup");

        if(validateInputData()){
            Toast.makeText(getBaseContext(), "Signup OK", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Signup !OK", Toast.LENGTH_LONG).show();
        }

    }

    private boolean validateInputData() {

        boolean result = true;
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText1.getText().toString();
        String password2 = passwordText2.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            result=false;
        }
        if (password.isEmpty() || password2.isEmpty() || !password.equals(password2)) {
            passwordText1.setError("redefine password");
            result = false;
        }
        if(firstName.isEmpty()){
            firstNameText.setError("first name can't be empty");
            result=false;
        }
        if(lastName.isEmpty()){
            lastNameText.setError("last name can't be empty");
            result=false;
        }

        return result;
    }

}
