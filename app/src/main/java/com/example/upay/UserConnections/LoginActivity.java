package com.example.upay.UserConnections;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upay.MainActivity;
import com.example.upay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText input_email_login;
    private EditText input_password_login;
    private TextView doorRegister;
    private Button button_login;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // retrieve all id
        input_email_login = findViewById(R.id.input_email_login);
        input_password_login = findViewById(R.id.input_password_login);
        button_login = findViewById(R.id.button_login);
        doorRegister = findViewById(R.id.register_section);

        // initialize progress dialog
        progressDialog = new ProgressDialog(LoginActivity.this);

        // set progress dialog message
        progressDialog.setMessage(getString(R.string.progress_dialog_login));

        // initialize firebase variable
        mAuth = FirebaseAuth.getInstance();

        doorRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to register page
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });
    }

    private void ValidateFields() {
        // get edit text value
        String password = input_password_login.getText().toString().trim();
        String email = input_email_login.getText().toString().trim();

        if (password.isEmpty()) {
            input_password_login.setError("Your password field is empty");
        }

        if (email.isEmpty()) {
            input_email_login.setError("Your email field is empty");
        }

        if (input_email_login.getError() == null && input_password_login.getError() == null) {
            LoginUser(email, password);
        }
    }

    private void LoginUser(String email, String password) {
        // show progress dialog
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // dismiss progress dialog
                            progressDialog.dismiss();

                            // Sign in success, update UI
                            UpdateUI();
                        } else {
                            // dismiss progress dialog
                            progressDialog.dismiss();

                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,
                                    "Something wrong happened try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // dismiss progress dialog
                        progressDialog.dismiss();

                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void UpdateUI() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // redirect user to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onStart();
    }
}