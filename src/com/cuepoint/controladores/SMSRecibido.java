package com.cuepoint.controladores;

import java.util.ArrayList;
import java.util.Date;

import com.cuepoint.clases.Mensaje;
import com.cuepoint.clases.Plano;
import com.cuepoint.clases.Punto;
import com.cuepoint.clases.Util;
import com.cuepoint.datos.MensajesSQLite;
import com.cuepoint.datos.PlanosSQLite;

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

/**
 * Gestiona la llegada de un SMS y las acciones a tomar
 */
public class SMSRecibido extends Activity
{
	//Variables globales
	private static final int SOLICITUD = 1;
	private static final int RESPUESTA = 2;
	
	Mensaje mensaje = null;
	String textoSMS = "";
	
	//Fecha del mensaje en milisegundos
	long tiempo = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        mensaje = new Mensaje();
        Intent i = getIntent();
        try
        {
        	//Obtengo el numero de quien mando el sms
        	mensaje.setNumeroOrigenDestino(i.getStringExtra("NumeroOrigen"));
        	//Obtengo el texto del mensaje
        	textoSMS = i.getStringExtra("Texto");
        	//Obtengo la fecha en milisegundos
        	Date d = new Date(i.getLongExtra("Fecha", 0));
    		Util u = new Util();
    		mensaje.setFecha(u.getFechaFormateada(d));
        }
        catch(Exception e)
        {
        	Log.e("SMSRecibido", "No se pudo leer el numero, texto o fecha");
        }
        //Guardo el tipo de mensaje
        verTipoMensaje();
        //Busco en el m�vil el nombre de la persona que envi� el mensaje
        buscarNombreContacto();
        //Guardar texto opcional en caso de que exista
        extraerTextoOpcionalDeMensaje();
        //Vibra por 2 segundos
        vibrar(2000);
        if (mensaje.getTipo() == 2) {
        	showDialog(SOLICITUD);
        } else {
        	showDialog(RESPUESTA);
        }
    }
	
	/**
	 * Asigna un tipo de mensaje dependiendo si es una solicitud o una respuesta
	 * Tipos de mensajes: 0: enviado solicitud, 1: enviado respuesta, 2: recibido solicitud, 3: recibido respuesta
	 */
	protected void verTipoMensaje()
	{
		//Tipo de mensaje (0: enviado solicitud, 1: enviado respuesta, 2: recibido solicitud, 3: recibido respuesta)
		if(textoSMS.substring(9, 10).equals("/"))
		{
			mensaje.setTipo(2);
		}
		else
		{
			mensaje.setTipo(3);
			String [] t = textoSMS.split(",");
			try
			{
				mensaje.setX(Float.parseFloat(t[1]));
				mensaje.setY(Float.parseFloat(t[2]));
				mensaje.setIdPlano(Integer.parseInt(t[3]));
			}
			catch (Exception e)
			{
				Log.e("Mensaje recibido", "ERROR AL CONVERTIR COORDENADAS");
			}
		}
	}
	
	/**
	 * Guarda el texto opcional que pudiera traer el sms
	 */
	protected void extraerTextoOpcionalDeMensaje()
	{
		String [] palabra = textoSMS.split(">");
		if(palabra.length > 1)
		{
			mensaje.setTexto(palabra[1]);
		}
	}
	
	/**
	 * Se encarga de vibrar la cantidad de milisegundos enviados por par�metro
	 * @param milisegundos
	 */
	protected void vibrar(long milisegundos)
	{
		//El vibrador del dispositivo
        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milisegundos);
	}
	
	/**
	 * Busca en la agenda del tel�fono el nombre de la persona que envi� el sms
	 */
	protected void buscarNombreContacto()
	{
		//Query: contacto con el numero de telefono ingresado
        //lanzamos una query al Content provider por medio del "contentresolver"
        //y guardamos la tabla de los resultados que nos devuelve con un Cursor
        //para iterar despues en las filas con el objeto de clase Cursor. 
        //(Es como una tabla de filas y columnas)
 		Cursor mCursor = getContentResolver().query(
 		Data.CONTENT_URI,
 		new String[] { Data.DISPLAY_NAME, Phone.NUMBER, },
 			Phone.NUMBER + " LIKE ? ",
 			new String[] { "%"+ mensaje.getNumeroOrigenDestino() +"%" },
 			Data.DISPLAY_NAME + " ASC");
 		//Estructura query= (tabla objetivo, campos a consultar, where, parametros, order by)
        //Ordenamos por orden alfabetico con campo Display_name.

 		//Esto asocia el ciclo de vida del cursor al ciclo de vida de la Activity. Si
        //la Activity para, el sistema libera el Cursor. No quedan recursos bloqueados.
 		startManagingCursor(mCursor);

 		int nameIndex = mCursor.getColumnIndexOrThrow(Data.DISPLAY_NAME);
        int numberIndex = mCursor.getColumnIndexOrThrow(Phone.NUMBER);
        
        
        if (mCursor.moveToFirst()) {
            do {
                mensaje.setNombre(mCursor.getString(nameIndex));
                mensaje.setNumeroOrigenDestino(mCursor.getString(numberIndex));
            } while (mCursor.moveToNext());
        }
	}
	
	/**
	 * Guarda un mensaje recibido en la base de datos de la aplicaci�n
	 */
	protected void guardarMensajeEnSQLite()
	{
		MensajesSQLite msql = new MensajesSQLite();
		msql.nuevoMensaje(this, mensaje);
		msql = null;
	}
	
	@Override
    protected Dialog onCreateDialog(int id) 
	{
    	Dialog dialogo = null;

    	switch(id)
    	{
    		case SOLICITUD:
    			dialogo = crearDialogoSolicitud();
    			break;
    		case RESPUESTA:
    			dialogo = crearDialogoRespuesta();
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    
    	return dialogo;
    }
    
	/**
	 * Crea un di�logo para alertar al usuario sobre la llegada de un sms con una solicitud de posici�n
	 * @return
	 */
    private Dialog crearDialogoSolicitud()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("CuePoint");
    	if (mensaje.getNombre().equals(""))
    	{
    		builder.setMessage("El numero " + mensaje.getNumeroOrigenDestino() + " solicita su ubicacion a traves de CuePoint, �Desea responder ahora?");
    	}
    	else
    	{
    		builder.setMessage(mensaje.getNombre() + " solicita su ubicacion a traves de CuePoint, �Desea responder ahora?");
    	}
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				PlanosSQLite p = new PlanosSQLite();
		        final ArrayList<Plano> lista = p.getPlanos(SMSRecibido.this);
		        mensaje.setEstado(1); //Mensaje leido
		        guardarMensajeEnSQLite();
				if(lista.size() == 1)
		        {
		        	Plano plano = lista.get(0);
		    	    Intent intent = new Intent(SMSRecibido.this, Imagen.class);
		    	    intent.putExtra("Plano", plano);
		    	    intent.putExtra("InsertarMarca", false);
		    	    intent.putExtra("Respuesta", true);
			        intent.putExtra("Mensaje", mensaje);
			        //Si el texto esta vac�o env�a el flag falso para no mostrar el mensaje
			        intent.putExtra("MostrarMensaje", (mensaje.getTexto().equals("")) ? false : true );
		    	    startActivity(intent);
		        }
		        else
		        {
		        	Intent intent = new Intent();
		        	intent.setComponent(new ComponentName(SMSRecibido.this, ListaPlanos.class));
		        	startActivity(intent);
		        }
		    	finish();
			}
		});
    	builder.setNegativeButton("Cancelar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mensaje.setEstado(0); //Mensaje no leido
		        guardarMensajeEnSQLite();
				dialog.cancel();
				finish();
			}
		});
    	return builder.create();
    }
    
    /**
	 * Crea un di�logo para alertar al usuario sobre la llegada de un sms con una respuesta de posici�n
	 * @return
	 */
    private Dialog crearDialogoRespuesta()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("CuePoint");
    	if (mensaje.getNombre().equals(""))
    	{
    		builder.setMessage("El numero " + mensaje.getNumeroOrigenDestino() + " le env�a su ubicacion a traves de CuePoint, �Desea ver ahora?");
    	}
    	else
    	{
    		builder.setMessage(mensaje.getNombre() + " env�a su ubicacion a traves de CuePoint, �Desea ver ahora?");
    	}
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//Obtengo los datos del plano desde la base de datos
				PlanosSQLite psql = new PlanosSQLite();
		        Plano p = psql.getPlanoPorId(SMSRecibido.this, mensaje.getIdPlano());
		        
		        //Guardo las coordenadas en un objeto Point para pasar a la otra activity
		        Punto xy = new Punto(mensaje.getX(), mensaje.getY());
		        
		        Intent intent = new Intent(SMSRecibido.this, Imagen.class);
		        intent.putExtra("Plano", p);
		        intent.putExtra("Punto", xy);
		        intent.putExtra("InsertarMarca", true);
	    	    intent.putExtra("Respuesta", true);
		        intent.putExtra("Mensaje", mensaje);
		        //Si el texto esta vac�o env�a el flag falso para no mostrar el mensaje
		        intent.putExtra("MostrarMensaje", (mensaje.getTexto().equals("")) ? false : true );
		        startActivity(intent);
		    	finish();
			}
		});
    	builder.setNegativeButton("Cancelar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();
			}
		});
    	
    	return builder.create();
    }
}
