package com.example.todo.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.example.todo.R;

public class confirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        final EditText number = findViewById(R.id.confirmationEditText);
        final Button confirmBtn = findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(v -> {
            // Commented out AWS to debug data flow
            try {
//            // Send to AWS to confirm
//            Amplify.Auth.confirmSignUp(
//                    "username",
//                    "the code you received via email",
//                    result -> Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
//                    error -> Log.e("AuthQuickstart", error.toString())
//            );
                Toast.makeText(this, "Sign up confirmed", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Confirmation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}