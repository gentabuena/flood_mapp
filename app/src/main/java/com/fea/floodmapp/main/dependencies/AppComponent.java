package com.fea.floodmapp.main.dependencies;

import com.fea.floodmapp.main.utils.SessionManager;
import com.fea.floodmapp.main.views.MainActivity;
import com.fea.floodmapp.main.views.SignInActivity;
import com.fea.floodmapp.main.views.SigninSignupActivity;
import com.fea.floodmapp.main.views.SplashActivity;

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

    // Utilities
    void inject(SessionManager sessionManager);
}
