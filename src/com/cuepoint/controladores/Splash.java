package com.cuepoint.controladores;

import com.cuepoint.actividades.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class Splash extends Activity implements OnTouchListener{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p00_bienvenida);
        ImageView myImage = (ImageView) findViewById(R.id.imagenEntrada);
        myImage.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
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

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Intent intent = new Intent();
	    	intent.setComponent(new ComponentName(this, MenuPrincipal.class));
	    	startActivity(intent);
		}
		return true;
	}
}
