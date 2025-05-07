package com.example.finalproject.ui.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.R;
import com.example.finalproject.models.Reservation;
import com.example.finalproject.utils.QRManager;
import com.google.gson.Gson;

public class Pantalla_usuario_QR extends AppCompatActivity {

    private Reservation reserva;
    private ImageView qrImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_usuario_qr);

        reserva = (Reservation) getIntent().getSerializableExtra("reservation");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Extraer y formatear fecha y hora
        String rawStart = reserva.getStartTime();
        String rawEnd = reserva.getEndTime();

        String fecha = "Fecha: " + rawStart.substring(8, 10) + "-" + rawStart.substring(5, 7) + "-" + rawStart.substring(0, 4);
        String horaInicio = rawStart.substring(11, 16);
        String horaFin = rawEnd.substring(11, 16);

        // Asignar valores a los TextView
        TextView classroomIdQR = findViewById(R.id.classroomIdQR);
        TextView dateQR = findViewById(R.id.dateQR);
        TextView timeRangeQR = findViewById(R.id.timeRangeQR);

        classroomIdQR.setText("Aula: " + reserva.getClassroomId());
        dateQR.setText("Fecha: " + fecha);
        timeRangeQR.setText("Horario: Inicio " + horaInicio + " - Fin " + horaFin);

        qrImageView = findViewById(R.id.qrImageView);
        // El texto que quieres convertir en QR
        String qrText = new Gson().toJson(reserva);

        // Genera el QR usando QRManager
        QRManager qrManager = QRManager.getInstance();
        Bitmap qrBitmap = qrManager.generateQRCode(qrText);

        // Asigna la imagen QR al ImageView
        if (qrBitmap != null) {
            qrImageView.setImageBitmap(qrBitmap);
        }
    }
}
