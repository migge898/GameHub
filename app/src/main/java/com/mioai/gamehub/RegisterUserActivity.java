package com.mioai.gamehub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserActivity extends AppCompatActivity
{
    private TextView banner, registerUser;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = findViewById(R.id.banner);
        registerUser = (Button) findViewById(R.id.registerUser);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


    }

    public void onBannerClick(View view)
    {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onRegisterClick(View view)
    {
        registerUser();
    }

    private void registerUser()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        if (fullName.isEmpty())
        {
            editTextFullName.setError(getString(R.string.fullname_required));
            editTextFullName.requestFocus();
            return;
        }

        if (age.isEmpty())
        {
            editTextAge.setError(getString(R.string.age_required));
            editTextAge.requestFocus();
            return;
        }


        if (email.isEmpty())
        {
            editTextEmail.setError(getString(R.string.email_required));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError(getString(R.string.email_invalid));
            editTextEmail.requestFocus();
            return;
        }


        if (password.isEmpty())
        {
            editTextPassword.setError(getString(R.string.password_required));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6)
        {
            editTextPassword.setError(getString(R.string.password_too_short));
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                User user = new User(fullName, age, email);

                FirebaseDatabase.getInstance("https://fir-test-8e119-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Users")
                        .child(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task1 ->
                {
                    if (task1.isSuccessful())
                    {
                        Toast.makeText(RegisterUserActivity.this, getString(R.string.register_success), Toast.LENGTH_LONG).show();
                    } else
                    {
                        Toast.makeText(RegisterUserActivity.this, getString(R.string.register_failed), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
            } else
            {
                Toast.makeText(RegisterUserActivity.this, getString(R.string.register_failed), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}