package com.cuepoint.controladores;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.cuepoint.actividades.R;

public class ListaContactos extends ListActivity
{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p03_contactos);
        setTitle("Elija un teléfono");
        
		// Query: contacts with phone shorted by name
		Cursor mCursor = getContentResolver().query(
		Data.CONTENT_URI,
		new String[] { Data._ID, Data.DISPLAY_NAME, Phone.NUMBER,Phone.TYPE },
			Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
			+ Phone.NUMBER + " IS NOT NULL", null,
			Data.DISPLAY_NAME + " ASC");

		startManagingCursor(mCursor);

		// Setup the list
		ListAdapter adapter = new SimpleCursorAdapter(this, // context
				android.R.layout.simple_list_item_2, // Layout for the rows
				mCursor, // cursor
				new String[] { Data.DISPLAY_NAME, Phone.NUMBER }, // cursor
				new int[] { android.R.id.text1, android.R.id.text2 } // view
				);
			setListAdapter(adapter);
		}

		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			Intent result = new Intent();

			// Get the data
			Cursor c = (Cursor) getListAdapter().getItem(position);
			int numId = c.getColumnIndex(Phone.NUMBER);
			int nomId = c.getColumnIndex(Phone.DISPLAY_NAME);
			
			String numero = c.getString(numId);
			String nombre = c.getString(nomId);

			// Save the phone to return it to the caller
			result.putExtra("numero", numero);
			result.putExtra("nombre", nombre);
			setResult(Activity.RESULT_OK, result);

			// Close this activity (return to caller)
			finish();
		}
	}
