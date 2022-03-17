package com.mioai.gamehub;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserFragment extends Fragment
{
    private TextView banner, registerUser;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        banner = view.findViewById(R.id.banner);
        registerUser = (Button) view.findViewById(R.id.registerUser);

        editTextFullName = (EditText) view.findViewById(R.id.username);
        editTextAge = (EditText) view.findViewById(R.id.age);
        editTextEmail = (EditText) view.findViewById(R.id.login_email);
        editTextPassword = (EditText) view.findViewById(R.id.login_password);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        navController = Navigation.findNavController(registerUser);
        registerUser.setOnClickListener(v -> registerUser());
    }

    /**
     * If successful redirects to {@link FirstFragment}
     */
    private void registerUser()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String username = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        if (username.isEmpty())
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
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();

                firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(updateTask ->
                        {
                            if (updateTask.isSuccessful())
                            {
                                User user = new User(
                                        firebaseUser.getUid(),
                                        firebaseUser.getDisplayName(),
                                        email,
                                        age);

                                FirebaseDatabase.getInstance("https://gamehub-4786a-default-rtdb.europe-west1.firebasedatabase.app/")
                                        .getReference("Users")
                                        .child(FirebaseAuth.getInstance()
                                                .getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task1 ->
                                {
                                    if (task1.isSuccessful())
                                    {
                                        Toast.makeText(getActivity(), getString(R.string.register_success), Toast.LENGTH_LONG).show();
                                        navController.navigate(R.id.action_registerUserFragment_to_firstFragment);
                                    } else
                                    {
                                        Toast.makeText(getActivity(), getString(R.string.register_failed), Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                });
                                Toast.makeText(getActivity(), "USER UPDATED!", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getActivity(), "ERROR UPDATING SUER!", Toast.LENGTH_SHORT).show();
                        });


            } else
            {
                Toast.makeText(getActivity(), getString(R.string.register_failed), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }

        });


    }
}