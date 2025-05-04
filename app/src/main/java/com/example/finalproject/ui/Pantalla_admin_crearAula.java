package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.models.Classroom;
import com.example.finalproject.network.ClassroomApiService;
import com.example.finalproject.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pantalla_admin_crearAula extends AppCompatActivity {

    private EditText etId, etName, etBlock, etFloor, etNumber, etCapacity;
    private Button btnCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_admin_crearaula);

        etId = findViewById(R.id.etId);
        etName = findViewById(R.id.etName);
        etBlock = findViewById(R.id.etBlock);
        etFloor = findViewById(R.id.etFloor);
        etNumber = findViewById(R.id.etNumber);
        etCapacity = findViewById(R.id.etCapacity);
        btnCrear = findViewById(R.id.btnCrear);

        ImageView homeIcon = findViewById(R.id.homeIcon);
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_admin_crearAula.this, Pantalla_usuario_inicial.class);
            startActivity(intent);
        });

        btnCrear.setOnClickListener(v -> crearAula());

    }

    private void crearAula() {
        // Obtener los datos ingresados por el usuario
        String id = etId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String block = etBlock.getText().toString().trim();
        int floor = Integer.parseInt(etFloor.getText().toString().trim());
        int number = Integer.parseInt(etNumber.getText().toString().trim());
        int capacity = Integer.parseInt(etCapacity.getText().toString().trim());

        // Verificamos que los campos no estén vacíos
        if (id.isEmpty() || name.isEmpty() || block.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos una nueva instancia de Classroom
        Classroom classroom = new Classroom(capacity, number, floor, block, name, id);

        // Llamada al API para crear el aula
        ClassroomApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ClassroomApiService.class);
        Call<Classroom> call = apiService.createClassroom(classroom);

        call.enqueue(new Callback<Classroom>() {
            @Override
            public void onResponse(Call<Classroom> call, Response<Classroom> response) {
                if (response.isSuccessful()) {
                    // Si la creación fue exitosa, redirigir a Pantalla_usuario_inicial
                    Intent intent = new Intent(Pantalla_admin_crearAula.this, Pantalla_usuario_inicial.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Si algo falla, mostramos un mensaje de error
                    Toast.makeText(Pantalla_admin_crearAula.this, "Error al crear el aula", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Classroom> call, Throwable t) {
                // En caso de fallo en la llamada, mostramos el error
                Toast.makeText(Pantalla_admin_crearAula.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
