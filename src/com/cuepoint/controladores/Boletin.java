package com.cuepoint.controladores;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.webkit.WebView;
import com.cuepoint.actividades.R;
import com.cuepoint.datos.CargaDatosWS;

public class Boletin extends Activity{
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
        
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        miNumero = tMgr.getLine1Number();
        
        boletin = (WebView) findViewById(R.id.boletin);
        
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
			boletin.loadDataWithBaseURL("", res, "", "utf-8", "");
		}
	}
}
