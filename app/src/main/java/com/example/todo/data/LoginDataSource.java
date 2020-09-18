package com.example.todo.data;

import android.util.Log;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.todo.data.model.LoggedInUser;

import java.io.IOException;
import java.util.UUID;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    Boolean loginSuccess = false;
    String errorMsg;

    public Result<LoggedInUser> login(String email, String password) {
//        loginSuccess = false; // ensure boolean is set correctly on call of login;
//        try {
//            // Attempt to log in with AWS
//            Amplify.Auth.signIn(
//                    email,
//                    password,
//                    result -> {
//                        loginSuccess = true;
//                        Log.i("Tutorial", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
//                        success();
//                    },
//                    error -> {
//                        Log.e("Tutorial", error.toString());
//                        failure(error.toString());
//                    }
//            );
//            Log.i("Tutorial", "Value of loginSuccess end of try: " + loginSuccess.toString());
//        } catch (Exception e) {
//            // To catch API failure
//            return new Result.Error(new IOException("API error logging in", e));
//        }

        // Attempt to log in with AWS
        Amplify.Auth.signIn(
                email,
                password,
                result -> {
                    loginSuccess = true;
                    Log.i("Tutorial", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                },
                error -> {
                    loginSuccess = false;
                    errorMsg = error.toString();
                    Log.e("Tutorial", error.toString());
                }
        );

        Log.i("Tutorial", "Value of loginSuccess out if try/catch: " + loginSuccess.toString());
        if (loginSuccess) {
            loginSuccess = false;
            return new Result.Success<>(new LoggedInUser(UUID.randomUUID().toString(), email));
        } else {
            return new Result.Error(new IOException(errorMsg));
        }

    }


    public Result<LoggedInUser> signUp(String username, String email, String password) {

        try {
            //To test data flow comment out aws calls.
            // Attempt AWS sign up
            Amplify.Auth.signUp(email, password,
                    AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email).build(),
                    result -> Log.i("Tutorial", "Result: " + result.toString()),
                    error -> Log.e("Tutorial", "Sign up failed", error)
            );
            // Create the new user
            LoggedInUser user = new LoggedInUser(UUID.randomUUID().toString(), email);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error signing up with AWS", e));
        }
    }

    // Not currently called. Signing out from button on main
    public void logout() {
        Amplify.Auth.signOut(
                () -> Log.i("Tutorial", "Signed out successfully"),
                error -> Log.e("Tutorial", error.toString())
        );
    }
}