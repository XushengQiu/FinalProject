package com.example.finalproject.network;

import com.example.finalproject.models.Reservation;
import com.example.finalproject.models.AvailableSlot;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ReservationApiService {

    // Endpoint para crear una nueva reserva
    @POST("api/reservations/")
    Call<Reservation> createReservation(@Body Reservation reservation);

    // Endpoint para obtener slots disponibles
    @GET("api/reservations/available")
    Call<List<AvailableSlot>> getAvailableSlots(
            @Query("classroom_id") String classroomId,
            @Query("date") String date);
}