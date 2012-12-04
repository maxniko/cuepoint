package com.cuepoint.controladores;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.cuepoint.actividades.R;
import com.cuepoint.datos.CargaDatosWS;

public class Boletin extends Activity{
	//WebView boletin;
	TextView txtboletin;
	String res = "";
	private ProgressDialog pd;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p06_boletin);
        
        txtboletin = (TextView) findViewById(R.id.textView1);
        //boletin = (WebView) findViewById(R.id.boletin);
        //boletin.loadUrl("http://www.uap.edu.ar/es/boletiniglesia");
        
        // Usamos un AsyncTask, para poder mostrar una ventana de por favor espere, mientras se consulta el servicio web
		new DownloadTask2().execute("");
		pd = ProgressDialog.show(this, "Por favor espere","Consultando Boletín", true, false);

    }
	
	//Tarea en Background
	private class DownloadTask2 extends AsyncTask<String, Void, Object> {
		protected Integer doInBackground(String... args) {
			CargaDatosWS ws=new CargaDatosWS();
			//Se invoca nuestro metodo
			res=ws.getBoletin();
			return 1;
		}

		protected void onPostExecute(Object result) {
			//Se elimina la pantalla de por favor espere.
			pd.dismiss();
			//Se muestra mensaje con la respuesta del servicio web
			txtboletin.setText(res);
			super.onPostExecute(result);
		}
	}
}
