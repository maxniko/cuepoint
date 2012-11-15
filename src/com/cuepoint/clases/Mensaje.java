package com.cuepoint.clases;

import java.util.Date;

public class Mensaje {
	private int idMensaje;
	private int tipo;
	private String texto;
	private int nroOrigen;
	private Date fecha;
	
	public Mensaje(int id, int tipo, String texto, int nroOrigen, Date fecha) 
	{
		this.idMensaje = id;
		this.tipo = tipo;
		this.texto = texto;
		this.nroOrigen = nroOrigen;
		this.fecha = fecha;
	}

	public Mensaje() {
	}

	public int getIdMensaje() {
		return idMensaje;
	}

	public void setIdMensaje(int idMensaje) {
		this.idMensaje = idMensaje;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public int getNroOrigen() {
		return nroOrigen;
	}

	public void setNroOrigen(int nroOrigen) {
		this.nroOrigen = nroOrigen;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date f) {
		this.fecha = f;
	}
	
	public String getFechaFormateada()
	{
		StringBuilder f = new StringBuilder();
		f.append(fecha.getDate() + "/");
		f.append(fecha.getMonth() + "/");
		f.append(fecha.getYear() + ", ");
		f.append(fecha.getHours() + ":");
		f.append(fecha.getMinutes() + ":");
		f.append(fecha.getSeconds());
		return f.toString();
	}
}
