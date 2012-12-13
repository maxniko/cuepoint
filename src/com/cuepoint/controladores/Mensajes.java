package com.cuepoint.controladores;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.cuepoint.actividades.R;

/**
 * Clase encargada de gestionar la pantalla de mensajes enviados y recibidos
 */
public class Mensajes extends TabActivity
{
	//Variables globales
	TabHost tabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p07_mensajes);
		
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			public void onTabChanged(String arg0) {
				for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
			    {
					tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#10494A")); //unselected
					RelativeLayout rl = (RelativeLayout) tabHost.getTabWidget().getChildAt(i);
			        TextView textView = (TextView) rl.getChildAt(1);//          
			        textView.setTextColor(Color.parseColor("#FFFFFF"));
			        textView.setTextSize(20);
			    }
				tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#006666")); // selected
			}
		});
        
		TabHost.TabSpec spec;
		Intent intent;
		Resources res = getResources();
		
		//Nueva pestaña para los mensajes enviados
		intent = new Intent().setClass(this, MensajesEnviados.class);
		spec = tabHost.newTabSpec("Enviados").setIndicator("Enviados", res.getDrawable(R.drawable.enviados)).setContent(intent);
		tabHost.addTab(spec);
		
		//Nueva pestaña para los mensajes recibidos
		intent = new Intent().setClass(this, MensajesRecibidos.class);
		spec = tabHost.newTabSpec("Recibidos").setIndicator("Recibidos", res.getDrawable(R.drawable.recibidos)).setContent(intent);
		tabHost.addTab(spec);
		
		//Cambiar el color al texto 'Enviados' y 'Recibidos'
		RelativeLayout rl = (RelativeLayout) tabHost.getTabWidget().getChildAt(1);
        TextView textView = (TextView) rl.getChildAt(1);//          
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setTextSize(20);
	}
}
