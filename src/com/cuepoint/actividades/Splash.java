package com.cuepoint.actividades;

import com.cuepoint.actividades.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Splash extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash, menu);
        return true;
    }
    
    public void onClicEnviar (View button)
    {
    	Intent intent = new Intent();
    	intent.setComponent(new ComponentName(this, ListaPlanos.class));
    	startActivity(intent);
    }
}
