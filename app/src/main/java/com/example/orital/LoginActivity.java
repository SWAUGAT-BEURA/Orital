package com.example.orital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText mEtUserName;
    private EditText mEtEmailAddress;
    private EditText mEtPassword;
    private String username1;
    private String password1;
    private String email1;
    FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private SharedPreferences prefManager;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mEtUserName = findViewById(R.id.et_user_name);
        mEtEmailAddress = findViewById(R.id.et_email_address);
        mEtPassword = findViewById(R.id.et_password);
        progressBar=findViewById(R.id.progressbarlogin);
        firebaseAuth=FirebaseAuth.getInstance();

        prefManager = getApplicationContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
        editor = prefManager.edit();

        boolean isUserLoggedIn = prefManager.getBoolean("ISLOGGEDIN", false);
        if (isUserLoggedIn) {
           moveToHomeScreen();
        }

    }
    public void onLoginClicked(View view) {
        String username = mEtUserName.getText().toString();
        String email = mEtEmailAddress.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            mEtUserName.setError("username required");
            return;
        }
        if (TextUtils.isEmpty(email)){
            mEtEmailAddress.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(password)){
            mEtPassword.setError("password required");
            return;
        }
        if (password.length()<6){
            mEtPassword.setError("password must have more than 6 characters");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Successfully Logged in",Toast.LENGTH_SHORT).show();
                    moveToHomeScreen();
                }else{
                    Toast.makeText(LoginActivity.this,"error occured"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
        editor.putBoolean("ISLOGGEDIN", true);
        editor.apply();
    }


    private void moveToHomeScreen() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();

    }

    public void onSignupClicked(View view) {
        Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(intent);

    }
}
