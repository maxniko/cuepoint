package com.cuepoint.clases;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

	public String getFechaFormateada(Date fecha)
	{
		Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
		StringBuilder f = new StringBuilder();
		f.append(agregarCero(c.get(Calendar.DAY_OF_MONTH)) + "-");
		f.append(agregarCero(c.get(Calendar.MONTH)) + "-");
		f.append(c.get(Calendar.YEAR) + ", ");
		f.append(agregarCero(c.get(Calendar.HOUR_OF_DAY)) + ":");
		f.append(agregarCero(c.get(Calendar.MINUTE)) + ":");
		f.append(agregarCero(c.get(Calendar.SECOND)));
		return f.toString();
	}
	
	protected String agregarCero(int numero)
	{
		String resultado = Integer.toString(numero);
		if (numero < 10)
		{
			resultado = "0" + Integer.toString(numero);
		}
		return resultado;
	}
}
