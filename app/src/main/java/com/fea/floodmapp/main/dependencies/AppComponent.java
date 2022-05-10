package com.fea.floodmapp.main.dependencies;

import com.fea.floodmapp.main.utils.ApiHelper;
import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.EvacuationSitesActivity;
import com.fea.floodmapp.main.views.MainActivity;
import com.fea.floodmapp.main.views.SettingsActivity;
import com.fea.floodmapp.main.views.SignInActivity;
import com.fea.floodmapp.main.views.SigninSignupActivity;
import com.fea.floodmapp.main.views.SplashActivity;
import com.fea.floodmapp.main.views.WeatherForecastActivity;
import com.fea.floodmapp.main.views.fragments.HomeFragment;
import com.fea.floodmapp.main.views.fragments.ProfileFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, NetworkModule.class, AppContainerModule.class})
public interface AppComponent {

    // ACTIVITIES
    void inject(SplashActivity splashActivity);
    void inject(MainActivity activity);
    void inject(SignInActivity signInActivity);
    void inject(SigninSignupActivity signinSignupActivity);
    void inject(SettingsActivity settingsActivity);
    void inject(WeatherForecastActivity weatherForecastActivity);
    void inject(EvacuationSitesActivity evacuationSitesActivity);

    // Utilities
    void inject(SessionManager sessionManager);
    void inject(CommonMethods commonMethods);
    void inject(ApiHelper apiHelper);

    // Fragments
    void inject(HomeFragment homeFragment);
    void inject(ProfileFragment profileFragment);
}
