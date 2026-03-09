package com.example.lekuak.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lekuak.R;
import com.example.lekuak.model.Lugar;
import com.example.lekuak.model.Visita;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VisitaGlobalAdapter extends RecyclerView.Adapter<VisitaGlobalAdapter.VisitaViewHolder> {

    private List<Visita> visitas = new ArrayList<>();
    private List<Lugar> lugares = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Visita visita);
        void onItemLongClick(Visita visita);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VisitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visita, parent, false);
        return new VisitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitaViewHolder holder, int position) {
        Visita visita = visitas.get(position);
        
        String nombreLugar = "Lugar desconocido";
        for (Lugar l : lugares) {
            if (l.id == visita.idLugar) {
                nombreLugar = l.nombre;
                break;
            }
        }

        holder.tvNombreLugar.setText(nombreLugar);
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

    public void setLugares(List<Lugar> lugares) {
        this.lugares = lugares;
        notifyDataSetChanged();
    }

    class VisitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreLugar, tvFecha, tvNotas;
        RatingBar rbValoracion;

        public VisitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreLugar = itemView.findViewById(R.id.tvNombreLugarVisita); 
            tvFecha = itemView.findViewById(R.id.tvFechaVisita); 
            rbValoracion = itemView.findViewById(R.id.rbValoracionVisita);
            tvNotas = itemView.findViewById(R.id.tvNotasVisita);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(visitas.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(visitas.get(position));
                    }
                    return true;
                }
            });
        }
    }
}
