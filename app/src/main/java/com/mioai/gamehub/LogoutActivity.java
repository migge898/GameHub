package com.mioai.gamehub;

import static com.mioai.gamehub.utils.Constants.USER;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LogoutActivity extends AppCompatActivity
{
    private GoogleSignInClient googleSignInClient;
    TextView name, mail;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logout = (Button) findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);

        User user = getUserFromIntent();
        initGoogleSignInClient();

        setNameView(user);
        setMailView(user);
    }

    private void setMailView(User user)
    {
        mail.setText(user.getEmail());
    }

    private void setNameView(User user)
    {
        name.setText(user.getFullName());
    }

    private void initGoogleSignInClient()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private User getUserFromIntent()
    {
        return (User) getIntent().getSerializableExtra(USER);
    }

    public void onLogoutClick(View view)
    {
    }
}