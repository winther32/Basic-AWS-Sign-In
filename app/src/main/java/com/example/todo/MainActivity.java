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

        // Verify the current auth session
        Amplify.Auth.fetchAuthSession(
                result -> Log.i("Tutorial", result.toString()),
                error -> Log.e("Tutorial", error.toString())
        );

        AuthUser awsUser = Amplify.Auth.getCurrentUser();
        if (awsUser == null) {
            user.setText("Not signed in");
        } else {
            user.setText("Welcome back " + awsUser.getUsername());
        }

        logIn.setOnClickListener(v -> launchLogIn());

        // Signs current user out.
        signOut.setOnClickListener(v -> {
            Amplify.Auth.signOut(
                    () -> Log.i("Tutorial", "Signed out successfully"),
                    error -> Log.e("Tutorial", error.toString())
            );
            user.setText("Signed out");
        });

        // Log the DataStore sync with the cloud
//        Amplify.DataStore.observe(Todo.class,
//                started -> Log.i("Tutorial", "Observation began."),
//                change -> Log.i("Tutorial", change.item().toString()),
//                failure -> Log.e("Tutorial", "Observation failed.", failure),
//                () -> Log.i("Tutorial", "Observation complete.")
//        );

        // Below are examples of creating a savable item and then calling the save
        // This will add the object into the aws DataStore which will hold data locally
        // It will also work to sync with the cloud
//        Todo item = Todo.builder()
//                .name("Build Android application")
//                .description("Build an Android application using Amplify")
//                .build();
//
//        Amplify.DataStore.save(
//                item,
//                success -> Log.i("Tutorial", "Saved item: " + success.item().getName()),
//                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
//        );


        // Example code of a query to the DataStore to retrieve the data within
//        Amplify.DataStore.query(
//                Todo.class,
//                Where.matches(
//                        Todo.PRIORITY.eq(Priority.HIGH)
//                ),
//                todos -> {
//                    while (todos.hasNext()) {
//                        Todo todo = todos.next();
//
//                        Log.i("Tutorial", "==== Todo ====");
//                        Log.i("Tutorial", "Name: " + todo.getName());
//
//                        if (todo.getPriority() != null) {
//                            Log.i("Tutorial", "Priority: " + todo.getPriority().toString());
//                        }
//
//                        if (todo.getDescription() != null) {
//                            Log.i("Tutorial", "Description: " + todo.getDescription());
//                        }
//                    }
//                },
//                failure -> Log.e("Tutorial", "Could not query DataStore", failure)
//        );
    }

    public void launchLogIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}