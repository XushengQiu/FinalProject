package com.example.finalproject.ui.Activities;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.Reservation;
import com.example.finalproject.network.ReservationApiService;
import com.example.finalproject.network.RetrofitInstance;

import com.example.finalproject.ui.Activity_Main;
import com.example.finalproject.utils.SessionDataManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pantalla_admin_reservados extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_admin_reservados);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_admin_reservados.this, Activity_Main.class);
            intent.putExtra("fragment_to_load", "reservas");
            startActivity(intent);
            finish();
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(divider);


        if ("ADMIN".equalsIgnoreCase(SessionDataManager.getInstance().getCurrentUser().getRole())) {
            fetchAllReservations();
        }
    }


    private void fetchAllReservations() {
        ReservationApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ReservationApiService.class);
        Call<List<Reservation>> call = apiService.getAllReservations();

        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reservation> reservations = response.body();
                    if (reservations.isEmpty()) {
                        Toast.makeText(Pantalla_admin_reservados.this, "No hay reservas disponibles", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter = new ReservationAdapter(reservations);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.e("API", "Error de red: ", t);
                // Mostrar un mensaje de error indicando que hubo un problema con la conexión
            }
        });
    }


    private class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

        private final List<Reservation> reservations;

        public ReservationAdapter(List<Reservation> reservations) {
            this.reservations = reservations;
        }

        @Override
        public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReservationViewHolder holder, int position) {
            holder.bind(reservations.get(position));
        }

        @Override
        public int getItemCount() {
            return reservations.size();
        }

        class ReservationViewHolder extends RecyclerView.ViewHolder {
            TextView classroomId, date, timeRange;
            Button btnDelete, btnShowQR;

            ReservationViewHolder(View itemView) {
                super(itemView);
                classroomId = itemView.findViewById(R.id.classroomId);
                date = itemView.findViewById(R.id.date);
                timeRange = itemView.findViewById(R.id.timeRange);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnShowQR = itemView.findViewById(R.id.btnShowQR);
            }

            void bind(Reservation reserva) {
                // Formateo de fecha y hora
                String rawStart = reserva.getStartTime();
                String rawEnd = reserva.getEndTime();

                String fecha = rawStart.substring(8, 10) + "-" + rawStart.substring(5, 7) + "-" + rawStart.substring(0, 4);
                String horaInicio = rawStart.substring(11, 16);
                String horaFin = rawEnd.substring(11, 16);

                // Asignar valores a los TextViews
                classroomId.setText("Aula: " + reserva.getClassroomId());
                date.setText("Fecha: " + fecha);
                timeRange.setText("Horario: Inicio " + horaInicio + " - Fin " + horaFin);

                // Mostrar y manejar el botón de eliminar reserva
                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(Pantalla_admin_reservados.this)
                            .setTitle("Confirmación")
                            .setMessage("¿Estás seguro de que quieres borrar la Reserva? Esto no se puede deshacer")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                eliminarReserva(reserva.getId(), getAdapterPosition());
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .show();
                });

                btnShowQR.setVisibility(View.GONE);
                btnShowQR.setEnabled(false);
                btnShowQR.setClickable(false);
            }
        }

        private void eliminarReserva(int reservaId, int position) {
            ReservationApiService apiService = RetrofitInstance.getRetrofitInstance(Pantalla_admin_reservados.this)
                    .create(ReservationApiService.class);

            Call<Void> call = apiService.deleteReservation(reservaId);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        reservations.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Log.e("DELETE", "Fallo al eliminar reserva: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("DELETE", "Error de red al eliminar reserva: ", t);
                }
            });
        }
    }

}