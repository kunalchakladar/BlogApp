package com.example.kunal.blogapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileSetupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1 ;
    private EditText nameEditText;
    private ImageView imageView;
    private Uri mImageUri;
    private String downloadUrl;
    
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private FirebaseStorage storage;
    private StorageReference imageStorageReference;
    private UploadTask uploadTask;

    private static final String TAG = "ProfileSetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference("users");

        storage = FirebaseStorage.getInstance();
        imageStorageReference = storage.getReference("profile_images");

        nameEditText = findViewById(R.id.editText_name);
        imageView = findViewById(R.id.imageView);
        
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ProfileSetupActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(imageView);
        }
    }

    public void next(View view){

        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");
        final String name = nameEditText.getText().toString();

        if(!name.isEmpty()) {

            if(mImageUri == null){
                setUpProfile(email, name, null);
            }

            else {

                StorageReference userImageRef = imageStorageReference.child(mAuth.getCurrentUser().getUid());
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
                        setUpProfile(email, name, downloadUrl);
                    }
                });
            }


        }
        else {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }

    }

    public void setUpProfile(String email, String name, String imgURL){
        User user = new User(name, email, imgURL);
        Log.d(TAG, "next: " + user.getImageURL());
        userDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
