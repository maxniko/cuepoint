package com.cuepoint.actividades;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class Splash extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash, menu);
        return true;
    }
    
    public void onClicEnviar (View button)
    {
    	Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaPlanos.class));
    	startActivity(intent);
    }
    
    public void onClickEnviarSMS (View button)
    {
    	String text = "mi posicion";
    	String phoneNumber = "5556";
    	
    	PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Splash.class), 0);
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(phoneNumber, null, text, pi, null);
    	
    	Toast toast = Toast.makeText(this, "SMS Enviado", Toast.LENGTH_LONG);
		toast.show();
    }
}
