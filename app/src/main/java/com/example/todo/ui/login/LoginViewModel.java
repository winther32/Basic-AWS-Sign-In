package com.example.todo.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignInResult;
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

    // TODO: Move this into the login dataSource passing through the repo
    public void login(String email, String password) throws ExecutionException, InterruptedException {
        // FutureTask is promise that task is going to be run with a runnable and result.
        final FutureTask<?> ft = new FutureTask<>(() -> {}, null);
        AuthSignInResult[] success = { null }; // Use this as a wrapper for the info
        AuthException[] failure = { null };

        // Sign in call to AWS
        Amplify.Auth.signIn(
                email,
                password,
                result -> {
                    Log.i("Tutorial", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    Log.i("Tutorial", result.toString());
                    success[0] = result;
                    ft.run(); // Start the future task
                },
                error -> {
                    Log.e("Tutorial", error.toString());
                    failure[0] = error;
                    ft.run(); // Start the future task
                }
        );

        // This waits for the ft to run which ensures that the background task (AWS signin) finishes
        ft.get();

        // Use results from signIn call below
        // Both failure and success shouldn't be empty at this point.
       if (success[0] != null) {
           // On success of the aws sign in call create a success result
           Result<LoggedInUser> result = new Result.Success<>(new LoggedInUser(UUID.randomUUID().toString(), email));
           LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
           loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
       } else if (failure[0] != null) {
           // Aws call received an error and was unable to sign in
           Throwable reason = failure[0].getCause(); // get cause from exception
           // Get the error message string from the aws authExecption.
           String failure_cause = reason.toString().split("(:)")[1].split("\\.")[0];
           loginResult.setValue(new LoginResult(failure_cause));
       } else {
           Log.e("Tutorial", "Auth SignIn fail. No write to either wrapper");
       }

    }

    // On change of the input fields this is called to update the state of the form
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