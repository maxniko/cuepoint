package com.cuepoint.controladores;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p10_sms_enviados);
		
		ListView enviados = (ListView)findViewById(R.id.msjsEnviados);
		MensajesSQLite m = new MensajesSQLite();
		itemsE = m.getMensajesEnviados(this);
		
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
}
