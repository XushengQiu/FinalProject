package com.example.finalproject.ui.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.Classroom;
import com.example.finalproject.network.ClassroomApiService;
import com.example.finalproject.network.RetrofitInstance;
import com.example.finalproject.ui.Activities.Pantalla_admin_crearAula;
import com.example.finalproject.ui.Activities.Pantalla_usuario_reservar;
import com.example.finalproject.utils.SessionDataManager;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_usuario_inicial extends Fragment {

    private RecyclerView recyclerView;
    private AulaAdapter adapter;
    private boolean isIdAsc = false, isNombreAsc = false, isCapacidadAsc = false;

    public Fragment_usuario_inicial() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_usuario_inicial, container, false);

        Button btnRegistrarAula = rootView.findViewById(R.id.btnRegistrarAula);

        // Verificar si el usuario es ADMIN
        String role = SessionDataManager.getInstance().getCurrentUser().getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            btnRegistrarAula.setVisibility(View.VISIBLE);
            btnRegistrarAula.setEnabled(true);
            btnRegistrarAula.setClickable(true);
            btnRegistrarAula.setFocusable(true);

            btnRegistrarAula.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), Pantalla_admin_crearAula.class);
                startActivity(intent);
            });
        }

        TextView headerId = rootView.findViewById(R.id.headerId);
        TextView headerNombre = rootView.findViewById(R.id.headerNombre);
        TextView headerCapacidad = rootView.findViewById(R.id.headerCapacidad);
        // Configurar los listeners para las cabeceras
        headerId.setOnClickListener(v -> {
            isIdAsc = !isIdAsc;
            resetHeaderStyles(headerId, headerNombre, headerCapacidad);
            headerId.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    isIdAsc ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float, 0);
            headerId.setPaintFlags(headerId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            adapter.sortById();
        });

        headerNombre.setOnClickListener(v -> {
            isNombreAsc = !isNombreAsc;
            resetHeaderStyles(headerId, headerNombre, headerCapacidad);
            headerNombre.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    isNombreAsc ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float, 0);
            headerNombre.setPaintFlags(headerNombre.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            adapter.sortByName();
        });

        headerCapacidad.setOnClickListener(v -> {
            isCapacidadAsc = !isCapacidadAsc;
            resetHeaderStyles(headerId, headerNombre, headerCapacidad);
            headerCapacidad.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    isCapacidadAsc ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float, 0);
            headerCapacidad.setPaintFlags(headerCapacidad.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            adapter.sortByCapacity();
        });
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(divider);
        fetchAulas();
        return rootView;
    }


    private void applyUnderline(TextView textView, boolean apply) {
        if (apply) {
            SpannableString content = new SpannableString(textView.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            textView.setText(content);
        } else {
            textView.setText(textView.getText()); // Eliminar subrayado
        }
    }

    private void resetHeaderStyles(TextView... headers) {
        for (TextView header : headers) {
            header.setPaintFlags(header.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }


    private void fetchAulas() {
        ClassroomApiService apiService = RetrofitInstance.getRetrofitInstance(getContext()).create(ClassroomApiService.class);
        Call<List<Classroom>> call = apiService.getAllClassrooms();

        call.enqueue(new Callback<List<Classroom>>() {
            @Override
            public void onResponse(Call<List<Classroom>> call, Response<List<Classroom>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Classroom> classrooms = response.body();
                    if (classrooms.isEmpty()) {
                        Toast.makeText(getContext(), "No hay Aulas", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter = new AulaAdapter(classrooms);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Classroom>> call, Throwable t) {
                Log.e("API", "Error de red: ", t);
            }
        });
    }

    private class AulaAdapter extends RecyclerView.Adapter<AulaAdapter.AulaViewHolder> {

        private final List<Classroom> aulas;
        private boolean isSortedById = false;
        private boolean isSortedByName = false;
        private boolean isSortedByCapacity = false;

        // Variables para las cabeceras
        private TextView headerId, headerNombre, headerCapacidad;

        public AulaAdapter(List<Classroom> aulas) {
            this.aulas = aulas;
        }

        // Establecer las cabeceras para el fragment
        public void setHeaders(TextView headerId, TextView headerNombre, TextView headerCapacidad) {
            this.headerId = headerId;
            this.headerNombre = headerNombre;
            this.headerCapacidad = headerCapacidad;
        }

        @Override
        public AulaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aula, parent, false);
            return new AulaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AulaViewHolder holder, int position) {
            holder.bind(aulas.get(position));
        }

        @Override
        public int getItemCount() {
            return aulas.size();
        }

        public void sortById() {
            if (isSortedById) {
                Collections.sort(aulas, (aula1, aula2) -> aula2.getId().compareTo(aula1.getId())); // Orden descendente
            } else {
                Collections.sort(aulas, (aula1, aula2) -> aula1.getId().compareTo(aula2.getId())); // Orden ascendente
            }
            isSortedById = !isSortedById; // Alternar el orden
            updateHeaderOrder(headerId, isSortedById);
            notifyDataSetChanged();
        }

        public void sortByName() {
            if (isSortedByName) {
                Collections.sort(aulas, (aula1, aula2) -> aula2.getName().compareTo(aula1.getName())); // Orden descendente
            } else {
                Collections.sort(aulas, (aula1, aula2) -> aula1.getName().compareTo(aula2.getName())); // Orden ascendente
            }
            isSortedByName = !isSortedByName; // Alternar el orden
            updateHeaderOrder(headerNombre, isSortedByName);
            notifyDataSetChanged();
        }

        public void sortByCapacity() {
            if (isSortedByCapacity) {
                Collections.sort(aulas, (aula1, aula2) -> Integer.compare(aula2.getCapacity(), aula1.getCapacity())); // Orden descendente
            } else {
                Collections.sort(aulas, (aula1, aula2) -> Integer.compare(aula1.getCapacity(), aula2.getCapacity())); // Orden ascendente
            }
            isSortedByCapacity = !isSortedByCapacity; // Alternar el orden
            updateHeaderOrder(headerCapacidad, isSortedByCapacity);
            notifyDataSetChanged();
        }

        private void updateHeaderOrder(TextView header, boolean isAscending) {
            if (header != null) {
                // Actualizar las flechas
                if (isAscending) {
                    header.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                } else {
                    header.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                }

                // Subrayar el texto
                SpannableString content = new SpannableString(header.getText());
                if (isAscending) {
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0); // Subrayado si es ascendente
                } else {
                    // Eliminar subrayado si es descendente
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                }
                header.setText(content);
            }
        }

        class AulaViewHolder extends RecyclerView.ViewHolder {
            TextView colId, colNombre, colCapacidad;
            Button btnReservar, btnEliminar;

            AulaViewHolder(View itemView) {
                super(itemView);
                colId = itemView.findViewById(R.id.colId);
                colNombre = itemView.findViewById(R.id.colNombre);
                colCapacidad = itemView.findViewById(R.id.colCapacidad);
                btnReservar = itemView.findViewById(R.id.btnReservar);
                btnEliminar = itemView.findViewById(R.id.btnEliminar);
            }

            void bind(Classroom aula) {
                colId.setText(aula.getId());
                colNombre.setText(aula.getName());
                colCapacidad.setText(String.valueOf(aula.getCapacity()));

                btnReservar.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), Pantalla_usuario_reservar.class);
                    intent.putExtra("AULA_ID", aula.getId());
                    intent.putExtra("AULA_NAME", aula.getName());
                    intent.putExtra("AULA_BLOCK", aula.getBlock());
                    intent.putExtra("AULA_FLOOR", aula.getFloor());
                    intent.putExtra("AULA_NUMBER", aula.getNumber());
                    intent.putExtra("AULA_CAPACITY", aula.getCapacity());
                    startActivity(intent);
                });

                // Mostrar botón eliminar solo si el rol es ADMIN
                String role = SessionDataManager.getInstance().getCurrentUser().getRole();
                if ("ADMIN".equalsIgnoreCase(role)) {
                    btnEliminar.setVisibility(View.VISIBLE);
                    btnEliminar.setOnClickListener(v -> {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Confirmación")
                                .setMessage("¿Estás seguro de que quieres borrar el registro del Aula? Esto no se puede deshacer")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    eliminarAula(aula.getId(), getAdapterPosition());
                                })
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                .show();
                    });
                } else {
                    btnEliminar.setVisibility(View.GONE);
                }
            }
        }
    }


    private void eliminarAula(String aulaId, int position) {
        ClassroomApiService apiService = RetrofitInstance.getRetrofitInstance(getContext()).create(ClassroomApiService.class);

        Call<Void> call = apiService.deleteClassroom(aulaId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.aulas.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Log.e("DELETE", "Fallo al eliminar aula: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DELETE", "Error de red al eliminar aula: ", t);
            }
        });
    }
}
