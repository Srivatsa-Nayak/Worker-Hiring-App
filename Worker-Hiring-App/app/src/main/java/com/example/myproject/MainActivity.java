package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText firstName, lastName, email, address, password, phoneno;
    Button register, login;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    Task<Void> dataBase;

    String regex = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[@#$%^&+=])" + "(?=\\S+$).{8,20}$";

    // Compile the ReGex
    Pattern p = Pattern.compile(regex);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        phoneno = findViewById(R.id.phoneno);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register:
                insertData();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent( MainActivity.this, HomeScreen.class));
            finish();
        }
    }

    //function for strong password
    boolean strongPassword(EditText text){
        CharSequence password = text.getText().toString();
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public void insertData(){
        String fname = firstName.getText().toString();
        String lname = lastName.getText().toString();
        String address1 = address.getText().toString();
        String email1 = email.getText().toString();
        String password1 = password.getText().toString();
        String phoneno1 = phoneno.getText().toString();


        //validation for wrong entry
        if(fname.isEmpty()){
            firstName.setError("First Name required!");
            firstName.requestFocus();
            return;
        }

        if(lname.isEmpty()){
            lastName.setError("Last Name required!");
            firstName.requestFocus();
            return;
        }

        if(phoneno1.isEmpty()){
            phoneno.setError("Phone Number required!");
            phoneno.requestFocus();
            return;
        }

        if(address1.isEmpty()){
            address.setError("Address required!");
            address.requestFocus();
            return;
        }

        if(email1.isEmpty()){
            email.setError("Email id required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            email.setError("Enter a valid Email!");
            email.requestFocus();
            return;
        }

        if(password1.isEmpty()){
            password.setError("Password required!");
            password.requestFocus();
            return;
        }

        if((password1.length()<8)){
            password.setError("Password must cbe atleast 8 characters!");
        }

        if(!strongPassword(password)){
            password.setError("Password must contain 1 capital letter, 1 special character and atleast 1 number");
        }

        //validation.. checking database for email
        progressBar.setVisibility(View.VISIBLE); //loading


        mAuth.createUserWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Database database = new Database(fname, lname, phoneno1,address1, email1, password1);
                    dataBase = FirebaseDatabase.getInstance().getReference().child("Accounts").child(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid()).setValue(database).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, "Registered!!",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);

                                startActivity(new Intent(MainActivity.this, HomeScreen.class));
                            }else {
                                Toast.makeText(MainActivity.this, "Not Registered!!",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else {
                    Toast.makeText(MainActivity.this, "Email already exists!!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}