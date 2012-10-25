/**
 * 
 */
package com.cuepoint.actividades;

import java.util.ArrayList;
import Clases.Plano;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
//import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Silvio
 * Clase que se encarga de rellenar el ListView
 *
 */
public class ItemPlanoAdapter extends BaseAdapter{
	protected Activity activity;
	protected ArrayList<Plano> items;

	/**
	 * 
	 */
	public ItemPlanoAdapter(Activity activity, ArrayList<Plano> items) {
	    this.activity = activity;
	    this.items = items;
	}
	
	public int getCount() {
	    return items.size();
	  }
	 
	  public Object getItem(int position) {
	    return items.get(position);
	  }
	 
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View vi=convertView;
	         
	    if(convertView == null) {
	      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      vi = inflater.inflate(R.layout.planos, null);
	    }
	             
	    Plano item = items.get(position);
	         
	    TextView nombre = (TextView) vi.findViewById(R.id.nombre);
	    nombre.setText(item.getNombre());
	 
	    return vi;
	  }

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
