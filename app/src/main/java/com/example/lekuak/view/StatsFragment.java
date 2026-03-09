package com.example.lekuak.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lekuak.R;
import com.example.lekuak.model.LekuakDatabase;
import com.example.lekuak.model.Lugar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsFragment extends Fragment {

    private LekuakDatabase db;
    private ExecutorService executor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = LekuakDatabase.getInstance(requireContext());
        executor = Executors.newSingleThreadExecutor();
        recargar();
    }

    public void recargar() {
        if (getView() == null) return;

        final TextView tvTotal = getView().findViewById(R.id.tvTotal);
        final TextView tvVisitados = getView().findViewById(R.id.tvVisitados);
        final TextView tvValoracionMedia = getView().findViewById(R.id.tvValoracionMedia);
        final TextView tvLugarTop = getView().findViewById(R.id.tvLugarTop);
        final TextView tvUltimaVisita = getView().findViewById(R.id.tvUltimaVisita);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                final int total = db.lugarDao().getTotalLugares();
                final int visitados = db.lugarDao().getNumVisitados();
                final Double rating = db.visitaDao().getAverageRating();
                final Long topLugarId = db.visitaDao().getMostVisitedLugarId();
                final Long lastDate = db.visitaDao().getLastVisitDate();

                String nombreLugarTop = null;
                if (topLugarId != null) {
                    List<Lugar> lugares = db.lugarDao().getAllLugares();
                    for (Lugar l : lugares) {
                        if (l.id == (long) topLugarId) {
                            nombreLugarTop = l.nombre;
                            break;
                        }
                    }
                }
                
                final String finalNombreLugarTop = nombreLugarTop;

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTotal.setText(getString(R.string.total) + total);
                        tvVisitados.setText(getString(R.string.visitados) + visitados);
                        
                        if (rating != null) {
                            tvValoracionMedia.setText(getString(R.string.valoracion_media) + String.format(Locale.getDefault(), "%.1f", rating) + " ★");
                        } else {
                            tvValoracionMedia.setText(getString(R.string.valoracion_media) + getString(R.string.sin_datos));
                        }

                        if (finalNombreLugarTop != null) {
                            tvLugarTop.setText(getString(R.string.lugar_top) + finalNombreLugarTop);
                        } else {
                            tvLugarTop.setText(getString(R.string.lugar_top) + getString(R.string.sin_datos));
                        }

                        if (lastDate != null && lastDate > 0) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            tvUltimaVisita.setText(getString(R.string.ultima_visita) + sdf.format(lastDate));
                        } else {
                            tvUltimaVisita.setText(getString(R.string.ultima_visita) + getString(R.string.sin_datos));
                        }
                    }
                });
            }
        });
    }
}
