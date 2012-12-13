package com.cuepoint.controladores;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.cuepoint.actividades.R;

/**
 * Clase que carga el archivo de ayuda en el navegador
 */
public class Ayuda extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p08_ayuda);
        
        WebView ayuda = (WebView) findViewById(R.id.webViewAyuda);
        ayuda.loadUrl("file:///android_asset/AyudaCP.html");
    }
}
	
