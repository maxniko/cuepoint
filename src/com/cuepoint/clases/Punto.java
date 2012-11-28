package com.cuepoint.clases;

import android.os.Parcel;
import android.os.Parcelable;

public class Punto implements Parcelable {
	float x;
	float y;
	
	public Punto(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Punto()
	{
		
	}
	
	public Punto(Parcel parcel) {
		readToParcel(parcel);
	}

	public static final Parcelable.Creator<Punto> CREATOR = new Parcelable.Creator<Punto>() {
		public Punto createFromParcel(Parcel parcel) {
			return new Punto(parcel);
		}

		public Punto[] newArray(int size) {
			return new Punto[size];
		}
	};
	
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeFloat(x);
		parcel.writeFloat(y);
	}
	
	public void readToParcel(Parcel parcel){
		x = parcel.readFloat();
		y = parcel.readFloat();
	}

	/**
	 * @return Coordenada x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param Coordenada x
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return Coordenada y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param Coordenada y
	 */
	public void setY(float y) {
		this.y = y;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
