package com.example.kunal.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    TextView textViewAccountAlready;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        textViewAccountAlready = findViewById(R.id.textView_account_already);
        emailEditText = findViewById(R.id.editText_signup_email);
        passwordEditText = findViewById(R.id.editText_signup_password);
        confirmPasswordEditText = findViewById(R.id.editText_signup_confirm_password);

        textViewAccountAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();

            }
        });

    }

    public void signUp(View view){
        registerUser();
    }

    private void registerUser() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && password.length() >= 6){

            if(password.equals(confirmPassword)){
                sigmupTOFirebase(email, password);
            }

            else{
                confirmPasswordEditText.setError("Password should match");
                confirmPasswordEditText.requestFocus();
            }

        }

        else {
            if(email.isEmpty()){
                emailEditText.setError("Should not empty");
                emailEditText.requestFocus();
            }
            if(password.isEmpty()){
                passwordEditText.setError("Should not empty");
                passwordEditText.requestFocus();
            }
            if(password.length() < 6){
                passwordEditText.setError("Password length must be 6 or more");
                passwordEditText.requestFocus();
            }
            if(confirmPassword.isEmpty()){
                confirmPasswordEditText.setError("Should not empty");
                confirmPasswordEditText.requestFocus();
            }
        }

    }

    private void sigmupTOFirebase(final String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignUpActivity.this, ProfileSetupActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            startActivity(new Intent(this, ProfileSetupActivity.class));
            finish();
        }
    }
}
