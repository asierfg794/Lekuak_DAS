package com.example.lekuak.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lekuak.R;
import com.example.lekuak.model.Visita;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VisitaAdapter extends RecyclerView.Adapter<VisitaAdapter.VisitaViewHolder> {

    private List<Visita> visitas = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @NonNull
    @Override
    public VisitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visita, parent, false);
        return new VisitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitaViewHolder holder, int position) {
        Visita visita = visitas.get(position);
        holder.tvFecha.setText(dateFormat.format(visita.fecha));
        holder.rbValoracion.setRating(visita.valoracion != null ? visita.valoracion : 0);
        holder.tvNotas.setText(visita.notasVisita);
        
        if (visita.notasVisita == null || visita.notasVisita.isEmpty()) {
            holder.tvNotas.setVisibility(View.GONE);
        } else {
            holder.tvNotas.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return visitas.size();
    }

    public void setVisitas(List<Visita> visitas) {
        this.visitas = visitas;
        notifyDataSetChanged();
    }

    static class VisitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNotas;
        RatingBar rbValoracion;

        public VisitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaVisita);
            rbValoracion = itemView.findViewById(R.id.rbValoracionVisita);
            tvNotas = itemView.findViewById(R.id.tvNotasVisita);
        }
    }
}
