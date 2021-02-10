package com.example.students4students.model;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    // General details
    private MutableLiveData<String> firstName;
    private MutableLiveData<String> lastName;
    private MutableLiveData<String> dateOfBirth;
    private MutableLiveData<Integer> gender;

    // Account details
    private MutableLiveData<String> email;
    private MutableLiveData<String> password;
    private MutableLiveData<String> confirmPassword;
    private MutableLiveData<String> nickname;
    private MutableLiveData<Integer> major;

    // Profile picture
    private MutableLiveData<Uri> profilePicUri;

    // General details get
    public LiveData<String> getFirstName() {
        if(firstName == null) {
            firstName = new MutableLiveData<>();
            firstName.setValue("");
        }
        return firstName;
    }
    public LiveData<String> getLastName() {
        if(lastName == null) {
            lastName = new MutableLiveData<>();
            lastName.setValue("");
        }
        return lastName;
    }
    public LiveData<String> getDateOfBirth() {
        if(dateOfBirth == null) {
            dateOfBirth = new MutableLiveData<>();
            dateOfBirth.setValue("Date of Birth");
        }
        return dateOfBirth;
    }
    public LiveData<Integer> getGender() {
        if(gender == null) {
            gender = new MutableLiveData<>();
            gender.setValue(0);
        }
        return gender;
    }

    // General details set
    public void setFirstName(String fname){ firstName.setValue(fname); }

    public void setLastName(String lname){ lastName.setValue(lname); }

    public void setGender(Integer g){ gender.setValue(g); }

    public void setDateOfBirth(String dob){ dateOfBirth.setValue(dob); }


    // Account details get
    public LiveData<String> getEmail() {
        if(email == null) {
            email = new MutableLiveData<>();
            email.setValue("");
        }
        return email;
    }
    public LiveData<String> getPassword() {
        if(password == null) {
            password = new MutableLiveData<>();
            password.setValue("");
        }
        return password;
    }
    public LiveData<String> getConfirmPassword() {
        if(confirmPassword == null) {
            confirmPassword = new MutableLiveData<>();
            confirmPassword.setValue("");
        }
        return confirmPassword;
    }
    public LiveData<String> getNickname() {
        if(nickname == null) {
            nickname = new MutableLiveData<>();
            nickname.setValue("");
        }
        return nickname;
    }
    public LiveData<Integer> getMajor() {
        if(major == null) {
            major = new MutableLiveData<>();
            major.setValue(0);
        }
        return major;
    }

    // Account details set
    public void setEmail(String e) { email.setValue(e); }

    public void setPassword(String pwd) { password.setValue(pwd); }

    public void setConfirmPassword(String pwd) { confirmPassword.setValue(pwd); }

    public void setNickname(String nick) { nickname.setValue(nick); }

    public void setMajor(Integer m) { major.setValue(m); }


    // Profile picture get
    public LiveData<Uri> getProfilePicUri(Uri defaultUri) {
        if(profilePicUri == null) {
            profilePicUri = new MutableLiveData<>();
            profilePicUri.setValue(defaultUri);
        }
        return profilePicUri;
    }

    // Profile picture set
    public void setProfilePicUri(Uri uri) { profilePicUri.setValue(uri); }

}
