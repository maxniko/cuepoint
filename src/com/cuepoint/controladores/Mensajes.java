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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import com.cuepoint.actividades.R;
import com.cuepoint.clases.Mensaje;
import com.cuepoint.datos.ItemMensajeAdapter;
import com.cuepoint.datos.MensajesSQLite;

public class Mensajes extends Activity{
	ArrayList<Mensaje> itemsE;
	ArrayList<Mensaje> itemsR;
	boolean acepto = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p07_mensajes);
		
		ListView enviados = (ListView)findViewById(R.id.msjsEnviados);
		ListView recibidos = (ListView)findViewById(R.id.msjsRecibidos);
        
        cargarListas();
        
        if(!itemsE.isEmpty()) {
	        ItemMensajeAdapter adapter = new ItemMensajeAdapter(this, itemsE);
	        enviados.setAdapter(adapter);
	        /*enviados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	
	        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	    Intent intent = new Intent(view.getContext(), Imagen.class);
	        	    String dir = lista.get(position).getRutaImagen();
	        	    intent.putExtra("path", dir );
	        	    startActivity(intent);
	        	  }
	        	});*/
        }
        if(!itemsR.isEmpty()) {
	        ItemMensajeAdapter adapterR = new ItemMensajeAdapter(this, itemsR);
	        recibidos.setAdapter(adapterR);
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
		MensajesSQLite msql = new MensajesSQLite();
		switch (item.getItemId()) {
		case R.id.eliminarEnviados:
			showDialog(1);
			if (acepto) {
				msql.borrarEnviados(this);
			}
			acepto = false;
			return true;
		case R.id.eliminarRecibidos:
			showDialog(2);
			if (acepto) {
				msql.borrarRecibidos(this);
			}
			acepto = false;
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
    		case 1:
    			dialogo = crearDialogoConfirmacion("enviados");
    			break;
    		case 2:
    			dialogo = crearDialogoConfirmacion("recibidos");
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    
    	return dialogo;
    }
	
	private Dialog crearDialogoConfirmacion(String mensaje)
	{
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Mensajes");
    	builder.setMessage("¿Desea eliminar todos los mensajes " + mensaje + "?");
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				acepto = true;
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
	
	private void cargarListas()
	{
		MensajesSQLite m = new MensajesSQLite();
		itemsE = m.getMensajesEnviados(this);
		itemsR = m.getMensajesRecibidos(this);
		m = null;
	}
}
