package com.example.lekuak.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lekuak.R;
import com.example.lekuak.model.LekuakDatabase;
import com.example.lekuak.model.Lugar;
import com.example.lekuak.model.Visita;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class VisitaDialogFragment extends DialogFragment {

    private EditText etFecha, etNotas;
    private RatingBar rbValoracion;
    private Spinner spLugar;
    private TextView tvSelectLugarLabel;
    private long idLugarPreseleccionado = -1;
    private long fechaSeleccionada;
    private VisitaDialogListener listener;
    private List<Lugar> listaLugares = new ArrayList<>();
    private Visita visitaAEditar;

    public interface VisitaDialogListener {
        void onVisitaGuardada(Visita visita);
    }

    public static VisitaDialogFragment newInstance(long idLugar, @Nullable Visita visita) {
        VisitaDialogFragment fragment = new VisitaDialogFragment();
        Bundle args = new Bundle();
        args.putLong("idLugar", idLugar);
        if (visita != null) {
            args.putSerializable("visita", visita);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof VisitaDialogListener) {
            listener = (VisitaDialogListener) getParentFragment();
        } else if (context instanceof VisitaDialogListener) {
            listener = (VisitaDialogListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            idLugarPreseleccionado = getArguments().getLong("idLugar", -1);
            if (getArguments().containsKey("visita")) {
                visitaAEditar = (Visita) getArguments().getSerializable("visita");
            }
        }

        if (idLugarPreseleccionado == -1 && visitaAEditar == null) {
            listaLugares = LekuakDatabase.getInstance(requireContext()).lugarDao().getAllLugares();
        }
        
        fechaSeleccionada = (visitaAEditar != null) ? visitaAEditar.fecha : System.currentTimeMillis();

        if (idLugarPreseleccionado == -1 && visitaAEditar == null && (listaLugares == null || listaLugares.isEmpty())) {
            return new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.nueva_visita)
                    .setMessage(R.string.crea_un_lugar_primero)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_visita, null);

        etFecha = view.findViewById(R.id.etFechaVisita);
        etNotas = view.findViewById(R.id.etNotasVisita);
        rbValoracion = view.findViewById(R.id.rbValoracion);
        spLugar = view.findViewById(R.id.spLugarVisita);
        tvSelectLugarLabel = view.findViewById(R.id.tvSelectLugarLabel);
        
        if (idLugarPreseleccionado == -1 && visitaAEditar == null) {
            tvSelectLugarLabel.setVisibility(View.VISIBLE);
            spLugar.setVisibility(View.VISIBLE);
            
            List<String> nombresLugares = new ArrayList<>();
            for (Lugar l : listaLugares) nombresLugares.add(l.nombre);
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresLugares);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLugar.setAdapter(adapter);
        } else {
            tvSelectLugarLabel.setVisibility(View.GONE);
            spLugar.setVisibility(View.GONE);
        }

        if (visitaAEditar != null) {
            etNotas.setText(visitaAEditar.notasVisita);
            rbValoracion.setRating(visitaAEditar.valoracion != null ? visitaAEditar.valoracion : 0);
        }

        etFecha.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaSeleccionada));
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        builder.setView(view)
                .setTitle(visitaAEditar == null ? R.string.nueva_visita : R.string.editar)
                .setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Visita v = (visitaAEditar != null) ? visitaAEditar : new Visita();

                        if (visitaAEditar == null) {
                            if (idLugarPreseleccionado != -1) {
                                v.idLugar = idLugarPreseleccionado;
                            } else if (spLugar != null && !listaLugares.isEmpty()) {
                                v.idLugar = listaLugares.get(spLugar.getSelectedItemPosition()).id;
                            }
                        }

                        v.fecha = fechaSeleccionada;
                        v.notasVisita = etNotas.getText().toString();
                        v.valoracion = (int) rbValoracion.getRating();

                        if (listener != null) {
                            listener.onVisitaGuardada(v);
                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VisitaDialogFragment.this.dismiss();
                    }
                });

        return builder.create();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(fechaSeleccionada);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);
                        fechaSeleccionada = selected.getTimeInMillis();
                        etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}