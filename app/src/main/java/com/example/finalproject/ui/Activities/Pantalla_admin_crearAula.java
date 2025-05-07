package com.example.finalproject.ui.Activities;

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
import com.example.finalproject.ui.Pantalla_usuario_inicial;

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
        String floorStr = etFloor.getText().toString().trim();
        String numberStr = etNumber.getText().toString().trim();
        String capacityStr = etCapacity.getText().toString().trim();

        // Verificamos que ningún campo esté vacío
        if (id.isEmpty() || name.isEmpty() || block.isEmpty() ||
                floorStr.isEmpty() || numberStr.isEmpty() || capacityStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertimos los valores numéricos
        int floor = Integer.parseInt(floorStr);
        int number = Integer.parseInt(numberStr);
        int capacity = Integer.parseInt(capacityStr);

        // Creamos una nueva instancia de Classroom
        Classroom classroom = new Classroom(capacity, number, floor, block, name, id);

        // Llamada al API para crear el aula
        ClassroomApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ClassroomApiService.class);
        Call<Classroom> call = apiService.createClassroom(classroom);

        call.enqueue(new Callback<Classroom>() {
            @Override
            public void onResponse(Call<Classroom> call, Response<Classroom> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(Pantalla_admin_crearAula.this, Pantalla_usuario_inicial.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Pantalla_admin_crearAula.this, "Error al crear el aula", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Classroom> call, Throwable t) {
                Toast.makeText(Pantalla_admin_crearAula.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
