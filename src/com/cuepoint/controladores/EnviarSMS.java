package com.cuepoint.controladores;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cuepoint.actividades.R;

public class EnviarSMS extends Activity {
	
	private String numero;
	private static final int REQUEST_CHOOSE_PHONE = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p05_enviar);
        
        Bundle bundle = getIntent().getExtras();
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
		String text = "<cuepoint/>";
    	//String phoneNumber = "5556";
    	
    	//PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, EnviarSMS.class), 0);
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(numero, null, text, null, null);
    	
    	finish();
    	
    	Toast toast = Toast.makeText(this, "SMS Enviado", Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void cancelarClick(View v)
	{
		finish();
	}
	
	
}
