package com.bczyzowski.locator.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.bczyzowski.locator.utils.HttpUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;


public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private String chosenFriend,userEmail,token;
    private Activity activity;


    public DatePickerDialogFragment(String chosenFriend, String userEmail, String token, Activity activity) {
        this.chosenFriend = chosenFriend;
        this.userEmail = userEmail;
        this.token = token;
        this.activity=activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //act date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.setTitle("Select date");
        return datePickerDialog;
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        getHistory(chosenFriend, day, month + 1, year); // months start from 0
    }
    public void getHistory(String friendEmail, int day, int month, int year) {
        HttpUtils.retrieveHistory(getActivity(), userEmail, friendEmail, token, day, month, year, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                showLocPickerDialog(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), "No history available for this user", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }
    public void showLocPickerDialog(JSONArray jsonArray) {
        DialogFragment newFragment = new LocationPickerDialogFragment(jsonArray);
        newFragment.setCancelable(false);
        newFragment.show(activity.getFragmentManager(), "locPicker");
    }
}
