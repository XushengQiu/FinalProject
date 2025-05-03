package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.R;

public class Pantalla_usuario_reservar extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_usuario_reservar);
        // Ajustar padding según los márgenes del sistema (barra de estado y navegación)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView imageView1 = findViewById(R.id.homeIcon3);
        imageView1.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_usuario_reservar.this, Pantalla_usuario_inicial.class);
            startActivity(intent);
        });
        ImageView imageView2 = findViewById(R.id.imageView3);
        imageView2.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_usuario_reservar.this, Pantalla_usuario_info.class);
            startActivity(intent);
        });

        TextView idFill = findViewById(R.id.IdFill);
        TextView nombreFill = findViewById(R.id.NombreFill);
        TextView bloqueFill = findViewById(R.id.BloqueFill);
        TextView plantaFill = findViewById(R.id.PlantaFill);
        TextView numeroFill = findViewById(R.id.NúmeroFill);
        TextView capacidadFill = findViewById(R.id.CapacidadFill);

        Intent intent = getIntent();
        if (intent != null) {
            idFill.setText(intent.getStringExtra("AULA_ID"));
            nombreFill.setText(intent.getStringExtra("AULA_NAME"));
            bloqueFill.setText(intent.getStringExtra("AULA_BLOCK"));
            plantaFill.setText(String.valueOf(intent.getIntExtra("AULA_FLOOR", -1)));
            numeroFill.setText(String.valueOf(intent.getIntExtra("AULA_NUMBER", -1)));
            capacidadFill.setText(String.valueOf(intent.getIntExtra("AULA_CAPACITY", -1)));
        }

    }

}
