package Clases;

public class Plano{
	protected String nombre = "";
	protected String rutaImagen = "";
	
	public Plano(String nombre, String rutaImagen)
	{
		this.nombre = nombre;
		this.rutaImagen = rutaImagen;
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
