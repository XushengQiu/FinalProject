package com.example.finalproject.ui.Fragments;

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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.Reservation;
import com.example.finalproject.network.ReservationApiService;
import com.example.finalproject.network.RetrofitInstance;
import com.example.finalproject.ui.Activities.Pantalla_admin_reservados;
import com.example.finalproject.ui.Activities.Pantalla_usuario_QR;
import com.example.finalproject.utils.SessionDataManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_usuario_reservados extends Fragment {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;

    public Fragment_usuario_reservados() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_reservados, container, false);
        Button btnVerTodasReservas = view.findViewById(R.id.btnVerTodasReservas);

        // Verificar si el usuario es ADMIN
        String role = SessionDataManager.getInstance().getCurrentUser().getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            // Si es admin, activar el botón
            btnVerTodasReservas.setVisibility(View.VISIBLE);
            btnVerTodasReservas.setEnabled(true);
            btnVerTodasReservas.setClickable(true);
            btnVerTodasReservas.setFocusable(true);
            btnVerTodasReservas.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), Pantalla_admin_reservados.class);
                startActivity(intent);
            });
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(divider);
        fetchReservationsByUid(SessionDataManager.getInstance().getCurrentUser().getId());
        return view;
    }

    private void fetchReservationsByUid(int id) {
        ReservationApiService apiService = RetrofitInstance.getRetrofitInstance(getActivity()).create(ReservationApiService.class);
        Call<List<Reservation>> call = apiService.getAllReservationsByUserId(id);

        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reservation> reservations = response.body();
                    if (reservations.isEmpty()) {
                        Toast.makeText(getActivity(), "No hay reservas disponibles", Toast.LENGTH_SHORT).show();
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
                String rawStart = reserva.getStartTime();
                String rawEnd = reserva.getEndTime();

                String fecha = rawStart.substring(8, 10) + "-" + rawStart.substring(5, 7) + "-" + rawStart.substring(0, 4);
                String horaInicio = rawStart.substring(11, 16);
                String horaFin = rawEnd.substring(11, 16);

                classroomId.setText("Aula: " + reserva.getClassroomId());
                date.setText("Fecha: " + fecha);
                timeRange.setText("Horario: Inicio " + horaInicio + " - Fin " + horaFin);

                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirmación")
                            .setMessage("¿Estás seguro de que quieres borrar la Reserva? Esto no se puede deshacer")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                eliminarReserva(reserva.getId(), getAdapterPosition());
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .show();
                });

                btnShowQR.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), Pantalla_usuario_QR.class);
                    intent.putExtra("reservation", reserva);
                    startActivity(intent);
                });
            }
        }

        private void eliminarReserva(int reservaId, int position) {
            ReservationApiService apiService = RetrofitInstance.getRetrofitInstance(getActivity())
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
