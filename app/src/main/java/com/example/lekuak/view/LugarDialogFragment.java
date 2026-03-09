package com.example.lekuak.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lekuak.R;
import com.example.lekuak.model.Lugar;

public class LugarDialogFragment extends DialogFragment {

    private EditText etNombre, etDireccion;
    private Spinner spCategoria;
    private Lugar lugarActual;
    private LugarDialogListener listener;


    private final String[] CLAVES_CATEGORIAS = {"Restaurante", "Bar", "Tienda", "Naturaleza", "Cultura", "Ocio", "Otro"};

    public interface LugarDialogListener {
        void onLugarGuardado(Lugar lugar);
    }

    public static LugarDialogFragment newInstance(Lugar lugar) {
        LugarDialogFragment fragment = new LugarDialogFragment();
        Bundle args = new Bundle();
        if (lugar != null) {
            args.putSerializable("lugar", lugar);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LugarDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar LugarDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey("lugar")) {
            lugarActual = (Lugar) getArguments().getSerializable("lugar");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_lugar, null);

        etNombre = view.findViewById(R.id.etNombre);
        etDireccion = view.findViewById(R.id.etDireccion);
        spCategoria = view.findViewById(R.id.spCategoria);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categorias_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);

        if (lugarActual != null) {
            etNombre.setText(lugarActual.nombre);
            etDireccion.setText(lugarActual.direccion);

            for (int i = 0; i < CLAVES_CATEGORIAS.length; i++) {
                if (CLAVES_CATEGORIAS[i].equalsIgnoreCase(lugarActual.categoria)) {
                    spCategoria.setSelection(i);
                    break;
                }
            }
        }

        builder.setView(view)
                .setTitle(lugarActual == null ? R.string.nuevo_lugar : R.string.editar_lugar)
                .setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (lugarActual == null) {
                            lugarActual = new Lugar();
                            lugarActual.fechaCreacion = System.currentTimeMillis();
                        }
                        lugarActual.nombre = etNombre.getText().toString();
                        lugarActual.direccion = etDireccion.getText().toString();

                        int index = spCategoria.getSelectedItemPosition();
                        lugarActual.categoria = CLAVES_CATEGORIAS[index];

                        listener.onLugarGuardado(lugarActual);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LugarDialogFragment.this.dismiss();
                    }
                });

        return builder.create();
    }
}