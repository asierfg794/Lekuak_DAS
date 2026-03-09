package com.example.lekuak.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lekuak.R;
import com.example.lekuak.model.LekuakDatabase;
import com.example.lekuak.model.Lugar;
import com.example.lekuak.model.Visita;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisitaDetailFragment extends Fragment {
    private Visita visita;
    private LekuakDatabase db;
    private ExecutorService executor;

    public static VisitaDetailFragment newInstance(Visita visita) {
        VisitaDetailFragment fragment = new VisitaDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("visita", visita);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visita_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = LekuakDatabase.getInstance(requireContext());
        executor = Executors.newSingleThreadExecutor();

        if (getArguments() != null) {
            visita = (Visita) getArguments().getSerializable("visita");
            if (visita != null) {
                final TextView tvLugar = view.findViewById(R.id.detailLugarVisita);
                TextView tvFecha = view.findViewById(R.id.detailFechaVisita);
                RatingBar rbRating = view.findViewById(R.id.detailRatingVisita);
                TextView tvNotas = view.findViewById(R.id.detailNotasVisita);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<Lugar> lugares = db.lugarDao().getAllLugares();
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (Lugar l : lugares) {
                                    if (l.id == visita.idLugar) {
                                        tvLugar.setText(l.nombre);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                });

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvFecha.setText(sdf.format(visita.fecha));
                rbRating.setRating(visita.valoracion != null ? visita.valoracion : 0);
                tvNotas.setText(visita.notasVisita);
            }
        }

        view.findViewById(R.id.btnEditarVisita).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitaDialogFragment dialog = VisitaDialogFragment.newInstance(visita.idLugar, visita);
                dialog.show(getParentFragmentManager(), "VisitaDialog");
            }
        });
    }
}
