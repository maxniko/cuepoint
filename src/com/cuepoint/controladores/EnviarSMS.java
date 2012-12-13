package com.cuepoint.controladores;

import java.util.Date;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.EditText_SMS;
import com.cuepoint.clases.Mensaje;
import com.cuepoint.clases.Util;
import com.cuepoint.datos.MensajesSQLite;

/**
 * Clase que gestiona el envío de un SMS con el tag correspondiente a nuestra aplicacion
 *
 */
public class EnviarSMS extends Activity
{
	//Variables globales
	private String numero;
	private static final int REQUEST_CHOOSE_PHONE = 1;
	
	private float cx;
	private float cy;
	private int idPlano = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
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
	
	@Override
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
		boolean coordenadas = false;
		EditText_SMS et = (EditText_SMS) findViewById(R.id.mensaje);
		//Obtiene el mensaje opcional escrito por el usuario
		String msj = et.getText().toString();
		//Se arma un string con los datos a enviar en el sms
		StringBuilder sb = new StringBuilder();
		sb.append("<cuepoint");
		if(idPlano > 0)
		{
			sb.append("," + cx + ",");
			sb.append(cy + ",");
			sb.append(idPlano + ",");
			coordenadas = true;
		}
		sb.append("/>" + msj);
    	
		//Envío del mensaje
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(numero, null, sb.toString(), null, null);
    	
    	//Guardar mensaje en SQLite
    	MensajesSQLite msql = new MensajesSQLite();
		Mensaje m = new Mensaje();
		if(coordenadas)
		{
			m.setTipo(1);
		}
		else
		{
			m.setTipo(0);
		}
		m.setTexto(msj);
		//Extraemos del número los simbolos que no sean dígitos
		m.setNumeroOrigenDestino(Util.extraerNumero(numero));
		Date d = new Date();
		Util u = new Util();
		m.setFecha(u.getFechaFormateada(d));
		if (idPlano > 0)
		{
			m.setX(cx);
			m.setY(cy);
			m.setIdPlano(idPlano);
		}
		m.setEstado(1); //Mensaje leido
		msql.nuevoMensaje(this, m);

		//Reseteo de variables
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
