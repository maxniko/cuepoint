package com.cuepoint.datos;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CargaDatosWS {
	public String getBoletin()
	{
		String texto = null;
		SoapObject rpc = new SoapObject("http://localhost:8080/WebServiceBoletin","Boletin");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		HttpTransportSE ht = null;
		try {
			String conexion = "http://localhost:8080/WebServiceBoletin/services/WebServiceBoletin";
			ht = new HttpTransportSE(conexion);
			ht.debug = true;
			ht.call("http://localhost:8080/WebServiceBoletin", envelope);
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