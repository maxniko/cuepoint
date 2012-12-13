package com.cuepoint.controladores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Plano;
import com.cuepoint.clases.Util;
import com.cuepoint.datos.CargaDatosWS;
import com.cuepoint.datos.PlanosSQLite;

/**
 * Gestiona el menú principal y las acciones al presionar los botones
 */
public class MenuPrincipal extends Activity
{
	//Variables globales
	private static final int REQUEST_CHOOSE_PHONE = 1;
	String res = "";
	String nombre = "";
	String numero = "";
	String hora = "";
	private ProgressDialog pd;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p01_principal);
    }
	
	/**
	 * Muestra la lista de contactos del teléfono para seleccionar un destinatario
	 * @param v
	 */
	public void enviarSolicitudClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaContactos.class));
		startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//Resultado cuando el usuario selecciona un número para enviar una solicitud de posición
		if ((requestCode == REQUEST_CHOOSE_PHONE) && (resultCode == Activity.RESULT_OK)) {
			try {
				Intent i = new Intent();
				
				Bundle bundle = new Bundle();
		        bundle.putString("nombre", data.getStringExtra("nombre"));
		        bundle.putString("numero", data.getStringExtra("numero"));
		        bundle.putInt("idPlano", 0);
		        i.putExtras(bundle);
				i.setComponent(new ComponentName(this, EnviarSMS.class));
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//Resultado cuando el usuario busca una persona para consultar si se ha notificado
		//en la base de datos al descargar el boletín
		else if((requestCode == 2) && (resultCode == Activity.RESULT_OK))
		{
			try {
				nombre = data.getStringExtra("nombre");
				numero = data.getStringExtra("numero");
				numero = Util.extraerNumero(numero);
				if(numero.length() > 7)
				{
					numero = numero.substring(numero.length() - 7, numero.length());
				}
				buscarContacto();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Inicia un plano o una lista de planos para que el usuario seleccione su posición
	 * @param v
	 */
	public void enviarPosicionClick(View v)
	{
		PlanosSQLite p = new PlanosSQLite();
        
        final ArrayList<Plano> lista = p.getPlanos(this);
        //Si hay un solo plano, se carga directamente
        if(lista.size() == 1)
        {
        	Plano plano = lista.get(0);
    	    Intent intent = new Intent(this, Imagen.class);
    	    intent.putExtra("Plano", plano);
    	    intent.putExtra("InsertarMarca", false);
    	    intent.putExtra("Respuesta", false);
	        intent.putExtra("MostrarMensaje", false );
    	    startActivity(intent);
        }
        //Si hay más de un plano se muestra una pantalla para seleccionar un plano
        else
        {
        	Intent intent = new Intent();
        	intent.setComponent(new ComponentName(this, ListaPlanos.class));
        	startActivity(intent);
        }
	}
	
	/**
	 * Inicia la pantalla de opciones de la aplicación
	 * @param v
	 */
	public void opcionesClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Preferencias.class));
    	startActivity(intent);
	}
	
	/**
	 * Inicia la actividad que administra los boletines
	 * @param v
	 */
	public void verBoletinClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Boletines.class));
    	startActivity(intent);
	}
	
	/**
	 * Inicia la actividad que gestiona los mensajes enviados/recibidos
	 * @param v
	 */
	public void mensajesClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Mensajes.class));
    	startActivity(intent);
	}
	
	/**
	 * Inicia la actividad que muestra la ayuda al usuario
	 * @param v
	 */
	public void ayudaClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Ayuda.class));
    	startActivity(intent);
	}
	
	/**
	 * Inicia la actividad que muestra la lista de contactos del teléfono
	 * @param v
	 */
	public void buscarContactoClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaContactos.class));
		startActivityForResult(intent, 2);
	}

	/**
	 * Crea una tarea en segundo plano para consultar el estado de un usuario a traves
	 * del webservice.
	 */
	private void buscarContacto()
	{
		new TareaEnBackground().execute(numero);
		pd = ProgressDialog.show(this, "Por favor espere","Consultando Base de Datos", true, false);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
    	Dialog dialogo = null;

    	switch(id)
    	{
    		//No se notifico el usuario
    		case 1:
    			dialogo = crearDialogoNegativo();
    			break;
    		//Se notifico el usuario
    		case 2:
    			dialogo = crearDialogoPositivo();
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    	return dialogo;
    }
	
	/**
	 * Dialogo que informa la no notificación de un usuario
	 * @return
	 */
	private Dialog crearDialogoNegativo()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipal.this);
    	
    	builder.setTitle("Estado de usuario");
    	builder.setMessage(nombre);
    	builder.setPositiveButton("Cerrar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	return builder.create();
    }
	
	/**
	 * Dialogo que informa la notificación de un usuario con la hora correspondiente
	 * @return
	 */
	private Dialog crearDialogoPositivo()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipal.this);
    	
    	builder.setTitle("Estado de usuario");
    	builder.setMessage(nombre);
    	builder.setPositiveButton("Cerrar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	return builder.create();
    }
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		((AlertDialog)dialog).setTitle("Estado de " + nombre);
		if(id == 1)
		{
			((AlertDialog)dialog).setMessage(nombre + " no se ha notificado");
		}
		else
		{
			((AlertDialog)dialog).setMessage(nombre + " se ha notificado a la hora " + hora);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	/**
	 * Gestiona en segundo plano la consulta al web service sobre el reporte de un usuario 
	 */
	private class TareaEnBackground extends AsyncTask<String, Void, Object>
	{
		
		@Override
		protected Integer doInBackground(String... args)
		{
			CargaDatosWS cd = new CargaDatosWS();
			res = cd.consultaUsuario(args[0]);
			return 1;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			//Se elimina la pantalla de por favor espere.
			pd.dismiss();
			if(res.equals("false"))
			{
				showDialog(1);
			}
			else
			{
				try
				{
					SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					Date fechaDate = null;
					fechaDate = formato.parse(res);
					Calendar cal = Calendar.getInstance();
					cal.setTime(fechaDate);
					hora = "";
					if(cal.get(Calendar.HOUR_OF_DAY) < 10)
					{
						hora = "0" + cal.get(Calendar.HOUR_OF_DAY);
					}
					else
					{
						hora = "" + cal.get(Calendar.HOUR_OF_DAY);
					}
					if(cal.get(Calendar.MINUTE) < 10)
					{
						hora = hora + ":0" + cal.get(Calendar.MINUTE);
					}
					else
					{
						hora = hora + ":" + cal.get(Calendar.MINUTE);
					}
					showDialog(2);
				}
				catch(Exception e)
				{
					Toast.makeText(MenuPrincipal.this, "Ocurrió un error al obtener los datos", Toast.LENGTH_LONG).show();
				}
			}
			super.onPostExecute(result);
		}
	}
}
