package com.example.orital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    FirebaseAuth firebaseAuth;
    EditText fullname,username,mpassword,memailid,mphonenumber;
    ProgressBar progressBar;
    Button signup;
    FirebaseFirestore firestore;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        progressBar=findViewById(R.id.progressbar);
        fullname=findViewById(R.id.et_regfull_name);
        username=findViewById(R.id.et_reguser_name);
        mpassword=findViewById(R.id.et_regpassword);
        memailid=findViewById(R.id.et_regemail_address);
        mphonenumber=findViewById(R.id.et_regphone);
        signup=findViewById(R.id.signup);
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=memailid.getText().toString().trim();
                String password=mpassword.getText().toString().trim();
                final String fullname1=fullname.getText().toString().trim();
                String user1=username.getText().toString().trim();
                final String mphonenumber1=mphonenumber.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    memailid.setError("Email required");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    mpassword.setError("password required");
                    return;
                }
                if (password.length()<6){
                    mpassword.setError("password must have more than 6 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                           // FirebaseUser user1=firebaseAuth.getCurrentUser();
                            //user1.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                              //  @Override
                               // public void onSuccess(Void aVoid) {
                                 //   Toast.makeText(SignupActivity.this,"email verification has been sent",Toast.LENGTH_SHORT).show();

                                //}
                            //}).addOnFailureListener(new OnFailureListener() {
                              //  @Override
                               // public void onFailure(@NonNull Exception e) {
                                 //   Log.d(TAG,"on Failure no email sent for "+userId);
                                //}
                            //});


                            Toast.makeText(SignupActivity.this,"user successfuly created",Toast.LENGTH_SHORT).show();
                            userId=firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=firestore.collection("users").document(userId);
                            Map<String,Object> user=new HashMap<>();
                            user.put("Name",fullname1);
                            user.put("email",email);
                            user.put("phone",mphonenumber1);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"on successfull user created for"+userId);
                                }
                            });
                            startActivity(new Intent(SignupActivity.this,HomeActivity.class));
                        }else{
                            Toast.makeText(SignupActivity.this,"error occured"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });

    }

    public void onbackloginclicked(View view) {
        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}

