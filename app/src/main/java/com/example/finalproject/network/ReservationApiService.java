package com.example.finalproject.network;

import com.example.finalproject.models.AvailableSlotsWrapper;
import com.example.finalproject.models.NewReservation;
import com.example.finalproject.models.Reservation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ReservationApiService {

    @GET("api/reservation/id/{id}")
    Call<List<Reservation>> getAllReservationsByUserId(@Path("id") int id);

    @GET("/api/reservation")
    Call<List<Reservation>> getAllReservations();

    @POST("api/reservation/")
    Call<Reservation> createReservation(@Body NewReservation reservation);

    @DELETE("/api/reservation/{id}")
    Call<Void> deleteReservation(@Path("id") int id);

    @GET("api/reservation/available")
    Call<AvailableSlotsWrapper> getAvailableSlots(
            @Query("classroom_id") String classroomId,
            @Query("date") String date);
}