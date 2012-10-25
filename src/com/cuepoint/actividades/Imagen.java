/**
 * 
 */
package com.cuepoint.actividades;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * @author Silvio
 *
 */
public class Imagen extends Activity implements OnTouchListener, SeekBar.OnSeekBarChangeListener{
	
	Bitmap myBitmap;
    static final int ZOOM_MAX = 10;
    static final int ZOOM_MIN = 0;
    private int ZOOM_ACTUAL = 1;
    private float scaleIn = 1.0f/0.8f;
    private float scaleOut = 1.0f/1.2f;
    
    int touchInitialTime = 0;
    
    SeekBar mSeekBar;
    
	 // These matrices will be used to move and zoom image  
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
	  
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagen);
        
        mSeekBar = (SeekBar)findViewById(R.id.seekBarZoom);
        mSeekBar.setOnSeekBarChangeListener(this);
        
        leerImagenesSD();
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
			    File imgFile = new File(ruta_sd.getAbsolutePath(), b.getString("path"));
			    
			    if(imgFile.exists()){
	                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	                
	                ImageView myImage = (ImageView) findViewById(R.id.imageViewPlano);
	                myImage.setOnTouchListener(this);
	                myImage.setImageBitmap(myBitmap);
	            }
			}
		}
		catch (Exception ex)
		{
		    Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
		}
	}
	

	   
	 public boolean onTouch(View v, MotionEvent event)
	 {
		 ImageView view = (ImageView) v;
		 // Dump touch event to log
		 dumpEvent(event);
		 
		 
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
		 			dibujarMarca((int)event.getX(), (int)event.getY());
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
	 
	 private void dibujarMarca(int x, int y)
	 {
		 
	 }
	  
	 /** Show an event in the LogCat view, for debugging */  
	 private void dumpEvent(MotionEvent event) {
		 String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				 "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		 StringBuilder sb = new StringBuilder();
		 int action = event.getAction();
		 int actionCode = action & MotionEvent.ACTION_MASK;
		 sb.append("event ACTION_").append(names[actionCode]);
		 if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
			 sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			 sb.append(")");
		 }
		 sb.append("[");
		 for (int i = 0; i < event.getPointerCount(); i++)
		 {
			 sb.append("#").append(i);
			 sb.append("(pid ").append(event.getPointerId(i));
			 sb.append(")=").append((int) event.getX(i));
			 sb.append(",").append((int) event.getY(i));
			 if (i + 1 < event.getPointerCount()) sb.append(";");
		 }
		 sb.append("]");  
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
		 {/*
			 if (progress%10 == 0)
			 {
				 if (ZOOM_ACTUAL < progress/10)
				 {
					 if (ZOOM_ACTUAL < ZOOM_MAX)
					 	{
					 		ZOOM_ACTUAL++;
							savedMatrix.set(matrix);
							float scale = 1.2f;
							matrix.postScale(scale, scale);
							//matrix.postScale(scale, scale, view.getWidth(), view.getHeight());
							ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
							iv.setImageMatrix(matrix);
					 	}
				 }
				 else if (ZOOM_ACTUAL > progress/10)
				 {
					 if(ZOOM_ACTUAL > ZOOM_MIN)
						{
							ZOOM_ACTUAL--;
							savedMatrix.set(matrix);
							float scale = 0.8f;
							matrix.postScale(scale, scale);
							ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
							iv.setImageMatrix(matrix);
						}
				 }
			 }*/
		 }

		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
}
