package com.cuepoint.clases;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.cuepoint.controladores.ListaPlanos;
import com.cuepoint.controladores.SMSRecibido;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage [] msgs = null;
		String msj = "";
		String nroOrigen = "";
		String codigo = "<cuepoint/>";
		
		// obtenemos el array de estructuras pdus
		Object[] pdus = (Object[]) bundle.get("pdus");

		msgs = new SmsMessage[pdus.length];
		
		for (int n = 0; n < msgs.length; n++) {
			// generamos mensajes sms a partir de las estructuras pdu			
			msgs[n] = SmsMessage.createFromPdu((byte[]) pdus[n]);
			nroOrigen = msgs[n].getOriginatingAddress();
			msj = msgs[n].getMessageBody().toString();
		}
		if (msj.length() >= 11)
		{
			if (msj.substring(0, 11).equalsIgnoreCase(codigo)) 
			{				
				Bundle b = new Bundle();
		        b.putString("numero", nroOrigen);
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.putExtras(bundle);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setClass(context, com.cuepoint.controladores.SMSRecibido.class);
		    	context.startActivity(i);
			}
		}
		
    	/* algunas de las propiedades que pueden obtenerse de un SMS 
		smsMessage[0].getDisplayOriginatingAddress() //dirección de origen
		smsMessage[0].getIndexOnSim() // posición en la SIM
		smsMessage[0].getStatusOnSim() //estado: leído, sin leer, envíado, sin enviar
		*/
	}

}