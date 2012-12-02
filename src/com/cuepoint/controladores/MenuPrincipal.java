package com.cuepoint.controladores;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.cuepoint.actividades.R;
import com.cuepoint.clases.Plano;
import com.cuepoint.datos.PlanosSQLite;

public class MenuPrincipal extends Activity {
	
	private static final int REQUEST_CHOOSE_PHONE = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p01_principal);
    }
	
	public void enviarSolicitudClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaContactos.class));
		startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == REQUEST_CHOOSE_PHONE) && (resultCode == Activity.RESULT_OK)) {
			try {
				Intent i = new Intent();
				
				Bundle bundle = new Bundle();
		        bundle.putString("nombre", data.getStringExtra("nombre"));
		        bundle.putString("numero", data.getStringExtra("numero"));
		        bundle.putInt("idPlano", 0);
		        i.putExtras(bundle);
				i.setComponent(new ComponentName(this, EnviarSMS.class));
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enviarPosicionClick(View v)
	{
		PlanosSQLite p = new PlanosSQLite();
        
        final ArrayList<Plano> lista = p.getPlanos(this);
        
        if(lista.size() == 1)
        {
        	Plano plano = lista.get(0);
    	    Intent intent = new Intent(this, Imagen.class);
    	    intent.putExtra("Plano", plano);
    	    intent.putExtra("InsertarMarca", false);
    	    intent.putExtra("Respuesta", false);
    	    startActivity(intent);
        }
        else
        {
        	Intent intent = new Intent();
        	intent.setComponent(new ComponentName(this, ListaPlanos.class));
        	startActivity(intent);
        }
	}
	
	public void opcionesClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Preferencias.class));
    	startActivity(intent);
	}
	
	public void verBoletinClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Boletin.class));
    	startActivity(intent);
	}
	
	public void mensajesClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Mensajes.class));
    	startActivity(intent);
	}
	
	public void ayudaClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, Ayuda.class));
    	startActivity(intent);
	}

}
