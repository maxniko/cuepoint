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
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.cuepoint.datos.CargaDatosWS;

/**
 * @author Silvio
 *
 */
public class Imagen extends Activity implements OnTouchListener, SeekBar.OnSeekBarChangeListener{
	//variables para la escala
    static final int ZOOM_MAX = 20;
    static final int ZOOM_MIN = 0;
    private int ZOOM_ACTUAL = 0;
    private float scaleIn = 1.169f;
    private float scaleOut = 0.85f;
    private float desplazZoomIn = -50f;
    private float desplazZoomOut = 50f;
    
    //variable que tendrá el tiempo de una pulsación en la pantalla
    int touchInitialTime = 0;
    
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
	 
	 private ImageView plano;
	 
	 private int escalaMarcador = 20;
	 
	 //coordenadas del marcador
	 float cx = 0;
	 float cy = 0;
	 
	 //id del plano
	 int idPlano = 0;
	 
	 float altoOriginal;
	 float anchoOriginal;
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p02_plano);
        
        mSeekBar = (SeekBar)findViewById(R.id.seekBarZoom);
        mSeekBar.setOnSeekBarChangeListener(this);
        
        plano = (ImageView) findViewById(R.id.imageViewPlano);
        
        BitmapDrawable d = leerImagenesSD();
        if(d != null)
        {
        	DisplayMetrics dm = new DisplayMetrics();
        	getWindowManager().getDefaultDisplay().getMetrics(dm);
        	float pantalla = dm.widthPixels;
        	float imagen = d.getIntrinsicWidth();
        	float escala = pantalla / imagen;
	        plano.setOnTouchListener(this);
	        plano.setImageDrawable(d);
	        savedMatrix.set(matrix);
	        matrix.setScale(escala, escala);
	        plano.setImageMatrix(matrix);
        }
        else
        {
        	Toast toast = Toast.makeText(this, "No se encontró la imagen", Toast.LENGTH_LONG);
    		toast.show();
        }
        
        if(cx != 0)
        {
        	dibujarMarca();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.enviar, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.enviar:
			Intent i = new Intent();
			i.setComponent(new ComponentName(this, ListaContactos.class));
			startActivityForResult(i, REQUEST_CHOOSE_PHONE);
			return true;
		case R.id.cancelar:
			System.gc();
			finish();
			return true;
		case R.id.opciones:
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
		        bundle.putInt("idPlano", idPlano);
		        i.putExtras(bundle);
				i.setComponent(new ComponentName(this, EnviarSMS.class));
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Plano getPlano()
	{
		Plano p = new Plano();
		Bundle b = getIntent().getExtras();
		
		p = b.getParcelable("Plano");
		
	    return p;
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
			    File imgFile = new File(ruta_sd.getAbsolutePath(), getPlano().getRutaImagen());
			    
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
	   
	public boolean onTouch(View v, MotionEvent event)
	 {
		 ImageView view = (ImageView) v;
		 
		 // Handle touch events here...
		 switch (event.getAction() & MotionEvent.ACTION_MASK)
		 {
		 	case MotionEvent.ACTION_DOWN:
		 		savedMatrix.set(matrix);
		 		start.set(event.getX(), event.getY());
		 		mode = NONE;
		 		break;
		 		
		 	case MotionEvent.ACTION_UP:
		 		//int touchFinalTime = (int) event.getEventTime();
		 		//if (mode != NONE && (touchFinalTime - touchInitialTime > 1000))
		 		if (mode == NONE)
		 		{
		 			calcularCoordenadasImagen(event.getX(), event.getY());
		 			dibujarMarca();
		 		}
		 		else if (mode == DRAG)
		 		{
		 			matrix.set(savedMatrix);
			 		
			 		float[] matrixValues = new float[9];
			 		matrix.set(savedMatrix);
			 		matrix.getValues(matrixValues);
			 		
			 		float imagenX = matrixValues[2]; // coordenada X de matrix (la imagen) relativo a ImageView
			 		float imagenY = matrixValues[5]; // coordenada Y de matrix (la imagen) relativo a ImageView
			 		// Ancho actual de la imagen
			 		float anchoImagen = matrixValues[0] * (((ImageView) view).getDrawable().getIntrinsicWidth());
			 		// Alto actual de la imagen
			 		float altoImagen = matrixValues[4] * (((ImageView) view).getDrawable().getIntrinsicHeight());
			 		
			 		// Ancho ImageView que contiene la imagen
			 		float anchoIV = view.getDrawable().getBounds().width();
			 		// Alto ImageView que contiene la imagen
			 		float altoIV = view.getDrawable().getBounds().height();
			 		
			        //if image will go outside left bound
			        if (imagenX < 0 && (imagenX + anchoImagen) < anchoIV){
			        	if(anchoImagen >= anchoIV){
			        		
			        	}
			            
			        }/*
			        //if image will go outside right bound
			        if(matrixX + dx + width > view.getWidth()){
			            dx = view.getWidth() - matrixX - width;
			        }
			        //if image will go oustside top bound
			        if (matrixY + dy < 0){
			            dy = -matrixY;
			        }
			        //if image will go outside bottom bound
			        if(matrixY + dy + height > view.getHeight()){
			            dy = view.getHeight() - matrixY - height;
			        }
			        matrix.postTranslate(dx, dy);*/
		 		}
		 		break;
		 		
		 	case MotionEvent.ACTION_MOVE:
		 		mode = DRAG;
		 		matrix.set(savedMatrix);
		 		matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
		 		break;
		 }
		 
		 view.setImageMatrix(matrix);
		 return true; // indicate event was handled  
	 }
	
	private void calcularCoordenadasImagen(float x, float y)
	{
		float[] values = new float[9];
		savedMatrix.set(matrix);
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
		BitmapDrawable bm = leerImagenesSD();
				
		// As described by Steve Pomeroy in a previous comment, 
		// use the canvas to combine them.
		// Start with the first in the constructor..
		Bitmap bmOverlay = Bitmap.createBitmap(bm.getBitmap().getWidth(), bm.getBitmap().getHeight(), bm.getBitmap().getConfig());
		Canvas comboImage = new Canvas(bmOverlay);
		Bitmap marca = getMarcador(bm.getIntrinsicWidth());
		
		// Then draw the second on top of that
		comboImage.drawBitmap(bm.getBitmap(), new Matrix(), null);
		comboImage.drawBitmap(marca, cx - (marca.getWidth()/2), cy - (marca.getHeight()/2), null);
		
		plano.setImageBitmap(bmOverlay);
		//plano.invalidate();
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
	
	private void limitDrag(Matrix m)
	{
	    float[] values = new float[9];
	    m.getValues(values);
	    float transX = values[Matrix.MTRANS_X];
	    float transY = values[Matrix.MTRANS_Y];
	    float scaleX = values[Matrix.MSCALE_X];
	    float scaleY = values[Matrix.MSCALE_Y];

	    ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
	    Rect bounds = iv.getDrawable().getBounds();
	    int anchoPantalla = getResources().getDisplayMetrics().widthPixels;
	    int altoPantalla = getResources().getDisplayMetrics().heightPixels;

	    int ancho = bounds.right - bounds.left;
	    int alto = bounds.bottom - bounds.top;

	    float minX = (-ancho + 20) * scaleX; 
	    float minY = (-alto + 20) * scaleY;

	    if(transX > (anchoPantalla - 20)) {
	        transX = anchoPantalla - 20;
	    } else if(transX < minX) {
	        transX = minX;
	    }

	    if(transY > (altoPantalla - 80)) {
	        transY = altoPantalla - 80;
	    } else if(transY < minY) {
	        transY = minY;
	    }

	    values[Matrix.MTRANS_X] = transX;
	    values[Matrix.MTRANS_Y] = transY; 
	    m.setValues(values);
	}
	  
	public void onClickZoomIn(View v)
	{
		if (ZOOM_ACTUAL < ZOOM_MAX)
		{
			ZOOM_ACTUAL++;
			savedMatrix.set(matrix);
			matrix.postScale(scaleIn, scaleIn);
			ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
			iv.setImageMatrix(matrix);
				
			actualizarSeekBar();
		}
	}
		
	public void onClickZoomOut(View v)
	{
		if(ZOOM_ACTUAL > ZOOM_MIN)
		{
			ZOOM_ACTUAL--;
			savedMatrix.set(matrix);
			matrix.postScale(scaleOut, scaleOut);
			ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
			iv.setImageMatrix(matrix);
				
			actualizarSeekBar();
		}
	}
		
	private void actualizarSeekBar()
	{
		SeekBar s = (SeekBar) findViewById(R.id.seekBarZoom);
		s.setProgress(ZOOM_ACTUAL*10);
	}
		
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
	{
		if (progress%5 == 0)
		{
			if (ZOOM_ACTUAL < progress/5)
			{
				if (ZOOM_ACTUAL < ZOOM_MAX)
					 	{
					ZOOM_ACTUAL = progress/5;
					savedMatrix.set(matrix);
					matrix.postScale(scaleIn, scaleIn);
					matrix.postTranslate(desplazZoomIn, desplazZoomIn);
					ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
					iv.setImageMatrix(matrix);
				}
			}
			else if (ZOOM_ACTUAL > progress/5)
			{
				if(ZOOM_ACTUAL > ZOOM_MIN)
				{
					ZOOM_ACTUAL = progress/5;
					savedMatrix.set(matrix);
					matrix.postScale(scaleOut, scaleOut);
					matrix.postTranslate(desplazZoomOut, desplazZoomOut);
					ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
					iv.setImageMatrix(matrix);
				}
			}
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar)
	{
			
	}

	public void onStopTrackingTouch(SeekBar arg0)
	{
		
	}
}
