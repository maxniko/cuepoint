package com.cuepoint.controladores;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p10_sms_enviados);
		
		enviados = (ListView)findViewById(R.id.msjsEnviados);
		MensajesSQLite m = new MensajesSQLite();
		itemsE = m.getMensajesEnviados(this);
		
		if(!itemsE.isEmpty()) {
	        ItemMensajeAdapter adapter = new ItemMensajeAdapter(this, itemsE);
	        enviados.setAdapter(adapter);
	        enviados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	
	        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	    Intent intent = new Intent(view.getContext(), Imagen.class);
	        	    Mensaje m = itemsE.get(position);
	        	    //Si es un mensaje no leido se actualiza el estado a leído
	        	    if(m.getEstado() == 0)
	        	    {
	        	    	m.setEstado(1);
	        	    	itemsE.set(position, m);
	        	    	MensajesSQLite msql = new MensajesSQLite();
	        	    	msql.marcarLeido(MensajesEnviados.this, m.getIdMensaje());
	        	    	msql = null;
		    			ItemMensajeAdapter adapter = new ItemMensajeAdapter(MensajesEnviados.this, itemsE);
		    	        enviados.setAdapter(adapter);
	        	    }
	        	    if (m.getTipo() == 1)
	        	    {
	        	    	//Obtengo los datos del plano desde la base de datos
	    				PlanosSQLite psql = new PlanosSQLite();
	    		        Plano p = psql.getPlanoPorId(MensajesEnviados.this, m.getIdPlano());
	    		        
	    		        //Guardo las coordenadas en un objeto Point para pasar a la otra activity
	    		        Punto xy = new Punto(m.getX(), m.getY());
	    		        
	    		        intent.putExtra("Plano", p);
	    		        intent.putExtra("Punto", xy);
	    		        intent.putExtra("InsertarMarca", true);
	    		        intent.putExtra("Mensaje", m);
	    		        intent.putExtra("Respuesta", true);
	    		        startActivity(intent);
	        	    }
	        	    else
	        	    {
	        	    	
	        	    }
	        	  }
	        	});
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
    protected Dialog onCreateDialog(int id) 
	{
		Dialog dialogo = null;

    	switch(id)
    	{
    		//Dialogo confirmación de eliminación de mensajes
    		case 1:
    			dialogo = crearDialogoEliminarMensaje();
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
}
