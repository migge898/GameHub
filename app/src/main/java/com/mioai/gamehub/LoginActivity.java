package com.mioai.gamehub;

import static com.mioai.gamehub.utils.Constants.RC_SIGN_IN;
import static com.mioai.gamehub.utils.Constants.USER;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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


public class LoginActivity extends AppCompatActivity
{
    private TextView register;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register);

        initGoogleSignInButton();
        initAuthViewModel();
        initGoogleSignInClient();
    }

    private void initGoogleSignInClient()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initAuthViewModel()
    {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void initGoogleSignInButton()
    {

        SignInButton signInGoogle = (SignInButton) findViewById(R.id.sign_in_button);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

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
        Intent intent = new Intent(LoginActivity.this, LogoutActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }

    private void createNewUser(User authenticatedUser)
    {
        authViewModel.createUser(authenticatedUser);
        authViewModel.getCreatedUserLiveData().observe(this, user ->
        {
            if (user.isCreated())
                Toast.makeText(this, user.getFullName(), Toast.LENGTH_SHORT).show();

            // TODO: 10.02.2022: Go to start screen
        });
    }

    public void onRegister(View view)
    {
        startActivity(new Intent(this, RegisterUserActivity.class));
    }
}