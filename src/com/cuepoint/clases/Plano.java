package com.cuepoint.clases;

/**
 * 
 * @author Silvio
 * Clase que aloja la definicion de un plano
 *
 */
public class Plano{
	protected int idPlano = 0;
	protected String nombre = "";
	protected String descripcion = "";
	protected String rutaImagen = "";
	
	public Plano(int id, String nombre, String rutaImagen, String descripc)
	{
		this.idPlano = id;
		this.nombre = nombre;
		this.rutaImagen = rutaImagen;
		this.descripcion = descripc;
	}

	public int getIdPlano() {
		return idPlano;
	}

	public void setIdPlano(int idPlano) {
		this.idPlano = idPlano;
	}

	/**
	 * @return Nombre del plano
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param Nombre del plano
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	/**
	 * @return Descripcion del plano
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param Descripcion del plano
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return Ruta de la imagen a mostrar
	 */
	public String getRutaImagen() {
		return rutaImagen;
	}

	/**
	 * @param Ruta de la imagen a mostrar
	 */
	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}
	
	
}
