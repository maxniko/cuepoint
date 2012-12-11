package com.cuepoint.controladores;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
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

public class MensajesRecibidos extends Activity{
	ArrayList<Mensaje> itemsR;
	ListView recibidos;
	Mensaje mensaje;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p10_sms_recibidos);
		
		recibidos = (ListView)findViewById(R.id.msjsRecibidos);
		MensajesSQLite m = new MensajesSQLite();
		itemsR = m.getMensajesRecibidos(this);
		
		if(!itemsR.isEmpty()) {
	        ItemMensajeAdapter adapterR = new ItemMensajeAdapter(this, itemsR);
	        recibidos.setAdapter(adapterR);
	        recibidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	
	        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	    mensaje = itemsR.get(position);
	        	    if(mensaje.getEstado() == 0)
	        	    {
	        	    	mensaje.setEstado(1);
	        	    	itemsR.set(position, mensaje);
	        	    	MensajesSQLite msql = new MensajesSQLite();
	        	    	msql.marcarLeido(MensajesRecibidos.this, mensaje.getIdMensaje());
	        	    	msql = null;
		    			ItemMensajeAdapter adapter = new ItemMensajeAdapter(MensajesRecibidos.this, itemsR);
		    	        recibidos.setAdapter(adapter);
	        	    }
	        	    if (mensaje.getTipo() == 3)
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
	
	private void iniciarActividadImagen()
	{
		//Obtengo los datos del plano desde la base de datos
		PlanosSQLite psql = new PlanosSQLite();
		if (mensaje.getTipo() == 3)
	    {
			Intent intent = new Intent(this, Imagen.class);
	        Plano p = psql.getPlanoPorId(MensajesRecibidos.this, mensaje.getIdPlano());
	        
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
		else
		{
			ArrayList<Plano> lista = psql.getPlanos(this);
	        
	        if(lista.size() == 1)
	        {
	        	Plano plano = lista.get(0);
	    	    Intent intent = new Intent(this, Imagen.class);
	    	    intent.putExtra("Plano", plano);
	    	    intent.putExtra("InsertarMarca", false);
		        intent.putExtra("Mensaje", mensaje);
		        intent.putExtra("Respuesta", true);
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
    		//Dialogo para responder un mensaje
    		case 3:
    			dialogo = crearDialogoResponder();
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    
    	return dialogo;
    }
	
	private Dialog crearDialogoResponder() {
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Titulo");
    	builder.setMessage("Desea responder a la persona seleccionada?");
    	builder.setNegativeButton("Cerrar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	builder.setPositiveButton("Responder", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				iniciarActividadImagen();
			}
		});
    	return builder.create();
	}

	protected Dialog crearDialogoEliminarMensaje()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Mensajes");
    	builder.setMessage("¿Desea eliminar todos los mensajes recibidos?");
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MensajesSQLite msql = new MensajesSQLite();
    			boolean exito = msql.borrarRecibidos(MensajesRecibidos.this);
    			if(exito) {
    				ListView recibidos = (ListView)findViewById(R.id.msjsRecibidos);
    				itemsR.clear();
	    			ItemMensajeAdapter adapter = new ItemMensajeAdapter(MensajesRecibidos.this, itemsR);
	    	        recibidos.setAdapter(adapter);
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
    	builder.setPositiveButton("Responder", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				iniciarActividadImagen();
			}
		});
    	builder.setNegativeButton("Cerrar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	return builder.create();
    }
}