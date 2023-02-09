package com.cagri.tripapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.guest).setOnClickListener(view -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginButton).setOnClickListener(view -> {
            login();
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LogInActivity.this, ProfileActivity.class));
            finish();
        }
    }

    private void login(){
        String mail = ((TextView) findViewById(R.id.userMail)).getText().toString();
        String password = ((TextView) findViewById(R.id.userPassword)).getText().toString();
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LogInActivity.this, "Authentication is succeed!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogInActivity.this, ProfileActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LogInActivity.this, "Authentication is not succeed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}