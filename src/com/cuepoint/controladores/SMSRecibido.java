package com.cuepoint.controladores;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

public class SMSRecibido extends Activity {
	
	private static final int DIALOGO_ALERTA = 1;
	private static final int DIALOGO_CONFIRMACION = 2;
	private static final int DIALOGO_SELECCION = 3;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        showDialog(DIALOGO_CONFIRMACION);
    }
	
	@Override
    protected Dialog onCreateDialog(int id) 
	{
    	Dialog dialogo = null;

    	switch(id)
    	{
    		case DIALOGO_ALERTA:
    			dialogo = crearDialogoAlerta();
    			break;
    		case DIALOGO_CONFIRMACION:
    			dialogo = crearDialogoConfirmacion();
    			break;
    		case DIALOGO_SELECCION:
    			dialogo = crearDialogoSeleccion();
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    
    	return dialogo;
    }
    
    private Dialog crearDialogoAlerta()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Informacion");
    	builder.setMessage("Esto es un mensaje de alerta.");
    	builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	
    	return builder.create();
    }
    
    private Dialog crearDialogoConfirmacion()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Confirmacion");
    	builder.setMessage("¿Confirma la accion seleccionada?");
    	builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("Dialogos", "Confirmacion Aceptada.");
				Intent intent = new Intent();
		    	intent.setComponent(new ComponentName(SMSRecibido.this, ListaPlanos.class));
		    	startActivity(intent);
		    	finish();
			}
		});
    	builder.setNegativeButton("Cancelar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("Dialogos", "Confirmacion Cancelada.");
				dialog.cancel();
				finish();
			}
		});
    	
    	return builder.create();
    }
    
    private Dialog crearDialogoSeleccion()
    {
    	final String[] items = {"Español", "Inglés", "Francés"};
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Selección");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        Log.i("Dialogos", "Opción elegida: " + items[item]);
    	    }
    	});
    	
    	//Dialogo de selección simple
//    	builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
//			public void onClick(DialogInterface dialog, int item, boolean isChecked) {
//				Log.i("Dialogos", "Opción elegida: " + items[item]);
//			}
//		});
    	
    	//Diálogo de selección múltiple
//    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
//    	    public void onClick(DialogInterface dialog, int item) {
//    	        Log.i("Dialogos", "Opción elegida: " + items[item]);
//    	    }
//    	});
    	
    	return builder.create();
    }

}
