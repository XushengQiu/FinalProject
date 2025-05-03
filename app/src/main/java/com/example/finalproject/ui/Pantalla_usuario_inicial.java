package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.Classroom;
import com.example.finalproject.network.ClassroomApiService;
import com.example.finalproject.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pantalla_usuario_inicial extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AulaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_usuario_inicial);

        ImageView imageView = findViewById(R.id.imageView);
        Button btnMisReservas = findViewById(R.id.button3);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageView.setOnClickListener(v -> startActivity(new Intent(this, Pantalla_usuario_info.class)));
        btnMisReservas.setOnClickListener(v -> startActivity(new Intent(this, Pantalla_usuario_reservados.class)));

        fetchAulas();
    }

    private void fetchAulas() {
        ClassroomApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ClassroomApiService.class);
        Call<List<Classroom>> call = apiService.getAllClassrooms();

        call.enqueue(new Callback<List<Classroom>>() {
            @Override
            public void onResponse(Call<List<Classroom>> call, Response<List<Classroom>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new AulaAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Classroom>> call, Throwable t) {
                Log.e("API", "Error de red: ", t);
            }
        });
    }

    private class AulaAdapter extends RecyclerView.Adapter<AulaAdapter.AulaViewHolder> {

        private final List<Classroom> aulas;

        public AulaAdapter(List<Classroom> aulas) {
            this.aulas = aulas;
        }

        @Override
        public AulaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aula, parent, false);
            return new AulaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AulaViewHolder holder, int position) {
            holder.bind(aulas.get(position));
        }

        @Override
        public int getItemCount() {
            return aulas.size();
        }

        class AulaViewHolder extends RecyclerView.ViewHolder {
            TextView colId, colNombre, colCapacidad;
            Button btnReservar;

            AulaViewHolder(View itemView) {
                super(itemView);
                colId = itemView.findViewById(R.id.colId);
                colNombre = itemView.findViewById(R.id.colNombre);
                colCapacidad = itemView.findViewById(R.id.colCapacidad);
                btnReservar = itemView.findViewById(R.id.btnReservar);
            }

            void bind(Classroom aula) {
                colId.setText(aula.getId());
                colNombre.setText(aula.getName());
                colCapacidad.setText(String.valueOf(aula.getCapacity()));

                btnReservar.setOnClickListener(v -> {
                    Intent intent = new Intent(Pantalla_usuario_inicial.this, Pantalla_usuario_reservar.class);
                    intent.putExtra("AULA_ID", aula.getId());
                    intent.putExtra("AULA_NAME", aula.getName());
                    intent.putExtra("AULA_BLOCK", aula.getBlock());
                    intent.putExtra("AULA_FLOOR", aula.getFloor());
                    intent.putExtra("AULA_NUMBER", aula.getNumber());
                    intent.putExtra("AULA_CAPACITY", aula.getCapacity());
                    startActivity(intent);
                });
            }
        }
    }
}

