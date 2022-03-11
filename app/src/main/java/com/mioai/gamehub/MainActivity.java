package com.mioai.gamehub;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener
{
    private NavController navController;
    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;

    private boolean doubleBackToExitPressedOnce = false;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.fragment);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        NavigationUI.setupWithNavController(navigationView, navController);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.firstFragment, R.id.secondFragment)
                .setOpenableLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((_c, destination, _b) ->
        {
            if (destination.getId() == R.id.mainFragment ||
                    destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.registerUserFragment)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            else
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
    {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null)
        {
            navController.navigate(R.id.loginFragment);
            Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
        }
        else
            navController.navigate(R.id.action_mainFragment_to_firstFragment);

    }

}