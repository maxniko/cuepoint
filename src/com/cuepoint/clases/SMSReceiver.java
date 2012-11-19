package com.cuepoint.clases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Clase que se encarga de escuchar la llegada de un SMS
 * @author Silvio
 *
 */
public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage [] msgs = null;
		String msj = "";
		String nroOrigen = "";
		// codigo que deberia tener el mensaje para que se active la aplicacion
		String codigo = "<cuepoint/>";
		
		// obtenemos el array de estructuras pdus
		Object[] pdus = (Object[]) bundle.get("pdus");

		msgs = new SmsMessage[pdus.length];
		
		for (int n = 0; n < msgs.length; n++) {
			// generamos mensajes sms a partir de las estructuras pdu			
			msgs[n] = SmsMessage.createFromPdu((byte[]) pdus[n]);
			// guardamos el numero de origen
			nroOrigen = msgs[n].getOriginatingAddress();
			// guardamos el mensaje
			msj = msgs[n].getMessageBody().toString();
		}
		if (msj.length() >= 11)
		{
			// comparamos si el mensaje tiene el codigo de nuestro programa
			// si es asi se llama a la clase SMSRecibido
			if (msj.substring(0, 11).equalsIgnoreCase(codigo)) 
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtra("NumeroOrigen", nroOrigen);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setClass(context, com.cuepoint.controladores.SMSRecibido.class);
				context.startActivity(i);
				/*
				Bundle b = new Bundle();
		        b.putString("numero", nroOrigen);
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtras(bundle);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setClass(context, com.cuepoint.controladores.SMSRecibido.class);
		    	context.startActivity(i);*/
			}
		}
	}
}