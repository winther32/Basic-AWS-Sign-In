package com.example.todo.data;

import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.example.todo.data.model.LoggedInUser;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
    public AuthSignUpResult signUp(String username, String email, String password) throws AmplifyException, ExecutionException, InterruptedException {

        final FutureTask<?> ft = new FutureTask<>(() -> {}, null);
        AuthSignUpResult[] success = {null};
        AmplifyException[] failure = {null};


        //To test data flow comment out aws calls.
        // Attempt AWS sign up
        Amplify.Auth.signUp(email, password,
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email).build(),
                result -> {
                    Log.i("Tutorial", "Result: " + result.toString());
                    success[0] = result;
                    ft.run();
                },
                error -> {
                    Log.e("Tutorial", "Sign up failed", error);
                    failure[0] = error;
                    ft.run();
                }
        );

        // Wait for the future task to run which ensure AWS call finishes when reach here.
        ft.get();

        // Check if AWS failed with exception
        if (failure[0] != null) {
            throw failure[0];
        }
        // If AWS failed without exception and did not succeed
        if (success[0] != null ){
            Log.e("Tutorial", "Amplify SignUp failure. No write to either wrapper");
        }
        return success[0]; // NOTE: this can be a null value

//            // Create the new user
//            LoggedInUser user = new LoggedInUser(UUID.randomUUID().toString(), email);
//            return new Result.Success<>(user);
//        } catch (Exception e) {
//            return new Result.Error(new IOException("Error signing up with AWS", e));
//        }
    }

    // Not currently called. Signing out from button on main
    public void logout() {
        Amplify.Auth.signOut(
                () -> Log.i("Tutorial", "Signed out successfully"),
                error -> Log.e("Tutorial", error.toString())
        );
    }
}