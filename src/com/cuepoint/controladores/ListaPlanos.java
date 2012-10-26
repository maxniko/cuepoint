package com.cuepoint.controladores;

import java.util.ArrayList;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Plano;
import com.cuepoint.datos.ItemPlanoAdapter;
import com.cuepoint.datos.PlanosSQLite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListaPlanos extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_planos);
        
        ListView lv = (ListView)findViewById(R.id.listView);
        
        final ArrayList<Plano> lista = obtenerPlanos();
        
        ItemPlanoAdapter adapter = new ItemPlanoAdapter(this, lista);
        
        lv.setAdapter(adapter);
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	
        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	    Intent intent = new Intent(view.getContext(), Imagen.class);
        	    String dir = lista.get(position).getRutaImagen();
        	    intent.putExtra("path", dir );
        	    startActivity(intent);
        	  }
        	});
        
    }
    
    private ArrayList<Plano> obtenerPlanos()
    {
    	ArrayList<Plano> items = new ArrayList<Plano>();
    	
    	//Abrimos la base de datos 'Planos'
        PlanosSQLite pdb = new PlanosSQLite(this, "Planos", null, 1);
        SQLiteDatabase db = pdb.getReadableDatabase();
 
        /* Insertar datos en la base de datos
        SQLiteDatabase db = pdb.getWritableDatabase();
 
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            db.execSQL("");
            //Cerramos la base de datos
            db.close();
        }
    	*/
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery("SELECT nombre,path FROM Planos", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                 String nombre = c.getString(0);
                 String path = c.getString(1);
                 items.add(new Plano(nombre,path));
            } while(c.moveToNext());
       }
       return items;
    }
}