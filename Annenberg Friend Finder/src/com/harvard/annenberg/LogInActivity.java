package com.harvard.annenberg;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

public class LogInActivity extends Activity {
	private DbAdapter database;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences("AFF", 0);
		database = new DbAdapter(this);
		setContentView(R.layout.log_in);
		Button logInButton = (Button) findViewById(R.id.login_go);
		logInButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int HUID = Integer
						.parseInt(((EditText) findViewById(R.id.login_HUID))
								.getText().toString());
				Cursor c = database.fetchUserByHUID(HUID);
				if (c == null) {
					showAlert("HUID not found. Do you need to sign up?");
					return;
				}
				String userPassword = c.getString(c
						.getColumnIndexOrThrow(database.KEY_PASSWORD));
				String enteredPassword = ((EditText) findViewById(R.id.login_password))
						.getText().toString();
				if (!userPassword.equals(enteredPassword)) {
					showAlert("Incorrect password entered.");
					return;
				}
				// TODO: Log-in stuff
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("login", true);
				editor.putInt("HUID", HUID);
				editor.commit();
				Intent i = new Intent(LogInActivity.this, ProfileActivity.class);
				startActivity(i);
				finish();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in, menu);
		return true;
	}

	private void showAlert(String text) {
		new AlertDialog.Builder(this)
				.setTitle("Alert")
				.setMessage(text)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// close
							}
						}).setCancelable(false).show();
	}

}
