package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class HomeFragment extends Fragment {



//        Button button1,button2;


        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
              View view = inflater.inflate(R.layout.fragment_home, container , false);
//            button1 = view.findViewById(R.id.jobbtn1);
//            button2 = view.findViewById(R.id.jobbtn2);
//
//
//
//
//
//
//            button1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent( getActivity() , FindJob.class);
//                    startActivity(intent);
//                }
//            });
//
//            button2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent( getActivity() , JobAd.class);
//                    startActivity(intent);
//                }
//            });

//            return inflater.inflate(R.layout.fragment_home, container, false);

            return view;
        }


    }
