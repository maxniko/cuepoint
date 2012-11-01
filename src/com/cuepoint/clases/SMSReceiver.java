package com.cuepoint.clases;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.cuepoint.controladores.ListaPlanos;
import com.cuepoint.controladores.SMSRecibido;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SMStutorial","SMSReceiver.onReceive");
		Bundle bundle = intent.getExtras();
		SmsMessage [] msgs = null;
		String msj = "";
		
		// obtenemos el array de estructuras pdus
		Object[] pdus = (Object[]) bundle.get("pdus");

		msgs = new SmsMessage[pdus.length];

		for (int n = 0; n < msgs.length; n++) {
			// generamos mensajes sms a partir de las estructuras pdu			
			msgs[n] = SmsMessage.createFromPdu((byte[]) pdus[n]);
			msj += "SMS de " + msgs[n].getOriginatingAddress();
			msj += ":\n";
			msj += msgs[n].getMessageBody().toString();
			msj += "\n";
		}

		// mostramos el primer mensaje
		Toast toast = Toast.makeText(context, msj, Toast.LENGTH_LONG);
		toast.show();
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setClass(context, com.cuepoint.controladores.SMSRecibido.class);
    	context.startActivity(i);
		
    	/* algunas de las propiedades que pueden obtenerse de un SMS 
		smsMessage[0].getDisplayOriginatingAddress() //dirección de origen
		smsMessage[0].getIndexOnSim() // posición en la SIM
		smsMessage[0].getStatusOnSim() //estado: leído, sin leer, envíado, sin enviar
		*/
	}

}