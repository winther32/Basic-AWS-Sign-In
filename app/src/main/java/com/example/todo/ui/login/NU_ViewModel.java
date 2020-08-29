package com.example.todo.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.todo.data.LoginRepository;
import com.example.todo.data.Result;
import com.example.todo.data.model.LoggedInUser;
import com.example.todo.R;

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

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String email, String password, String confirm) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new NU_FormState(R.string.invalid_username, null, null, null));
        } else if (!isEmailValid(email)) {
            loginFormState.setValue(new NU_FormState(null, R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new NU_FormState(null, null, R.string.invalid_password, null));
        } else if (!isConfirmationValid(password, confirm)) {
            loginFormState.setValue(new NU_FormState(null, null, null, R.string.invalid_confirm));
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
        return password != null && password.equals(confirm); // implicit that confirm != null
    }
}