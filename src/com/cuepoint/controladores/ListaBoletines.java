package com.cuepoint.controladores;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.cuepoint.actividades.R;
import com.cuepoint.clases.Boletin;
import com.cuepoint.datos.BoletinesSQLite;

public class ListaBoletines extends ListActivity
{
	ListView boletines;
	ArrayList<String> lista;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p11_boletines);
        setTitle("Elija un boletín");
        
        boletines = (ListView) findViewById(android.R.id.list);
        
        BoletinesSQLite bsql = new BoletinesSQLite();
        
        lista = bsql.getBoletines(this);
        
        boletines.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista));
		
	}

		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			Intent result = new Intent();

			Boletin b = new Boletin();
			b.setNombre(lista.get(position));
			result.putExtra("boletin", b);
			setResult(Activity.RESULT_OK, result);

			// Close this activity (return to caller)
			finish();
		}
	}
