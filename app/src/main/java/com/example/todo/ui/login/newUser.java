package com.example.todo.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo.R;

public class newUser extends AppCompatActivity {

    private NU_ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("New User Sign In");

        viewModel = new ViewModelProvider(this, new NU_ViewModelFactory())
                .get(NU_ViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username_nu);
        final EditText emailEditText = findViewById(R.id.email_nu);
        final EditText passwordEditText = findViewById(R.id.password_nu);
        final EditText passwordConfirmEditText = findViewById(R.id.passwordConfirm_nu);
        final Button submit = findViewById(R.id.submit_nu);

        viewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            submit.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
            if (loginFormState.getConfirmError() != null) {
                passwordConfirmEditText.setError(getString(loginFormState.getConfirmError()));
            }
        });

        viewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

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
                viewModel.loginDataChanged(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(), passwordEditText.getText().toString(),
                        passwordConfirmEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        submit.setOnClickListener(v -> {
            viewModel.login(emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}