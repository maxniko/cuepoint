package com.cuepoint.datos;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.cuepoint.clases.Mensaje;

public class MensajesSQLite extends Activity{
	ArrayList<Mensaje> itemsE;
	ArrayList<Mensaje> itemsR;
	ContentResolver cr = null;
	
	//Tipo de mensaje (0: enviado solicitud, 1: enviado respuesta, 2: recibido solicitud, 3: recibido respuesta)
	
	public ArrayList<Mensaje> getMensajesEnviados(Context contexto)
	{
		itemsE = new ArrayList<Mensaje>();
    	
    	//Abrimos la base de datos 'CuePoint'
		ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //Leer datos de la base de datos
        //Tipo de mensaje (0: enviado solicitud, 1: enviado respuesta, 2: recibido solicitud, 3: recibido respuesta)
        Cursor c = db.rawQuery("SELECT idMensaje,tipo,nroOrigen,texto,fecha,coordenadaX,coordenadaY,idPlano " +
        		"FROM Mensajes " +
        		"WHERE tipo<2 ORDER BY fecha", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	Mensaje m = new Mensaje();
            	m.setIdMensaje(c.getInt(0));
            	m.setTipo(c.getInt(1));
            	m.setNumeroOrigenDestino(c.getInt(2));
            	m.setTexto(c.getString(3));
            	//long milisegundos = c.getLong(3);
            	//Date f = new Date(milisegundos);
            	m.setFecha(c.getString(4));
            	m.setX(c.getFloat(5));
            	m.setY(c.getFloat(6));
            	m.setIdPlano(c.getInt(7));
            	cr = contexto.getContentResolver();
            	m.setNombre(buscarNombreContacto(m.getNumeroOrigenDestino()));
            	itemsE.add(m);
            } while(c.moveToNext());
       }
       db.close();
       return itemsE;
	}
	
	public ArrayList<Mensaje> getMensajesRecibidos(Context contexto)
	{
		itemsR = new ArrayList<Mensaje>();
    	
    	//Abrimos la base de datos 'CuePoint'
		ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //Leer datos de la base de datos
        //Tipo de mensaje (0: enviado solicitud, 1: enviado respuesta, 2: recibido solicitud, 3: recibido respuesta)
        Cursor c = db.rawQuery("SELECT idMensaje,tipo,nroOrigen,texto,fecha,coordenadaX,coordenadaY,idPlano " +
        		"FROM Mensajes " +
        		"WHERE tipo>1 ORDER BY fecha", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	Mensaje m = new Mensaje();
            	m.setIdMensaje(c.getInt(0));
            	m.setTipo(c.getInt(1));
            	m.setNumeroOrigenDestino(c.getInt(2));
            	m.setTexto(c.getString(3));
            	//long milisegundos = c.getLong(3);
            	//Date f = new Date(milisegundos);
            	m.setFecha(c.getString(4));
            	m.setX(c.getFloat(5));
            	m.setY(c.getFloat(6));
            	m.setIdPlano(c.getInt(7));
            	cr = contexto.getContentResolver();
            	m.setNombre(buscarNombreContacto(m.getNumeroOrigenDestino()));
            	itemsR.add(m);
            } while(c.moveToNext());
       }
       db.close();
       return itemsR;
	}
	
	public void nuevoMensaje(Context contexto, Mensaje mensaje)
	{
    	//Abrimos la base de datos 'CuePoint'
		ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
         
        // Insertar datos en la base de datos
        SQLiteDatabase db = pdb.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        
        if(mensaje.getIdPlano() == 0)
        {
	        sb.append("INSERT INTO Mensajes (tipo, nroOrigen, texto, fecha) values (");
	        sb.append(mensaje.getTipo() + ",");
	        sb.append(mensaje.getNumeroOrigenDestino() + ",");
	        sb.append("'" + mensaje.getTexto() + "','");
	        sb.append(mensaje.getFecha());
	        sb.append("');");
        }
        else
        {
        	sb.append("INSERT INTO Mensajes (tipo, nroOrigen, texto, fecha, coordenadaX, coordenadaY, idPlano) values (");
	        sb.append(mensaje.getTipo() + ",");
	        sb.append(mensaje.getNumeroOrigenDestino() + ",");
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
		//Abrimos la base de datos 'CuePoint'
		ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
         
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
		ConexionSQLite pdb = new ConexionSQLite(contexto, "CuePoint", null, 1);
         
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
	
	protected String buscarNombreContacto(int numero)
	{
		String nombre = "";
		// Query: contacto con el numero de telefono ingresado
        //lanzamos una query al Content provider por medio del "contentresolver"
        //y guardamos la tabla de los resultados que nos devuelve con un Cursor
        //para iterar despues en las filas con el objeto de clase Cursor. 
        //(Es como una tabla de filas y columnas)
 		Cursor mCursor = cr.query(
 		Data.CONTENT_URI,
 		new String[] { Data.DISPLAY_NAME, Phone.NUMBER, },
 			Phone.NUMBER + " LIKE ? ",
 			new String[] { "%"+ numero +"%" },
 			Data.DISPLAY_NAME + " ASC");
 		//estructura query= (tabla objetivo, campos a consultar, where, parametros, ordered by)
        //ordenamos por orden alfabetico con campo Display_name.

 		// Esto asocia el ciclo de vida del cursor al ciclo de vida de la Activity. Si
        // la Activity para, el sistema libera el Cursor. No quedan recursos bloqueados.
 		startManagingCursor(mCursor);

 		int nameIndex = mCursor.getColumnIndexOrThrow(Data.DISPLAY_NAME);
        
        if (mCursor.moveToFirst()) {
            do {
                nombre = mCursor.getString(nameIndex);
            } while (mCursor.moveToNext());
        }
        return nombre;
	}

}
