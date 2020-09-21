package com.example.todo.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.core.Amplify;
import com.example.todo.data.LoginRepository;
import com.example.todo.data.Result;
import com.example.todo.data.model.LoggedInUser;
import com.example.todo.R;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) throws ExecutionException, InterruptedException {
        // FutureTask is promise that task is going to be run with a runnable and result.
        final FutureTask<?> ft = new FutureTask<>(() -> {}, null);
        String[] success = {"empty"};

        Amplify.Auth.signIn(
                email,
                password,
                result -> {
//                    login_success(email);
                    Log.i("Tutorial", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
//                    Log.i("Tutorial", result.toString());
                    success[0] = result.toString();
                    ft.run();
                },
                error -> {
//                    login_failure(error.toString());
                    Log.e("Tutorial", error.toString());
                    ft.run();
                }
        );

        ft.get();
        Log.i("Tutorial", success[0]);
    }

    // Called in success of AWS login call
    public void login_success(String email) {
        Result<LoggedInUser> result = new Result.Success<>(new LoggedInUser(UUID.randomUUID().toString(), email));
        LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
    }

    // Called on failed AWS login call
    public void login_failure(String errorMsg) {
        loginResult.setValue(new LoginResult(R.string.login_failed));
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}