package com.cuepoint.controladores;

import com.cuepoint.actividades.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * Clase que muestra la pantalla inicial con el logo de la aplicación
 * @author Silvio
 *
 */
public class Splash extends Activity implements OnTouchListener{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p00_bienvenida);
        ImageView myImage = (ImageView) findViewById(R.id.imagenEntrada);
        myImage.setOnTouchListener(this);
    }

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Intent intent = new Intent();
	    	intent.setComponent(new ComponentName(this, MenuPrincipal.class));
	    	startActivity(intent);
	    	finish();
		}
		return true;
	}
}
