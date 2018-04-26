package com.example.kunal.blogapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Button logout;
    ImageView profileImageView;
    TextView nameTextView;

    private static final String TAG = "ProfileFragment";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView = view.findViewById(R.id.imageView_fragment_profile);
        nameTextView = view.findViewById(R.id.textView_fragment_profile);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue(User.class);
                Log.d(TAG, "onDataChange: " + dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue());
                Log.d(TAG, "onDataChange: " + user.getName());
                Log.d(TAG, "onDataChange: " + user.getImageURL());
                nameTextView.setText(user.getName());
                if(user.getImageURL() != null){
                    String imageUrl = user.getImageURL();
                    Picasso.get().load(imageUrl).into(profileImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(view.getContext(), LogInActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

}
