package com.mioai.gamehub;


import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener
{
    private NavController navController;
    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private NavController.OnDestinationChangedListener listener;

    private TextView textViewUsernameDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.fragment);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        NavigationUI.setupWithNavController(navigationView, navController);

        textViewUsernameDrawer = navigationView.getHeaderView(0).findViewById(R.id.textViewUsernameDrawer);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.firstFragment, R.id.settingsFragment,
                R.id.aboutFragment, R.id.friendListFragment, R.id.navProfilePage, R.id.avatarGen)
                .setOpenableLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        FirebaseAuth.getInstance().addAuthStateListener(this);


        listener = (_c, destination, _b) ->
        {
            if (destination.getId() == R.id.mainFragment ||
                    destination.getId() == R.id.loginFragment ||
                    destination.getId() == R.id.registerUserFragment)
            {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        };

        navigationView.getMenu().findItem(R.id.menu_logout).setOnMenuItemClickListener(i ->
        {

            FirebaseAuth.getInstance().signOut();
            navController.navigate(R.id.action_firstFragment_to_mainFragment);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        navController.addOnDestinationChangedListener(listener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        navController.removeOnDestinationChangedListener(listener);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        navController = Navigation.findNavController(this, R.id.fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
    {
        if (firebaseAuth.getCurrentUser() != null)
        {
            textViewUsernameDrawer.setText(firebaseAuth.getCurrentUser().getDisplayName() != null ?
                    firebaseAuth.getCurrentUser().getDisplayName() : "-------");
            navController.navigate(R.id.firstFragment);
        }
    }
}