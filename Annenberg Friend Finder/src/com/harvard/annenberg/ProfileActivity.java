package com.harvard.annenberg;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView;

public class ProfileActivity extends Activity {
	DbAdapter database;
	SharedPreferences prefs;

	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.profile_layout);
		prefs = getSharedPreferences("AFF", 0);
		database = new DbAdapter(this);

		// Update Name and HUID
		Cursor c = database.fetchUserByHUID(prefs.getInt("HUID", 0));
		TextView name = (TextView) findViewById(R.id.profile_name);
		name.setText(c.getString(c
				.getColumnIndexOrThrow(DbAdapter.KEY_USER_NAME)));
		TextView huid = (TextView) findViewById(R.id.profile_HUID);
		huid.setText(c.getString(c
				.getColumnIndexOrThrow(DbAdapter.KEY_USER_HUID)));

		String uriString = c.getString(c
				.getColumnIndexOrThrow(DbAdapter.KEY_USER_IMAGE));
		if (uriString.equals("") == false) {
			Uri uri = Uri.parse(uriString);
			ImageView image = (ImageView) findViewById(R.id.profile_image);
			image.setImageURI(uri);
		}

		// Spinner
		Spinner s = (Spinner) findViewById(R.id.profile_status);
		ArrayAdapter a = ArrayAdapter.createFromResource(this,
				R.array.status_array, android.R.layout.simple_spinner_item);
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(a);
		s.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Server update
				switch (pos) {
				case 0:
					// N/A
					break;
				case 1:
					// In Line
					break;
				case 2:
					// Eating
					break;
				}

			}
		});
		
		// Table
		TextView tableText = (TextView) findViewById(R.id.profile_table);
		
		
	}
}
