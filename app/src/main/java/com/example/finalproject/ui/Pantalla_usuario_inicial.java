package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Pantalla_usuario_inicial extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AulaAdapter adapter;
    private List<Aula> aulas;
    private boolean ascending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_usuario_inicial);

        ImageView imageView = findViewById(R.id.imageView);
        Button btnMisReservas = findViewById(R.id.button3);
        recyclerView = findViewById(R.id.recyclerView);

        View mainView = findViewById(R.id.main);
        mainView.setPadding(0, 0, 0, 0);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_usuario_inicial.this, Pantalla_usuario_info.class);
            startActivity(intent);
        });

        btnMisReservas.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_usuario_inicial.this, Pantalla_usuario_reservados.class);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        aulas = getSampleData();
        adapter = new AulaAdapter(aulas, this::openReservarScreen);
        recyclerView.setAdapter(adapter);
    }

    private void openReservarScreen(String id) {
        Intent intent = new Intent(Pantalla_usuario_inicial.this, Pantalla_usuario_reservar.class);
        intent.putExtra("AULA_ID", id);
        startActivity(intent);
    }

    private List<Aula> getSampleData() {
        List<Aula> data = new ArrayList<>();
        data.add(new Aula("1001", "Aula Magna", "1", 0, 1, 40));
        data.add(new Aula("3102", "Laboratorio 1", "3", 1, 2, 10));
        data.add(new Aula("4203", "Sala de Reuniones", "4", 2, 3, 25));
        data.add(new Aula("CIC1", "Aula 101", "CIC", 0, 1, 20));
        data.add(new Aula("2303", "Laboratorio de Informática", "2", 3, 4, 35));
        return data;
    }

    // Aula model class
    public static class Aula {
        public String id, nombre, bloque;
        public int planta, numero, capacidad;

        public Aula(String id, String nombre, String bloque, int planta, int numero, int capacidad) {
            this.id = id;
            this.nombre = nombre;
            this.bloque = bloque;
            this.planta = planta;
            this.numero = numero;
            this.capacidad = capacidad;
        }
    }

    // Adapter para RecyclerView
    public class AulaAdapter extends RecyclerView.Adapter<AulaViewHolder> {

        private List<Aula> aulaList;
        private OnReservarClickListener listener;

        public AulaAdapter(List<Aula> aulaList, OnReservarClickListener listener) {
            this.aulaList = aulaList;
            this.listener = listener;
        }

        @Override
        public AulaViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new AulaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AulaViewHolder holder, int position) {
            Aula aula = aulaList.get(position);
            holder.bind(aula);
            holder.itemView.setOnClickListener(v -> listener.onClick(aula.id));
        }

        @Override
        public int getItemCount() {
            return aulaList.size();
        }
    }

    public interface OnReservarClickListener {
        void onClick(String id);
    }

    public class AulaViewHolder extends RecyclerView.ViewHolder {

        private android.widget.TextView text1;
        private android.widget.Button reservarBtn;

        public AulaViewHolder(android.view.View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            reservarBtn = new android.widget.Button(itemView.getContext());
            reservarBtn.setText("Reservar");
            ((android.widget.LinearLayout) itemView).addView(reservarBtn);
        }

        public void bind(Aula aula) {
            text1.setText(aula.id + " - " + aula.nombre + " (Cap: " + aula.capacidad + ")");
            reservarBtn.setOnClickListener(v -> openReservarScreen(aula.id));
        }
    }
}

