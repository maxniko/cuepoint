package com.cuepoint.controladores;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Mensaje;

public class Mensajes extends TabActivity{
	ArrayList<Mensaje> itemsE;
	ArrayList<Mensaje> itemsR;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p07_mensajes);
		
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		Resources res = getResources();
		
		intent = new Intent().setClass(this, MensajesEnviados.class);
		spec = tabHost.newTabSpec("Enviados").setIndicator("Enviados", res.getDrawable(R.drawable.enviados)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, MensajesRecibidos.class);
		spec = tabHost.newTabSpec("Recibidos").setIndicator("Recibidos", res.getDrawable(R.drawable.recibidos)).setContent(intent);
		tabHost.addTab(spec);
	}
}
