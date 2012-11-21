package com.cuepoint.controladores;

import java.util.Date;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.EditText_SMS;
import com.cuepoint.clases.Mensaje;
import com.cuepoint.clases.Util;
import com.cuepoint.datos.MensajesSQLite;

public class EnviarSMS extends Activity {
	
	private String numero;
	private static final int REQUEST_CHOOSE_PHONE = 1;
	
	private float cx;
	private float cy;
	private int idPlano = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p05_enviar);
        
        Bundle bundle = getIntent().getExtras();
        idPlano = bundle.getInt("idPlano");
        if(idPlano > 0)
        {
	        cx = bundle.getFloat("x");
	        cy = bundle.getFloat("y");
        }
        setNombreYNumero(bundle.getString("nombre"), bundle.getString("numero"));
    }
	
	public void setNombreYNumero(String nombre, String numero)
	{
		TextView nom = (TextView) findViewById(R.id.nombre);
        this.numero = numero;
        nom.setText(nombre);
	}
	
	public void cambiarClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaContactos.class));
		startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == REQUEST_CHOOSE_PHONE) && (resultCode == Activity.RESULT_OK)) {
			try {
				setNombreYNumero(data.getStringExtra("nombre"), data.getStringExtra("numero"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enviarClick(View button)
	{
		EditText_SMS et = (EditText_SMS) findViewById(R.id.mensaje);
		String msj = et.getText().toString();
		StringBuilder sb = new StringBuilder();
		sb.append("<cuepoint");
		if(idPlano > 0)
		{
			sb.append(cx + ",");
			sb.append(cy + ",");
			sb.append(idPlano);
		}
		sb.append("/>" + msj);
    	
		Log.d("CODIGO", sb.toString());
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(numero, null, sb.toString(), null, null);
    	
    	//Guardar mensaje en SQLite
    	MensajesSQLite msql = new MensajesSQLite();
		Mensaje m = new Mensaje();
		m.setTipo(0);
		m.setTexto(sb.toString());
		m.setNroOrigen(Integer.parseInt(numero));
		Date d = new Date();
		Util u = new Util();
		m.setFecha(u.getFechaFormateada(d));
		if (idPlano > 0)
		{
			m.setX(cx);
			m.setY(cy);
			m.setIdPlano(idPlano);
		}
		msql.nuevoMensaje(this, m);
		d = null;
		u = null;
		m = null;
		msql = null;
    	
    	finish();
    	
    	Toast toast = Toast.makeText(this, "SMS Enviado", Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void cancelarClick(View v)
	{
		finish();
	}
}
