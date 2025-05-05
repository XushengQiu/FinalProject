package com.example.finalproject.network;

import android.content.Context;

import com.example.finalproject.utils.SessionDataManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitInstance {
    private static Retrofit retrofit;
    //classroom y user:
    //private static final String BASE_URL = "http://user-classroom-crud.ahdjd3hgg6bccqcs.spaincentral.azurecontainer.io:8080/";
    //Middleware
    //private static final String BASE_URL = "http://cumn-middleware.eddedvgkbugqf2ag.spaincentral.azurecontainer.io:3000/";
    private static final String BASE_URL = "http://10.0.2.2:4000/";
    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {

            // Interceptor para añadir el token de Firebase
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    String token = SessionDataManager.getInstance().getFirebaseToken();
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder();
                    if (token != null && !token.isEmpty()) {
                        builder.header("Authorization", "Bearer " + token);
                    }
                    Request modifiedRequest = builder.build();
                    return chain.proceed(modifiedRequest);
                }
            };

            // Interceptor para loguear la llamada
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cliente HTTP con ambos interceptores
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}