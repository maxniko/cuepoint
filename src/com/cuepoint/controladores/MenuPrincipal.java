package com.cuepoint.controladores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Plano;
import com.cuepoint.datos.CargaDatosWS;
import com.cuepoint.datos.PlanosSQLite;

public class MenuPrincipal extends Activity {
	
	private static final int REQUEST_CHOOSE_PHONE = 1;
	String res = "";
	String nombre = "";
	String numero = "";
	private ProgressDialog pd;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p01_principal);
    }
	
	public void enviarSolicitudClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaContactos.class));
		startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		else if((requestCode == 2) && (resultCode == Activity.RESULT_OK))
		{
			try {
				nombre = data.getStringExtra("nombre");
				numero = data.getStringExtra("numero");
				buscarContacto();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enviarPosicionClick(View v)
	{
		PlanosSQLite p = new PlanosSQLite();
        
        final ArrayList<Plano> lista = p.getPlanos(this);
        
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
        else
        {
        	Intent intent = new Intent();
        	intent.setComponent(new ComponentName(this, ListaPlanos.class));
        	startActivity(intent);
        }
	}
	
	public void opcionesClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Preferencias.class));
    	startActivity(intent);
	}
	
	public void verBoletinClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Boletin.class));
    	startActivity(intent);
	}
	
	public void mensajesClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Mensajes.class));
    	startActivity(intent);
	}
	
	public void ayudaClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Ayuda.class));
    	startActivity(intent);
	}
	
	public void buscarContactoClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaContactos.class));
		startActivityForResult(intent, 2);
	}

	private void buscarContacto()
	{
		new DownloadTask2().execute(numero);
		pd = ProgressDialog.show(this, "Por favor espere","Consultando Base de Datos", true, false);
	}
	
	//Tarea en Background
	private class DownloadTask2 extends AsyncTask<String, Void, Object> {
		protected Integer doInBackground(String... args) {
			CargaDatosWS cd = new CargaDatosWS();
			res = cd.consultaUsuario(args[0]);
			return 1;
		}

		protected void onPostExecute(Object result) {
			//Se elimina la pantalla de por favor espere.
			pd.dismiss();
			if(res.equals("false"))
			{
				Toast.makeText(MenuPrincipal.this, nombre + " no se ha notificado", Toast.LENGTH_LONG).show();
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
					String hora = "";
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
					Toast.makeText(MenuPrincipal.this, nombre + " se ha notificado a la hora " + hora, Toast.LENGTH_LONG).show();
				}
				catch(Exception e)
				{
					Toast.makeText(MenuPrincipal.this, "Ocurrió un error al obtener los datos", Toast.LENGTH_LONG).show();
				}
			}
			//Se muestra mensaje con la respuesta del servicio web
			super.onPostExecute(result);
		}
	}
}
