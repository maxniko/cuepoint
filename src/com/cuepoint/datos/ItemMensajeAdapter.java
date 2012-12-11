package com.cuepoint.datos;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Mensaje;

public class ItemMensajeAdapter extends BaseAdapter{
	protected Activity activity;
	protected ArrayList<Mensaje> items;

	/**
	 * 
	 */
	public ItemMensajeAdapter(Activity activity, ArrayList<Mensaje> items)
	{
		this.activity = activity;
		this.items = items;
	}
	
	public int getCount()
	{
		return items.size();
	}
	 
	public Object getItem(int position)
	{
		return items.get(position);
	}
	 
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View vi=convertView;
	         
	    if(convertView == null)
	    {
	    	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	vi = inflater.inflate(R.layout.mensajes, null);
	    }
	             
	    Mensaje item = items.get(position);
	         
	    TextView nombre = (TextView) vi.findViewById(R.id.nombre);
	    if(!item.getNombre().equals(""))
	    {
	    	nombre.setText(item.getNombre());
	    }
	    else
	    {
	    	nombre.setText(item.getNumeroOrigenDestino());
	    }
	    TextView descr = (TextView) vi.findViewById(R.id.descripcion);
	    descr.setText(item.getFecha());
	    
	  //Tipo de mensaje (0: enviado solicitud, 1: enviado respuesta, 2: recibido solicitud, 3: recibido respuesta)
	    ImageView img = (ImageView) vi.findViewById(R.id.imagenListView);
	    if(item.getTipo() == 1 || item.getTipo() == 3)
	    {
	    	img.setImageResource(R.drawable.coordenadas);
	    }
	    else
	    {
	    	img.setImageBitmap(null);
	    }
	    ImageView sobre = (ImageView) vi.findViewById(R.id.estadoSMS);
	    //Si el mensaje no esta leido, asigno el icono del sobre cerrado
	    if(item.getEstado() == 0)
	    {
	    	sobre.setImageResource(R.drawable.no_leido);
	    }
	    else
	    {
	    	sobre.setImageResource(R.drawable.leido);
	    }
	    return vi;
	  }

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}