package com.fea.floodmapp.main.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.FragmentProfileBinding;
import com.fea.floodmapp.main.database.DatabaseHelper;
import com.fea.floodmapp.main.datamodels.UserInfoModel;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.ApiHelper;
import com.fea.floodmapp.main.utils.ApiService;
import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.MainActivity;
import com.fea.floodmapp.main.views.SettingsActivity;
import com.fea.floodmapp.main.views.SigninSignupActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    FragmentProfileBinding fragmentProfileBinding;

    boolean profileOnEdit = false;
    UserInfoModel userInfoModel;

    private final int popupSettings = R.id.menu_settings;
    private final int popupEditProfile = R.id.menu_edit_profile;

    String chosenGender, strResponse;
    ApiService apiService;

    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiHelper apiHelper;
    @Inject
    Gson gson;

    Context context;
    DatabaseHelper databaseHelper;
    AlertDialog dialog;
    Dialog progressDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getAppComponent().inject(this);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater,container,false);
        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = apiHelper.feaappApi.create(ApiService.class);
        initializeViews();
        fetchFromLocalDB();
        setImageViewToGmailDP();
        setViewsInfo();
    }

    private void fetchFromLocalDB(){
        databaseHelper = DatabaseHelper.getInstance(context);
        userInfoModel = databaseHelper.getUserInfoFromLocalDB(context);

        if (!TextUtils.isEmpty(userInfoModel.getUserGender())) chosenGender = userInfoModel.getUserGender();
    }

    private void setImageViewToGmailDP(){
        Glide.with(this)
                .load(userInfoModel.getUserDisplayPhoto())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        fragmentProfileBinding.ivFragProfileDp.setBackgroundResource(R.drawable.user_icon);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        fragmentProfileBinding.ivFragProfileDp.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(fragmentProfileBinding.ivFragProfileDp);
    }

    private void setViewsInfo() {
        fragmentProfileBinding.etFragProfileEmail.setText(userInfoModel.getUserGmail());
        fragmentProfileBinding.etFragProfileName.setText(userInfoModel.getUserFullName());
        fragmentProfileBinding.etFragProfileAddress.setText(userInfoModel.getUserAddress());
        fragmentProfileBinding.etFragProfileContact.setText(userInfoModel.getUserContactNumber());
        fragmentProfileBinding.etFragProfileAge.setText(String.valueOf(userInfoModel.getUserAge()));
        fragmentProfileBinding.tvFragProfileGender.setText(userInfoModel.getUserGender());

        fragmentProfileBinding.tvFragProfileSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(fragmentProfileBinding.tvFragProfileGender.getText())
                    || TextUtils.isEmpty(fragmentProfileBinding.etFragProfileContact.getText())
                    || TextUtils.isEmpty(fragmentProfileBinding.etFragProfileAddress.getText())
                    || TextUtils.isEmpty(fragmentProfileBinding.etFragProfileAge.getText())){
                dialog = commonMethods.getAlertDialog(context, "Please fill-up the missing informations.");
                dialog.show();
            } else if (fragmentProfileBinding.etFragProfileContact.getText().length() < 10 ) { // Check if mobile number is valid
                dialog = commonMethods.getAlertDialog(context, "Please enter a valid mobile number.");
                dialog.show();
            } else if (Integer.parseInt(fragmentProfileBinding.etFragProfileAge.getText().toString()) < 7 ) { // Check if age is valid
                dialog = commonMethods.getAlertDialog(context, "Please enter a valid age.");
                dialog.show();
            }else {
                showLoadingDialog();
                updateUserInfoLocalDB();
                updateUserInfoLocalAPI();
            }
        });

        fragmentProfileBinding.tvFragProfileGender.setOnClickListener(v -> {
            showGenderDialog();
        });
    }

    private void showGenderDialog() {
        Dialog genderDialog = new Dialog(context, R.style.AlertDialog);
        genderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        genderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        genderDialog.setContentView(R.layout.dialog_gender);
        genderDialog.setCanceledOnTouchOutside(true);

        RadioButton rbMale, rbFemale;
        rbMale = genderDialog.findViewById(R.id.rb_male);
        rbFemale = genderDialog.findViewById(R.id.rb_female);

        if (!TextUtils.isEmpty(chosenGender)){
            if (chosenGender.equalsIgnoreCase("male")) rbMale.setChecked(true);
            else rbFemale.setChecked(true);
        }

        rbMale.setOnClickListener(view -> {
            chosenGender = "Male";
            fragmentProfileBinding.tvFragProfileGender.setText(chosenGender);
        });

        rbFemale.setOnClickListener(view -> {
            chosenGender = "Female";
            fragmentProfileBinding.tvFragProfileGender.setText(chosenGender);
        });

        genderDialog.show();
    }

    private void initializeViews(){
        fragmentProfileBinding.ivFragProfileSettings.setOnClickListener(this::showPopup);
    }

    private void goToSettings(){
        Intent settingsActivity = new Intent(context, SettingsActivity.class);
        startActivity(settingsActivity);
    }

    private void showPopup(View v){
        ContextThemeWrapper wrapper= new ContextThemeWrapper(context, R.style.BasePopupMenu);
        PopupMenu popupMenu = new PopupMenu(wrapper, v, Gravity.END);

        /* try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } */

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case popupSettings:
                    goToSettings();
                    break;
                case popupEditProfile:
                    if (profileOnEdit) {
                        disableViewsForEdit();
                    } else {
                        enableViewsForEdit();
                    }
                    break;
            }
            return false;
        });
        popupMenu.inflate(R.menu.menu_profile);

        MenuItem item = popupMenu.getMenu().findItem(R.id.menu_edit_profile);
        if (profileOnEdit) item.setTitle("Cancel Edit Profile");
        else item.setTitle("Edit Profile");

        popupMenu.show();
    }

    private void enableViewsForEdit(){
        profileOnEdit = true;
        Toast.makeText(context, "You can now edit your profile!", Toast.LENGTH_SHORT).show();

        //fragmentProfileBinding.etFragProfileName.setEnabled(true);
        fragmentProfileBinding.etFragProfileAddress.setEnabled(true);
        fragmentProfileBinding.etFragProfileContact.setEnabled(true);
        fragmentProfileBinding.etFragProfileAge.setEnabled(true);
        fragmentProfileBinding.etFragProfileGender.setEnabled(true);
        fragmentProfileBinding.lltFragProfileSave.setVisibility(View.VISIBLE);
        fragmentProfileBinding.tvFragProfileGender.setEnabled(true);
    }

    public void disableViewsForEdit(){
        profileOnEdit = false;
        //Toast.makeText(context, "Editing privilege revoked!", Toast.LENGTH_SHORT).show();

        //fragmentProfileBinding.etFragProfileName.setEnabled(false);
        fragmentProfileBinding.etFragProfileAddress.setEnabled(false);
        fragmentProfileBinding.etFragProfileContact.setEnabled(false);
        fragmentProfileBinding.etFragProfileAge.setEnabled(false);
        fragmentProfileBinding.etFragProfileGender.setEnabled(false);
        fragmentProfileBinding.lltFragProfileSave.setVisibility(View.GONE);
        fragmentProfileBinding.tvFragProfileGender.setEnabled(false);
    }

    private void updateUserInfoLocalDB(){
        try {
            int userAge = Integer.parseInt(fragmentProfileBinding.etFragProfileAge.getText().toString());
            String userGender = chosenGender;
            String userMobile = fragmentProfileBinding.etFragProfileContact.getText().toString();
            String userAddress = fragmentProfileBinding.etFragProfileAddress.getText().toString();
            databaseHelper.updateUserInfoOnLocalDB(context, userInfoModel.getUserID(), userAge, userGender, userMobile, userAddress);
            //Toast.makeText(context, "Update on Local Success!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(context, "Update on Local Failed!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error here -- " + e);
            e.printStackTrace();
        }
    }



    private void updateUserInfoLocalAPI(){
        int userAge = Integer.parseInt(fragmentProfileBinding.etFragProfileAge.getText().toString());
        String userGender = chosenGender;
        String userMobile = fragmentProfileBinding.etFragProfileContact.getText().toString();
        String userAddress = fragmentProfileBinding.etFragProfileAddress.getText().toString();
        apiService.updateProfile("Bearer " + sessionManager.getToken(),
                                 userInfoModel.getUserFullName(),
                                 userAddress,
                                 userAge,
                                 userGender,
                                 userMobile).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        strResponse = response.body().string();
                        Log.d(TAG, "Response here -- " + strResponse);
                        dismissLoadingDialog();
                        dialog = commonMethods.getAlertDialog(context, "Account was successfully updated!");
                        dialog.show();
                        disableViewsForEdit();
                    } catch (IOException e) {
                        e.printStackTrace();
                        dismissLoadingDialog();
                    }

                } else {
                    if (response.errorBody() != null){
                        try {
                            strResponse = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(strResponse);
                            String message;
                            if (jsonObject.has("message")) {
                                message = (String) jsonObject.get("message") + ".";
                            } else {
                                message = getResources().getString(R.string.internal_server_error);
                            }
                            dismissLoadingDialog();
                            dialog = commonMethods.getAlertDialog(context, message);
                            dialog.show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissLoadingDialog();
                if (!TextUtils.isEmpty(t.getMessage())){
                    dialog = commonMethods.getAlertDialog(context, t.getMessage());
                } else {
                    dialog = commonMethods.getAlertDialog(context, getResources().getString(R.string.internal_server_error));
                }
                dialog.show();
            }
        });
    }

    private void showLoadingDialog(){
        progressDialog = new Dialog(context, R.style.AlertDialog);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void dismissLoadingDialog(){
        progressDialog.hide();
    }
}