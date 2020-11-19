package com.example.todo.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo.MainActivity;
import com.example.todo.R;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    TextView errorTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        errorTextView = findViewById(R.id.errorMsg);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button newUserButton = findViewById(R.id.newUser_login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // Watches the form state and sets the validity error messages in the edit text boxes accordingly
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });


        // Listens for the log in result to change. This occurs when a login attempt is made
        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());

                setResult(Activity.RESULT_OK);
            }
        });

        // TextWatcher listens for updates to the input fields in order to update the view model
        // The view model then updates the form state
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        // Launches the login process from the keyboard
//        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//            return false;
//        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            // Disable the ability to edit the user inputs when loading
            usernameEditText.setFocusable(false);
            passwordEditText.setFocusable(false);
            try {
                loginViewModel.login(usernameEditText.getText().toString().trim(),
                        passwordEditText.getText().toString());
            } catch (Exception e) {
                // Expect to catch exceptions where the future task doesn't complete
                Log.e("Tutorial", e.toString());
                //TODO: update the UI to reflect the failure of the log in attempt & verify no crash
            }
        });

        newUserButton.setOnClickListener(v -> {
            launchNewUserActivity();
        });

        Button confirm = findViewById(R.id.confirmBtn_login);
        confirm.setOnClickListener(v -> {
            launchConfirm();
        });
    }

    // Launcher for the new user registration log in path.
    private void launchNewUserActivity() {
        Intent intent = new Intent(this, newUser.class);
        startActivity(intent);
    }

    private void launchConfirm() {
        Intent intent = new Intent(this, confirmation.class);
//        intent.putExtra("username", "");
        startActivity(intent);
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        //Complete and destroy login activity once successful
        finish();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
        errorTextView.setText(errorString);
    }
}