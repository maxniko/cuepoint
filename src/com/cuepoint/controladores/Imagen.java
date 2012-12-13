/**
 * 
 */
package com.cuepoint.controladores;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cuepoint.actividades.R;
import com.cuepoint.clases.Mensaje;
import com.cuepoint.clases.Plano;
import com.cuepoint.clases.Punto;
import com.cuepoint.datos.MarcadoresSQLite;

/**
 * Clase que se encarga del manejo de un plano en pantalla: zoom, desplazamiento,
 * inserción de marcadores, busqueda y lectura de planos
 */
public class Imagen extends Activity implements OnTouchListener
{
	//Variables para la escala
    static final int ZOOM_MAX = 10;
    static final int ZOOM_MIN = 0;
    private int ZOOM_ACTUAL = 0;
    private float scaleIn = 1.17f;
    private float scaleOut = 0.8547f;
    
    private static final int REQUEST_CHOOSE_PHONE = 1;
    
	 //Matrices que serán usadas para el zoom y mover la imagen
	 Matrix matrix = new Matrix();
	 Matrix savedMatrix = new Matrix();
	 
	 //Estados en los que puede estar la aplicación luego de tocar la pantalla  
	 static final int NONE = 0;
	 static final int DRAG = 1;
	 int mode = NONE;
	  
	 //Punto donde el usuario hizo el primer toque en la pantalla  
	 PointF start = new PointF();
	 
	 //Porcentaje de escala que hay que aplicar al marcador con respecto a la imagen
	 private int escalaMarcador = 20;
	 
	 //Coordenadas del marcador
	 float cx = -1;
	 float cy = -1;
	 
	 //Medidas de la imagen del plano
	 float altoOriginal;
	 float anchoOriginal;
	 
	 //Si el mensaje ya trae las coordenadas como respuesta a una solicitud
	 //no se puede insertar marcadores nuevos en esta imagen. Es sólo para mostrar
	 //la posición de otra persona.
	 boolean imagenAccesoEscritura = true;
	 
	 Plano imagenPlano;
	 
	 //Indica si es una respuesta a un mensaje
	 boolean respuesta = false;
	 Mensaje mensaje = new Mensaje();
	 
	 //Marcador favorito de un plano
	 Punto marcador;
    
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p02_plano);
        
        getPlanoEnIntent();
        
        cargarImagen();
        
        cargarMarcador();
        
        Bundle b = getIntent().getExtras();
        respuesta = b.getBoolean("Respuesta");
        boolean insertarMarca = b.getBoolean("InsertarMarca");
        boolean mostrarMensaje = b.getBoolean("MostrarMensaje");
        if(respuesta)
        {
        	mensaje = (Mensaje) b.getParcelable("Mensaje");
        }
        if(insertarMarca)
        {
        	cx = ((Punto) b.getParcelable("Punto")).getX();
        	cy = ((Punto) b.getParcelable("Punto")).getY();
        	dibujarMarca();
        	imagenAccesoEscritura = false;
        }
        if(mostrarMensaje)
        {
	        showDialog(1);
        }
    }
	
	@Override
	public void onResume()
	{
		super.onResume();
		dibujarMarca();
	}
	
	@Override
    protected Dialog onCreateDialog(int id) 
	{
    	Dialog dialogo = null;
    	switch(id)
    	{
    		//Dialogo para mostrar el mensaje opcional
    		case 1:
    			dialogo = crearDialogoMensaje();
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    	return dialogo;
    }
    
	/**
	 * Crea un Objeto Dialog con los datos a mostrar en pantalla
	 * @return Objeto Dialog
	 */
    private Dialog crearDialogoMensaje()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle("Mensaje adicional");
    	builder.setMessage(mensaje.getTexto());
    	builder.setPositiveButton("Cerrar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	return builder.create();
    }

    /**
     * Obtiene el marcador favorito desde la base de datos
     */
	private void cargarMarcador()
	{
		MarcadoresSQLite m = new MarcadoresSQLite();
		marcador = m.getMarcadorPorIdPlano(this, imagenPlano.getIdPlano());
	}
	
	/**
	 * Guarda un marcador en la base de datos. Si ya existe un marcador lo actualiza,
	 * de lo contrario agrega uno nuevo
	 */
	private void guardarMarcador()
	{
		MarcadoresSQLite m = new MarcadoresSQLite();
		if(marcador.getX() > 0)
		{
			marcador.setX(cx);
			marcador.setY(cy);
			m.guardarMarcador(this, marcador, imagenPlano.getIdPlano());
		}
		else
		{
			marcador.setX(cx);
			marcador.setY(cy);
			m.nuevoMarcador(this, marcador, imagenPlano.getIdPlano());
		}
	}
	
	/**
	 * Carga un plano desde la tarjeta SD, aplicandole escala para que quepa en la pantalla
	 */
	private void cargarImagen()
	{
		ImageView plano = (ImageView) findViewById(R.id.imageViewPlano);
		BitmapDrawable d = leerImagenesSD();
        if(d != null)
        {
        	matrix.reset();
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
	
	/**
	 * Libera recursos de la memoria para evitar 'ahogar' el móvil
	 * @param view Layout donde se encuentran los componentes de la pantalla
	 */
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
	
	/**
	 * Agrega las opciones del menú antes de ser mostrado en pantalla, según las características del entorno
	 * @param menu Objeto Menu
	 */
	private void construirMenu(Menu menu)
	{
		if(imagenAccesoEscritura && cx != -1 && cx != -1)
		{
			menu.add(Menu.NONE, 1, Menu.NONE, "Enviar").setIcon(R.drawable.enviar);
		}
		else if(!imagenAccesoEscritura)
		{
			menu.add(Menu.NONE, 4, Menu.NONE, "Responder").setIcon(R.drawable.enviar);
		}
		menu.add(Menu.NONE, 2, Menu.NONE, "Cancelar").setIcon(R.drawable.cancelar);
		SubMenu submenu1 = menu.addSubMenu(Menu.NONE, 5, Menu.NONE, "Marcadores Favoritos").setIcon(R.drawable.marcador);
			submenu1.add(Menu.NONE, 6, Menu.NONE, "Insertar marcador");
			submenu1.add(Menu.NONE, 7, Menu.NONE, "Guardar marcador");
		menu.add(Menu.NONE, 3, Menu.NONE, "Opciones").setIcon(R.drawable.opciones);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		construirMenu(menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();
		construirMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		//Menú Enviar
		case 1:
			Intent i = new Intent();
			//Es una respuesta para una persona en particular
			if(respuesta)
			{
				Bundle bundle = new Bundle();
				if(mensaje.getNombre().equals(""))
				{
					bundle.putString("nombre", mensaje.getNumeroOrigenDestino());
				}
				else
				{
					bundle.putString("nombre", mensaje.getNombre());
				}
				bundle.putString("numero", mensaje.getNumeroOrigenDestino());
		        bundle.putFloat("x", cx);
		        bundle.putFloat("y", cy);
		        bundle.putInt("idPlano", imagenPlano.getIdPlano());
		        i.putExtras(bundle);
				i.setComponent(new ComponentName(this, EnviarSMS.class));
				startActivity(i);
			}
			//No es una respuesta entonces se muestra la lista de contactos
			else
			{
				i.setComponent(new ComponentName(this, ListaContactos.class));
				startActivityForResult(i, REQUEST_CHOOSE_PHONE);
			}
			return true;
		//Menú Cancelar
		case 2:
			System.gc();
			finish();
			return true;
		//Menú Opciones
		case 3:
			Intent in = new Intent();
			in.setComponent(new ComponentName(this, Preferencias.class));
			startActivity(in);
			return true;
		//Menú Responder
		case 4:
			cx = 0;
			cy = 0;
			imagenAccesoEscritura = true;
			ZOOM_ACTUAL = 0;
			ProgressBar pb = (ProgressBar) findViewById(R.id.barraProgresoZoom);
			pb.setProgress(0);
			cargarImagen();
			cargarMarcador();
			return true;
		//Submenú Insertar marcador favorito
		case 6:
			if(marcador.getX() != -1)
			{
				imagenAccesoEscritura = true;
				cx = marcador.getX();
				cy = marcador.getY();
				dibujarMarca();
			}
			else
			{
				Toast toast = Toast.makeText(this, "No hay marcadores guardados en este plano", Toast.LENGTH_LONG);
	    		toast.show();
			}
			return true;
		//Submenú Guardar marcador favorito
		case 7:
			if(cx > -1 && cy > -1)
			{
				guardarMarcador();
				Toast t = Toast.makeText(this, "Marcador guardado", Toast.LENGTH_SHORT);
				t.show();
			}
			else
			{
				Toast toast = Toast.makeText(this, "Debe marcar un punto en el plano antes de guardar", Toast.LENGTH_LONG);
	    		toast.show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
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
	
	/**
	 * Obtiene el objeto Plano que se envió desde la otra activity
	 */
	private void getPlanoEnIntent()
	{
		Bundle b = getIntent().getExtras();
		imagenPlano = b.getParcelable("Plano");
	}
	
	/**
	 * Obtiene una imagen desde la tarjeta SD
	 * @return Objeto BitmapDrawable con la imagen cargada, o null si no se la encontró
	 */
	private BitmapDrawable leerImagenesSD()
	{
		BitmapDrawable imagen = null;
		boolean sdDisponible = false;
		boolean sdAccesoEscritura = false;
		
		//Comprobamos el estado de la memoria SD
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
			    //Si existe la imagen se carga en la variable global
			    if(imgFile.exists())
			    {
			    	imagen = (BitmapDrawable) Drawable.createFromPath(imgFile.getAbsolutePath());
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
	
	/**
	 * Permite limitar la traslación de la imagen por fuera de la pantalla del movil.
	 * Si ocurre eso, la imagen vuelve a la posición tocando el borde de la pantalla.
	 * @param plano Objeto ImageView
	 */
	private void limitarDesplazamientoFueraPantalla(ImageView plano)
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
        if (imagenX < 0 && (imagenX + anchoImagen) < anchoIV)
        {
        	//La imagen es más grande que la pantalla
        	if(anchoImagen >= anchoIV)
        	{
        		//Distancia del borde derecho de la imagen con el borde derecho del ImageView
        		desplazarX = anchoIV - (imagenX + anchoImagen);
        	}
        	else
        	{
        		//Distancia del borde izquierdo de la imagen con el borde izquierdo del ImageView
        		desplazarX = (-1) * imagenX;
        	}
        }
        
        //Si la imagen se sale por el borde derecho pero no por el izquierdo
        if((imagenX + anchoImagen) > anchoIV && imagenX > 0)
        {
        	//La imagen es más grande que la pantalla
        	if(anchoImagen >= anchoIV)
        	{
        		//Distancia del borde derecho de la imagen con el borde derecho del ImageView
        		desplazarX = (-1) * imagenX;
        	}
        	else
        	{
        		//Distancia del borde izquierdo de la imagen con el borde izquierdo del ImageView
        		desplazarX = anchoIV - (imagenX + anchoImagen);
        	}
        }
        
        //Si la imagen se sale por el borde superior
        if (imagenY < 0 && (imagenY + altoImagen) < altoIV)
        {
        	if(altoImagen >= altoIV)
        	{
        		//Distancia del borde inferior de la imagen con el borde inferior del ImageView
        		desplazarY = altoIV - (imagenY + altoImagen);
        	}
        	else
        	{
        		//Distancia del borde superior de la imagen con el borde superior del ImageView
        		desplazarY = (-1) * imagenY;
        	}
        }
        
        //Si la imagen se sale por el borde inferior
        if((imagenY + altoImagen) > altoIV && imagenY > 0)
        {
        	if(altoImagen >= altoIV)
        	{
        		//Distancia del borde inferior de la imagen con el borde inferior del ImageView
        		desplazarY = (-1) * imagenY;
        	}
        	else
        	{
        		//Distancia del borde superior de la imagen con el borde superior del ImageView
        		desplazarY = altoIV - (imagenY + altoImagen);
        	}
        }
        matrix.postTranslate(desplazarX, desplazarY);
        plano.setImageMatrix(matrix);
	}
	
	/**
	 * Acciones a tomar cuando el usuario toca la pantalla
	 */
	public boolean onTouch(View v, MotionEvent event)
	{
		ImageView plano = (ImageView) findViewById(R.id.imageViewPlano);
		matrix.set(plano.getImageMatrix());
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			//Acción al toque inicial en la pantalla
		 	case MotionEvent.ACTION_DOWN:
		 		savedMatrix.set(matrix);
		 		start.set(event.getX(), event.getY());
		 		mode = NONE;
		 		break;
		 	//Acción al levantar el dedo de la pantalla
		 	case MotionEvent.ACTION_UP:
		 		if (mode == NONE && imagenAccesoEscritura)
		 		{
		 			calcularCoordenadasImagen(event.getX(), event.getY());
		 			dibujarMarca();
		 		}
		 		else if (mode == DRAG)
		 		{
		 			limitarDesplazamientoFueraPantalla(plano);
		 		}
		 		else if (!imagenAccesoEscritura)
		 		{
		 			Toast toast = Toast.makeText(this, "Para responder ir a Menú - Responder", Toast.LENGTH_SHORT);
		    		toast.show();
		 		}
		 		break;
		 	//Acción al desplazar el dedo sobre la pantalla
		 	case MotionEvent.ACTION_MOVE:
		 		mode = DRAG;
		 		matrix.set(savedMatrix);
		 		matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
		 		break;
		 }
		 plano.setImageMatrix(matrix);
		 return true; 
	}
	
	/**
	 * Calcula la posición en la imagen donde el usuario toco la pantalla
	 * @param x
	 * @param y
	 */
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
	
	/**
	 * Dibuja el marcador en la imagen del plano según las coordenadas x e y calculadas
	 */
	private void dibujarMarca()
	{
		if(cx > -1 && cy > -1)
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
	}

	/**
	 * Obtiene una imagen usada como marcador según las preferencias del usuario
	 * @param anchoPlano Ancho de la imagen del plano
	 * @return Objeto Bitmap que contiene el marcador
	 */
	private Bitmap getMarcador(int anchoPlano)
	{
		Bitmap m = null;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int tipo = Integer.parseInt(pref. getString("marcador", "1"));
		int color = Integer.parseInt(pref.getString("color", "1"));
		int recurso = 0;
		switch(tipo)
		{
		//Marcador tipo círculo
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
		//Marcador tipo cruz
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
	
	/**
	 * Cuando se hace zoom, se mantiene el mismo centro de la imagen en medio de la pantalla
	 * @param tipoZoom Entero que distingue entre zoom in y zoom out
	 * @return Objeto Punto con las distancias que hay que desplazar la imagen
	 */
	private Punto desplazarCentroPantalla(int tipoZoom)
	{
		Punto p = new Punto();
		ImageView plano = (ImageView) findViewById(R.id.imageViewPlano);
		matrix.set(plano.getImageMatrix());
		//Extraer los parámetros de la matriz referentes a la imagen.
 		float[] matrixValues = new float[9];
 		matrix.getValues(matrixValues);
 		// coordenada X de matrix (la imagen) relativo a ImageView
 		float imagenX = matrixValues[2];
 		// coordenada Y de matrix (la imagen) relativo a ImageView
 		float imagenY = matrixValues[5];
 		// Ancho ImageView que contiene la imagen
 		float anchoIV = plano.getWidth();
 		// Alto ImageView que contiene la imagen
 		float altoIV = plano.getHeight();
 		
 		//Zoom in
 		if(tipoZoom == 0)
 		{
 			float anchoEscalado = (-imagenX + (anchoIV / 2)) * scaleIn;
 			float anchoActual = (-imagenX + (anchoIV / 2));
 			p.setX(-(anchoEscalado - anchoActual));
 			float altoEscalado = (-imagenY + (altoIV / 2)) * scaleIn;
 			float altoActual = (-imagenY + (altoIV / 2));
 			p.setY(-(altoEscalado - altoActual));
 		}
 		//Zoom out
 		else if(tipoZoom == 1)
 		{
 			float anchoEscalado = (-imagenX + (anchoIV / 2)) * scaleOut;
 			float anchoActual = (-imagenX + (anchoIV / 2));
 			p.setX(-(anchoEscalado - anchoActual));
 			float altoEscalado = (-imagenY + (altoIV / 2)) * scaleOut;
 			float altoActual = (-imagenY + (altoIV / 2));
 			p.setY(-(altoEscalado - altoActual));
 		}
 		return p;
	}
	
	/**
	 * Aumenta el tamaño de la imagen en pantalla manteniendo el mismo centro
	 * @param v
	 */
	public void onClickZoomIn(View v)
	{
		if (ZOOM_ACTUAL < ZOOM_MAX)
		{
			ZOOM_ACTUAL++;
			matrix.set(((ImageView)v).getImageMatrix());
			savedMatrix.set(matrix);
			Punto p = desplazarCentroPantalla(0);
			matrix.preScale(scaleIn, scaleIn);
			matrix.postTranslate(p.getX(), p.getY());
			ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
			iv.setImageMatrix(matrix);
			
			actualizarBarraZoom();
			limitarDesplazamientoFueraPantalla(iv);
		}
	}
	
	/**
	 * Disminuye el tamaño de la imagen en pantalla manteniendo el mismo centro
	 */
	public void onClickZoomOut(View v)
	{
		if(ZOOM_ACTUAL > ZOOM_MIN)
		{
			ZOOM_ACTUAL--;
			matrix.set(((ImageView)v).getImageMatrix());
			savedMatrix.set(matrix);
			Punto p = desplazarCentroPantalla(1);
			matrix.preScale(scaleOut, scaleOut);
			matrix.postTranslate(p.getX(), p.getY());
			ImageView iv = (ImageView)findViewById(R.id.imageViewPlano);
			iv.setImageMatrix(matrix);
			
			actualizarBarraZoom();
			limitarDesplazamientoFueraPantalla(iv);
		}
	}
	
	/**
	 * Actualiza el progreso de la barra que muestra el estado del zoom
	 */
	private void actualizarBarraZoom()
	{
		ProgressBar s = (ProgressBar) findViewById(R.id.barraProgresoZoom);
		s.setProgress(ZOOM_ACTUAL*10);
	}
}