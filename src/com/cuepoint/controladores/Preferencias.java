package com.cuepoint.controladores;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.cuepoint.actividades.R;

/**
 * Inicia la pantalla de configuración de la aplicación
 */
public class Preferencias extends PreferenceActivity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferencias);
	}
}
