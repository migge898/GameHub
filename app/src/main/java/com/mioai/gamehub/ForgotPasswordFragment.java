package com.mioai.gamehub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment
{

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        EditText editTextResetMail = view.findViewById(R.id.resetMail);
        Button btnSendReset = view.findViewById(R.id.btnSendPasswordReset);


        btnSendReset.setOnClickListener(v ->
                {
                    firebaseAuth.sendPasswordResetEmail(editTextResetMail.getText().toString().trim());
                    Navigation.findNavController(v).navigate(R.id.action_forgotPasswordFragment_to_loginFragment2);
                }
        );

    }
}