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

        // Not used anymore since the login call is now a the top.
//    public Result<LoggedInUser> login(String email, String password) {
//        // Attempt to log in with AWS
//        Amplify.Auth.signIn(
//                email,
//                password,
//                result -> {
//                    loginSuccess = true;
//                    Log.i("Tutorial", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
//                },
//                error -> {
//                    loginSuccess = false;
//                    errorMsg = error.toString();
//                    Log.e("Tutorial", error.toString());
//                }
//        );
//
//        Log.i("Tutorial", "Value of loginSuccess out if try/catch: " + loginSuccess.toString());
//        if (loginSuccess) {
//            loginSuccess = false;
//            return new Result.Success<>(new LoggedInUser(UUID.randomUUID().toString(), email));
//        } else {
//            return new Result.Error(new IOException(errorMsg));
//        }
//
//    }


    // Bug Theory:
    // This call suffers from the same issue that the login did before re-arch.
    // The AWS result is taking longer to return and is not done before the code launches the
    // next lines creating a new user. This is why it seems to crash.
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