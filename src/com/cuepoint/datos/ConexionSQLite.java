package com.cuepoint.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionSQLite extends SQLiteOpenHelper{
	
	String sqlCreate = "CREATE TABLE Planos (idPlano INTEGER, nombre TEXT, descripcion TEXT, path TEXT, rank INTEGER); +" +
			"INSERT INTO Planos (idPlano, nombre, descripcion, path, rank) values " +
						"(1,'Templo UAP', 'Templo de la Universidad Adventista del Plata', 'Planos_Cue_Point/templo.jpg', 0);" +
			"CREATE TABLE Mensajes " +
				"(idMensaje INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"tipo INTEGER  NOT NULL, " +
				"nroOrigen INTEGER  NOT NULL, " +
				"texto TEXT  NULL, " +
				"fecha TEXT  NOT NULL, " +
				"coordenadaX FLOAT  NULL, " +
				"coordenadaY FLOAT  NULL, " +
				"idPlano INTEGER  NULL, " +
				"estado INTEGER  NULL " +
				");" +
			"CREATE TABLE Marcadores (x FLOAT NOT NULL, y FLOAT NOT NULL, idPlano INTEGER NOT NULL PRIMARY KEY);" + 
			"CREATE TABLE Boletines (idBoletin INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, nombre TEXT  NOT NULL);";
	
	public ConexionSQLite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Se ejecuta la sentencia SQL de creaci�n de la tabla
        db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//NOTA: Por simplicidad del ejemplo aqu� utilizamos directamente la opci�n de
        //      eliminar la tabla anterior y crearla de nuevo vac�a con el nuevo formato.
        //      Sin embargo lo normal ser� que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este m�todo deber�a ser m�s elaborado.
 
        //Se elimina la versi�n anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Planos");
 
        //Se crea la nueva versi�n de la tabla
        db.execSQL(sqlCreate);
	}
	
	
	
}
