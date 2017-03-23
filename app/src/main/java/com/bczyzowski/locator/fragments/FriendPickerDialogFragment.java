package com.bczyzowski.locator.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.List;


public class FriendPickerDialogFragment extends DialogFragment {

    private List<String> friends;
    private String chosenFriend,userEmail,token;
    private Activity activity;

    public FriendPickerDialogFragment() {
    }

    public FriendPickerDialogFragment(List<String> friends, String chosedFriend, String userEmail, String token, Activity activity) {
        this.friends = friends;
        this.chosenFriend = chosedFriend;
        this.userEmail=userEmail;
        this.token=token;
        this.activity=activity;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] friendsInCharSeq = initCharSeqForDialog(friends);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose user")
                .setItems(friendsInCharSeq, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String user = String.valueOf(friendsInCharSeq[which]);
                        if (user.contains("(")) {
                            user = user.substring(0, user.lastIndexOf("("));
                        }
                        chosenFriend = user;
                        // user chosed - > show dateDialog
                        showDatePickerDialog();
                    }
                });
        return builder.create();
    }

    private CharSequence[] initCharSeqForDialog(List<String> list) {
        CharSequence[] data = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i);
        }
        return data;
    }
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerDialogFragment(chosenFriend,userEmail,token,activity);
        newFragment.setCancelable(false);
        newFragment.show(activity.getFragmentManager(), "datePicker");
    }
}