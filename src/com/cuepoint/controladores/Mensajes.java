package com.cuepoint.controladores;

import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import com.cuepoint.actividades.R;
import com.cuepoint.clases.Mensaje;
import com.cuepoint.datos.ItemMensajeAdapter;
import com.cuepoint.datos.PlanosSQLite;

public class Mensajes extends Activity{
	ArrayList<Mensaje> itemsE;
	ArrayList<Mensaje> itemsR;
	
	
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
	
	private void cargarListas()
	{
		itemsE = new ArrayList<Mensaje>();
		itemsR = new ArrayList<Mensaje>();
    	
    	//Abrimos la base de datos 'Planos'
        PlanosSQLite pdb = new PlanosSQLite(this, "Planos", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery("SELECT idMensaje,tipo,nroOrigen,texto," +
        		"(strftime('%s',fecha)*1000) FROM Mensajes ORDER BY fecha", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	Mensaje m = new Mensaje();
            	m.setIdMensaje(c.getInt(0));
            	m.setTipo(c.getInt(1));
            	m.setNroOrigen(c.getInt(2));
            	m.setTexto(c.getString(3));
            	long milisegundos = c.getLong(4);
            	Date f = new Date(milisegundos);
            	m.setFecha(f);
            	//Mensaje enviado
            	if(m.getTipo() == 0)
            	{
            		itemsE.add(m);
            	}
            	//Mensaje recibido
            	else if(m.getTipo() == 1)
            	{
            		itemsR.add(m);
            	}
            } while(c.moveToNext());
       }
       db.close();
	}
}
