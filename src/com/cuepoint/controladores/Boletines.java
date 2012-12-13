package com.cuepoint.controladores;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Boletin;
import com.cuepoint.datos.BoletinesSQLite;
import com.cuepoint.datos.CargaDatosWS;

public class Boletines extends Activity{
	WebView boletin;
	String res = "";
	String reporte = "";
	private ProgressDialog pd;
	String miNumero;
	int visible;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p06_boletin);

        boletin = (WebView) findViewById(R.id.boletin);
        
        BoletinesSQLite bsql = new BoletinesSQLite();
        ArrayList<String> lista = bsql.getBoletines(this);
        if(lista.size() > 0)
        {
        	Boletin b = new Boletin();
        	b.setNombre(lista.get(lista.size() - 1));
			File files = this.getFilesDir();
			File bol = new File(files.getAbsolutePath(), b.getNombre());
			if(bol.exists()){
				String texto = leerBoletinGuardado(b);
				boletin.loadDataWithBaseURL("", texto, "", "utf-8", "");
            }
        }
        else
        {
	        descargarBoletin();
        }
    }
	
	private void descargarBoletin()
	{
		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        miNumero = tMgr.getLine1Number();
        
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean v = (pref.getBoolean("visible", true));
		if(v)
		{
			visible = 0;
		}
		else
		{
			visible = 1;
		}
        
        // Usamos un AsyncTask, para poder mostrar una ventana de por favor espere, mientras se consulta el servicio web
		new DownloadTask2().execute("");
		pd = ProgressDialog.show(this, "Por favor espere","Consultando Boletín", true, false);
	}
	
	//Tarea en Background
	private class DownloadTask2 extends AsyncTask<String, Void, Object> {
		@Override
		protected Integer doInBackground(String... args) {
			CargaDatosWS ws=new CargaDatosWS();
			//Se invoca nuestro metodo
			res=ws.getBoletin();
			reporte=ws.reportarUsuario(miNumero, visible);
			return 1;
		}

		@Override
		protected void onPostExecute(Object result) {
			//Se elimina la pantalla de por favor espere.
			pd.dismiss();
			//Se muestra mensaje con la respuesta del servicio web
			super.onPostExecute(result);
			
			int indice = res.indexOf("INPUT TYPE=\"hidden\" NAME=\"fecha\" VALUE=\"", 20);
			Boletin b = new Boletin();
			b.setNombre(res.substring(indice + 40, indice + 48));
			b.setTexto(res);
			guardarBoletin(b);
			
			boletin.loadDataWithBaseURL("", res, "", "utf-8", "");
		}
	}
	
	private void guardarBoletin(Boletin b)
	{
		BoletinesSQLite bsql = new BoletinesSQLite();
		if(bsql.getBoletinPorFecha(this, b.getNombre()) == null)
		{
			if(bsql.nuevoBoletin(this, b))
			{
				File files = this.getFilesDir();
				File bol = new File(files.getAbsolutePath(), b.getNombre());
				try {
					FileWriter datawriter = new FileWriter(bol);
					BufferedWriter out = new BufferedWriter(datawriter);
					out.write(b.getTexto());
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String leerBoletinGuardado(Boletin b)
	{
		try
		{
			File files = this.getFilesDir();
			File bol = new File(files.getAbsolutePath(), b.getNombre());
			FileInputStream file = new FileInputStream(bol.getAbsolutePath());
			@SuppressWarnings("resource")
			BufferedReader buffer = new BufferedReader(new InputStreamReader(file));
			String readString = new String();
			while((readString = buffer.readLine())!= null)
			{
				b.setTexto(b.getTexto() + "\r\n" + readString);
			}
		}
		catch (Exception e)
		{
			
		}
		return b.getTexto();
	}
	
	private void construirMenu(Menu menu)
	{
		menu.add(Menu.NONE, 1, Menu.NONE, "Boletines guardados").setIcon(R.drawable.abrir);
		menu.add(Menu.NONE, 2, Menu.NONE, "Descargar boletin").setIcon(R.drawable.download);
		menu.add(Menu.NONE, 3, Menu.NONE, "Opciones").setIcon(R.drawable.opciones);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		construirMenu(menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		construirMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		//Abrir boletin guardado
		case 1:
			Intent intent = new Intent();
	    	intent.setComponent(new ComponentName(this, ListaBoletines.class));
			startActivityForResult(intent, 1);
			return true;
		//DEscargar boletin
		case 2:
			descargarBoletin();
			return true;
		//Opciones
		case 3:
			Intent in = new Intent();
			in.setComponent(new ComponentName(this, Preferencias.class));
			startActivity(in);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == 1) && (resultCode == Activity.RESULT_OK)) {
			try {
				Boletin b = data.getParcelableExtra("boletin");
				File files = this.getFilesDir();
				File bol = new File(files.getAbsolutePath(), b.getNombre());
				if(bol.exists()){
					String texto = leerBoletinGuardado(b);
					boletin.loadDataWithBaseURL("", texto, "", "utf-8", "");
	            }
			} catch (Exception e) {
			e.printStackTrace();
			}
		}
	}
}