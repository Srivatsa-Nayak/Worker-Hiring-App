package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference database;
    MainAdapter mainAdapter;

    Button seeAds;
//    ArrayList<MainModel> list;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_home, container , false);

        seeAds = view.findViewById(R.id.seeAds);

        recyclerView = view.findViewById(R.id.joblist);
        database = FirebaseDatabase.getInstance().getReference("WorkDetails");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        list = new ArrayList<>();

        FirebaseRecyclerOptions<MainModel> options = new FirebaseRecyclerOptions.Builder<MainModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("WorkDetails"), MainModel.class).build();

        mainAdapter = new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);

//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    user user = dataSnapshot.getValue(user.class);
//                    list.add(user);
//                }
//                mainAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });


//        seeAds.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                seeyourads();
//            }
//        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainAdapter.startListening();
    }

    public void seeyourads(View v){
        startActivity(new Intent(getContext(), yourAds.class));
    }

}