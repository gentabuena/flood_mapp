package com.fea.floodmapp.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.ActivitySigninSignupBinding;
import com.fea.floodmapp.main.database.DatabaseHelper;
import com.fea.floodmapp.main.datamodels.UserInfoModel;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.KeyboardUtil;
import com.fea.floodmapp.main.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import javax.inject.Inject;

public class SigninSignupActivity extends AppCompatActivity {

    ActivitySigninSignupBinding activitySigninSignupBinding;
    static String TAG = "SignInActivity";

    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount gmailAccount;
    UserInfoModel userInfoModel;
    DatabaseHelper databaseHelper;
    AlertDialog dialog;

    @Inject
    SessionManager sessionManager;

    @Inject
    CommonMethods commonMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySigninSignupBinding = ActivitySigninSignupBinding.inflate(getLayoutInflater());
        setContentView(activitySigninSignupBinding.getRoot());

        MyApp.getAppComponent().inject(this);

        databaseHelper = DatabaseHelper.getInstance(this);

        overlayImageOnStatusBar();
        clickHandlers();
        initGoogleSignin();
    }

    private void overlayImageOnStatusBar(){
        // Handle Keyboard Issue when on Fullscreen
        new KeyboardUtil(this, activitySigninSignupBinding.getRoot());

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void clickHandlers() {
        activitySigninSignupBinding.rltSigninupLogin.setOnClickListener(v -> googleIntent());

        activitySigninSignupBinding.rltSigninupRegister.setOnClickListener(v -> googleIntent());
    }

    private void openFeaHomeScreen(){
        Intent mainActivity = new Intent(this, MainActivity .class);
        startActivity(mainActivity);
        finish();
    }

    private void initGoogleSignin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.google_oauth))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        gmailAccount = GoogleSignIn.getLastSignedInAccount(this);
    }

    private void googleIntent(){
        if (commonMethods.isOnline(this)){
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        } else {
            dialog = commonMethods.getAlertDialog(this, getResources().getString(R.string.network_failure));
            dialog.show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(this, "Signin Success", Toast.LENGTH_SHORT).show();
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //Toast.makeText(this, "Signin Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null){
            Log.d(TAG, "Gmail Info: \n" + account.getDisplayName() + "\n " + account.getId() + "\n " + account.getIdToken() +  "\n " + account.getPhotoUrl());
            saveInfoToLocalDatabase(account);
            sessionManager.setToken(account.getIdToken());
            openFeaHomeScreen();
        } else {
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInfoToLocalDatabase(GoogleSignInAccount account){
        userInfoModel = new UserInfoModel(account.getId(), account.getEmail(), account.getDisplayName(), 0, "", "", "",(Objects.requireNonNull(account.getPhotoUrl()).toString()));
        /** userInfoModel.setUserID(account.getId());
        userInfoModel.setUserGmail(account.getEmail());
        userInfoModel.setUserFullName(account.getDisplayName());
        userInfoModel.setUserDisplayPhoto((Objects.requireNonNull(account.getPhotoUrl()).toString())); **/

        databaseHelper.insertGmailInfoToSQL(this, userInfoModel);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            });
}
