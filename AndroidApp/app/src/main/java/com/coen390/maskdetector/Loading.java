package com.coen390.maskdetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;

public class Loading {

    private Activity activity;
    private AlertDialog dialog;

    Loading(Activity myActivity) {
        activity = myActivity;

    }

    void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_loading, null));

        //this can be changed to true. if changed to true, you can click outside of the loading spinner box and it will cancel. 
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

    }

    void dismissLoading() {
        dialog.dismiss();
    }
}
