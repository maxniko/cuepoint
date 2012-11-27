/**
 * 
 */
package com.cuepoint.controladores;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Plano;
import com.cuepoint.clases.Punto;

/**
 * @author Silvio
 *
 */
public class Imagen extends Activity implements OnTouchListener{
	//variables para la escala
    static final int ZOOM_MAX = 10;
    static final int ZOOM_MIN = 0;
    private int ZOOM_ACTUAL = 0;
    private float scaleIn = 1.169f;
    private float scaleOut = 0.85f;
    private float desplazZoomIn = -50f;
    private float desplazZoomOut = 50f;
    
    //barra que actua de zoom
    SeekBar mSeekBar;
    
    private static final int REQUEST_CHOOSE_PHONE = 1;
    
	 // Estas matrices serán usadas para el zoom y mover la imagen
	 Matrix matrix = new Matrix();  
	 Matrix savedMatrix = new Matrix();  
	  
	 // We can be in one of these 3 states  
	 static final int NONE = 0;  
	 static final int DRAG = 1;  
	 static final int ZOOM = 2;  
	 int mode = NONE;  
	  
	 // Remember some things for zooming  
	 PointF start = new PointF();  
	 PointF mid = new PointF();  
	 float oldDist = 1f;
	 	 
	 private int escalaMarcador = 20;
	 
	 //coordenadas del marcador
	 float cx = 0;
	 float cy = 0;
	 
	 float altoOriginal;
	 float anchoOriginal;
	 
	 //Si el mensaje ya trae las coordenadas como respuesta a una solicitud
	 //no se puede insertar marcadores nuevos en esta imagen. Es sólo para mostrar
	 //la posición de otra persona.
	 boolean imagenAccesoEscritura = true;
	 
	 Plano imagenPlano;
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p02_plano);
        
        mSeekBar = (SeekBar)findViewById(R.id.seekBarZoom);
                
        getPlanoEnIntent();
        
        cargarImagen();
        
        Bundle b = getIntent().getExtras();
        if(b.getBoolean("InsertarMarca"))
        {
        	cx = ((Punto) b.getParcelable("Punto")).getX();
        	cy = ((Punto) b.getParcelable("Punto")).getY();
        	dibujarMarca();
        	imagenAccesoEscritura = false;
        }
    }
	protected void cargarImagen()
	{
		ImageView plano = (ImageView) findViewById(R.id.imageViewPlano);
		BitmapDrawable d = leerImagenesSD();
        if(d != null)
        {
        	savedMatrix.set(matrix);
        	DisplayMetrics dm = new DisplayMetrics();
        	getWindowManager().getDefaultDisplay().getMetrics(dm);
        	float pantalla = dm.widthPixels;
        	float imagen = d.getIntrinsicWidth();
        	float escala = pantalla / imagen;
	        plano.setOnTouchListener(this);
	        plano.setImageDrawable(d);
	        matrix.postScale(escala, escala);
	        plano.setImageMatrix(matrix);
        }
        else
        {
        	Toast toast = Toast.makeText(this, "No se encontró la imagen", Toast.LENGTH_LONG);
    		toast.show();
        }
	}
	
	 @Override
	protected void onDestroy()
	{
		 super.onDestroy();
	 	 this.liberarMemoria(findViewById(R.id.LinearLayoutImagen));
	 	 System.gc();
	}
	 
	public void liberarMemoria(View view)
	{
		if (view.getBackground() != null)
		{
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup)
		{
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
			{
				liberarMemoria(((ViewGroup) view).getChildAt(i));
		    }
		    ((ViewGroup) view).removeAllViews();
		}
	}
	
	private void construirMenu(Menu menu)
	{
		if(imagenAccesoEscritura)
		{
			menu.add(Menu.NONE, 1, Menu.NONE, "Enviar").setIcon(R.drawable.enviar);
		}
		else
		{
			menu.add(Menu.NONE, 4, Menu.NONE, "Responder").setIcon(R.drawable.enviar);
		}
		menu.add(Menu.NONE, 2, Menu.NONE, "Cancelar").setIcon(R.drawable.cancelar);
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
		case 4:
			cx = 0;
			cy = 0;
			imagenAccesoEscritura = true;
			cargarImagen();
			
			return true;
		case 1:
			Intent i = new Intent();
			i.setComponent(new ComponentName(this, ListaContactos.class));
			startActivityForResult(i, REQUEST_CHOOSE_PHONE);
			return true;
		case 2:
			System.gc();
			finish();
			return true;
		case 3:
			Intent in = new Intent();
			in.setComponent(new ComponentName(this, Preferencias.class));
			startActivity(in);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == REQUEST_CHOOSE_PHONE) && (resultCode == Activity.RESULT_OK)) {
			try {
				Intent i = new Intent();
				
				Bundle bundle = new Bundle();
		        bundle.putString("nombre", data.getStringExtra("nombre"));
		        bundle.putString("numero", data.getStringExtra("numero"));
		        bundle.putFloat("x", cx);
		        bundle.putFloat("y", cy);
		        bundle.putInt("idPlano", imagenPlano.getIdPlano());
		        i.putExtras(bundle);
				i.setComponent(new ComponentName(this, EnviarSMS.class));
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getPlanoEnIntent()
	{
		Bundle b = getIntent().getExtras();
		imagenPlano = b.getParcelable("Plano");
	}
	
	public BitmapDrawable leerImagenesSD()
	{
		BitmapDrawable imagen = null;
		boolean sdDisponible = false;
		boolean sdAccesoEscritura = false;
		
		//Comprobamos el estado de la memoria externa (tarjeta SD)
		String estado = Environment.getExternalStorageState();
		
		if (estado.equals(Environment.MEDIA_MOUNTED))
		{
		    sdDisponible = true;
		    sdAccesoEscritura = true;
		}
		else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
		{
		    sdDisponible = true;
		    sdAccesoEscritura = false;
		}
		else
		{
		    sdDisponible = false;
		    sdAccesoEscritura = false;
		}
		
		try
		{
			if (sdDisponible & sdAccesoEscritura)
			{
				//Obtenemos la ruta a la tarjeta SD
			    File ruta_sd = Environment.getExternalStorageDirectory();
			    
			    //Obtenemos la ruta al archivo del plano
			    File imgFile = new File(ruta_sd.getAbsolutePath(), imagenPlano.getRutaImagen());
			    
			    if(imgFile.exists()){
			    	imagen = (BitmapDrawable) BitmapDrawable.createFromPath(imgFile.getAbsolutePath());
			    	altoOriginal = imagen.getBitmap().getHeight();
			    	anchoOriginal = imagen.getBitmap().getWidth();
	            }
			}
		}
		catch (Exception ex)
		{
		    Log.e("Ficheros", "Error al abrir el fichero de la tarjeta SD");
		    Log.e("Ficheros", ex.getMessage());
		}
		System.gc();
		return imagen;
	}
	
	public void centrarImagenEnPantalla(ImageView plano)
	{
		//Extraer los parámetros de la matriz referentes a la imagen.
 		float[] matrixValues = new float[9];
 		matrix.getValues(matrixValues);
 		
 		// coordenada X de matrix (la imagen) relativo a ImageView
 		float imagenX = matrixValues[2];
 		// coordenada Y de matrix (la imagen) relativo a ImageView
 		float imagenY = matrixValues[5];
 		// Ancho actual de la imagen
 		float anchoImagen = matrixValues[0] * plano.getDrawable().getBounds().width();
 		// Alto actual de la imagen
 		float altoImagen = matrixValues[4] * plano.getDrawable().getBounds().height();
 		
 		// Ancho ImageView que contiene la imagen
 		float anchoIV = plano.getWidth();
 		// Alto ImageView que contiene la imagen
 		float altoIV = plano.getHeight();
 		
 		float desplazarX = 0;
 		float desplazarY = 0;
 		
        //Si la imagen se sale por el borde izquierdo pero no por el derecho
        if (imagenX < 0 && (imagenX + anchoImagen) < anchoIV){
        	//La imagen es más grande que la pantalla
        	if(anchoImagen >= anchoIV){
        		//Distancia del borde derecho de la imagen con el borde derecho del ImageView
        		desplazarX = anchoIV - (imagenX + anchoImagen);
        	}
        	else{
        		//Distancia del borde izquierdo de la imagen con el borde izquierdo del ImageView
        		desplazarX = (-1) * imagenX;
        	}
        }
        
        //Si la imagen se sale por el borde derecho pero no por el izquierdo
        if((imagenX + anchoImagen) > anchoIV && imagenX > 0){
        	//La imagen es más grande que la pantalla
        	if(anchoImagen >= anchoIV){
        		//Distancia del borde derecho de la imagen con el borde derecho del ImageView
        		desplazarX = (-1) * imagenX;
        	}
        	else{
        		//Distancia del borde izquierdo de la imagen con el borde izquierdo del ImageView
        		desplazarX = anchoIV - (imagenX + anchoImagen);
        	}
        }
        
        //Si la imagen se sale por el borde superior
        if (imagenY < 0 && (imagenY + altoImagen) < altoIV){
        	if(altoImagen >= altoIV){
        		//Distancia del borde inferior de la imagen con el borde inferior del ImageView
        		desplazarY = altoIV - (imagenY + altoImagen);
        	}
        	else{
        		//Distancia del borde superior de la imagen con el borde superior del ImageView
        		desplazarY = (-1) * imagenY;
        	}
        }
        
        //Si la imagen se sale por el borde inferior
        if((imagenY + altoImagen) > altoIV && imagenY > 0){
        	if(altoImagen >= altoIV){
        		//Distancia del borde inferior de la imagen con el borde inferior del ImageView
        		desplazarY = (-1) * imagenY;
        	}
        	else{
        		//Distancia del borde superior de la imagen con el borde superior del ImageView
        		desplazarY = altoIV - (imagenY + altoImagen);
        	}
        }
        
        matrix.postTranslate(desplazarX, desplazarY);
	}
	   
	public boolean onTouch(View v, MotionEvent event)
	 {
		ImageView plano = (ImageView) findViewById(R.id.imageViewPlano);
		 // Handle touch events here...
		 switch (event.getAction() & MotionEvent.ACTION_MASK)
		 {
		 	case MotionEvent.ACTION_DOWN:
		 		savedMatrix.set(matrix);
		 		start.set(event.getX(), event.getY());
		 		mode = NONE;
		 		break;
		 		
		 	case MotionEvent.ACTION_UP:
		 		if (mode == NONE && imagenAccesoEscritura)
		 		{
		 			calcularCoordenadasImagen(event.getX(), event.getY());
		 			dibujarMarca();
		 		}
		 		else if (mode == DRAG)
		 		{
		 			centrarImagenEnPantalla(plano);
		 		}
		 		else if (!imagenAccesoEscritura)
		 		{
		 			Toast toast = Toast.makeText(this, "No se puede modificar la imagen", Toast.LENGTH_SHORT);
		    		toast.show();
		 		}
		 		break;
		 		
		 	case MotionEvent.ACTION_MOVE:
		 		mode = DRAG;
		 		matrix.set(savedMatrix);
		 		matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
		 		break;
		 }
		 
		 plano.setImageMatrix(matrix);
		 return true; // indicate event was handled  
	 }
	
	private void calcularCoordenadasImagen(float x, float y)
	{
		ImageView plano = (ImageView) findViewById(R.id.imageViewPlano);
		float[] values = new float[9];
		matrix.set(savedMatrix);
		matrix.getValues(values);
		float desplazamientoX = values[Matrix.MTRANS_X];
		float desplazamientoY = values[Matrix.MTRANS_Y];
		
		//height and width of the visible (scaled) image
		float altoEscalado = (plano.getDrawable().getIntrinsicHeight()) * values[Matrix.MSCALE_X];
		float anchoEscalado = (plano.getDrawable().getIntrinsicWidth()) * values[Matrix.MSCALE_Y];

		//Find the ratio of the original image to the scaled image
		//Should normally be equal unless a disproportionate scaling
		//(e.g. fitXY) is used.
		float altoRatio = 1 / (altoOriginal / altoEscalado);
		float anchoRatio = 1 / (anchoOriginal / anchoEscalado);

		//scale these distances according to the ratio of your scaling
		//For example, if the original image is 1.5x the size of the scaled
		//image, and your offset is (10, 20), your original image offset
		//values should be (15, 30). 
		cx = (-desplazamientoX + x) / anchoRatio;
		cy = (-desplazamientoY + y) / altoRatio;
	}
	
	private void dibujarMarca()
	{
		ImageView plano = (ImageView) findViewById(R.id.imageViewPlano);
		BitmapDrawable bm = leerImagenesSD();
		matrix.set(savedMatrix);
		// As described by Steve Pomeroy in a previous comment, 
		// use the canvas to combine them.
		// Start with the first in the constructor..
		Bitmap bmOverlay = Bitmap.createBitmap(bm.getBitmap().getWidth(), bm.getBitmap().getHeight(), bm.getBitmap().getConfig());
		Canvas comboImage = new Canvas(bmOverlay);
		Bitmap marca = getMarcador(bm.getIntrinsicWidth());
		
		// Dibujar el plano sobre la imagen
		comboImage.drawBitmap(bm.getBitmap(), new Matrix(), null);
		// Dibujar la marca sobre el plano
		comboImage.drawBitmap(marca, cx - (marca.getWidth()/2), cy - (marca.getHeight()/2), null);
		
		plano.setImageBitmap(bmOverlay);
		comboImage = null;
		bmOverlay = null;
		marca = null;
		bm = null;
		System.gc();
	}

	private Bitmap getMarcador(int anchoPlano)
	{
		Bitmap m = null;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int tipo = Integer.parseInt(pref. getString("marcador", "1"));
		int color = Integer.parseInt(pref.getString("color", "1"));
		int recurso = 0;
		switch(tipo)
		{
		case 1:
			switch (color)
			{
			case 1:
				recurso = R.drawable.circulo_azul;
				break;
				
			case 2:
				recurso = R.drawable.circulo_rojo;
				break;
				
			case 3:
				recurso = R.drawable.circulo_verde;
				break;
			}
			break;
			
		case 2:
			switch (color)
			{
			case 1:
				recurso = R.drawable.cruz_azul;
				break;
				
			case 2:
				recurso = R.drawable.cruz_roja;
				break;
				
			case 3:
				recurso = R.drawable.cruz_verde;
				break;
			}
		}
		m = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), recurso), anchoPlano/escalaMarcador, anchoPlano/escalaMarcador, true);
		return m;
	}
	
	public void onClickZoomIn(View v)
	{
		if (ZOOM_ACTUAL < ZOOM_MAX)
		{
			ZOOM_ACTUAL++;
			savedMatrix.set(matrix);
			matrix.postScale(scaleIn, scaleIn);
			matrix.postTranslate(desplazZoomIn, desplazZoomIn);
			ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
			iv.setImageMatrix(matrix);
			
			actualizarSeekBar();
			centrarImagenEnPantalla(iv);
		}
	}
		
	public void onClickZoomOut(View v)
	{
		if(ZOOM_ACTUAL > ZOOM_MIN)
		{
			ZOOM_ACTUAL--;
			savedMatrix.set(matrix);
			matrix.postScale(scaleOut, scaleOut);
			matrix.postTranslate(desplazZoomOut, desplazZoomOut);
			ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
			iv.setImageMatrix(matrix);
			
			actualizarSeekBar();
			centrarImagenEnPantalla(iv);
		}
	}
		
	private void actualizarSeekBar()
	{
		SeekBar s = (SeekBar) findViewById(R.id.seekBarZoom);
		s.setProgress(ZOOM_ACTUAL*10);
	}
}
