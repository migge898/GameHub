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

    private NavController.OnDestinationChangedListener listener;
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


    private void exitOnDoubleBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        if(fragment != null && fragment.isVisible())
            exitOnDoubleBackPressed();
        else
            super.onBackPressed();
//        if (fragmentCount == 0)
//        {
//            exitOnDoubleBackPressed();
//        } else
//        {
//            if (fragmentCount > 1)
//            {
//                getFragmentManager().popBackStack();
//            } else
//            {
//                super.onBackPressed();
//            }
//
//        }
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

    }

}