package com.example.todo.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo.R;

public class newUser extends AppCompatActivity {

    private NU_ViewModel viewModel;
    TextView errorTextView;

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

        errorTextView = findViewById(R.id.NU_errorMsg);

        // Watches the form state and sets the validity error messages in the edit text boxes accordingly
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

        // Listens for the log in result to change. This occurs when a login attempt is made
        viewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                // Re-enable the ability to change the data fields.
                usernameEditText.setEnabled(true);
                emailEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                passwordConfirmEditText.setEnabled(true);
                // Re-enable submit button
                submit.setEnabled(true);
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
                viewModel.loginDataChanged(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(), passwordEditText.getText().toString(),
                        passwordConfirmEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmEditText.addTextChangedListener(afterTextChangedListener);

        // Code to be able to hit the check on the keyboard to auto launch submit
//        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                viewModel.login(emailEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//            return false;
//        });

        // TODO1: Verify which is used to actually log in (email or username)
        // --> AWS likely confirgured to accept both but will want to just use email
        submit.setOnClickListener(v -> {
            //TODO: figure out the loading wheel.

            // Disable the ability to edit the user inputs when loading
            usernameEditText.setEnabled(false);
            emailEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            passwordConfirmEditText.setEnabled(false);
            // Disable the button. Prevent button spam.
            submit.setEnabled(false);

            viewModel.signUp(usernameEditText.getText().toString().toLowerCase(), emailEditText.getText().toString().toLowerCase(),
                    passwordEditText.getText().toString());
        });

    }

    private void launchConfirmation(String email) {
        Intent intent = new Intent(this, confirmation.class);
        intent.putExtra("username", email);
        startActivity(intent);
    }

    // Successful sign up completed. This launches next step -> email verification (acct. confirmation)
    private void updateUiWithUser(LoggedInUserView model) {
        // Create a toast to indicate successful sign up
        Toast.makeText(getApplicationContext(), getString(R.string.signup_success), Toast.LENGTH_LONG).show();
        // Launch the new confirmation activity.
        launchConfirmation(model.getDisplayName());
        finish(); //Close the sign up activity
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), R.string.signup_failed, Toast.LENGTH_SHORT).show();
        errorTextView.setText(errorString);
    }
}