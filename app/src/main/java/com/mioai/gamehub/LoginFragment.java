package com.mioai.gamehub;

import static com.mioai.gamehub.utils.Constants.RC_SIGN_IN;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mioai.gamehub.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment
{
    private TextView register;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        register = view.findViewById(R.id.register);

        initGoogleSignInButton();
        initAuthViewModel();
        initGoogleSignInClient();

        register.setOnClickListener(v ->
        {
            Navigation.findNavController(v).navigate(R.id.registerUserFragment);
        });
    }

    private void initGoogleSignInClient()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void initAuthViewModel()
    {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void initGoogleSignInButton()
    {

        SignInButton signInGoogle = (SignInButton) getActivity().findViewById(R.id.sign_in_button);
        signInGoogle.setOnClickListener(v -> signIn());
    }

    // TODO: 10.02.2022: Use ActivityResultLauncher instead
    //  (see: https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)

    @SuppressWarnings("deprecation")
    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null)
                    getGoogleAuthCredential(account);
            } catch (ApiException e)
            {
                // Google Sign In failed, update UI appropriately


            }
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount)
    {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential)
    {
        authViewModel.signInWithGoogle(googleAuthCredential);
        authViewModel.getAuthenticatedUserLiveData().observe(this, authenticatedUser ->
        {
            if (authenticatedUser.isNew())
            {
                createNewUser(authenticatedUser);
            } else
            {
                goToMainActivity(authenticatedUser);
            }
        });
    }

    private void goToMainActivity(User user)
    {
        // TODO: 07.03.2022
    }

    private void createNewUser(User authenticatedUser)
    {
        authViewModel.createUser(authenticatedUser);
        authViewModel.getCreatedUserLiveData().observe(this, user ->
        {


            // TODO: 10.02.2022: Go to start screen
        });
    }

    public void onRegister(View view)
    {
        Navigation.findNavController(view).navigate(R.id.registerUserFragment);
    }
}