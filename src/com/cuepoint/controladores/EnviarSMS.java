package com.cuepoint.controladores;

import java.util.Date;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.EditText_SMS;
import com.cuepoint.clases.Mensaje;
import com.cuepoint.clases.Util;
import com.cuepoint.datos.ConexionSQLite;
import com.cuepoint.datos.MensajesSQLite;

public class EnviarSMS extends Activity {
	
	private String numero;
	private static final int REQUEST_CHOOSE_PHONE = 1;
	
	private float cx;
	private float cy;
	private int idPlano = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p05_enviar);
        
        Bundle bundle = getIntent().getExtras();
        idPlano = bundle.getInt("idPlano");
        if(idPlano > 0)
        {
	        cx = bundle.getFloat("x");
	        cy = bundle.getFloat("y");
        }
        setNombreYNumero(bundle.getString("nombre"), bundle.getString("numero"));
    }
	
	public void setNombreYNumero(String nombre, String numero)
	{
		TextView nom = (TextView) findViewById(R.id.nombre);
        this.numero = numero;
        nom.setText(nombre);
	}
	
	public void cambiarClick(View v)
	{
		Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaContactos.class));
		startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == REQUEST_CHOOSE_PHONE) && (resultCode == Activity.RESULT_OK)) {
			try {
				setNombreYNumero(data.getStringExtra("nombre"), data.getStringExtra("numero"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enviarClick(View button)
	{
		boolean coordenadas = false;
		EditText_SMS et = (EditText_SMS) findViewById(R.id.mensaje);
		String msj = et.getText().toString();
		StringBuilder sb = new StringBuilder();
		sb.append("<cuepoint");
		if(idPlano > 0)
		{
			sb.append("," + cx + ",");
			sb.append(cy + ",");
			sb.append(idPlano + ",");
			coordenadas = true;
		}
		sb.append("/>" + msj);
    	
		Log.d("CODIGO", sb.toString());
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(numero, null, sb.toString(), null, null);
    	
    	//Guardar mensaje en SQLite
    	MensajesSQLite msql = new MensajesSQLite();
		Mensaje m = new Mensaje();
		if(coordenadas)
		{
			m.setTipo(1);
		}
		else
		{
			m.setTipo(0);
		}
		m.setTexto(msj);
		Util u = new Util();
		m.setNumeroOrigenDestino(u.extraerNumero(numero));
		Date d = new Date();
		m.setFecha(u.getFechaFormateada(d));
		if (idPlano > 0)
		{
			m.setX(cx);
			m.setY(cy);
			m.setIdPlano(idPlano);
		}
		m.setEstado(1); //Mensaje leido
		msql.nuevoMensaje(this, m);
		
		//Guardar el SMS en la base de datos de Android para que sea accesible desde el SO
		//nuevoMensajeDBAndroid(this, m);
		
		d = null;
		u = null;
		m = null;
		msql = null;
    	
    	finish();
    	
    	Toast toast = Toast.makeText(this, "SMS Enviado", Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void nuevoMensajeDBAndroid(Context contexto, Mensaje mensaje)
	{
    	//Abrimos la base de datos 'Planos'
		ConexionSQLite pdb = new ConexionSQLite(contexto, "mmssms", null, 1);
         
        // Insertar datos en la base de datos
        SQLiteDatabase db = pdb.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        
        sb.append("INSERT INTO sms (_id, thread_id, address, person, date, protocol, read, status, type, reply_path_present, subject, body, service_center, locked, error_code, seen) values (");
        sb.append("NULL" + ",");
        sb.append("555" + ",");
        sb.append("'" + mensaje.getNumeroOrigenDestino() + "'" + ",");
        sb.append("NULL" + ",");
        sb.append(date.getTime() + ",");
        sb.append("0" + ",");
        sb.append("0" + ",");
        sb.append("-1" + ",");
        sb.append("1" + ",");
        sb.append("0" + ",");
        sb.append("NULL" + ",");
        sb.append("'" + mensaje.getTexto() + "'" + ",");
        sb.append("NULL" + ",");
        sb.append("0" + ",");
        sb.append("0" + ",");
        sb.append("0");
        sb.append(");");
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
        	Log.d("Insert", sb.toString());
            db.execSQL(sb.toString());
            //Cerramos la base de datos
            db.close();
        }
	}
	
	public void cancelarClick(View v)
	{
		finish();
	}
}
