package com.cuepoint.controladores;

import com.cuepoint.actividades.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Boletin extends Activity{
	WebView boletin;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p06_boletin);
        
        boletin = (WebView) findViewById(R.id.boletin);
        boletin.loadUrl("http://www.uap.edu.ar/es/boletiniglesia");
    }

}
