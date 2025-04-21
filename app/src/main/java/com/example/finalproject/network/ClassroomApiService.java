package com.example.finalproject.network;

import com.example.finalproject.models.Classroom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ClassroomApiService {

    @GET("/api/classroom")
    Call<List<Classroom>> getAllClassrooms();

    @GET("/api/classroom/{id}")
    Call<Classroom> getClassroomById(@Path("id") String id);

    @POST("/api/classroom")
    Call<Classroom> createClassroom(@Body Classroom classroom);

    @PUT("/api/classroom/{id}")
    Call<Classroom> updateClassroom(@Path("id") String id, @Body Classroom classroom);

    @DELETE("/api/classroom/{id}")
    Call<Void> deleteClassroom(@Path("id") String id);
}