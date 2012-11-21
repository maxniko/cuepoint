package com.cuepoint.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PlanosSQLite extends SQLiteOpenHelper{

	String sqlCreate = "CREATE TABLE Planos (idPlano INTEGER, nombre TEXT, path TEXT, rank INTEGER); +" +
			"INSERT INTO Planos (idPlano, nombre, path, rank) values (1,'Templo UAP', 'templo.png', 0);";
	
	 
    public PlanosSQLite(Context contexto, String nombre,
                               CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creaci�n de la tabla
        db.execSQL(sqlCreate);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
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