package com.bczyzowski.locator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submit = (Button) findViewById(R.id.activity_login_submit_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                login();
                startActivity(intent);
                finish();
            }
        });


    }

    private void login() {

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating ...");
        progressDialog.show();

        //auth

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        };

        Handler progressDialogHandler = new Handler();
        progressDialogHandler.postDelayed(runnable, 3000);
    }
}
