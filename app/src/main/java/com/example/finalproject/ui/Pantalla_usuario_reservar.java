package com.example.finalproject.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.AvailableSlot;
import com.example.finalproject.models.AvailableSlotsWrapper;
import com.example.finalproject.network.ReservationApiService;
import com.example.finalproject.network.RetrofitInstance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        Button selectDateButton = findViewById(R.id.selectDateButton);
        TextView dateTextView = findViewById(R.id.dateTextView);

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Pantalla_usuario_reservar.this,
                    (view, year, month, dayOfMonth) -> {
                        // Crear el objeto Date y formatear a "yyyy-MM-dd"
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = sdf.format(selectedCalendar.getTime());

                        // Mostrar la fecha en el TextView
                        dateTextView.setText(formattedDate);

                        // Obtener el classroomId desde el intent y llamar al fetch
                        if (intent != null && intent.getStringExtra("AULA_ID") != null) {
                            String classroomId = intent.getStringExtra("AULA_ID");
                            fetchAvailableSlots(classroomId, formattedDate);
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Limitar selección desde hoy hasta dentro de 7 días
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 7);
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            datePickerDialog.show();
        });
    }

    private void fetchAvailableSlots(String classroomId, String selectedDate) {

        ReservationApiService apiService = RetrofitInstance.getRetrofitInstance(Pantalla_usuario_reservar.this).create(ReservationApiService.class);

        Call<AvailableSlotsWrapper> call = apiService.getAvailableSlots(classroomId, selectedDate);

        call.enqueue(new Callback<AvailableSlotsWrapper>() {
            @Override
            public void onResponse(Call<AvailableSlotsWrapper> call, Response<AvailableSlotsWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AvailableSlot> slots = response.body().getAvailableSlots();
                    mostrarSlots(slots);
                } else {
                    Toast.makeText(Pantalla_usuario_reservar.this, "No se encontraron franjas horarias.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AvailableSlotsWrapper> call, Throwable t) {
                Toast.makeText(Pantalla_usuario_reservar.this, "Error al obtener las franjas: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarSlots(List<AvailableSlot> slots) {
        RecyclerView recyclerView = findViewById(R.id.slotsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SlotsAdapter adapter = new SlotsAdapter(slots);
        recyclerView.setAdapter(adapter);
    }

    private class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.SlotViewHolder> {

        private List<AvailableSlot> availableSlots;

        public SlotsAdapter(List<AvailableSlot> availableSlots) {
            this.availableSlots = availableSlots;
        }

        @Override
        public SlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
            return new SlotViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SlotViewHolder holder, int position) {
            AvailableSlot slot = availableSlots.get(position);
            holder.slotStart.setText("Inicio: " + slot.getStart());
            holder.slotEnd.setText("Fin: " + slot.getEnd());

            holder.selectSlotButton.setOnClickListener(v -> {
                // Aquí puedes agregar un TimePickerDialog o lo que necesites para seleccionar un rango
                // Y luego reservar el slot o hacer alguna acción
            });
        }

        @Override
        public int getItemCount() {
            return availableSlots.size();
        }

        public static class SlotViewHolder extends RecyclerView.ViewHolder {

            TextView slotStart, slotEnd;
            Button selectSlotButton;

            public SlotViewHolder(View itemView) {
                super(itemView);
                slotStart = itemView.findViewById(R.id.slotStart);
                slotEnd = itemView.findViewById(R.id.slotEnd);
                selectSlotButton = itemView.findViewById(R.id.selectSlotButton);
            }
        }
    }
}
