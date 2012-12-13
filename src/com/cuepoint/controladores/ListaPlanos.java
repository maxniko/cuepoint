package com.cuepoint.controladores;

import java.util.ArrayList;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Plano;
import com.cuepoint.datos.ItemPlanoAdapter;
import com.cuepoint.datos.PlanosSQLite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Gestiona la lista de planos y la muestra en pantalla para que el usuario seleccione uno
 */
public class ListaPlanos extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p09_planos);
        
        ListView lv = (ListView)findViewById(R.id.listView);
        
        PlanosSQLite p = new PlanosSQLite();
        
        final ArrayList<Plano> lista = p.getPlanos(this);
        
        ItemPlanoAdapter adapter = new ItemPlanoAdapter(this, lista);
        
        lv.setAdapter(adapter);
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Plano plano = lista.get(position);
        	    Intent intent = new Intent(view.getContext(), Imagen.class);
        	    intent.putExtra("Plano", plano);
        	    intent.putExtra("InsertarMarca", false);
        	    startActivity(intent);
        	  }
        	});
        p = null;
    }
}