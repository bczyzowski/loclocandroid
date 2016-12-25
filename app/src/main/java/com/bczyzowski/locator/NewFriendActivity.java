package com.bczyzowski.locator;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bczyzowski.locator.utils.HttpUtils;
import com.bczyzowski.locator.utils.SharedPrefReadWrite;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class NewFriendActivity extends Activity {

    private EditText friendEmail;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        friendEmail = (EditText) findViewById(R.id.input_friend_email);
        button = (Button) findViewById(R.id.btn_send_request);

        final String userEmail = SharedPrefReadWrite.getUserFromSharedPref(getApplicationContext()).getEmail();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = friendEmail.getText().toString();
                if (validateEmail(email)) {
                    HttpUtils.addNewFriend(getApplicationContext(), userEmail, email, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Try another e-mail", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });

    }

    private boolean validateEmail(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            friendEmail.setError("enter a valid email address");
            return false;
        } else {
            return true;
        }
    }


}
