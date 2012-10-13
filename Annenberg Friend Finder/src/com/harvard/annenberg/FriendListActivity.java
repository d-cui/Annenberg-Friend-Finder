package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class FriendListActivity extends Activity {
	FriendListAdapter fla;
	ArrayList<Request> requests;
	ArrayList<Person> friends;

	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;

	public static final String FETCHFRIENDS_URL = "http://mgm.funformobile.com/aff/fetchFriends.php";
	public static final String FETCHREQ_URL = "http://mgm.funformobile.com/aff/fetchFriendRequests.php";
	public static final String ADDFRIEND_URL = "http://mgm.funformobile.com/aff/addFriend.php";
	public static final String ACCEPTFRIEND_URL = "http://mgm.funformobile.com/aff/acceptFriend.php";

	public void onCreate(Bundle bunny) {
		super.onCreate(bunny);
		setContentView(R.layout.friends_list_layout);

		// TODO: Server call, update requests + friends

		ExpandableListView expListView = (ExpandableListView) findViewById(android.R.id.list);
		fla = new FriendListAdapter(this, getGroups(), getChilds(), expListView);
		expListView.setAdapter(fla);

		Button b = (Button) findViewById(R.id.add_new_friend);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText e = (EditText) findViewById(R.id.add_friend_huid);
				int friendHUID;
				try {
					friendHUID = Integer.parseInt(e.getText().toString());
				} catch (NumberFormatException nfe) {
					showAlert("Please enter valid HUID");
					return;
				}
				// TODO: addFriend based on friendHUID
			}
		});
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

	private ArrayList<HashMap<String, String>> getGroups() {
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("category", "Friend Requests");
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		result.add(h);
		h = new HashMap<String, String>();
		h.put("category", "Friends");
		result.add(h);
		return result;
	}

	private ArrayList<ArrayList<HashMap<String, String>>> getChilds() {
		ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
		// Do requests.
		ArrayList<HashMap<String, String>> requestChildren = new ArrayList<HashMap<String, String>>();
		for (Request r : requests) {
			HashMap<String, String> h = new HashMap<String, String>();
			h.put("HUID", r.HUID);
			h.put("name", r.name);
			h.put("img", r.img);
			requestChildren.add(h);
		}
		result.add(requestChildren);

		// Do friends.
		ArrayList<HashMap<String, String>> friendChildren = new ArrayList<HashMap<String, String>>();
		for (Person f : friends) {
			HashMap<String, String> h = new HashMap<String, String>();
			h.put("name", f.name);
			h.put("img", f.img);
			h.put("status", f.status);
			h.put("table", f.table);
			h.put("time", f.time);

		}
		result.add(friendChildren);
		return result;
	}

	public class Request {
		String HUID;
		String name;
		String img;
	}

	// Sign in server stuff
	public void doSignUp(String huid, String password, String username) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Sign Up");
		mProgressDialog.setMessage("Signing Up, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();

		parameters.put("passwd", password);
		String usrnm = username.replace("\n", " ");
		parameters.put("name", usrnm.trim());
		parameters.put("huid", huid);
		FetchFriendsTask upl = new FetchFriendsTask();
		upl.execute(FETCHFRIENDS_URL);
	}

	private class FetchFriendsTask extends AsyncTask<String, Integer, String> {

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

				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

		}
	};

	private void showFinalAlert(CharSequence message) {
		new AlertDialog.Builder(FriendListActivity.this)
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

				SharedPreferences prefs = getSharedPreferences("AFF",
						MODE_PRIVATE);
				SharedPreferences.Editor prefsEditor = prefs.edit();
				prefsEditor.putString("h", object.getString("h"));
				prefsEditor.putString("huid", object.getString("huid"));
				prefsEditor.putString("n", object.getString("n"));
				prefsEditor.putBoolean("login", true);
				prefsEditor.commit();

				Toast.makeText(this, "You have successfully Signed Up",
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