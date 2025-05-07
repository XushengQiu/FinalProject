package com.example.finalproject.ui.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.AvailableSlot;
import com.example.finalproject.models.AvailableSlotsWrapper;
import com.example.finalproject.models.NewReservation;
import com.example.finalproject.models.Reservation;
import com.example.finalproject.network.ReservationApiService;
import com.example.finalproject.network.RetrofitInstance;
import com.example.finalproject.ui.Activity_Main;
import com.example.finalproject.utils.SessionDataManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pantalla_usuario_reservar extends AppCompatActivity {

    private List<AvailableSlot> lastAvailableSlots;


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

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

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

        TextView startTimeTextView = findViewById(R.id.startTimeTextView);
        TextView endTimeTextView = findViewById(R.id.endTimeTextView);

        startTimeTextView.setOnClickListener(v -> showTimePicker(startTimeTextView));
        endTimeTextView.setOnClickListener(v -> showTimePicker(endTimeTextView));

        Button reservarButton = findViewById(R.id.reservarButton);
        reservarButton.setOnClickListener(v -> {
            validateSelectedTimes();
        });

    }

    private void fetchAvailableSlots(String classroomId, String selectedDate) {

        ReservationApiService apiService = RetrofitInstance.getRetrofitInstance(Pantalla_usuario_reservar.this).create(ReservationApiService.class);

        Call<AvailableSlotsWrapper> call = apiService.getAvailableSlots(classroomId, selectedDate);

        call.enqueue(new Callback<AvailableSlotsWrapper>() {
            @Override
            public void onResponse(Call<AvailableSlotsWrapper> call, Response<AvailableSlotsWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lastAvailableSlots = response.body().getAvailableSlots();
                    mostrarSlots(lastAvailableSlots);
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

        // Mostrar u ocultar el RecyclerView
        if (slots == null || slots.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showTimePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    targetTextView.setText(formattedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }


    private void validateSelectedTimes() {
        TextView startTimeView = findViewById(R.id.startTimeTextView);
        TextView endTimeView = findViewById(R.id.endTimeTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);

        String startTimeStr = startTimeView.getText().toString();
        String endTimeStr = endTimeView.getText().toString();
        String selectedDate = dateTextView.getText().toString();

        if (startTimeStr.equals("Seleccionar") || endTimeStr.equals("Seleccionar")) {
            Toast.makeText(this, "Por favor selecciona ambas horas", Toast.LENGTH_SHORT).show();
            return;
        }

        //La hora de fin debe ser mayor que la hora de inicio
        if (startTimeStr.compareTo(endTimeStr) >= 0) {
            Toast.makeText(this, "La hora de fin debe ser mayor que la hora de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String startDateTime = selectedDate + "T" + startTimeStr + ":00";
            String endDateTime = selectedDate + "T" + endTimeStr + ":00";

            // Validar si ambos tiempos están dentro del mismo available slot
            boolean esValido = false;
            for (AvailableSlot slot : lastAvailableSlots) {
                if (startDateTime.compareTo(slot.getStart()) >= 0 &&
                        endDateTime.compareTo(slot.getEnd()) <= 0) {
                    esValido = true;
                    break;
                }
            }


            if (!esValido) {
                Toast.makeText(this, "Las horas seleccionadas no son válidas", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = SessionDataManager.getInstance().getCurrentUser().getId();
            String classroomId = getIntent().getStringExtra("AULA_ID");

            NewReservation reservation = new NewReservation(userId, classroomId, startDateTime, endDateTime);
            crearReserva(reservation);

        } catch (Exception e) {
            Toast.makeText(this, "Error procesando las horas seleccionadas", Toast.LENGTH_SHORT).show();
        }
    }

    private void crearReserva(NewReservation reservation) {
        ReservationApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(ReservationApiService.class);
        Call<Reservation> call = apiService.createReservation(reservation);

        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Pantalla_usuario_reservar.this, "Reserva realizada correctamente", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Pantalla_usuario_reservar.this, Activity_Main.class);
                    intent.putExtra("fragment_to_load", "reservas");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el back stack
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Pantalla_usuario_reservar.this, "Error al crear la reserva", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Toast.makeText(Pantalla_usuario_reservar.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
            String startFormatted = formatTime(slot.getStart());
            String endFormatted = formatTime(slot.getEnd());
            holder.slotTimeText.setText(startFormatted + " - " + endFormatted);
        }


        @Override
        public int getItemCount() {
            return availableSlots.size();
        }

        public static class SlotViewHolder extends RecyclerView.ViewHolder {
            TextView slotTimeText;

            public SlotViewHolder(View itemView) {
                super(itemView);
                slotTimeText = itemView.findViewById(R.id.slotTimeText);
            }
        }

        private String formatTime(String isoDateTime) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = isoFormat.parse(isoDateTime);
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return isoDateTime; // fallback en caso de error
            }
        }
    }

}
