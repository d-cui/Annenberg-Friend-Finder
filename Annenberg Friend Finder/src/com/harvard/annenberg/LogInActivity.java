package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class LogInActivity extends Activity {
	private SharedPreferences prefs;

	public static final String LOGIN_URL = "http://mgm.funformobile.com/aff/SignIn.php";

	private Hashtable<String, String> parameters;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences("AFF", MODE_PRIVATE);
		setContentView(R.layout.log_in);
		Button logInButton = (Button) findViewById(R.id.login_go);
		logInButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int HUID = Integer
						.parseInt(((EditText) findViewById(R.id.login_HUID))
								.getText().toString());
				String enteredPassword = ((EditText) findViewById(R.id.login_password))
						.getText().toString();

				doLogIn(String.valueOf(HUID), enteredPassword);
			}
		});

		Button signUpButton = (Button) findViewById(R.id.signup);
		signUpButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(LogInActivity.this, SignUpActivity.class);
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

	// Log in server stuff
	public void doLogIn(String huid, String password) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Log In");
		mProgressDialog.setMessage("Logging In, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();

		parameters.put("passwd", password);
		parameters.put("huid", huid);
		LoginTask upl = new LoginTask();
		upl.execute(LOGIN_URL);
	}

	private class LoginTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return login(url, parameters);
				// return "SUCCESS";
				// return downloadImage(url);
			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());
				return null;

			}
		}

		protected void onPostExecute(String result) {
			// Toast.makeText(this.get, "Your Ringtone has been downloaded",
			// Toast.LENGTH_LONG).show();
			try {
				// displayMsg();
				// displayImage(resultbm);
				mProgressDialog.dismiss();
				showUploadSuccess(result);

				Intent i = new Intent(LogInActivity.this,
						FriendFinderTabHost.class);
				startActivity(i);
				finish();
				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

		}

		public String login(String serverUrl, Hashtable<String, String> params) {
			try {

				// Log.v("serverUrl", serverUrl);
				URL url = new URL(serverUrl);
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Connection", "Keep-Alive");
				String postString = "";

				Enumeration<String> keys = params.keys();
				String key, val;
				while (keys.hasMoreElements()) {
					key = keys.nextElement().toString();
					// Log.v("KEY", key);
					val = params.get(key);
					// Log.v("VAL", val);
					postString += key + "=" + URLEncoder.encode(val, "UTF-8")
							+ "&";
				}
				postString = postString.substring(0, postString.length() - 1);
				Log.v("postString", postString);
				con.setRequestProperty("Content-Length",
						"" + Integer.toString(postString.getBytes().length));
				con.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				DataOutputStream dos = new DataOutputStream(
						con.getOutputStream());
				dos.writeBytes(postString);
				dos.flush();
				dos.close();
				// Responses from the server (code and message)
				int serverResponseCode = con.getResponseCode();
				Log.v("CODE", String.valueOf(serverResponseCode));
				String serverResponseMessage = con.getResponseMessage();
				Log.v("PAGE", serverResponseMessage);

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String line;
				StringBuffer response = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				String json = response.toString();
				return json;

			} catch (MalformedURLException me) {
				Log.v("MalformedURLException", me.toString());
				return null;
			} catch (IOException ie) {
				Log.v("IOException", ie.toString());
				return null;

			} catch (Exception e) {
				Log.v("Exception", e.toString());
				return null;
				// Log.e("HREQ", "Exception: "+e.toString());
			}
		}

	};

	private void showFinalAlert(CharSequence message) {
		new AlertDialog.Builder(LogInActivity.this)
				.setTitle("Error")
				.setMessage(message)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// finish();
							}
						}).setCancelable(false).show();
	}

	public void showUploadSuccess(String json) {
		Log.v("JSON", json);
		if (json == null) {
			String message = "An network error has occured. Please try again later";
			showFinalAlert(message);
			return;
		}
		try {
			JSONObject object = new JSONObject(json);
			String status = object.getString("status");
			status = status.trim();
			Log.v("STATUS", "Status is: " + status);
			if (status.equals("OK")) {

				SharedPreferences.Editor prefsEditor = prefs.edit();
				prefsEditor.putString("h", object.getString("h"));
				prefsEditor.putString("huid", object.getString("huid"));
				prefsEditor.putString("n", object.getString("n"));
				prefsEditor.putBoolean("login", true);
				prefsEditor.commit();

				// Bundle bundle = new Bundle();
				// bundle.putString("json", json);
				//
				// Intent mIntent = new Intent();
				// mIntent.putExtras(bundle);
				// setResult(RESULT_OK, mIntent);

				Toast.makeText(this, "You have successfully logged in.",
						Toast.LENGTH_LONG).show();
				finish();
			} else {
				Log.v("STATUS", status);
				String message = status;
				showFinalAlert(message);
			}
		} catch (Exception e) {

		}

	}

}
