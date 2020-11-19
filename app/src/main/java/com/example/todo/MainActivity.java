package com.example.todo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.datastore.generated.model.Priority;
import com.amplifyframework.datastore.generated.model.Todo;
import com.example.todo.data.LoginRepository;
import com.example.todo.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    Button logIn, signOut;
    TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        logIn = findViewById(R.id.login_main);
        signOut = findViewById(R.id.signOut_main);
        user = findViewById(R.id.userdisp_main);


        //TODO: This should be moved to the login Repo as a method there
        // Verify the current auth session
        Amplify.Auth.fetchAuthSession(
                result -> Log.i("Tutorial", result.toString()),
                error -> Log.e("Tutorial", error.toString())
        );

        // TODO: This should be moved to the login Repo as the isLoggedIn method
        AuthUser awsUser = Amplify.Auth.getCurrentUser();

        if (awsUser == null) {
            user.setText("Not signed in");
        } else {
            user.setText("Welcome back " + awsUser.getUsername());
        }

        logIn.setOnClickListener(v -> launchLogIn());

        // Signs current user out.
        signOut.setOnClickListener(v -> {
            // TODO: This should be called by the login repo as the logout method
            Amplify.Auth.signOut(
                    () -> Log.i("Tutorial", "Signed out successfully"),
                    error -> Log.e("Tutorial", error.toString())
            );
            user.setText("Signed out");
        });
    }

    public void launchLogIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}