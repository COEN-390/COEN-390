package com.coen390.maskdetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
/**
    LoadingSpinnerView Spinner. Used Dialogs and progress bar
 */
public class LoadingSpinnerView {

    private Activity activity;
    private AlertDialog dialog;

    LoadingSpinnerView(Activity myActivity) {
        activity = myActivity;
    }

    void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_loading, null));

        //this can be changed to false. if changed to false, you cannot click outside of the loading spinner box and it will cancel.
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    void dismissLoading() {
        dialog.dismiss();
    }
}
