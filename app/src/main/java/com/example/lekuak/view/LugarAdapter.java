package com.example.lekuak.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lekuak.R;
import com.example.lekuak.model.Lugar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LugarAdapter extends RecyclerView.Adapter<LugarAdapter.LugarViewHolder> {

    private List<Lugar> lugares = new ArrayList<>();
    private Set<Long> visitedLugarIds = new HashSet<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Lugar lugar);
        void onItemLongClick(Lugar lugar);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LugarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lugar_card, parent, false);
        return new LugarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LugarViewHolder holder, int position) {
        final Lugar lugarActual = lugares.get(position);
        final Context context = holder.itemView.getContext();
        
        holder.tvNombre.setText(lugarActual.nombre);
        holder.tvCategoria.setText(Lugar.getTranslatedCategoria(context, lugarActual.categoria));

        if (visitedLugarIds.contains(lugarActual.id)) {
            holder.tvEstado.setText(context.getString(R.string.estado_visitado));
            holder.tvEstado.setTextColor(context.getResources().getColor(R.color.green_primary));
        } else {
            holder.tvEstado.setText(context.getString(R.string.estado_pendiente));
            holder.tvEstado.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
        holder.tvEstado.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return lugares.size();
    }

    public void setLugares(List<Lugar> lugares, Set<Long> visitedLugarIds) {
        this.lugares = lugares;
        this.visitedLugarIds = visitedLugarIds;
        notifyDataSetChanged();
    }

    public class LugarViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvEstado;
        ImageButton btnMapa;

        public LugarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreLugar);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaLugar);
            tvEstado = itemView.findViewById(R.id.tvEstadoLugar);
            btnMapa = itemView.findViewById(R.id.btnMapaCard);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(lugares.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(lugares.get(position));
                    }
                    return true;
                }
            });

            btnMapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Lugar lugar = lugares.get(position);
                        Context context = v.getContext();
                        String direccion = lugar.nombre + ", " + lugar.direccion;
                        Uri intentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        try {
                            context.startActivity(mapIntent);
                        } catch (ActivityNotFoundException e) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, intentUri));
                            } catch (Exception ex) {
                                Toast.makeText(context, R.string.sin_datos, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }
}
