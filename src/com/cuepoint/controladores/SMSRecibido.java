package com.cuepoint.controladores;

import java.util.Date;

import com.cuepoint.clases.Mensaje;
import com.cuepoint.clases.Util;
import com.cuepoint.datos.MensajesSQLite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.util.Log;

public class SMSRecibido extends Activity {
	
	private static final int DIALOGO_ALERTA = 1;
	private static final int DIALOGO_CONFIRMACION = 2;
	private static final int DIALOGO_SELECCION = 3;
	String nombre = "";
	String numero = "";
	String texto = "";
	long tiempo = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        try
        {
        	//Obtengo el numero de quien mando el sms
        	numero = i.getStringExtra("NumeroOrigen");
        	//Obtengo el texto del mensaje
        	texto = i.getStringExtra("Texto");
        	//Obtengo la fecha en milisegundos
        	tiempo = i.getLongExtra("Fecha", 0);
        }
        catch(Exception e)
        {
        	Log.e("SMSRecibido", "No se pudo leer el numero, texto o fecha");
        }
        
        
        // Query: contacto con el numero de telefono ingresado
        //lanzamos una query al Content provider por medio del "contentresolver"
        //y guardamos la tabla de los resultados que nos devuelve con un Cursor
        //para iterar despues en las filas con el objeto de clase Cursor. 
        //(Es como una tabla de filas y columnas)
     		Cursor mCursor = getContentResolver().query(
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
            int numberIndex = mCursor.getColumnIndexOrThrow(Phone.NUMBER);
            
            
            if (mCursor.moveToFirst()) {
                do {
                    nombre = mCursor.getString(nameIndex);
                    numero = mCursor.getString(numberIndex);
                } while (mCursor.moveToNext());
            }
            guardarMensajeEnSQLite();
          //El vibrador del dispositivo
            Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);
        showDialog(DIALOGO_CONFIRMACION);
    }
	
	protected void guardarMensajeEnSQLite()
	{
		MensajesSQLite msql = new MensajesSQLite();
		Mensaje m = new Mensaje();
		m.setTipo(1);
		m.setTexto(texto);
		m.setNroOrigen(Integer.parseInt(numero));
		Date d = new Date(tiempo);
		Util u = new Util();
		m.setFecha(u.getFechaFormateada(d));
		msql.nuevoMensaje(this, m);
		m = null;
		msql = null;
	}
	
	@Override
    protected Dialog onCreateDialog(int id) 
	{
    	Dialog dialogo = null;

    	switch(id)
    	{
    		case DIALOGO_ALERTA:
    			dialogo = crearDialogoAlerta();
    			break;
    		case DIALOGO_CONFIRMACION:
    			dialogo = crearDialogoConfirmacion();
    			break;
    		case DIALOGO_SELECCION:
    			dialogo = crearDialogoSeleccion();
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    
    	return dialogo;
    }
    
    private Dialog crearDialogoAlerta()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Informacion");
    	builder.setMessage("Esto es un mensaje de alerta.");
    	builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	
    	return builder.create();
    }
    
    private Dialog crearDialogoConfirmacion()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("CuePoint");
    	if (nombre.equals(""))
    	{
    		builder.setMessage("El numero " + numero + " solicita su ubicacion a traves de CuePoint, �Desea responder ahora?");
    	}
    	else
    	{
    		builder.setMessage(nombre + " solicita su ubicacion a traves de CuePoint, �Desea responder ahora?");
    	}
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
		    	intent.setComponent(new ComponentName(SMSRecibido.this, ListaPlanos.class));
		    	startActivity(intent);
		    	finish();
			}
		});
    	builder.setNegativeButton("Cancelar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("Dialogos", "Confirmacion Cancelada.");
				dialog.cancel();
				finish();
			}
		});
    	
    	return builder.create();
    }
    
    private Dialog crearDialogoSeleccion()
    {
    	final String[] items = {"Espa�ol", "Ingl�s", "Franc�s"};
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Selecci�n");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("Dialogos", "Opci�n elegida: " + items[item]);
    	    }
    	});
    	
    	//Dialogo de selecci�n simple
//    	builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
//			public void onClick(DialogInterface dialog, int item, boolean isChecked) {
//				Log.i("Dialogos", "Opci�n elegida: " + items[item]);
//			}
//		});
    	
    	//Di�logo de selecci�n m�ltiple
//    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
//    	    public void onClick(DialogInterface dialog, int item) {
//    	        Log.i("Dialogos", "Opci�n elegida: " + items[item]);
//    	    }
//    	});
    	
    	return builder.create();
    }
    
}
