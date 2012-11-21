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
        setContentView(R.layout.p09_planos);
        
        ListView lv = (ListView)findViewById(R.id.listView);
        
        final ArrayList<Plano> lista = obtenerPlanos();
        
        ItemPlanoAdapter adapter = new ItemPlanoAdapter(this, lista);
        
        lv.setAdapter(adapter);
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	
        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	    Intent intent = new Intent(view.getContext(), Imagen.class);
        	    String dir = lista.get(position).getRutaImagen();
        	    int idPlano = lista.get(position).getIdPlano();
        	    intent.putExtra("path", dir );
        	    intent.putExtra("idPlano", idPlano);
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
        
        //Leer datos de la base de datos
        Cursor c = db.rawQuery("SELECT idPlano,nombre,descripcion,path FROM Planos", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
            	int id = c.getInt(0);
                 String nombre = c.getString(1);
                 String desc = c.getString(2);
                 String path = c.getString(3);
                 Plano p = new Plano(id,nombre, path, desc);
                 items.add(p);
            } while(c.moveToNext());
       }
       db.close();
       return items;
    }
}