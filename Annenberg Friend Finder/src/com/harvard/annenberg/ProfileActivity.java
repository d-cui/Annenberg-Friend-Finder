package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

public class ProfileActivity extends Activity {
	DbAdapter database;
	private SharedPreferences prefs;
	private int currentSelection;
	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;
	private boolean notSet = false;
	private Spinner s;

	private String timeOfUpdate;

	public static final String UPDATE_URL = "http://mgm.funformobile.com/aff/updateIsEating.php";
	public static final String GET_URL = "http://mgm.funformobile.com/aff/getStatus.php";

	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.profile_layout);
		prefs = getSharedPreferences("AFF", 0);
		// Update Name and HUID
		TextView name = (TextView) findViewById(R.id.profile_name);
		name.setText(prefs.getString("n", ""));
		TextView huid = (TextView) findViewById(R.id.profile_HUID);
		huid.setText(prefs.getString("huid", ""));

		// Image
		// TODO: Gallery set image stuff.
		// String uriString = c.getString(c
		// .getColumnIndexOrThrow(DbAdapter.KEY_USER_IMAGE));
		// if (uriString.equals("") == false) {
		// Uri uri = Uri.parse(uriString);
		// ImageView image = (ImageView) findViewById(R.id.profile_image);
		// image.setImageURI(uri);
		// }
		// c.close();
		// database.close();
		// Spinner
		s = (Spinner) findViewById(R.id.profile_status);
		ArrayAdapter a = ArrayAdapter.createFromResource(this,
				R.array.status_array, android.R.layout.simple_spinner_item);
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(a);
		s.setOnItemSelectedListener(statusListener);

		getStatus();

		// Table
		TextView tableText = (TextView) findViewById(R.id.profile_table);
		String table = prefs.getString("table", "");
		tableText.setText((table.equals("") ? "N/A" : "" + table));

		// Check in button
		Button b = (Button) findViewById(R.id.check_in);
		b.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(ProfileActivity.this,
						AnnenbergActivity.class);
				startActivity(i);

			}
		}

		);

	}

	OnItemSelectedListener statusListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			currentSelection = position + 1;
			if (notSet) {
				updateStatus();
				prefs.edit().putInt("status", position).commit();
			} else
				notSet = true;
			// TODO Auto-generated method stub

		}

		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	};

	protected void onResume() {
		notSet = false;
		if (prefs.getInt("status", -1) != -1)
			s.setSelection(prefs.getInt("status", -1));
		super.onResume();
	}

	// Fetch list of users
	public void updateStatus() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Updating Status");
		mProgressDialog.setMessage("Updating Status, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();

		parameters.put("huid", prefs.getString("huid", ""));
		if (currentSelection == 1)
			parameters.put("eatStatus", "N");
		else
			parameters.put("eatStatus", "Y");
		parameters.put("state", String.valueOf(currentSelection));

		UpdateStatusTask upl = new UpdateStatusTask();
		upl.execute(UPDATE_URL);
		
	}

	public void getStatus() {
		// mProgressDialog = new ProgressDialog(this);
		// mProgressDialog.setTitle("Updating Status");
		// mProgressDialog.setMessage("Updating Status, Please Wait");
		// mProgressDialog.setIndeterminate(true);
		//
		// mProgressDialog.setCancelable(false);
		//
		// mProgressDialog.show();
		parameters = new Hashtable<String, String>();

		parameters.put("huid", prefs.getString("huid", ""));

		GetStatusTask upl = new GetStatusTask();
		upl.execute(GET_URL);
	}

	private class UpdateStatusTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return updateStatus(url, parameters);
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

				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

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

					Calendar c = Calendar.getInstance();
					timeOfUpdate = c.getTime().toString().split(" ")[3];
					String message = "Status updated!";
					showFinalAlert(message);
					return;

				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}

		public String updateStatus(String serverUrl,
				Hashtable<String, String> params) {
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
		new AlertDialog.Builder(ProfileActivity.this)
				.setTitle("Notice")
				.setMessage(message)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// finish();
							}
						}).setCancelable(false).show();
	}

	private class GetStatusTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return statusGetter(url, parameters);
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
//				mProgressDialog.dismiss();
				showUploadSuccess(result);

				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

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

					currentSelection = Integer.valueOf(object
							.getString("stateNum"));
					timeOfUpdate = object.getString("time").split(" ")[1];
					prefs.edit().putInt("status", currentSelection - 1)
							.commit();

					if (prefs.getInt("status", -1) != -1)
						s.setSelection(prefs.getInt("status", -1));

				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}

		public String statusGetter(String serverUrl,
				Hashtable<String, String> params) {
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
}
