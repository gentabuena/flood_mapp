package com.fea.floodmapp.main.dependencies;

import com.fea.floodmapp.main.utils.CommonMethods;
import com.fea.floodmapp.main.utils.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (includes = com.fea.floodmapp.main.dependencies.AppModule.class)
public class AppContainerModule {
    @Provides
    @Singleton
    SessionManager providesSessionManager() {
        return new SessionManager();
    }

    @Provides
    @Singleton
    CommonMethods providesCommonMethods(){ return new CommonMethods();}
}
