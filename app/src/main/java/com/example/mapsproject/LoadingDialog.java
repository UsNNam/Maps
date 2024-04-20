package com.example.mapsproject;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class LoadingDialog {
    private AlertDialog loadingDialog;
    private Context context;
    LoadingDialog(Context context)
    {
        this.context = context;
    }
    public void createLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Loading Data");

        ProgressBar progressBar = new ProgressBar(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(lp);
        builder.setView(progressBar);

        builder.setCancelable(false); // Người dùng không thể hủy bỏ dialog
        loadingDialog = builder.create();
    }
    public void showDialog()
    {
        loadingDialog.show();
    }
    public void hideDialog()
    {
        loadingDialog.dismiss();
    }
}
