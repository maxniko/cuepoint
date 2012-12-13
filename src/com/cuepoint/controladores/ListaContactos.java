package com.cuepoint.controladores;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.cuepoint.actividades.R;

/**
 * Se encarga de mostrar la lista de contactos que tiene el telefono
 */
public class ListaContactos extends ListActivity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p03_contactos);
        setTitle("Elija un teléfono");
        
		//Consulta: Contactos con el numero de telefono listados por nombre
		Cursor mCursor = getContentResolver().query(
		Data.CONTENT_URI,
		new String[] { BaseColumns._ID, Data.DISPLAY_NAME, Phone.NUMBER,Phone.TYPE },
			Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
			+ Phone.NUMBER + " IS NOT NULL", null,
			Data.DISPLAY_NAME + " ASC");

		startManagingCursor(mCursor);

		ListAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2,
				mCursor,
				new String[] { Data.DISPLAY_NAME, Phone.NUMBER },
				new int[] { android.R.id.text1, android.R.id.text2 }
				);
			setListAdapter(adapter);
		}

		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			Intent result = new Intent();

			Cursor c = (Cursor) getListAdapter().getItem(position);
			int numId = c.getColumnIndex(Phone.NUMBER);
			int nomId = c.getColumnIndex(Phone.DISPLAY_NAME);
			
			String numero = c.getString(numId);
			String nombre = c.getString(nomId);

			//Guardar los datos para enviarlos a la activity que llamo
			result.putExtra("numero", numero);
			result.putExtra("nombre", nombre);
			setResult(Activity.RESULT_OK, result);
			
			finish();
		}
	}
