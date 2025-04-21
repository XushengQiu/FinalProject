package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.models.User;
import com.example.finalproject.network.RetrofitInstance;
import com.example.finalproject.network.UserApiService;
import com.example.finalproject.utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;

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
                                            // Guarda el token en SharedPreferences
                                            SharedPrefManager.getInstance(this).saveToken(idToken);
                                            // Redirige a Admin_screen
                                        } else {
                                            Toast.makeText(this, "Error al obtener el token: " + tokenTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                        //Crear cuenta en nuestra BD con name, schoolnumber y degree
                        User newUser = new User(0, uid, email, name, degree, schoolNumber);
                        //UserApiService userService = RetrofitInstance.getRetrofitInstance(this).create(UserApiService.class);
                        //Call<User> call = userService.createUser(newUser);
                        //Redirigir a pantalla ver classrooms



                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


    }
}
