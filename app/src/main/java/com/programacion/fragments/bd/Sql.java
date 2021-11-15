package com.programacion.fragments.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sql extends SQLiteOpenHelper {
    private static final String DATABASE = "mascotas";
    private static final int VERSION = 1;
    private final String tabMascotas = "CREATE TABLE MASCOTAS (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "NOMBRE_MASCOTA TEXT NOT NULL," +
            "NOMBRE_PROPIETARIO TEXT NOT NULL," +
            "NOMBRE_VETERINARIO TEXT NOT NULL," +
            "ESPECIE TEXT NOT NULL," +
            "RAZA TEXT NOT NULL," +
            "COLOR TEXT NOT NULL," +
            "SEXO TEXT NOT NULL," +
            "DIRECCION TEXT NOT NULL," +
            "TELEFONO TEXT NOT NULL," +
            "EMAIL TEXT NOT NULL," +
            "PARTICULARIDADES TEXT NOT NULL," +
            "NACIMIENTO TEXT NOT NULL," +
            "IMAGEN TEXT NOT NULL," +
            "ESTATUS TEXT NOT NULL);";



    public Sql(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tabMascotas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS MASCOTAS");
            db.execSQL(tabMascotas);
        }
    }
}
