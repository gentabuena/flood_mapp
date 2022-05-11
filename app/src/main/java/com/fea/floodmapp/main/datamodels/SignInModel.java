package com.fea.floodmapp.main.datamodels;

public class SignInModel {
    private UserInfoApiModel user;
    private String access_token;

    public UserInfoApiModel getUser() {
        return user;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
