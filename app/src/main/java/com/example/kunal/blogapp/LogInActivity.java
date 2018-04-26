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

public class LogInActivity extends AppCompatActivity {

    TextView textViewDontHaveAccount;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private static final String TAG = "LogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        textViewDontHaveAccount = findViewById(R.id.textView_dont_have_account);
        emailEditText = findViewById(R.id.editText_email);
        passwordEditText = findViewById(R.id.editText_password);

        textViewDontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.slide_in_rightt, R.anim.slide_out_left);
                finish();

            }
        });

    }

    public void login(View view){
        loginUser();
    }

    private void loginUser() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
            loginToFirebase(email, password);
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

        }

    }

    private void loginToFirebase(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LogInActivity.this, MainActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
