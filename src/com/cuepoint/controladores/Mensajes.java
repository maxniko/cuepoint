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

public class Mensajes extends Activity{
	ArrayList<Mensaje> itemsE;
	ArrayList<Mensaje> itemsR;
	private static final int ENVIADOS = 1;
	private static final int RECIBIDOS = 2;
	
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
	        enviados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	
	        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	    Intent intent = new Intent(view.getContext(), Imagen.class);
	        	    Mensaje m = itemsE.get(position);
	        	    if (m.getTipo() == 1)
	        	    {
	        	    	//Obtengo los datos del plano desde la base de datos
	    				PlanosSQLite psql = new PlanosSQLite();
	    		        Plano p = psql.getPlanoPorId(Mensajes.this, m.getIdPlano());
	    		        
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
        if(!itemsR.isEmpty()) {
	        ItemMensajeAdapter adapterR = new ItemMensajeAdapter(this, itemsR);
	        recibidos.setAdapter(adapterR);
	        recibidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	
	        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	    Intent intent = new Intent(view.getContext(), Imagen.class);
	        	    Mensaje m = itemsR.get(position);
	        	    if (m.getTipo() == 3)
	        	    {
	        	    	//Obtengo los datos del plano desde la base de datos
	    				PlanosSQLite psql = new PlanosSQLite();
	    		        Plano p = psql.getPlanoPorId(Mensajes.this, m.getIdPlano());
	    		        
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
		case R.id.eliminarEnviados:
			showDialog(ENVIADOS);
			return true;
		case R.id.eliminarRecibidos:
			showDialog(RECIBIDOS);
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
    		case ENVIADOS:
    			dialogo = crearDialogoConfirmacion("enviados", ENVIADOS);
    			break;
    		case RECIBIDOS:
    			dialogo = crearDialogoConfirmacion("recibidos", RECIBIDOS);
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    
    	return dialogo;
    }
	
	private Dialog crearDialogoConfirmacion(String mensaje, final int id)
	{
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Mensajes");
    	builder.setMessage("¿Desea eliminar todos los mensajes " + mensaje + "?");
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MensajesSQLite msql = new MensajesSQLite();
				switch(id)
		    	{
		    		case ENVIADOS:
		    			boolean exitoE = msql.borrarEnviados(Mensajes.this);
		    			if(exitoE) {
		    				ListView enviados = (ListView)findViewById(R.id.msjsEnviados);
		    				itemsE.clear();
			    			ItemMensajeAdapter adapter = new ItemMensajeAdapter(Mensajes.this, itemsE);
			    	        enviados.setAdapter(adapter);
		    			}
		    			break;
		    		case RECIBIDOS:
		    			boolean exitoR = msql.borrarRecibidos(Mensajes.this);
		    			if(exitoR) {
		    				ListView recibidos = (ListView) findViewById(R.id.msjsRecibidos);
		    				itemsR.clear();
		    				ItemMensajeAdapter ima = new ItemMensajeAdapter(Mensajes.this, itemsR);
		    				recibidos.setAdapter(ima);
		    			}
		    			break;
		    		default:
		    			break;
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
	
	private void cargarListas()
	{
		MensajesSQLite m = new MensajesSQLite();
		itemsE = m.getMensajesEnviados(this);
		itemsR = m.getMensajesRecibidos(this);
		m = null;
	}
}
