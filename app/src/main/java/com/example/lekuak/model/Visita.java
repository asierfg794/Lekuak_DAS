package com.example.lekuak.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "visitas",
        foreignKeys = @ForeignKey(entity = Lugar.class,
                parentColumns = "id",
                childColumns = "idLugar",
                onDelete = ForeignKey.CASCADE))
public class Visita implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long idLugar;
    public long fecha;
    public String notasVisita;
    public Integer valoracion;

    public Visita() {}
}
