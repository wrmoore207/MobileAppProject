package com.example.scheduleapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userPhoneNumber = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();

    private final MutableLiveData<String> currentUserId = new MutableLiveData<>();

    public void setUserData(String name, String phoneNumber, String email) {
        userName.setValue(name);
        userPhoneNumber.setValue(phoneNumber);
        userEmail.setValue(email);
    }
    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }
}
