package com.example.sonsor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.sonsor.fragments.HomeFragment;
import com.example.sonsor.fragments.SensorsFragment;
import com.example.sonsor.fragments.TempFragment;
import com.example.sonsor.fragments.HumidityFragment;
import com.example.sonsor.fragments.ProximityFragment;
import com.example.sonsor.fragments.MagneticFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Affiche HomeFragment par défaut
        if (savedInstanceState == null) {
            Fragment selectedFragment = new HomeFragment(); // ← ici on définit la variable
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, selectedFragment)
                    .commit();

            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_sensors) {
            selectedFragment = new SensorsFragment();
        } else if (id == R.id.nav_temp) {
            selectedFragment = new TempFragment();
        } else if (id == R.id.nav_humidity) {
            selectedFragment = new HumidityFragment();
        } else if (id == R.id.nav_proximity) {
            selectedFragment = new ProximityFragment();
        } else if (id == R.id.nav_magnetic) {
            selectedFragment = new MagneticFragment();
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SensorApp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Téléchargez SensorApp : https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Partager via"));
        }



        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, selectedFragment)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
