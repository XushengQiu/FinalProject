package com.example.finalproject.ui.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

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

public class Fragment_usuario_info extends Fragment {

    private Button btnBorrarCuenta;

    public Fragment_usuario_info() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_usuario_info, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        User user = SessionDataManager.getInstance().getCurrentUser();
        if (user != null) {
            ((TextView) rootView.findViewById(R.id.textNombreFill)).setText(user.getName());
            ((TextView) rootView.findViewById(R.id.textGradoFill)).setText(user.getDegree());
            ((TextView) rootView.findViewById(R.id.textMatriculaFill)).setText(user.getSchoolNumber());
            ((TextView) rootView.findViewById(R.id.textView13)).setText(user.getRole());
        }
        Button buttonLogout =rootView.findViewById(R.id.btnLogout);
        buttonLogout.setOnClickListener(v -> {
            logout();
        });
        btnBorrarCuenta = rootView.findViewById(R.id.btnBorrarCuenta);
        btnBorrarCuenta.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que quieres borrar tu cuenta? Borrar tu cuenta implica borrar también tus datos y es irreversible.")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        borrarCuenta(SessionDataManager.getInstance().getCurrentUser().getId());
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return rootView;
    }

    private void borrarCuentaFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            logout();
                        } else {
                            Log.e("Firebase", "Error al eliminar la cuenta: " + task.getException());
                            Toast.makeText(getContext(), "No se pudo eliminar la cuenta.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("Firebase", "No hay usuario autenticado.");
            Toast.makeText(getContext(), "No estás autenticado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void borrarCuenta(int userId) {
        UserApiService apiService = RetrofitInstance.getRetrofitInstance(getContext()).create(UserApiService.class);
        Call<Void> call = apiService.deleteUser(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    borrarCuentaFirebase();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                    Toast.makeText(getContext(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error de red: ", t);
                Toast.makeText(getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        SessionDataManager.getInstance().clear();
        Intent intent = new Intent(getContext(), Login_screen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
