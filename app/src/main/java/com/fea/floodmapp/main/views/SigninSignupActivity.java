package com.fea.floodmapp.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.fea.floodmapp.main.datamodels.SignInModel;
import com.fea.floodmapp.main.datamodels.UserInfoModel;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.ApiHelper;
import com.fea.floodmapp.main.utils.ApiService;
import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.KeyboardUtil;
import com.fea.floodmapp.main.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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

public class SigninSignupActivity extends AppCompatActivity {

    ActivitySigninSignupBinding activitySigninSignupBinding;
    static String TAG = "SignInActivity";

    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount gmailAccount;
    UserInfoModel userInfoModel;
    DatabaseHelper databaseHelper;
    AlertDialog dialog;
    ApiService apiService;
    int entryType; // 0 = SignIn, 1 = Signup
    String email, strResponse;
    GoogleSignInAccount fetchGmailAccount;

    @Inject
    SessionManager sessionManager;

    @Inject
    CommonMethods commonMethods;

    @Inject
    ApiHelper apiHelper;

    @Inject
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySigninSignupBinding = ActivitySigninSignupBinding.inflate(getLayoutInflater());
        setContentView(activitySigninSignupBinding.getRoot());

        MyApp.getAppComponent().inject(this);

        databaseHelper = DatabaseHelper.getInstance(this);
        apiService = apiHelper.feaappApi.create(ApiService.class);

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
        activitySigninSignupBinding.rltSigninupLogin.setOnClickListener(v -> {
            entryType = 0;
            googleIntent();
        });

        activitySigninSignupBinding.rltSigninupRegister.setOnClickListener(v -> {
            entryType = 1;
            googleIntent();
        });
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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            });

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        commonMethods.showProgressDialog(this);
        if (account != null){
            Log.d(TAG, "Gmail Info: \n" + account.getDisplayName() + "\n " + account.getId() + "\n " + account.getIdToken() +  "\n " + account.getPhotoUrl());
            //saveInfoToLocalDatabase(account);
            fetchGmailAccount = account;
            //sessionManager.setToken(account.getIdToken());
            email = account.getEmail();
            // RUN API IF SignIn or Register
            if (entryType == 0) signinAPI();
            else signupAPI();

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

    private void signinAPI(){
        apiService.signinUser(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null){
                    //dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, response.message());
                    try {
                        strResponse = response.body().string();
                        Log.d(TAG, "Response here -- " + strResponse);
                        SignInModel signInModel = gson.fromJson(strResponse, SignInModel.class);


                        JSONObject jsonObject = new JSONObject(strResponse);
                        String userAddress;
                        if (jsonObject.getJSONObject("user").has("userAddress")) {
                            userAddress = (String) jsonObject.getJSONObject("user").get("userAddress");
                        } else {
                            userAddress = "";
                        }

                        userInfoModel = new UserInfoModel(fetchGmailAccount.getId(), signInModel.getUser().getEmail(), fetchGmailAccount.getDisplayName(), signInModel.getUser().getAge(), signInModel.getUser().getGender(), signInModel.getUser().getContact(), userAddress,(Objects.requireNonNull(fetchGmailAccount.getPhotoUrl()).toString()));
                        databaseHelper.insertGmailInfoToSQL(SigninSignupActivity.this, userInfoModel);
                        sessionManager.setToken(signInModel.getAccess_token());
                        openFeaHomeScreen();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
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
                            dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, message);
                            dialog.show();
                            googleSignInClient.signOut(); // Clear google SignIn Cache, to be able to choose an Account on SignIn
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            googleSignInClient.signOut(); // Clear google SignIn Cache, to be able to choose an Account on SignIn
                        }
                    } else {
                        dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, getResources().getString(R.string.internal_server_error));
                        dialog.show();
                        googleSignInClient.signOut(); // Clear google SignIn Cache, to be able to choose an Account on SignIn
                    }
                }
                commonMethods.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!TextUtils.isEmpty(t.getMessage())){
                    dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, t.getMessage());
                } else {
                    dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, getResources().getString(R.string.internal_server_error));
                }
                commonMethods.hideProgressDialog();
                googleSignInClient.signOut(); // Clear google SignIn Cache, to be able to choose an Account on SignIn
                dialog.show();
            }
        });
    }

    private void signupAPI(){
        apiService.registerUser(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        strResponse = response.body().string();
                        Log.d(TAG, "Response here -- " + strResponse);
                        dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, getResources().getString(R.string.register_successful));
                        dialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (response.errorBody() != null){
                        try {
                            strResponse = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(strResponse);
                            String message;
                            if (jsonObject.has("message")) {
                                if (((String) jsonObject.get("message")).equalsIgnoreCase("This account is already exist.")){
                                    message = (String) jsonObject.get("message") + " You can now proceed to Sign In!";
                                } else {
                                    message = (String) jsonObject.get("message") + ".";
                                }
                            } else {
                                message = getResources().getString(R.string.internal_server_error);
                            }
                            dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, message);
                            dialog.show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, getResources().getString(R.string.internal_server_error));
                        dialog.show();
                    }
                }
                commonMethods.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!TextUtils.isEmpty(t.getMessage())){
                    dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, t.getMessage());
                } else {
                    dialog = commonMethods.getAlertDialog(SigninSignupActivity.this, getResources().getString(R.string.internal_server_error));
                }
                commonMethods.hideProgressDialog();
                dialog.show();
            }
        });
        googleSignInClient.signOut(); // Clear google SignIn Cache, to be able to choose an Account on SignIn
    }

    private void openFeaHomeScreen(){
        Intent mainActivity = new Intent(this, MainActivity .class);
        startActivity(mainActivity);
        finish();
    }
}
