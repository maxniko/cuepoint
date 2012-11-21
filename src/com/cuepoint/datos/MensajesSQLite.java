package com.cuepoint.datos;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cuepoint.clases.Mensaje;

public class MensajesSQLite {
	ArrayList<Mensaje> itemsE;
	ArrayList<Mensaje> itemsR;
	
	//0: Enviados, 1: Recibidos
	
	public ArrayList<Mensaje> getMensajesEnviados(Context contexto)
	{
		itemsE = new ArrayList<Mensaje>();
    	
    	//Abrimos la base de datos 'Planos'
        PlanosSQLite pdb = new PlanosSQLite(contexto, "Planos", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery("SELECT idMensaje,nroOrigen,texto,fecha " +
        		"FROM Mensajes " +
        		"WHERE tipo=0 ORDER BY fecha", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	Mensaje m = new Mensaje();
            	m.setIdMensaje(c.getInt(0));
            	m.setNroOrigen(c.getInt(1));
            	m.setTexto(c.getString(2));
            	//long milisegundos = c.getLong(3);
            	//Date f = new Date(milisegundos);
            	m.setFecha(c.getString(3));
            	m.setTipo(0);
            	itemsE.add(m);
            } while(c.moveToNext());
       }
       db.close();
       return itemsE;
	}
	
	public ArrayList<Mensaje> getMensajesRecibidos(Context contexto)
	{
		itemsR = new ArrayList<Mensaje>();
    	
    	//Abrimos la base de datos 'Planos'
        PlanosSQLite pdb = new PlanosSQLite(contexto, "Planos", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery("SELECT idMensaje,nroOrigen,texto,fecha " +
        		"FROM Mensajes " +
        		"WHERE tipo=1 ORDER BY fecha", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	Mensaje m = new Mensaje();
            	m.setIdMensaje(c.getInt(0));
            	m.setNroOrigen(c.getInt(1));
            	m.setTexto(c.getString(2));
            	//long milisegundos = c.getLong(3);
            	//Date f = new Date(milisegundos);
            	m.setFecha(c.getString(3));
            	m.setTipo(1);
            	itemsR.add(m);
            } while(c.moveToNext());
       }
       db.close();
       return itemsR;
	}
	
	public void nuevoMensaje(Context contexto, Mensaje mensaje)
	{
    	//Abrimos la base de datos 'Planos'
        PlanosSQLite pdb = new PlanosSQLite(contexto, "Planos", null, 1);
         
        // Insertar datos en la base de datos
        SQLiteDatabase db = pdb.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        
        if(mensaje.getIdPlano() == 0)
        {
	        sb.append("INSERT INTO Mensajes (tipo, nroOrigen, texto, fecha) values (");
	        sb.append(mensaje.getTipo() + ",");
	        sb.append(mensaje.getNroOrigenDestino() + ",");
	        sb.append("'" + mensaje.getTexto() + "','");
	        sb.append(mensaje.getFecha());
	        sb.append("');");
        }
        else
        {
        	sb.append("INSERT INTO Mensajes (tipo, nroOrigen, texto, fecha, coordenadaX, coordenadaY, idPlano) values (");
	        sb.append(mensaje.getTipo() + ",");
	        sb.append(mensaje.getNroOrigenDestino() + ",");
	        sb.append("'" + mensaje.getTexto() + "','");
	        sb.append(mensaje.getFecha() + "','");
	        sb.append(mensaje.getX() + "','");
	        sb.append(mensaje.getY() + "','");
	        sb.append(mensaje.getIdPlano());
	        sb.append("');");
        }
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
        	Log.d("Insert", sb.toString());
            db.execSQL(sb.toString());
            //Cerramos la base de datos
            db.close();
        }
	}
	
	public void borrarEnviados(Context contexto)
	{
		//Abrimos la base de datos 'Planos'
        PlanosSQLite pdb = new PlanosSQLite(contexto, "Planos", null, 1);
         
        // Insertar datos en la base de datos
        SQLiteDatabase db = pdb.getWritableDatabase();
        
        
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            db.execSQL("DELETE FROM Mensajes WHERE tipo = 0;");
            //Cerramos la base de datos
            db.close();
        }
	}
	
	public void borrarRecibidos(Context contexto)
	{
		//Abrimos la base de datos 'Planos'
        PlanosSQLite pdb = new PlanosSQLite(contexto, "Planos", null, 1);
         
        // Insertar datos en la base de datos
        SQLiteDatabase db = pdb.getWritableDatabase();
        
        
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            db.execSQL("DELETE FROM Mensajes WHERE tipo = 1;");
            //Cerramos la base de datos
            db.close();
        }
	}
}
