package com.fea.floodmapp.main.datamodels;

public class UserInfoModel {

    private String userID;
    private String userGmail;
    private String userFullName;
    private int userAge;
    private String userGender;
    private String userContactNumber;
    private String userAddress;
    private String userDisplayPhoto;

    public UserInfoModel(String userID, String userGmail, String userFullName, int userAge, String userGender, String userContactNumber, String userAddress, String userDisplayPhoto) {
        this.userID = userID;
        this.userGmail = userGmail;
        this.userFullName = userFullName;
        this.userAge = userAge;
        this.userGender = userGender;
        this.userContactNumber = userContactNumber;
        this.userAddress = userAddress;
        this.userDisplayPhoto = userDisplayPhoto;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserGmail() {
        return userGmail;
    }

    public void setUserGmail(String userGmail) {
        this.userGmail = userGmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserDisplayPhoto() {
        return userDisplayPhoto;
    }

    public void setUserDisplayPhoto(String userDisplayPhoto) {
        this.userDisplayPhoto = userDisplayPhoto;
    }
}
