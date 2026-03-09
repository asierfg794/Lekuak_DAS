package com.example.lekuak.model;

import android.content.Context;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.lekuak.R;

import java.io.Serializable;

@Entity(tableName = "lugares")
public class Lugar implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String nombre;
    public String categoria;
    public String direccion;
    public Double latitud;
    public Double longitud;
    public long fechaCreacion;
    public String notas;

    public Lugar() {}

    public String getNombre(){
        return nombre;
    }

    public String getDescripcion(){
        return categoria;
    }

    public static String getTranslatedCategoria(Context context, String categoriaOriginal) {
        if (categoriaOriginal == null) return "";
        String[] claves = {"Restaurante", "Bar", "Tienda", "Naturaleza", "Cultura", "Ocio", "Otro"};
        String[] traducciones = context.getResources().getStringArray(R.array.categorias_array);
        
        for (int i = 0; i < claves.length; i++) {
            if (claves[i].equalsIgnoreCase(categoriaOriginal)) {
                return traducciones[i];
            }
        }
        return categoriaOriginal;
    }
}
