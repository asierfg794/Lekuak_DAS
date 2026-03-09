package com.example.lekuak.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Lugar.class, Visita.class}, version = 2, exportSchema = false)
public abstract class LekuakDatabase extends RoomDatabase {

    private static volatile LekuakDatabase instance;

    public abstract LugarDao lugarDao();
    public abstract VisitaDao visitaDao();

    public static synchronized LekuakDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            LekuakDatabase.class, "lekuak_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}