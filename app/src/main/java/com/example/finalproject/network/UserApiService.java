package com.example.finalproject.network;

import com.example.finalproject.models.NewUser;
import com.example.finalproject.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApiService {

    @GET("api/user")
    Call<List<User>> getAllUsers();

    @GET("api/user/uid/{uid}")
    Call<User> getUserByUid(@Path("uid") String uid);

    @POST("api/user")
    Call<User> createUser(@Body NewUser user);

    @PUT("api/user/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @DELETE("api/user/{id}")
    Call<Void> deleteUser(@Path("id") int id);
}