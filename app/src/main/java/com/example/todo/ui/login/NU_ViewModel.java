package com.example.todo.ui.login;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;

import com.amplifyframework.AmplifyException;
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


    public void signUp(String username, String email, String password) {

        try {
            Result<LoggedInUser> result = loginRepository.signUp(username, email, password);
            // If we get to here without an exception. Should expect this to be a successful attempt
            //Downcast result to LoggedInUser
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } catch (AmplifyException e){
            // AWS returned an error
            String failure_cause = e.getCause().toString();
            Log.e("Tutorial", failure_cause); // Log full error message
            // Parse the AWS error into a readable string for UI
            failure_cause = failure_cause.split("(:)")[1].split("\\.")[0];
            // Setting to a string tells listener in newUser that this is an error. Failed attempt
            loginResult.setValue(new LoginResult(failure_cause));
        } catch (Exception e) {
            // FT did not complete and threw or got a nullPtr meaning AWS did not return anything
            Log.e("Tutorial", e.toString()); // Log the actual exception
            loginResult.setValue(new LoginResult("Unexpected Error")); // Return error string for display
        }
    }

    // On change of the input fields this is called to update the state of the form
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