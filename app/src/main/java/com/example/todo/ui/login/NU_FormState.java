package com.example.todo.ui.login;

import androidx.annotation.Nullable;

public class NU_FormState {
    @Nullable
    private  Integer usernameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer confirmError;

    private boolean isDataValid;

    NU_FormState(@Nullable Integer usernameError, @Nullable Integer emailError,
                 @Nullable Integer passwordError, @Nullable Integer confirmError) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.confirmError = confirmError;
        this.isDataValid = false;
    }

    NU_FormState(Boolean isDataValid) {
        this.usernameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.confirmError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() { return  usernameError; }

    @Nullable
    Integer getEmailError() { return emailError; }

    @Nullable
    Integer getPasswordError() { return passwordError; }

    @Nullable
    Integer getConfirmError() { return  confirmError; }

    boolean isDataValid() { return isDataValid; }
}
