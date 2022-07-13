package com.example.myproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText email,password;
    Button register;
    boolean isValid = true;
    TextView signup;
    ProgressBar progressBar;
    int flag=0;

//    String filename="myfile";

    FirebaseAuth mAuth;
//    DatabaseReference dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.signup);

        progressBar = findViewById(R.id.progressBar);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register:
                setupListeners();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this, HomeScreen.class));
            finish();
        }
    }

    private void setupListeners() {
        String email1 = email.getText().toString();
        String password1 = password.getText().toString();

        if (email1.isEmpty()) {
            email.setError("You must enter email to login!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            email.setError("Enter valid email!!");
            email.requestFocus();
            return;
        }

        if (password1.isEmpty()) {
            password.setError("You must enter password to login!");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();// If login is successful
                    Intent intent = new Intent(LoginActivity.this, HomeScreen.class);  // Move to home activity
                    startActivity(intent);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    // sign-in failed
                    Toast.makeText(getApplicationContext(), "Incorrect Email or password", Toast.LENGTH_LONG).show();  //If login fails
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

//        mAuth.signInWithEmailAndPassword(email1, password1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();// If login is successful
//                        Intent intent = new Intent(LoginActivity.this, HomeScreen.class);  // Move to home activity
//                        startActivity(intent);
//                        progressBar.setVisibility(View.VISIBLE);
//                        progressBar.setVisibility(View.GONE);
//                        finish();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Incorrect Email or password", Toast.LENGTH_LONG).show();  //If login fails
//                progressBar.setVisibility(View.GONE);
//            }
//        });
    }
}