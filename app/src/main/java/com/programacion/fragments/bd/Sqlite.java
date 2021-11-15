package com.programacion.fragments.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;

import java.util.ArrayList;

public class Sqlite {
    private final Sql sql;
    private SQLiteDatabase db;


    public Sqlite(Context context) {
        sql = new Sql(context);
    }

    public SQLiteDatabase abrirDB() {
        Log.i("Sqlite", "Se abre conexión con BD " + sql.getDatabaseName());
        db = sql.getWritableDatabase();
        return db;
    }

    public void cerrarDB() {
        Log.i("Sqlite", "Se cierra conexión con BD " + sql.getDatabaseName());
        sql.close();
    }

    /*
   "ID
   NOMBRE_MASCOTA
   NOMBRE_PROPIETARIO
   NOMBRE_VETERINARIO
   ESPECIE
   RAZA
   COLOR
   SEXO
   DIRECCION
   TELEFONO
   EMAIL
   PARTICULARIDADES
   NACIMIENTO
   IMAGEN
   ESTATUS
    */
    public boolean addRegistroMascota(String nombre_mas, String nombre_prop, String nombre_veter,
                                      String raza, String especie, String color, String sexo, String direccion,
                                      String telefono, String email, String particularidades,
                                      String fecha_nac, String imagen,String estatus) {

        ContentValues cv = new ContentValues();
        cv.put("NOMBRE_MASCOTA", nombre_mas);
        cv.put("NOMBRE_PROPIETARIO", nombre_prop);
        cv.put("NOMBRE_VETERINARIO", nombre_veter);
        cv.put("ESPECIE", especie);
        cv.put("RAZA", raza);
        cv.put("COLOR", color);
        cv.put("SEXO", sexo);
        cv.put("DIRECCION", direccion);
        cv.put("TELEFONO", telefono);
        cv.put("EMAIL", email);
        cv.put("PARTICULARIDADES", particularidades);
        cv.put("NACIMIENTO", fecha_nac);
        cv.put("IMAGEN", imagen);
        cv.put("ESTATUS", estatus);

        boolean mascotas;
        mascotas = db.insert("MASCOTAS", null, cv) != 1;
        return mascotas;
    }

    //public Cursor getRegistro() {
    //    return db.rawQuery("SELECT * FROM MASCOTAS", null);
   // }

    public Cursor getRegistroActivos() {
        return db.rawQuery("SELECT * FROM MASCOTAS WHERE ESTATUS='ACTIVO'", null);
    }
    public Cursor getRegistroInActivos() {
        return db.rawQuery("SELECT * FROM MASCOTAS WHERE ESTATUS='INACTIVO'", null);
    }

    public Cursor getRegistrosNombreActivos(String id) {
        return db.rawQuery("SELECT * FROM MASCOTAS WHERE NOMBRE_MASCOTA LIKE '%" + id + "%' AND ESTATUS='ACTIVO'", null);
    }

    public Cursor getRegistrosNombreInactivos(String id) {
        return db.rawQuery("SELECT * FROM MASCOTAS WHERE NOMBRE_MASCOTA LIKE '%" + id + "%' AND ESTATUS='INACTIVO'", null);
    }

    public ArrayList<String> getMascotas(Cursor cursor) {
        ArrayList<String> lstData = new ArrayList<>();
        String item = "";

        if (cursor.moveToFirst()) {
            do {
                item += "ID: [" + cursor.getString(0) + "]\r\n";
                item += "NOMBRE_MASCOTA: [" + cursor.getString(1) + "]\r\n";
                item += "NOMBRE_PROPIETARIO: [" + cursor.getString(2) + "]\r\n";
                item += "NOMBRE_VETERINARIO: [" + cursor.getString(3) + "]\r\n";
                item += "ESPECIE: [" + cursor.getString(4) + "]\r\n";
                item += "RAZA: [" + cursor.getString(5) + "]\r\n";
                item += "COLOR: [" + cursor.getString(6) + "]\r\n";
                item += "SEXO: [" + cursor.getString(7) + "]\r\n";
                item += "DIRECCION: [" + cursor.getString(8) + "]\r\n";
                item += "TELEFONO: [" + cursor.getString(9) + "]\r\n";
                item += "EMAIL: [" + cursor.getString(10) + "]\r\n";
                item += "PARTICULARIDADES: [" + cursor.getString(11) + "]\r\n";
                item += "NACIMIENTO: [" + cursor.getString(12) + "]\r\n";


                lstData.add(item);
                item = "";
            } while (cursor.moveToNext());
        }
        return lstData;
    }

    public ArrayList<String> getImagenes(Cursor cursor) {
        ArrayList<String> lstData = new ArrayList<>();
        String item = "";

        if (cursor.moveToFirst()) {
            do {
                lstData.add(cursor.getString(13));
            } while (cursor.moveToNext());
        }
        return lstData;
    }

    public ArrayList<String> getID(Cursor cursor) {
        ArrayList<String> lstData = new ArrayList<>();
        String item = "";

        if (cursor.moveToFirst()) {
            do {
                item += "ID: [" + cursor.getString(0) + "]\r\n";
                lstData.add(item);
                item = "";
            } while (cursor.moveToNext());
        }
        return lstData;
    }

    public String updateRegistroMascota(int id, String nombre_mas, String nombre_prop, String nombre_veter,
                                        String raza, String especie, String color, String sexo, String direccion,
                                        String telefono, String email, String particularidades,
                                        String fecha_nac, String imagen,String estatus) {
        ContentValues cv = new ContentValues();
        cv.put("ID", id);
        cv.put("NOMBRE_MASCOTA", nombre_mas);
        cv.put("NOMBRE_PROPIETARIO", nombre_prop);
        cv.put("NOMBRE_VETERINARIO", nombre_veter);
        cv.put("ESPECIE", especie);
        cv.put("RAZA", raza);
        cv.put("COLOR", color);
        cv.put("SEXO", sexo);
        cv.put("DIRECCION", direccion);
        cv.put("TELEFONO", telefono);
        cv.put("EMAIL", email);
        cv.put("PARTICULARIDADES", particularidades);
        cv.put("NACIMIENTO", fecha_nac);
        cv.put("IMAGEN", imagen);
        cv.put("ESTATUS", estatus);

        int valor = db.update("MASCOTAS", cv, "ID=" + id, null);
        if (valor == 1) {
            return "Información de la mascota actualizada";
        } else {
            return "Error al actualizar";
        }
    }

    public Cursor getValor(int id) {
        return db.rawQuery("SELECT * FROM MASCOTAS WHERE ID=" + id, null);
    }

    public int eliminar(int id) {
        return db.delete("MASCOTAS", "ID=" + id, null);
    }


    public void eliminarPapelera(String id) {
        db.execSQL("UPDATE MASCOTAS SET ESTATUS='INACTIVO' WHERE ID="+String.valueOf(id));
    }
    public void restaurarPapelera(String id) {
        db.execSQL("UPDATE MASCOTAS SET ESTATUS='ACTIVO' WHERE ID="+String.valueOf(id));
    }
}
