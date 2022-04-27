package com.fea.floodmapp.main.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.FragmentHomeBinding;
import com.fea.floodmapp.main.dependencies.MyApp;
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.EmergencyNumbersActivity;
import com.fea.floodmapp.main.views.SigninSignupActivity;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    FragmentHomeBinding fragmentHomeBinding;
    Context context;

    @Inject
    SessionManager sessionManager = new SessionManager();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        MyApp.getAppComponent().inject(this);
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
    }

    private void initializeViews() {
        fragmentHomeBinding.rltFragHomeEvacuationSites.setOnClickListener(v -> toastMessage());
        fragmentHomeBinding.rltFragHomeWeatherForecast.setOnClickListener(v -> toastMessage());
        fragmentHomeBinding.rltFragHomeSafetyPrecautions.setOnClickListener(v -> toastMessage());
        fragmentHomeBinding.rltFragHomeEmergencyNumbers.setOnClickListener(v -> gotoEmergencyNumbers());
        fragmentHomeBinding.rltFragHomeFloodPreparedness.setOnClickListener(v -> toastMessage());
        fragmentHomeBinding.rltFragHomePagasaUpdates.setOnClickListener(v -> toastMessage());
        fragmentHomeBinding.ivFragHomeSignout.setOnClickListener(v -> signOutAccount());
    }

    private void signOutAccount(){
        Log.d(TAG, "Sign out account method");
        Intent intent = new Intent(context, SigninSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sessionManager.clearCache();
        startActivity(intent);
    }

    private void toastMessage(){
        Toast.makeText(context, "I was click", Toast.LENGTH_SHORT).show();
    }

    private void gotoEmergencyNumbers(){
        Log.d(TAG, "Sign out account method");
        Intent intent = new Intent(context, EmergencyNumbersActivity.class);
        startActivity(intent);
    }
}