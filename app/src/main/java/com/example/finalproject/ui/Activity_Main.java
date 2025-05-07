package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;
import com.example.finalproject.ui.Fragments.Fragment_usuario_info;
import com.example.finalproject.ui.Fragments.Fragment_usuario_inicial;
import com.example.finalproject.ui.Fragments.Fragment_usuario_reservados;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Activity_Main extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Detectar si viene un fragmento específico desde otro intent
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String fragmentToLoad = intent.getStringExtra("fragment_to_load");
            Fragment fragment = null;

            if (fragmentToLoad != null) {
                switch (fragmentToLoad) {
                    case "reservas":
                        fragment = new Fragment_usuario_reservados();
                        bottomNavigationView.setSelectedItemId(R.id.nav_reservas);
                        break;
                    case "info":
                        fragment = new Fragment_usuario_info();
                        bottomNavigationView.setSelectedItemId(R.id.nav_info);
                        break;
                    case "inicio":
                    default:
                        fragment = new Fragment_usuario_inicial();
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                }
            } else {
                // Carga por defecto si no se envió fragmento
                fragment = new Fragment_usuario_inicial();
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }

            loadFragment(fragment);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new Fragment_usuario_inicial();
            } else if (item.getItemId() == R.id.nav_reservas) {
                selectedFragment = new Fragment_usuario_reservados();
            } else if (item.getItemId() == R.id.nav_info) {
                selectedFragment = new Fragment_usuario_info();
            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
