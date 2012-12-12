package com.cuepoint.controladores;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Mensaje;
import com.cuepoint.clases.Plano;
import com.cuepoint.clases.Punto;
import com.cuepoint.datos.ItemMensajeAdapter;
import com.cuepoint.datos.MensajesSQLite;
import com.cuepoint.datos.PlanosSQLite;

public class MensajesEnviados extends Activity{
	ArrayList<Mensaje> itemsE;
	ListView enviados;
	Mensaje mensaje;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p10_sms_enviados);
		
		enviados = (ListView)findViewById(R.id.msjsEnviados);
		
		actualizarListaMensajes();
	}
	
	private void actualizarListaMensajes()
	{
		MensajesSQLite m = new MensajesSQLite();
		itemsE = m.getMensajesEnviados(this);
		
		if(!itemsE.isEmpty()) {
	        ItemMensajeAdapter adapter = new ItemMensajeAdapter(this, itemsE);
	        enviados.setAdapter(adapter);
	        enviados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	
	        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	    mensaje = itemsE.get(position);
	        	    //Si es un mensaje no leido se actualiza el estado a leído
	        	    if(mensaje.getEstado() == 0)
	        	    {
	        	    	mensaje.setEstado(1);
	        	    	itemsE.set(position, mensaje);
	        	    	MensajesSQLite msql = new MensajesSQLite();
	        	    	msql.marcarLeido(MensajesEnviados.this, mensaje.getIdMensaje());
	        	    	msql = null;
		    			ItemMensajeAdapter adapter = new ItemMensajeAdapter(MensajesEnviados.this, itemsE);
		    	        enviados.setAdapter(adapter);
	        	    }
	        	    if (mensaje.getTipo() == 1)
	        	    {
	        	    	iniciarActividadImagen();
	        	    }
	        	    else
	        	    {
	        	    	if(!mensaje.getTexto().equals(""))
	        	    	{
	        	    		showDialog(2);
	        	    	}
	        	    	else
	        	    	{
	        	    		showDialog(3);
	        	    	}
	        	    }
	        	  }
	        	});
        }
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		actualizarListaMensajes();
	}
	
	private void iniciarActividadImagen()
	{
		Intent intent = new Intent(this, Imagen.class);
		//Obtengo los datos del plano desde la base de datos
		PlanosSQLite psql = new PlanosSQLite();
        Plano p = psql.getPlanoPorId(MensajesEnviados.this, mensaje.getIdPlano());
        
        //Guardo las coordenadas en un objeto Point para pasar a la otra activity
        Punto xy = new Punto(mensaje.getX(), mensaje.getY());
        
        intent.putExtra("Plano", p);
        intent.putExtra("Punto", xy);
        intent.putExtra("InsertarMarca", true);
        intent.putExtra("Mensaje", mensaje);
        intent.putExtra("Respuesta", true);
        //Si el texto esta vacío envía el flag falso para no mostrar el mensaje
        intent.putExtra("MostrarMensaje", (mensaje.getTexto().equals("")) ? false : true );
        startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.mensajes, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.eliminar:
			showDialog(1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == 3)
		{
			String nombre = "";
			if(mensaje.getNombre().equals(""))
			{
				nombre = mensaje.getNumeroOrigenDestino();
			}
			else
			{
				nombre = mensaje.getNombre();
			}
			((AlertDialog)dialog).setTitle(nombre);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
    protected Dialog onCreateDialog(int id) 
	{
		Dialog dialogo = null;

    	switch(id)
    	{
    		//Dialogo confirmación de eliminación de mensajes
    		case 1:
    			dialogo = crearDialogoEliminarMensaje();
    			break;
    		//Dialogo para mostrar mensaje opcional
    		case 2:
    			dialogo = crearDialogoMensaje();
    			break;
    		//Dialogo para reenviar un mensaje
    		case 3:
    			dialogo = crearDialogoReenviar();
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    	return dialogo;
    }
	
	protected Dialog crearDialogoEliminarMensaje()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Mensajes");
    	builder.setMessage("¿Desea eliminar todos los mensajes enviados?");
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MensajesSQLite msql = new MensajesSQLite();
    			boolean exito = msql.borrarEnviados(MensajesEnviados.this);
    			if(exito) {
    				ListView enviados = (ListView)findViewById(R.id.msjsEnviados);
    				itemsE.clear();
	    			ItemMensajeAdapter adapter = new ItemMensajeAdapter(MensajesEnviados.this, itemsE);
	    	        enviados.setAdapter(adapter);
    			}
			}
		});
    	builder.setNegativeButton("Cancelar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	
    	return builder.create();
	}
	
	protected Dialog crearDialogoMensaje()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Mensaje adicional");
    	builder.setMessage(mensaje.getTexto());
    	builder.setPositiveButton("Reenviar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				reenviarMensaje();
			}
		});
    	builder.setNegativeButton("Cerrar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	return builder.create();
    }
	
	protected Dialog crearDialogoReenviar()
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Titulo");
    	builder.setMessage("Desea reenviar la solicitud a la persona seleccionada?");
    	builder.setNegativeButton("Cerrar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	builder.setPositiveButton("Reenviar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				reenviarMensaje();
			}
		});
    	return builder.create();
    }
	
	private void reenviarMensaje()
	{
		Intent i = new Intent();
		Bundle bundle = new Bundle();
		if(mensaje.getNombre().equals(""))
		{
			bundle.putString("nombre", mensaje.getNumeroOrigenDestino());
		}
		else
		{
			bundle.putString("nombre", mensaje.getNombre());
		}
		bundle.putString("numero", mensaje.getNumeroOrigenDestino());
		bundle.putInt("idPlano", 0);
        i.putExtras(bundle);
		i.setComponent(new ComponentName(this, EnviarSMS.class));
		startActivity(i);
	}
}