package com.harvard.annenberg;

import java.util.Hashtable;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
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
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class LogInActivity extends Activity {
	private SharedPreferences prefs;

	public static final String LOGIN_URL = "http://mgm.funformobile.com/aff/SignIn.php";
	public static final String LOGIN_URL2 = "http://mgm.funformobile.com/aff/login.php";

	private Hashtable<String, String> parameters;
	private ProgressDialog mProgressDialog;
	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		prefs = getSharedPreferences("AFF", MODE_PRIVATE);
		setContentView(R.layout.log_in);

		TextView loginTitle = (TextView) findViewById(R.id.loginTitle);
		loginTitle.setText("Log In");
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

		// this.webView = new WebView(this);
		// this.webView.clearCache(true); // !!!
		// setContentView(this.webView);
		//
		// this.webView.loadUrl(LOGIN_URL2);
		// this.webView.getSettings().setJavaScriptEnabled(true);
		// this.webView.setWebChromeClient(new WebChromeClient() {
		// // Show loading progress in activity's title bar.
		// @Override
		// public void onProgressChanged(WebView view, int progress) {
		// setProgress(progress * 100);
		// }
		// });
		//
		// this.webView.setWebViewClient(new WebViewClient() {
		// public void onPageFinished(WebView view, String url) {
		//
		// }
		// });

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
				return ServerDbAdapter.connectToServer(url, parameters);
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
