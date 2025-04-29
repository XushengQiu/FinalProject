package com.example.finalproject.network;

import android.content.Context;

import com.example.finalproject.utils.SharedPrefManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;
    //momentaneo classroom y user
    private static final String BASE_URL = "http://user-classroom-crud.ahdjd3hgg6bccqcs.spaincentral.azurecontainer.io:8080/";

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {

            // Interceptor que añade el token JWT desde SharedPreferences
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            String token = SharedPrefManager.getInstance(context).getToken();
                            Request request = chain.request();

                            if (token != null && !token.isEmpty()) {
                                request = request.newBuilder()
                                        .addHeader("Authorization", token)
                                        .build();
                            }

                            return chain.proceed(request);
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)  // Cliente con el interceptor incluido
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}