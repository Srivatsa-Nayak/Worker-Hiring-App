package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class JobFragment extends Fragment {


    EditText workname, workemail, phone, worksalary, workloc, workdesc,workreq,workstartdate;
    Button post;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_job, container, false);

        workname = view.findViewById(R.id.workname);
        workemail = view.findViewById(R.id.workemail);
        worksalary = view.findViewById(R.id.worksalary);
        phone = view.findViewById(R.id.phone);
        workloc = view.findViewById(R.id.workloc);
        workdesc = view.findViewById(R.id.workdesc);
        workreq = view.findViewById(R.id.workreq);
        workstartdate = view.findViewById(R.id.workstartdate);
        post = view.findViewById(R.id.post);

        firebaseDatabase = FirebaseDatabase.getInstance();

        database = firebaseDatabase.getReference("Work Details");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
//                Toast.makeText(getContext(), "Loop", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getContext(), HomeScreen.class));
            }
        });

        return view;
    }
    public void insertData() {


//        Toast.makeText(getContext(), "Working!!", Toast.LENGTH_SHORT).show();


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
//
//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                database.setValue(workDetails);
//                Toast.makeText(getContext(), "Data added", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Fail to add data " + error, Toast.LENGTH_SHORT).show();
//            }
//        });


        FirebaseDatabase.getInstance().getReference().child("Work Details").push()
                .setValue(workDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Posted!!", Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
//        Toast.makeText(getContext(), "Loop", Toast.LENGTH_SHORT).show();
    }
}