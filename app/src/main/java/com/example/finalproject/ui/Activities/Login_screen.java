package com.example.finalproject.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.models.User;
import com.example.finalproject.network.RetrofitInstance;
import com.example.finalproject.network.UserApiService;
import com.example.finalproject.ui.Pantalla_usuario_inicial;
import com.example.finalproject.utils.SessionDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                            String uid = user.getUid();
                            // Obtener el ID token (JWT)
                            if (user != null) {
                                user.getIdToken(true) // 'true' fuerza la actualización del token si es necesario
                                        .addOnCompleteListener(tokenTask -> {
                                            if (tokenTask.isSuccessful()) {
                                                String idToken = tokenTask.getResult().getToken();
                                                // Guarda el token
                                                SessionDataManager.getInstance().setFirebaseToken(idToken);
                                                fetchUserByUid(uid);
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

    private void fetchUserByUid(String uid) {
        UserApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(UserApiService.class);
        Call<User> call = apiService.getUserByUid(uid);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SessionDataManager.getInstance().setUser(response.body());
                    Log.d(SessionDataManager.getInstance().getCurrentUser().getRole(), "Login Exitoso");
                    Intent intent = new Intent(Login_screen.this, Pantalla_usuario_inicial.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API", "Error de red: ", t);
            }
        });
    }


}

