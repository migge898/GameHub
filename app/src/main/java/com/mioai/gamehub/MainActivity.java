package com.mioai.gamehub;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
{
    private NavController navController;
    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;

    private NavController.OnDestinationChangedListener listener;


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

//        listener = (controller, destination, arguments) ->
//        {
//            if (destination.getId() == R.id.firstFragment)
//                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.design_default_color_primary_dark)));
//            else if (destination.getId() == R.id.secondFragment)
//                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.design_default_color_secondary)));
//        };
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
}