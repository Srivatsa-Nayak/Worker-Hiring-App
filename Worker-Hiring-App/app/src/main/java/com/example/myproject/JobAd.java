package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JobAd extends AppCompatActivity implements View.OnClickListener {

    EditText workname, workemail, phone, worksalary, workloc, workdesc,workreq,workstartdate;
    Button postbtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_ad);

        workname = findViewById(R.id.workname);
        workemail = findViewById(R.id.workemail);
        worksalary = findViewById(R.id.worksalary);
        phone = findViewById(R.id.phone);
        workloc = findViewById(R.id.workloc);
        workdesc = findViewById(R.id.workdesc);
        workreq = findViewById(R.id.workreq);
        workstartdate = findViewById(R.id.workstartdate);
        postbtn = findViewById(R.id.post);

        firebaseDatabase = FirebaseDatabase.getInstance();

        database = firebaseDatabase.getReference("WorkDetails");

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobAd.this, HomeScreen.class));
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.post:
                insertData();
                startActivity(new Intent(JobAd.this, HomeScreen.class));
                break;
        }
    }

    public void insertData() {


        Toast.makeText(JobAd.this, "Working!!", Toast.LENGTH_SHORT).show();


        String wname = workname.getText().toString().trim();
        String wemail = workemail.getText().toString().trim();
        String wphone = phone.getText().toString().trim();
        String wloc = workloc.getText().toString().trim();
        String wsalary = worksalary.getText().toString().trim();
        String wstartdate = workstartdate.getText().toString().trim();
        String wdesc = workdesc.getText().toString().trim();
        String wreq = workreq.getText().toString().trim();

        if(wname.isEmpty()){
            workname.setError("Name required!!");
            workname.requestFocus();
            return;
        }

        if(wemail.isEmpty()){
            workemail.setError("Email required!!");
            workemail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(wemail).matches()){
            workemail.setError("Enter a valid Email!");
            workemail.requestFocus();
            return;
        }

        if(wphone.isEmpty()){
            phone.setError("Phone number required!!");
            phone.requestFocus();
            return;
        }

        if(wloc.isEmpty()){
            workloc.setError("Work location required!!");
            workloc.requestFocus();
            return;
        }

        if(wdesc.isEmpty()){
            workdesc.setError("Work description required!!");
            workdesc.requestFocus();
            return;
        }

        if(wsalary.isEmpty()){
            worksalary.setError("Salary required!!");
            worksalary.requestFocus();
            return;
        }

        if(wstartdate.isEmpty()){
            workstartdate.setError("Start Date required!!");
            workstartdate.requestFocus();
            return;
        }

        if(wreq.isEmpty()){
            workreq.setError("Work requirements required!!");
            workreq.requestFocus();
            return;
        }

        WorkDetails workDetails = new WorkDetails(wname, wemail,wdesc,wreq, wphone, wsalary, wstartdate, wloc );

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.setValue(workDetails);
                Toast.makeText(JobAd.this, "Data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JobAd.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}