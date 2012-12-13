package com.cuepoint.clases;

import android.os.Parcel;
import android.os.Parcelable;

public class Boletin implements Parcelable{
	protected int idBoletin = 0;
	protected String nombre = "";
	protected String texto = "";
	
	public Boletin() {
	}
	
	public Boletin(Parcel parcel) {
		readToParcel(parcel);
	}

	public static final Parcelable.Creator<Boletin> CREATOR = new Parcelable.Creator<Boletin>() {
		public Boletin createFromParcel(Parcel parcel) {
			return new Boletin(parcel);
		}

		public Boletin[] newArray(int size) {
			return new Boletin[size];
		}
	};
	
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(idBoletin);
		parcel.writeString(nombre);
		parcel.writeString(texto);
	}
	
	public void readToParcel(Parcel parcel){
		idBoletin = parcel.readInt();
		nombre = parcel.readString();
		texto = parcel.readString();
	}

	public int getIdBoletin() {
		return idBoletin;
	}

	public void setIdBoletin(int idBoletin) {
		this.idBoletin = idBoletin;
	}
	/**
	 * @return Fecha del boletin
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param Fecha del boletin
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the texto
	 */
	public String getTexto() {
		return texto;
	}

	/**
	 * @param texto the texto to set
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
