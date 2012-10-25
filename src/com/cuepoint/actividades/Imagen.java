/**
 * 
 */
package com.cuepoint.actividades;

import java.io.File;
import com.cuepoint.actividades.*;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author Silvio
 *
 */
public class Imagen extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagen);
        
        Bundle b = getIntent().getExtras();

        String path = "/"+b.getString("path");
        
        File imgFile = new File(getFilesDir(),path);;
        try {
            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                ImageView myImage = (ImageView) findViewById(R.id.imageViewPlano);
                myImage.setImageBitmap(myBitmap);

            }	
        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }
    }
}
