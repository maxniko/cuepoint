/**
 * 
 */
package com.cuepoint.controladores;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
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
	 float cx;
	 float cy;
    
    
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
		        i.putExtras(bundle);
				i.setComponent(new ComponentName(this, EnviarSMS.class));
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
			    Bundle b = getIntent().getExtras();
			    //Obtenemos la ruta al archivo del plano
			    File imgFile = new File(ruta_sd.getAbsolutePath(), b.getString("path"));
			    
			    if(imgFile.exists()){
			    	imagen = (BitmapDrawable) BitmapDrawable.createFromPath(imgFile.getAbsolutePath());
	            }
			}
		}
		catch (Exception ex)
		{
		    Log.e("Ficheros", "Error al abrir el fichero de la tarjeta SD");
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
		 		mode = DRAG;
		 		touchInitialTime = (int) event.getEventTime();
		 		break;
		 		
		 	case MotionEvent.ACTION_UP:
		 		//int touchFinalTime = (int) event.getEventTime();
		 		//if (mode != NONE && (touchFinalTime - touchInitialTime > 1000))
		 		if (mode != NONE)
		 		{
		 			dibujarMarca(event.getX(), event.getY(), v);
		 		}
		 		break;
		 		
		 	case MotionEvent.ACTION_MOVE:
		 		mode = NONE;
		 		matrix.set(savedMatrix);
		 		
		 		float dx; // postTranslate X distance
		 		float dy; // postTranslate Y distance
		 		float[] matrixValues = new float[9];
		 		float matrixX = 0; // X coordinate of matrix inside the ImageView
		 		float matrixY = 0; // Y coordinate of matrix inside the ImageView
		 		float width = 0; // width of drawable
		 		float height = 0; // height of drawable
		 		
		 		matrix.set(savedMatrix);
		 		matrix.getValues(matrixValues);
		 		matrixX = matrixValues[2];
		        matrixY = matrixValues[5];
		        width = matrixValues[0] * (((ImageView) view).getDrawable().getIntrinsicWidth());
		        height = matrixValues[4] * (((ImageView) view).getDrawable().getIntrinsicHeight());

		        dx = event.getX() - start.x;
		        dy = event.getY() - start.y;

		        //if image will go outside left bound
		        if (matrixX + dx < 0){
		            dx = -matrixX;
		        }
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
		        matrix.postTranslate(dx, dy); 
		 		break;
		 }
		 
		 view.setImageMatrix(matrix);
		 return true; // indicate event was handled  
	 }
	 
	private void dibujarMarca(float x, float y, View v)
	{
		BitmapDrawable bm = leerImagenesSD();
		
		float[] values = new float[9];
		savedMatrix.set(matrix);
		matrix.getValues(values);
		float desplazamientoX = values[Matrix.MTRANS_X];
		float desplazamientoY = values[Matrix.MTRANS_Y];

		//original height and width of the bitmap
		float altoOriginal = bm.getBitmap().getHeight();
		float anchoOriginal = bm.getBitmap().getWidth();
		
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
		float originalImageOffsetX = (-desplazamientoX + x) / anchoRatio;
		float originalImageOffsetY = (-desplazamientoY + y) / altoRatio;
		
		// As described by Steve Pomeroy in a previous comment, 
		// use the canvas to combine them.
		// Start with the first in the constructor..
		Bitmap bmOverlay = Bitmap.createBitmap(bm.getBitmap().getWidth(), bm.getBitmap().getHeight(), bm.getBitmap().getConfig());
		Canvas comboImage = new Canvas(bmOverlay);
		Bitmap marca = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circulo), bm.getIntrinsicWidth()/escalaMarcador, bm.getIntrinsicWidth()/escalaMarcador, true);
		cx = originalImageOffsetX - (marca.getWidth()/2);
		cy = originalImageOffsetY - (marca.getHeight()/2);
		// Then draw the second on top of that
		comboImage.drawBitmap(bm.getBitmap(), new Matrix(), null);
		comboImage.drawBitmap(marca, cx, cy, null);
		
		plano.setImageBitmap(bmOverlay);
		//plano.invalidate();
		bmOverlay = null;
		marca = null;
		bm = null;
		System.gc();
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
