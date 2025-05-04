package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.models.NewUser;
import com.example.finalproject.models.User;
import com.example.finalproject.network.RetrofitInstance;
import com.example.finalproject.network.UserApiService;
import com.example.finalproject.utils.SessionDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup_screen extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText, schoolNumberEditText;
    private Spinner degreeSpinner;
    private Button registerButton, goToLoginButton;
    private FirebaseAuth mAuth;

    private final String[] degreeOptions = {"IW", "CI", "TSI", "SI", "IW+TSI", "CI+TSI"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        schoolNumberEditText = findViewById(R.id.schoolNumberEditText);
        degreeSpinner = findViewById(R.id.degreeSpinner);
        registerButton = findViewById(R.id.registerButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);
        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, degreeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        degreeSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(v -> registerUser());

        goToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login_screen.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String schoolNumber = schoolNumberEditText.getText().toString().trim();
        String degree = degreeSpinner.getSelectedItem().toString();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || schoolNumber.isEmpty() || degree.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "usuario registrado correctamente", Toast.LENGTH_SHORT).show();

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
                                        } else {
                                            Toast.makeText(this, "Error al obtener el token: " + tokenTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                        //Crear cuenta en nuestra BD con name, schoolnumber y degree
                        NewUser newUser = new NewUser(uid, name, degree, schoolNumber);
                        createUser(newUser);
                    } else {
                        Toast.makeText(this, "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void createUser(NewUser user) {
        UserApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(UserApiService.class);
        Call<User> call = apiService.createUser(user);  // Este endpoint devuelve un User, no NewUser

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User createdUser = response.body();
                    Toast.makeText(Signup_screen.this, "Usuario creado: " + createdUser.getName(), Toast.LENGTH_SHORT).show();
                    SessionDataManager.getInstance().setUser(createdUser);
                    Intent intent = new Intent(Signup_screen.this, Pantalla_usuario_inicial.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                    Toast.makeText(Signup_screen.this, "Error al crear usuario: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API", "Error de red: ", t);
                Toast.makeText(Signup_screen.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

