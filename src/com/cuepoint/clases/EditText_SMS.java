package com.cuepoint.clases;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.EditText;

/*
	Clase que modifica un edittext para ubicar un contador en
	la esquina superior derecha del control
*/
public class EditText_SMS extends EditText
{
	//Pinceles para pintar el rectangulo y el texto
	private Paint p1;
	private Paint p2;
	//Variable que guarda la cantidad maxima de caracteres que puede escribir el usuario
	private static final int cantidad_max_caracteres = 60;

	//Constructor
	public EditText_SMS(Context context, AttributeSet attrs, int defStyle){
	    super(context, attrs,defStyle);
	    inicializacion();
	}
	
	//Constructor
	public EditText_SMS(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    inicializacion();
	}
	
	//Constructor
	public EditText_SMS(Context context) {
	    super(context);
	    inicializacion();
	}
	
	//Seteo de los valores por defecto de los pinceles
	private void inicializacion()
	{
		p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p1.setColor(Color.BLACK);
		p1.setStyle(Style.FILL);
		
		p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p2.setColor(Color.WHITE);
		p2.setTextSize(20f);
	}
	
	@Override
	//Al dibujar el edittext se le dibuja tambien el contador de caracteres
	public void onDraw(Canvas canvas) 
	{
		//Llamamos al método de la clase base (EditText)
		super.onDraw(canvas);
	
		//Dibujamos el fondo negro del contador
		canvas.drawRect(this.getWidth()-30, 5, this.getWidth()-5, 30, p1);
				
		//Calculamos la cantidad de caracteres restantes
		int con = cantidad_max_caracteres - this.getText().toString().length();
		
		//Dibujamos el número de caracteres restantes sobre el cuadrado negro
		canvas.drawText(Integer.toString(con), this.getWidth()-28, 25, p2);
	}

}
