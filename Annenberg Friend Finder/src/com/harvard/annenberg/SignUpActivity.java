package com.harvard.annenberg;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.*;

public class SignUpActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		Button signUpButton = (Button) findViewById(R.id.signup_submit);
		signUpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = ((EditText) findViewById(R.id.signup_name))
						.getText().toString();
				if (name.length() < 2) {
					showAlert("Please enter a valid name");
					return;
				}
				String password = ((EditText) findViewById(R.id.signup_password))
						.getText().toString();
				String passwordConfirm = ((EditText) findViewById(R.id.signup_password_confirm))
						.getText().toString();
				if (!password.equals(passwordConfirm)) {
					showAlert("Error: Your passwords did not match");
					return;
				}
				if (password.length() < 6) {
					showAlert("Please enter a password of at least 6 characters");
					return;
				}
				int HUID = Integer
						.parseInt(((EditText) findViewById(R.id.signup_HUID))
								.getText().toString());
				DbAdapter database = new DbAdapter(SignUpActivity.this);
				if (database.fetchUserByHUID(HUID) == null) {
					showAlert("Error: That HUID is already registered");
					return;
				}
				database.createUser(name, "", HUID, passwordConfirm);

				showAlertAndTransfer("Profile created");
			}
		});
	}

	private void showAlertAndTransfer(String text) {
		new AlertDialog.Builder(this)
				.setTitle("Alert")
				.setMessage(text)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = new Intent(SignUpActivity.this,
										LogInActivity.class);
								startActivity(i);
								finish();
							}
						}).setCancelable(false).show();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in, menu);
		return true;
	}
}
