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

/**
 * Clase que se encarga de la administración de boletines, como guardar,
 * mostrar y descargar nuevos boletines.
 */
public class Boletines extends Activity
{
	//Variables globales
	WebView boletin;
	String res = "";
	String reporte = "";
	private ProgressDialog pd;
	String miNumero;
	int visible;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
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
		//Obtener el numero de teléfono de este movil
		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        miNumero = tMgr.getLine1Number();
        
        //Chequear las preferencias para saber si el usuario desea estar visible o no
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
		new TareaEnBackground().execute("");
		pd = ProgressDialog.show(this, "Por favor espere","Consultando Boletín", true, false);
	}
	
	/**
	 *  Clase que extiende de AsyncTask para realizar el acceso al web service en segundo plano
	 */
	private class TareaEnBackground extends AsyncTask<String, Void, Object>
	{
		@Override
		protected Integer doInBackground(String... args) {
			CargaDatosWS ws=new CargaDatosWS();
			//Se invoca el método para descargar el boletin y guardarlo como String en una variable
			res=ws.getBoletin();
			//Se invoca el método que reporta un usuario a la base de datos usando su número de teléfono
			reporte=ws.reportarUsuario(miNumero, visible);
			return 1;
		}

		@Override
		protected void onPostExecute(Object result) {
			//Se elimina la pantalla de por favor espere.
			pd.dismiss();
			super.onPostExecute(result);
			
			//Se obtiene la fecha del boletín que se encuentra en el archivo html dentro de un tipo de dato oculto
			int indice = res.indexOf("INPUT TYPE=\"hidden\" NAME=\"fecha\" VALUE=\"", 20);
			Boletin b = new Boletin();
			b.setNombre(res.substring(indice + 40, indice + 49));
			b.setTexto(res);
			guardarBoletinEnArchivo(b);
			
			boletin.loadDataWithBaseURL("", res, "", "utf-8", "");
		}
	}
	
	/**
	 * Guarda el boletín como archivo de texto dentro de la memoria del teléfono
	 * @param b Objeto Boletín con los datos a guardar
	 */
	private void guardarBoletinEnArchivo(Boletin b)
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
	
	/**
	 * Obtiene un boletin previamente guardado en la memoria del teléfono
	 * @param b Objeto Boletín para extraer el nombre del archivo
	 * @return Texto del boletín
	 */
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
			e.printStackTrace();
		}
		return b.getTexto();
	}
	
	/**
	 * Agrega las opciones del menú antes de ser mostrado en pantalla
	 * @param menu Objeto Menu
	 */
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
		//Abrir boletines guardados
		case 1:
			Intent intent = new Intent();
	    	intent.setComponent(new ComponentName(this, ListaBoletines.class));
			startActivityForResult(intent, 1);
			return true;
		//Descargar boletin
		case 2:
			descargarBoletin();
			return true;
		//Mostrar opciones
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
				//Obtiene el boletín enviado desde la otra activity
				Boletin b = data.getParcelableExtra("boletin");
				File files = this.getFilesDir();
				File bol = new File(files.getAbsolutePath(), b.getNombre());
				if(bol.exists()){
					String texto = leerBoletinGuardado(b);
					//Carga boletín en el navegador
					boletin.loadDataWithBaseURL("", texto, "", "utf-8", "");
	            }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}