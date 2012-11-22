package com.cuepoint.clases;

/*
 * Clase que guarda un mensaje de texto recibido/enviado
 */
public class Mensaje {
	private int idMensaje;
	private int tipo;
	private String textoOpcional;
	private int numeroOrigenDestino;
	private String fecha;
	private float x;
	private float y;
	private int idPlano = 0;
	
	public Mensaje(int id, int tipo, String texto, int nroOrigen, String fecha) 
	{
		this.idMensaje = id;
		this.tipo = tipo;
		this.textoOpcional = texto;
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
	 * @return Tipo de mensaje (0: enviado solicitud, 1:enviado respuesta, 2: recibido solicitud, 3: recibido respuesta)
	 */
	public int getTipo() {
		return tipo;
	}

	/**
	 * @param Tipo de mensaje (0: enviado solicitud, 1:enviado respuesta, 2: recibido solicitud, 3: recibido respuesta)
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return Cuerpo del mensaje
	 */
	public String getTexto() {
		return textoOpcional;
	}

	/**
	 * @param Cuerpo del mensaje
	 */
	public void setTexto(String texto) {
		this.textoOpcional = texto;
	}

	/**
	 * @return Objeto Date con la fecha y hora del mensaje
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * @param Objeto Date con la fecha y hora del mensaje
	 */
	public void setFecha(String f) {
		this.fecha = f;
	}

	public int getNumeroOrigenDestino() {
		return numeroOrigenDestino;
	}

	public void setNumeroOrigenDestino(int numeroOrigenDestino) {
		this.numeroOrigenDestino = numeroOrigenDestino;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getIdPlano() {
		return idPlano;
	}

	public void setIdPlano(int idPlano) {
		this.idPlano = idPlano;
	}
	
	/**
	 * @return Fecha con formato: dd/mm/aa hh:mm:ss
	 *//*
	public String getFechaFormateada()
	{
		Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
		StringBuilder f = new StringBuilder();
		f.append(c.get(Calendar.DAY_OF_MONTH) + "/");
		f.append(c.get(Calendar.MONTH) + "/");
		f.append(c.get(Calendar.YEAR) + ", ");
		f.append(c.get(Calendar.HOUR_OF_DAY) + ":");
		f.append(c.get(Calendar.MINUTE) + ":");
		f.append(c.get(Calendar.SECOND));
		Log.i("Fecha", Long.toString(fecha.getTime()));
		return f.toString();
	}*/
}
