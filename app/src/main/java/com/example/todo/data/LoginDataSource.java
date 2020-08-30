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

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication

            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<LoggedInUser> signUp(String username, String email, String password) {

        try {
            // To test data flow comment out aws calls.
//            // Attempt AWS sign up
//            Amplify.Auth.signUp(username, password,
//                    AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email).build(),
//                    result -> Log.i("Tutorial", "Result: " + result.toString()),
//                    error -> Log.e("Tutorial", "Sign up failed", error)
//            );
//            Log.i("Tutorial", "AWS sign up success");
            // Create the new user
            LoggedInUser user = new LoggedInUser(UUID.randomUUID().toString(), username);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error signing up with AWS", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}