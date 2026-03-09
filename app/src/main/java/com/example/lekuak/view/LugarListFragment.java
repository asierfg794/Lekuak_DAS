package com.example.lekuak.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lekuak.R;
import com.example.lekuak.model.LekuakDatabase;
import com.example.lekuak.model.Lugar;
import com.example.lekuak.model.Visita;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LugarListFragment extends Fragment {

    private LekuakDatabase db;
    private ExecutorService executor;
    private LugarAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private boolean mostrarSoloPendientes = false;

    public interface OnFragmentInteractionListener {
        void onLugarSelected(Lugar lugar);
        void onLugarLongClick(Lugar lugar);
        void onListaActualizada(List<Lugar> lugares);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lugar_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = LekuakDatabase.getInstance(requireContext());
        executor = Executors.newSingleThreadExecutor();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLugares);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new LugarAdapter();
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            mostrarSoloPendientes = getArguments().getBoolean("soloPendientes", false);
        }

        recargar();

        adapter.setOnItemClickListener(new LugarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lugar lugar) {
                mListener.onLugarSelected(lugar);
            }

            @Override
            public void onItemLongClick(Lugar lugar) {
                mListener.onLugarLongClick(lugar);
            }
        });
    }

    public void recargar() {
        if (executor == null) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Lugar> lugares;
                if (mostrarSoloPendientes) {
                    lugares = db.lugarDao().getPendientes();
                } else {
                    lugares = db.lugarDao().getAllLugares();
                }

                List<Visita> visitas = db.visitaDao().getAllVisitas();
                final Set<Long> vIds = new HashSet<>();
                for (Visita v : visitas) {
                    vIds.add(v.idLugar);
                }

                if (isAdded()) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setLugares(lugares, vIds);
                            mListener.onListaActualizada(lugares);
                        }
                    });
                }
            }
        });
    }
}
