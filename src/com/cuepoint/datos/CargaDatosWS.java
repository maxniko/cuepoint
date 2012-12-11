package com.cuepoint.datos;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class CargaDatosWS {
	public String getBoletin()
	{
		String texto = null;
		SoapObject rpc = new SoapObject("http://silviokucharski.com.ar/service/servicio.php","getBoletin");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.encodingStyle = SoapEnvelope.XSD;
		HttpTransportSE ht = null;
		try {
			String conexion = "http://silviokucharski.com.ar/service/servicio.php";
			ht = new HttpTransportSE(conexion);
			ht.debug = true;
			ht.call("http://silviokucharski.com.ar/service/servicio.php", envelope);
			Log.d("REQUEST", ht.requestDump);
			Log.d("RESPONSE", ht.responseDump);
			texto = envelope.getResponse().toString();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			texto = e.getMessage();
		}
		return texto;
	}
	
	public String consultaUsuario(String numero)
	{
		String texto = null;
		SoapObject rpc = new SoapObject("http://silviokucharski.com.ar/service/servicio.php","consultaUsuario");
		rpc.addProperty("numero", numero);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.encodingStyle = SoapEnvelope.XSD;
		HttpTransportSE ht = null;
		try {
			String conexion = "http://silviokucharski.com.ar/service/servicio.php";
			ht = new HttpTransportSE(conexion);
			ht.debug = true;
			ht.call("http://silviokucharski.com.ar/service/servicio.php", envelope);
			Log.d("REQUEST", ht.requestDump);
			Log.d("RESPONSE", ht.responseDump);
			texto = envelope.getResponse().toString();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			texto = e.getMessage();
		}
		return texto;
	}
	
	public String reportarUsuario(String numero, int visible)
	{
		String texto = null;
				
		SoapObject rpc = new SoapObject("http://silviokucharski.com.ar/service/servicio.php","reportar");
		rpc.addProperty("numero", numero);
		rpc.addProperty("visible", visible);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.encodingStyle = SoapEnvelope.XSD;
		HttpTransportSE ht = null;
		try {
			String conexion = "http://silviokucharski.com.ar/service/servicio.php";
			ht = new HttpTransportSE(conexion);
			ht.debug = true;
			ht.call("http://silviokucharski.com.ar/service/servicio.php", envelope);
			texto = envelope.getResponse().toString();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			texto = e.getMessage();
		}
		return texto;
	}
}
