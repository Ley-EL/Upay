package com.example.upay.UserConnections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.upay.R;
import com.example.upay.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText input_email_login;
    private EditText input_password_login;
    private Button button_login;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        input_email_login = findViewById(R.id.input_email_login);
        input_password_login = findViewById(R.id.input_password_login);
        button_login = findViewById(R.id.button_login);
        mAuth = FirebaseAuth.getInstance();


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = input_password_login.getText().toString().trim();
                String email = input_email_login.getText().toString().trim();

                if(password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please, fill out this field", Toast.LENGTH_SHORT).show();
                    input_password_login.setError("Your password field is empty");

                }

                if(email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please, fill out this field", Toast.LENGTH_SHORT).show();
                    input_email_login.setError("Your email field is empty");

                }


                if(password.length() < 6){
                    Toast.makeText(LoginActivity.this, "Your password is too short", Toast.LENGTH_SHORT).show();
                    input_password_login.setError("Password too short, fill out this fill with a least 6 characters");
                }

                else{
                    LoginUser(email, password);

                }





            }
        });






    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Go_To_Main_Page();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Something wrong happened "+"Try again", Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Go_To_Main_Page() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}