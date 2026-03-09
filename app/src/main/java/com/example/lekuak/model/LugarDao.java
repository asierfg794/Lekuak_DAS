package com.example.lekuak.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LugarDao {

    @Insert
    void insert(Lugar lugar);

    @Update
    void update(Lugar lugar);

    @Delete
    void delete(Lugar lugar);

    @Query("SELECT * FROM lugares ORDER BY fechaCreacion DESC")
    List<Lugar> getAllLugares();

    @Query("SELECT * FROM lugares WHERE id NOT IN (SELECT DISTINCT idLugar FROM visitas) ORDER BY fechaCreacion DESC")
    List<Lugar> getPendientes();

    @Query("SELECT COUNT(DISTINCT idLugar) FROM visitas")
    int getNumVisitados();

    @Query("SELECT COUNT(*) FROM lugares")
    int getTotalLugares();
}
