package com.cuepoint.clases;

import java.util.Date;

/*
 * Clase que guarda un mensaje de texto recibido/enviado
 */
public class Mensaje {
	private int idMensaje;
	private int tipo;
	private String texto;
	private int numeroOrigenDestino;
	private Date fecha;
	
	public Mensaje(int id, int tipo, String texto, int nroOrigen, Date fecha) 
	{
		this.idMensaje = id;
		this.tipo = tipo;
		this.texto = texto;
		this.numeroOrigenDestino = nroOrigen;
		this.fecha = fecha;
	}

	public Mensaje() {
	}

	/**
	 * @return id del mensaje
	 */
	public int getIdMensaje() {
		return idMensaje;
	}

	/**
	 * @param id del mensaje
	 */
	public void setIdMensaje(int idMensaje) {
		this.idMensaje = idMensaje;
	}

	/**
	 * @return Tipo de mensaje (0: enviado, 1:recibido)
	 */
	public int getTipo() {
		return tipo;
	}

	/**
	 * @param Tipo de mensaje (0: enviado, 1:recibido)
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return Cuerpo del mensaje
	 */
	public String getTexto() {
		return texto;
	}

	/**
	 * @param Cuerpo del mensaje
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}

	/**
	 * @return Integer con el numero enviado/recibido
	 */
	public int getNroOrigenDestino() {
		return numeroOrigenDestino;
	}

	/**
	 * @param Integer con el numero enviado/recibido
	 */
	public void setNroOrigen(int nroOrigenDestino) {
		this.numeroOrigenDestino = nroOrigenDestino;
	}

	/**
	 * @return Objeto Date con la fecha y hora del mensaje
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param Objeto Date con la fecha y hora del mensaje
	 */
	public void setFecha(Date f) {
		this.fecha = f;
	}
	
	/**
	 * @return Fecha con formato: dd/mm/aa hh:mm:ss
	 */
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
