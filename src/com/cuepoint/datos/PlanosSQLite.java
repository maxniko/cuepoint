package com.cuepoint.datos;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cuepoint.clases.Plano;

public class PlanosSQLite {
    
    public ArrayList<Plano> getPlanos(Context contexto)
    {
    	ArrayList<Plano> items = new ArrayList<Plano>();
    	
    	//Abrimos la base de datos 'Planos'
        ConexionSQLite pdb = new ConexionSQLite(contexto, "Planos", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery("SELECT idPlano,nombre,descripcion,path FROM Planos", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	int id = c.getInt(0);
                 String nombre = c.getString(1);
                 String desc = c.getString(2);
                 String path = c.getString(3);
                 Plano p = new Plano(id,nombre, path, desc);
                 items.add(p);
            } while(c.moveToNext());
       }
       db.close();
       return items;
    }
    
    public Plano getPlanoPorId(Context contexto, int id)
    {
    	Plano p = null;
    	//Abrimos la base de datos 'Planos'
        ConexionSQLite pdb = new ConexionSQLite(contexto, "Planos", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //String de consulta
        String consulta = "SELECT idPlano,nombre,descripcion,path FROM Planos " +
        		"WHERE idPlano = " + id + ";";
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery(consulta, null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
	        int idP = c.getInt(0);
            String nombre = c.getString(1);
            String desc = c.getString(2);
            String path = c.getString(3);
            p = new Plano(idP,nombre, path, desc);
        }
        db.close();
        
    	return p;
    }

}
