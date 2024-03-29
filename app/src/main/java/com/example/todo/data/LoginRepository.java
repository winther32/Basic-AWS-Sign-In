package com.example.todo.data;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.example.todo.data.model.LoggedInUser;

import java.util.concurrent.ExecutionException;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    // Not used since the call is now at the top of the call stack and not at the bottom
//    public Result<LoggedInUser> login(String email, String password) {
//        // handle login
//        Result<LoggedInUser> result = dataSource.login(email, password);
//        if (result instanceof Result.Success) {
//            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
//        }
//        return result;
//    }

    // Sign up does not sign in
    public Result<LoggedInUser> signUp(String username, String email, String password) throws NullPointerException, AmplifyException, ExecutionException, InterruptedException {
        // Attempt to run the signUp call from AWS via dataSource
        AuthSignUpResult result = dataSource.signUp(username, email, password);
        if (result != null) {
            Result<LoggedInUser> success = new Result.Success<>(new LoggedInUser("101", email));
            return success;
        } else {
            // got a null result = big failure and got nothing back from AWS and FT completed
            throw new NullPointerException("Got nothing back from AWS and FT completed");
        }
    }
}