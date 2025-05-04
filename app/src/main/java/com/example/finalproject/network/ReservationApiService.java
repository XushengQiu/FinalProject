package com.example.finalproject.network;

import com.example.finalproject.models.AvailableSlotsWrapper;
import com.example.finalproject.models.Reservation;

import retrofit2.Call;
import retrofit2.http.*;

public interface ReservationApiService {

    // Endpoint para crear una nueva reserva
    @POST("api/reservations/")
    Call<Reservation> createReservation(@Body Reservation reservation);

    // Endpoint para obtener slots disponibles
    @GET("api/reservations/available")
    Call<AvailableSlotsWrapper> getAvailableSlots(
            @Query("classroom_id") String classroomId,
            @Query("date") String date);
}