package com.cuepoint.datos;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cuepoint.clases.Boletin;

public class BoletinesSQLite {

	public ArrayList<String> getBoletines(Context contexto)
    {
    	ArrayList<String> items = new ArrayList<String>();
    	
    	//Abrimos la base de datos 'Boletins'
        ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery("SELECT idBoletin,nombre FROM Boletines", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	Boletin b = new Boletin();
            	b.setIdBoletin(c.getInt(0));
            	b.setNombre(c.getString(1));
                items.add(b.getNombre());
            } while(c.moveToNext());
       }
       db.close();
       return items;
    }
    
    public Boletin getBoletinPorFecha(Context contexto, String nombre)
    {
    	Boletin b = null;
    	//Abrimos la base de datos 'CuePoint'
        ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //String de consulta
        String consulta = "SELECT idBoletin,nombre FROM Boletines " +
        		"WHERE nombre = '" + nombre + "';";
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery(consulta, null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
        	b = new Boletin();
        	b.setIdBoletin(c.getInt(0));
        	b.setNombre(c.getString(1));
        }
        db.close();
        
    	return b;
    }

    public boolean nuevoBoletin(Context contexto, Boletin boletin)
	{
    	boolean exito = false;
    	//Abrimos la base de datos 'CuePoint'
		ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
         
        // Insertar datos en la base de datos
        SQLiteDatabase db = pdb.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        
        sb.append("INSERT INTO Boletines (nombre) values (");
        sb.append("'" + boletin.getNombre() + "'");
        sb.append(");");
        
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
        	Log.d("Insert", sb.toString());
            db.execSQL(sb.toString());
            //Cerramos la base de datos
            db.close();
            exito = true;
        }
        return exito;
	}
}