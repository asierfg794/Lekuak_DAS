package com.example.lekuak.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VisitaDao {

    @Insert
    void insert(Visita visita);

    @Update
    void update(Visita visita);

    @Delete
    void delete(Visita visita);

    @Query("SELECT * FROM visitas WHERE idLugar = :idLugar ORDER BY fecha DESC")
    List<Visita> getVisitasByLugar(long idLugar);

    @Query("SELECT * FROM visitas ORDER BY fecha DESC")
    List<Visita> getAllVisitas();

    @Query("SELECT AVG(valoracion) FROM visitas")
    Double getAverageRating();

    @Query("SELECT MAX(fecha) FROM visitas")
    Long getLastVisitDate();

    @Query("SELECT idLugar FROM visitas GROUP BY idLugar ORDER BY COUNT(idLugar) DESC LIMIT 1")
    Long getMostVisitedLugarId();
}
