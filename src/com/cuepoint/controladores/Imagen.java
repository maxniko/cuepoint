/**
 * 
 */
package com.cuepoint.controladores;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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
import com.cuepoint.actividades.R;

/**
 * @author Silvio
 *
 */
public class Imagen extends Activity implements OnTouchListener, SeekBar.OnSeekBarChangeListener{
	//variables para la escala
    static final int ZOOM_MAX = 20;
    static final int ZOOM_MIN = 0;
    private int ZOOM_ACTUAL = 2;
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
	 
	 private static int anchoPantalla;
	 private static int altoPantalla;
	 private static float XX;
	 private static float YY;
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p02_plano);
        
        mSeekBar = (SeekBar)findViewById(R.id.seekBarZoom);
        mSeekBar.setOnSeekBarChangeListener(this);
        
        leerImagenesSD();
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
	
	public void leerImagenesSD()
	{
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
	                Drawable d = Drawable.createFromPath(imgFile.getAbsolutePath());
	                ImageView myImage = (ImageView) findViewById(R.id.imageViewPlano);
	                myImage.setOnTouchListener(this);
	                myImage.setImageDrawable(d);
	                savedMatrix.set(matrix);
	                matrix.postTranslate(-(d.getIntrinsicWidth()/2), -(d.getIntrinsicHeight()/2));
	                matrix.setScale(0.6f, 0.6f);
	                myImage.setImageMatrix(matrix);
	            }
			}
		}
		catch (Exception ex)
		{
		    Log.e("Ficheros", "Error al abrir el fichero de la tarjeta SD");
		}
		System.gc();
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
		 		int touchFinalTime = (int) event.getEventTime();
		 		if (mode != NONE && (touchFinalTime - touchInitialTime > 1000))
		 		{
		 			XX = event.getX();
		 			YY = event.getY();
		 			dibujarMarca(XX, YY, v);
		 		}
		 		break;
		 		
		 	case MotionEvent.ACTION_MOVE:
		 		mode = NONE;
		 		matrix.set(savedMatrix);
		 		matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
		 		break;
		 }
		 
		 view.setImageMatrix(matrix);
		 return true; // indicate event was handled  
	 }
	 
	private void dibujarMarca(float x, float y, View v)
	{
		CircleView cv = new CircleView(this);
	}
	 
	private static class CircleView extends View
	{
		 private Paint mPaint = new Paint();
		 
		 public CircleView(Context context)
		 {
			 super(context);
	     }

		 protected void onSizeChanged(int w, int h, int oldw, int oldh)
		 {
			 super.onSizeChanged(w, h, oldw, oldh);
			 anchoPantalla = w;
			 altoPantalla = h;
		 }

		 @Override
		 protected void onDraw(Canvas canvas)
		 {
			 Paint paint = mPaint;
			 paint.setColor(Color.GREEN);
			 paint.setStrokeWidth(3);
			 canvas.drawCircle(XX, YY, 100, paint);
		 }

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
					Log.i("zoomActual", Integer.toString(ZOOM_ACTUAL)+","+Integer.toString(progress));
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
					Log.i("zoomActual", Integer.toString(ZOOM_ACTUAL)+","+Integer.toString(progress));
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
