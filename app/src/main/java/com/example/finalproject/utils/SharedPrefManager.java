package com.example.finalproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_TOKEN = "user_token";
    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Singleton: para acceder desde cualquier parte
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    // Guardar el token
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    // Obtener el token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Borrar el token (ej: logout)
    public void clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply();
    }
}
