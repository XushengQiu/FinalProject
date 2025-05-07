package com.example.finalproject.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.R;
import com.example.finalproject.models.User;
import com.example.finalproject.network.RetrofitInstance;
import com.example.finalproject.network.UserApiService;
import com.example.finalproject.ui.Activities.Login_screen;
import com.example.finalproject.utils.SessionDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pantalla_usuario_info extends AppCompatActivity {

    private Button btnBorrarCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_usuario_info);
        // Ajustar padding según los márgenes del sistema (barra de estado y navegación)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.textView3);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        ImageView imageView = findViewById(R.id.homeIcon);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Pantalla_usuario_info.this, Pantalla_usuario_inicial.class);
            startActivity(intent);
        });

        User user = SessionDataManager.getInstance().getCurrentUser();
        if (user != null) {
            ((TextView) findViewById(R.id.textNombreFill)).setText(user.getName());
            ((TextView) findViewById(R.id.textGradoFill)).setText(user.getDegree());
            ((TextView) findViewById(R.id.textMatriculaFill)).setText(user.getSchoolNumber());
            ((TextView) findViewById(R.id.textView13)).setText(user.getRole());
        }
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        btnBorrarCuenta.setOnClickListener(v -> {
            // Crear el pop-up de confirmación
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que quieres borrar tu cuenta? Borrar tu cuenta implica borrar también tus datos y es irreversible.")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Si el usuario confirma, llama al API para borrar la cuenta
                        borrarCuenta(SessionDataManager.getInstance().getCurrentUser().getId());
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Si el usuario cancela, cierra el pop-up
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void borrarCuentaFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // El usuario está autenticado, se puede borrar la cuenta
            user.delete()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // La cuenta se ha eliminado con éxito
                            Log.d("Firebase", "Cuenta eliminada exitosamente.");
                            logout();
                        } else {
                            // Hubo un error al intentar eliminar la cuenta
                            Log.e("Firebase", "Error al eliminar la cuenta: " + task.getException());
                            Toast.makeText(getApplicationContext(), "No se pudo eliminar la cuenta.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si el usuario no está autenticado, mostrar mensaje de error
            Log.e("Firebase", "No hay usuario autenticado.");
            Toast.makeText(getApplicationContext(), "No estás autenticado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void borrarCuenta(int userId) {
        //1º se borra de nuetsra BD
        // Crear la instancia de Retrofit
        UserApiService apiService = RetrofitInstance.getRetrofitInstance(this).create(UserApiService.class);
        // Llamada al endpoint DELETE para borrar la cuenta
        Call<Void> call = apiService.deleteUser(userId);
        // Ejecutar la llamada de manera asíncrona
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Si la respuesta es exitosa, redirigir al home o realizar la acción necesaria
                    Log.d("USER", "Cuenta eliminada exitosamente");
                    // Si sale bien se borra de Firebase
                    borrarCuentaFirebase();
                } else {
                    // Si la respuesta no fue exitosa, muestra el error
                    Log.e("API", "Error en la respuesta: " + response.code());
                    Toast.makeText(getApplicationContext(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Si hubo un error en la conexión, mostrar el error
                Log.e("API", "Error de red: ", t);
                Toast.makeText(getApplicationContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        //Cerrar sesión de Firebase
        FirebaseAuth.getInstance().signOut();
        //Limpiar datos de la sesión local
        SessionDataManager.getInstance().clear();
        //Redirigir al login
        Intent intent = new Intent(this, Login_screen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el back stack
        startActivity(intent);
        finish();
    }

}