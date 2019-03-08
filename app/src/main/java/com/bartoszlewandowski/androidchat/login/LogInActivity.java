package com.bartoszlewandowski.androidchat.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bartoszlewandowski.androidchat.R;
import com.bartoszlewandowski.androidchat.chat.users.ChatUsersActivity;
import com.bartoszlewandowski.androidchat.signup.SignUpActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.edtLogInEmailOrUsername)
    EditText edtLogInEmailOrUsername;
    @BindView(R.id.edtLogInPassword)
    EditText edtLogInPassword;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        ButterKnife.bind(this);
        registerOnEnterListenerInPassword();
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOutInBackground();
        }
    }

    private void registerOnEnterListenerInPassword() {
        edtLogInPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClickBtnLogIn();
                }
                return false;
            }
        });
    }

    @OnClick(R.id.btnLogIn)
    public void onClickBtnLogIn() {
        if (edtLogInEmailOrUsername.getText().toString().equals("") ||
                edtLogInPassword.getText().toString().equals("")) {
            FancyToast.makeText(LogInActivity.this, "Email and Password is required!",
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else {
            setUpProgressDialog("Logging in...");
            String emailOrUsername = edtLogInEmailOrUsername.getText().toString();
            String password = edtLogInPassword.getText().toString();
            logInInBackground(emailOrUsername, password);
        }
    }

    private void logInInBackground(String emailOrUsername, String password) {
        ParseUser.logInInBackground(emailOrUsername, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (validateLogIn(user, e)) {
                    startChatUsersActivity();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void setUpProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private boolean validateLogIn(ParseUser user, ParseException e) {
        if (e == null) {
            FancyToast.makeText(LogInActivity.this, user.getUsername() + " is logged in",
                    Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
            return true;
        } else {
            FancyToast.makeText(LogInActivity.this, e.getMessage(),
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        }
    }

    private void startChatUsersActivity() {
        Intent intent = new Intent(LogInActivity.this, ChatUsersActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnGoToSignUp)
    public void onClickBtnGoToSignUp() {
        Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
