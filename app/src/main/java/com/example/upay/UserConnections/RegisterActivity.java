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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText edittext_name, edittext_email, edittext_password, editTextPasswordConfirmation;
    Button button_register;
    TextView door_login;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // retrieve all id
        edittext_email = findViewById(R.id.user_email);
        edittext_name = findViewById(R.id.username);
        edittext_password = findViewById(R.id.user_password);
        editTextPasswordConfirmation = findViewById(R.id.user_password_confirmation);
        button_register = findViewById(R.id.login_btn);
        door_login = findViewById(R.id.login_section);

        // initialize progress dialog
        progressDialog = new ProgressDialog(this);

        // set progress dialog msg
        progressDialog.setMessage(getString(R.string.progress_dialog_register));

        // set progress dialog not cancelable
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        // initialize firebase variable
        mAuth = FirebaseAuth.getInstance();

        door_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });
    }

    private void ValidateFields() {
        // get edit text value
        String username = edittext_name.getText().toString().trim();
        String email = edittext_email.getText().toString().trim();
        String password = edittext_password.getText().toString().trim();
        String passwordConfirmation = editTextPasswordConfirmation.getText().toString().trim();

        if (username.isEmpty()) {
            edittext_name.setError(getString(R.string.fill_in_field_err_msg));
        }

        if (email.isEmpty()) {
            edittext_email.setError(getString(R.string.fill_in_field_err_msg));
        }

        if (password.isEmpty()) {
            edittext_password.setError(getString(R.string.fill_in_field_err_msg));
        }

        if (passwordConfirmation.isEmpty()) {
            editTextPasswordConfirmation.setError(getString(R.string.fill_in_field_err_msg));
        }

        if (password.length() < 6) {
            edittext_password.setError(getString(R.string.min_password_length));
        }

        if (!passwordConfirmation.equals(password)) {
            editTextPasswordConfirmation.setError(getString(R.string.password_different));
        }

        if (edittext_name.getError() == null && edittext_email.getError() == null &&
                edittext_password.getError() == null && editTextPasswordConfirmation.getError() == null) {
            RegisterUser(username, email, password);
        }
    }

    private void RegisterUser(String name, String email, String password) {
        // show progress dialog
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();

                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // dismiss progress dialog
                                            progressDialog.dismiss();

                                            UpdateUI();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // dismiss progress dialog
                                            progressDialog.dismiss();

                                            Toast.makeText(RegisterActivity.this,
                                                    "Something wrong : " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // dismiss progress dialog
                            progressDialog.dismiss();

                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // dismiss progress dialog
                        progressDialog.dismiss();

                        Toast.makeText(RegisterActivity.this, "Something wrong : " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void UpdateUI() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}