package com.fea.floodmapp.main.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.fea.floodmapp.R;
import com.fea.floodmapp.databinding.FragmentProfileBinding;
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.SettingsActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    FragmentProfileBinding fragmentProfileBinding;
    SlidingUpPanelLayout settingsSlider;
    boolean profileOnEdit = false;

    private final int popupSettings = R.id.menu_settings;
    private final int popupEditProfile = R.id.menu_edit_profile;

    @Inject
    SessionManager sessionManager;

    Context context;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater,container,false);
        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
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
        Toast.makeText(context, "Editing privilege granted!", Toast.LENGTH_SHORT).show();

        fragmentProfileBinding.etFragProfileName.setEnabled(true);
        fragmentProfileBinding.etFragProfileAddress.setEnabled(true);
        fragmentProfileBinding.etFragProfileContact.setEnabled(true);
        fragmentProfileBinding.etFragProfileAge.setEnabled(true);
        fragmentProfileBinding.etFragProfileGender.setEnabled(true);
        fragmentProfileBinding.lltFragProfileSave.setVisibility(View.VISIBLE);
    }

    public void disableViewsForEdit(){
        profileOnEdit = false;
        Toast.makeText(context, "Editing privilege revoked!", Toast.LENGTH_SHORT).show();

        fragmentProfileBinding.etFragProfileName.setEnabled(false);
        fragmentProfileBinding.etFragProfileAddress.setEnabled(false);
        fragmentProfileBinding.etFragProfileContact.setEnabled(false);
        fragmentProfileBinding.etFragProfileAge.setEnabled(false);
        fragmentProfileBinding.etFragProfileGender.setEnabled(false);
        fragmentProfileBinding.lltFragProfileSave.setVisibility(View.GONE);
    }
}