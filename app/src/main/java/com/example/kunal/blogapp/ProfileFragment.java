package com.example.kunal.blogapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    Button logout;
    ImageView profileImageView;
    TextView nameTextView;

    Button addPost;
    ImageView imageViewPost;
    EditText editTextTitle;
    EditText editTextDescription;
    String title, name, description;
    String imageUrl;

    Uri mImageUri;
    String downloadUrl;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private FirebaseStorage storage;
    private StorageReference imageStorageReference;
    private UploadTask uploadTask;

    private static final String TAG = "ProfileFragment";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");


    public ProfileFragment() {
        // Required empty public constructor
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(imageViewPost);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference("posts");

        storage = FirebaseStorage.getInstance();
        imageStorageReference = storage.getReference("posts");

        profileImageView = view.findViewById(R.id.imageView_fragment_profile);
        nameTextView = view.findViewById(R.id.textView_fragment_profile);

        addPost = view.findViewById(R.id.addPost);
        imageViewPost = view.findViewById(R.id.imageView_post);
        editTextTitle = view.findViewById(R.id.editText_title);
        editTextDescription = view.findViewById(R.id.editText_description);

        imageViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editTextTitle.getText().toString();
                description = editTextDescription.getText().toString();
                if(!title.isEmpty() && !description.isEmpty()) {
                    uploadImage();
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Must fill the title and description", Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue(User.class);
                Log.d(TAG, "onDataChange: " + dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue());
                Log.d(TAG, "onDataChange: " + user.getName());
                Log.d(TAG, "onDataChange: " + user.getImageURL());
                name = user.getName();
                nameTextView.setText(name);
                if(user.getImageURL() != null){
                    imageUrl = user.getImageURL();
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

    private void uploadImage() {

        if(mImageUri == null){
            Toast.makeText(getActivity().getApplicationContext(), "Please add image to post", Toast.LENGTH_SHORT).show();
        }

        else {

            StorageReference userImageRef = imageStorageReference.child(mAuth.getCurrentUser().getUid() + DateFormat.getDateTimeInstance().format(new Date()));
            uploadTask = userImageRef.putFile(mImageUri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    Log.d(TAG, "onSuccess: " + DateFormat.getDateTimeInstance().format(new Date()));
                    setUpDatabasePost(mAuth.getUid(), new Date().toString(), downloadUrl, title, description);
                }
            });
        }


    }

    private void setUpDatabasePost(String uid, String date, String downloadUrl, String title, String description) {

        Posts post = new Posts(downloadUrl, date, name, uid, imageUrl, title, description);
        userDatabaseReference.push().setValue(post);
        editTextDescription.setText("");
        editTextTitle.setText("");
        imageViewPost.setImageResource(R.drawable.ic_launcher_background);
        Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
    }


}
