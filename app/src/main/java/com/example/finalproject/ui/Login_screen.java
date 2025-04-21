package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_screen extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signupButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            // Obtener el usuario autenticado
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Obtener el ID token (JWT)
                            if (user != null) {
                                user.getIdToken(true) // 'true' fuerza la actualización del token si es necesario
                                        .addOnCompleteListener(tokenTask -> {
                                            if (tokenTask.isSuccessful()) {
                                                String idToken = tokenTask.getResult().getToken();
                                                // Guarda el token en SharedPreferences
                                                SharedPrefManager.getInstance(this).saveToken(idToken);

                                                // Redirige a Admin_screen
                                                Intent intent = new Intent(this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(this, "Error al obtener el token: " + tokenTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Signup_screen.class);
            startActivity(intent);
            finish();
        });
    }
}

