package com.example.students4students.user_homepage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.students4students.Firebase;
import com.example.students4students.LoginActivity;
import com.example.students4students.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Firebase auth & currentUser
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(toolbar);

        // Hamburger button
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation view click listeners
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation header data & click listeners
        View headerView = navigationView.getHeaderView(0);

        ImageView navProfilePic =  headerView.findViewById(R.id.nav_header_profile_pic);
        TextView userNickname = headerView.findViewById(R.id.nav_header_nickname);
        TextView userEmail = headerView.findViewById(R.id.nav_header_email);

        Uri profileUri = currentUser.getPhotoUrl();
        String nickname = currentUser.getDisplayName();
        String email = currentUser.getEmail();

        if(profileUri != null) {
            Picasso.with(this).load(profileUri).noFade().into(navProfilePic);
        }
        userNickname.setText(nickname);
        userEmail.setText(email);

        // Open HomepageFragment on profile picture press
        navProfilePic.setOnClickListener(v -> {
            // Uncheck all items
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++)
                navigationView.getMenu().getItem(i).setChecked(false);

            drawerLayout.closeDrawer(GravityCompat.START);

            if(getTitle().equals("Dashboard")) return;

            setTitle("Dashboard");
            getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container,
                    new HomepageDashboardFragment()).commit();

        });

        // Open HomepageFragment the first time
        if(savedInstanceState == null) {
            setTitle("Dashboard");
            getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container,
                    new HomepageDashboardFragment()).commit();
        }

    }

    @Override
    public void onBackPressed() {
        // Close drawer on back button pressed if its open
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_my_questions:
                // Opening MyQuestionsFragment
                setTitle("My Questions");
                getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container,
                        new MyQuestionsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_my_questions);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_find_courses:
                // Opening FindCoursesFragment
                setTitle("Courses");
                getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container,
                        new FindCoursesFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_find_courses);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_find_questions:
                // Opening FindQuestionsFragment
                setTitle("Find Questions");
                getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container,
                        new FindQuestionsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_find_questions);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_logout:
                logoutUser();
                break;

            case R.id.nav_settings:
                // TODO: Open settings (android 2)
                Toast.makeText(this, "Opening settings...Next semester", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
            default: return false;
        }

        return false;
    }

    private void logoutUser() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}