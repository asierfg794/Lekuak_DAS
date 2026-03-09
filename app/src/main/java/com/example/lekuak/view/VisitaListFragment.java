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

public class VisitaListFragment extends Fragment {

    private LekuakDatabase db;
    private ExecutorService executor;
    private VisitaGlobalAdapter adapter;
    private OnVisitaInteractionListener mListener;

    public interface OnVisitaInteractionListener {
        void onVisitaSelected(Visita visita);
        void onVisitaLongClick(Visita visita);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnVisitaInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar OnVisitaInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visita_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = LekuakDatabase.getInstance(requireContext());
        executor = Executors.newSingleThreadExecutor();

        RecyclerView recyclerView = view.findViewById(R.id.rvVisitasGlobal);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new VisitaGlobalAdapter();
        recyclerView.setAdapter(adapter);

        recargar();

        adapter.setOnItemClickListener(new VisitaGlobalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Visita visita) {
                mListener.onVisitaSelected(visita);
            }

            @Override
            public void onItemLongClick(Visita visita) {
                mListener.onVisitaLongClick(visita);
            }
        });
    }

    public void recargar() {
        if (executor == null) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Lugar> lugares = db.lugarDao().getAllLugares();
                final List<Visita> visitas = db.visitaDao().getAllVisitas();

                if (isAdded()) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setLugares(lugares);
                            actualizarUI(getView(), visitas);
                        }
                    });
                }
            }
        });
    }

    private void actualizarUI(View root, List<Visita> visitas) {
        if (root == null) return;
        RecyclerView recyclerView = root.findViewById(R.id.rvVisitasGlobal);
        TextView tvNoVisitas = root.findViewById(R.id.tvNoVisitas);
        
        if (visitas == null || visitas.isEmpty()) {
            tvNoVisitas.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoVisitas.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setVisitas(visitas);
        }
    }
}
