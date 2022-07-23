package com.example.myproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    Button seeAds;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    TextView fullName, lastName,phoneno, place;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Accounts");
        userId = user.getUid();

        fullName = view.findViewById(R.id.fullName);
        place = view.findViewById(R.id.place);
        phoneno = view.findViewById(R.id.phoneno);

        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileDet userProfile = snapshot.getValue(profileDet.class);

                if(userProfile !=null){
                    String firstname = userProfile.fname;
                    String lastname = userProfile.lname;
                    String fullname = firstname+" "+lastname;
                    String place1 = userProfile.address;
                    String phoneno1 = userProfile.phoneno;

                    fullName.setText(fullname);
                    place.setText(place1);
                    phoneno.setText(phoneno1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "No profile",Toast.LENGTH_LONG);
            }
        });


//
//        seeAds.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                seeAds = (Button) view.findViewById(R.id.seeAds);
//                startActivity(new Intent(getContext(), yourAds.class));
//                return;
//            }
//        });
        return view;
    }

    //    public void seeyourads(View v){
//        startActivity(new Intent(getContext(), yourAds.class));
//    }
}