package com.example.lekuak.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lekuak.R;
import com.example.lekuak.model.LekuakDatabase;
import com.example.lekuak.model.Lugar;
import com.example.lekuak.model.Visita;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LugarDetailFragment extends Fragment implements VisitaDialogFragment.VisitaDialogListener {
    private Lugar lugar;
    private LekuakDatabase db;
    private ExecutorService executor;
    private VisitaAdapter visitaAdapter;
    private VisitaDialogFragment.VisitaDialogListener activityListener;

    public static LugarDetailFragment newInstance(Lugar lugar) {
        LugarDetailFragment fragment = new LugarDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("lugar", lugar); 
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof VisitaDialogFragment.VisitaDialogListener) {
            activityListener = (VisitaDialogFragment.VisitaDialogListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lugar_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = LekuakDatabase.getInstance(requireContext());
        executor = Executors.newSingleThreadExecutor();

        if (getArguments() != null) {
            lugar = (Lugar) getArguments().getSerializable("lugar");
            if (lugar != null) {
                ((TextView) view.findViewById(R.id.detailNombre)).setText(lugar.getNombre());
                ((TextView) view.findViewById(R.id.detailDireccion)).setText(lugar.direccion);
                
                String categoriaTraducida = Lugar.getTranslatedCategoria(requireContext(), lugar.categoria);
                ((TextView) view.findViewById(R.id.detailDescripcion)).setText(categoriaTraducida);
                
                RecyclerView rvVisitas = view.findViewById(R.id.rvVisitas);
                rvVisitas.setLayoutManager(new LinearLayoutManager(getContext()));
                visitaAdapter = new VisitaAdapter();
                rvVisitas.setAdapter(visitaAdapter);

                cargarVisitas();
            }
        }

        view.findViewById(R.id.btnEditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LugarDialogFragment dialog = LugarDialogFragment.newInstance(lugar);
                dialog.show(getChildFragmentManager(), "LugarDialog");
            }
        });

        view.findViewById(R.id.btnNuevaVisita).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitaDialogFragment dialog = VisitaDialogFragment.newInstance(lugar.id, null);
                dialog.show(getChildFragmentManager(), "VisitaDialog");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    private void cargarVisitas() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Visita> visitas = db.visitaDao().getVisitasByLugar(lugar.id);
                if (isAdded()) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            visitaAdapter.setVisitas(visitas);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onVisitaGuardada(final Visita visita) {
        cargarVisitas();
        
        if (activityListener != null) {
            activityListener.onVisitaGuardada(visita);
        }
    }
}
