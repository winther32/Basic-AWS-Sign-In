package com.example.todo.ui.login;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Intent;
import android.util.Patterns;

import com.example.todo.data.LoginRepository;
import com.example.todo.data.Result;
import com.example.todo.data.model.LoggedInUser;
import com.example.todo.R;

import java.util.Objects;

public class NU_ViewModel extends ViewModel {

    private MutableLiveData<NU_FormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    NU_ViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<NU_FormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    // Not used anymore since upon new sign up the user should be dumped to the registered user sign in page
//    public void login(String username, String password) {
//        // can be launched in a separate asynchronous job
//        Result<LoggedInUser> result = loginRepository.login(username, password);
//
//        if (result instanceof Result.Success) {
//            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//        } else {
//            loginResult.setValue(new LoginResult(R.string.login_failed));
//        }
//    }

    public Boolean signUp(String username, String email, String password) {
        Result<LoggedInUser> result = loginRepository.signUp(username, email, password);
        // Boolean if Sign Up with AWS was a success or not
        if (result instanceof Result.Success) {
            return true;
        } else {
            loginResult.setValue(new LoginResult(R.string.signup_failed));
            return false;
        }
    }

    public void loginDataChanged(String username, String email, String password, String confirm) {

        // Maybe not the perfect solution for form behavior but good enough for prelim
        @Nullable Integer u = null, e = null, p = null, c = null;

        if (!isUserNameValid(username)) {
            u = R.string.invalid_username;
        }
        if (!isEmailValid(email)) {
            e = R.string.invalid_email;
        }
        if (!isPasswordValid(password)) {
            p = R.string.invalid_password;
        }
        if (!isConfirmationValid(password, confirm)) {
            c = R.string.invalid_confirm;
        }

        if ( u != null || e != null || p != null || c != null) {
            loginFormState.setValue(new NU_FormState(u, e, p, c));
        } else {
            loginFormState.setValue(new NU_FormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return username != null && !username.trim().isEmpty();
    }

    // Email validation check
    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // Password check verification
    private boolean isConfirmationValid(String password, String confirm) {
        return password != null && confirm != null && confirm.equals(password); // implicit that confirm != null
    }
}