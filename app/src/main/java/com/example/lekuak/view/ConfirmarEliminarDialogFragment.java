package com.example.lekuak.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lekuak.R;

public class ConfirmarEliminarDialogFragment extends DialogFragment {

    private ConfirmacionListener listener;
    private String titulo;
    private String mensaje;
    private int idItem; 

    public interface ConfirmacionListener {
        void onConfirmarEliminacion(int idItem);
    }

    public static ConfirmarEliminarDialogFragment newInstance(String titulo, String mensaje, int idItem) {
        ConfirmarEliminarDialogFragment fragment = new ConfirmarEliminarDialogFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("mensaje", mensaje);
        args.putInt("idItem", idItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmacionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar ConfirmacionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            titulo = getArguments().getString("titulo");
            mensaje = getArguments().getString("mensaje");
            idItem = getArguments().getInt("idItem");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirmarEliminacion(idItem);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfirmarEliminarDialogFragment.this.dismiss();
                    }
                });
        return builder.create();
    }
}
