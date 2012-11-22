package com.cuepoint.clases;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author Silvio
 * Clase que aloja la definicion de un plano
 *
 */
public class Plano implements Parcelable{
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
	
	public Plano() {
	}
	
	public Plano(Parcel parcel) {
		readToParcel(parcel);
	}

	public static final Parcelable.Creator<Plano> CREATOR = new Parcelable.Creator<Plano>() {
		public Plano createFromParcel(Parcel parcel) {
			return new Plano(parcel);
		}

		public Plano[] newArray(int size) {
			return new Plano[size];
		}
	};
	
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(idPlano);
		parcel.writeString(nombre);
		parcel.writeString(descripcion);
		parcel.writeString(rutaImagen);
	}
	
	public void readToParcel(Parcel parcel){
		idPlano = parcel.readInt();
		nombre = parcel.readString();
		descripcion = parcel.readString();
		rutaImagen = parcel.readString();
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
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
