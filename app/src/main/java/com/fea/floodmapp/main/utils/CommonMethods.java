package com.fea.floodmapp.main.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.fea.floodmapp.R;
import com.fea.floodmapp.main.dependencies.MyApp;

public class CommonMethods {

    Dialog progressDialog;

    public CommonMethods() {
        MyApp.getAppComponent().inject(this);
    }

    public boolean isOnline(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public AlertDialog getAlertDialog(Context context, String message) {
        //final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialog));
        builder.setCancelable(true);

        //builder.setMessage(Html.fromHtml("<font color='" + ContextCompat.getColor(context, R.color.black) +"'>" + message + "</font>"));
        builder.setPositiveButton(Html.fromHtml("<font color='" + ContextCompat.getColor(context, R.color.signin_button_blue) +"'>" + context.getResources().getString(R.string.text_ok) + "</font>"), (dialog, arg1) -> dialog.dismiss());
        //builder.setPositiveButton(context.getResources().getString(R.string.text_ok), (dialogInterface, i) -> dialogInterface.dismiss());

        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void showProgressDialog(Context context){
        progressDialog = new Dialog(context, R.style.AlertDialog);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.dismiss();
    }

}
