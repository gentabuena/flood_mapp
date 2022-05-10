package com.fea.floodmapp.main.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
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
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.MainActivity;
import com.fea.floodmapp.main.views.SettingsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    FragmentProfileBinding fragmentProfileBinding;

    boolean profileOnEdit = false;
    UserInfoModel userInfoModel;

    private final int popupSettings = R.id.menu_settings;
    private final int popupEditProfile = R.id.menu_edit_profile;

    @Inject
    SessionManager sessionManager;

    Context context;
    DatabaseHelper databaseHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initializeViews();
        fetchFromLocalDB();
        setImageViewToGmailDP();
        setViewsInfo();
    }

    private void setViewsInfo() {
        fragmentProfileBinding.etFragProfileEmail.setText(userInfoModel.getUserGmail());
        fragmentProfileBinding.etFragProfileName.setText(userInfoModel.getUserFullName());
        fragmentProfileBinding.etFragProfileAddress.setText(userInfoModel.getUserAddress());
        fragmentProfileBinding.etFragProfileContact.setText(userInfoModel.getUserContactNumber());
        fragmentProfileBinding.etFragProfileAge.setText(String.valueOf(userInfoModel.getUserAge()));
        fragmentProfileBinding.etFragProfileGender.setText(userInfoModel.getUserGender());

        fragmentProfileBinding.tvFragProfileSave.setOnClickListener(v -> {
            updateUserInfoLocalDB();
        });
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
    }

    private void fetchFromLocalDB(){
        databaseHelper = DatabaseHelper.getInstance(context);
        userInfoModel = databaseHelper.getUserInfoFromLocalDB(context);
    }

    private void updateUserInfoLocalDB(){
        try {
            int userAge = Integer.parseInt(fragmentProfileBinding.etFragProfileAge.getText().toString());
            String userGender = fragmentProfileBinding.etFragProfileGender.getText().toString();
            String userMobile = fragmentProfileBinding.etFragProfileContact.getText().toString();
            String userAddress = fragmentProfileBinding.etFragProfileAddress.getText().toString();
            databaseHelper.updateUserInfoOnLocalDB(context, userInfoModel.getUserID(), userAge, userGender, userMobile, userAddress);
            Toast.makeText(context, "Update on Local Success!", Toast.LENGTH_SHORT).show();

            disableViewsForEdit();
        } catch (Exception e) {
            Toast.makeText(context, "Update on Local Failed!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error here -- " + e);
            e.printStackTrace();
        }
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
}