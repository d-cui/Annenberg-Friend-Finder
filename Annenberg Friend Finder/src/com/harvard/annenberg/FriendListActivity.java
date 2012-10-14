package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

public class FriendListActivity extends Activity {
	private FriendListAdapter fla;
	private ExpandableListView expListView;
	private ArrayList<Request> requests;
	private ArrayList<Person> friends;

	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;
	private SharedPreferences prefs;

	private ArrayList<HashMap<String, String>> groups;
	private ArrayList<ArrayList<HashMap<String, String>>> children;

	public static final String FETCHFRIENDS_URL = "http://mgm.funformobile.com/aff/fetchFriends.php";
	public static final String FETCHREQ_URL = "http://mgm.funformobile.com/aff/fetchFriendRequests.php";
	public static final String ADDFRIEND_URL = "http://mgm.funformobile.com/aff/addFriend.php";
	public static final String ACCEPTFRIEND_URL = "http://mgm.funformobile.com/aff/acceptFriend.php";

	public void onCreate(Bundle bunny) {
		super.onCreate(bunny);
		setContentView(R.layout.friends_list_layout);

		friends = new ArrayList<Person>();
		requests = new ArrayList<Request>();
		prefs = this.getSharedPreferences("AFF", MODE_PRIVATE);

		// TODO: Server call, update requests + friends
		fetchFriends(prefs.getString("huid", ""));
		fetchReq(prefs.getString("huid", ""));
		expListView = (ExpandableListView) findViewById(android.R.id.list);
		expListView.setOnChildClickListener(reqListener);
		expListView.setGroupIndicator(null);

		ImageView b = (ImageView) findViewById(R.id.add_new_friend);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText e = (EditText) findViewById(R.id.add_friend_huid);
				int friendHUID = 0;
				try {
					if (e.getText().toString().length() != 8) {
						showAlert("Please enter valid HUID");
						return;
					}
					friendHUID = Integer.parseInt(e.getText().toString());
				} catch (NumberFormatException nfe) {
					showAlert("Please enter valid HUID");
					return;
				}

				if (friendHUID != 0) {
					addFriend(prefs.getString("huid", ""),
							String.valueOf(friendHUID));
				}
				// TODO: addFriend based on friendHUID
			}
		});
	}

	private OnChildClickListener reqListener = new OnChildClickListener() {

		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			if (groupPosition == 0) {

				final String friendHUID = children.get(0).get(childPosition)
						.get("HUID");

				AlertDialog.Builder builder = new AlertDialog.Builder(
						FriendListActivity.this);
				builder.setCancelable(true);
				builder.setTitle("Options");
				builder.setItems(R.array.req_array,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									acceptFriend(prefs.getString("huid", ""),
											friendHUID, "Y");
								} else if (which == 1) {
									acceptFriend(prefs.getString("huid", ""),
											friendHUID, "N");
								}
							}
						});
				builder.create();
				builder.show();
			}
			return true;
		}

	};

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
			friendChildren.add(h);
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
	public void fetchFriends(String huid) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Fetching Friends");
		mProgressDialog.setMessage("Fetching Friends, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		FetchFriendsTask upl = new FetchFriendsTask();
		upl.execute(FETCHFRIENDS_URL);
	}

	public void addFriend(String huid, String fhuid) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Adding Friend");
		mProgressDialog.setMessage("Adding Friend, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		parameters.put("f_huid", fhuid);
		AddFriendTask upl = new AddFriendTask();
		upl.execute(ADDFRIEND_URL);
	}

	public void acceptFriend(String huid, String fhuid, String add) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Adding Friend");
		mProgressDialog.setMessage("Adding Friend, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		parameters.put("f_huid", fhuid);
		parameters.put("add", add);
		AcceptFriendTask upl = new AcceptFriendTask();
		upl.execute(ACCEPTFRIEND_URL);
	}

	public void fetchReq(String huid) {
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		FetchReqTask upl = new FetchReqTask();
		upl.execute(FETCHREQ_URL);
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

					JSONArray list = new JSONArray(object.getString("list"));
					int length = list.length();
					for (int i = 0; i < length; i++) {
						JSONObject user = list.getJSONObject(i);

						String time = user.getString("time");
						String HUID = user.getString("huid");
						String state = user.getString("state");
						String table = user.getString("tableNum");
						String image = user.getString("imageUri");
						if (image.equals("null"))
							image = "";
						String name = user.getString("name");

						Person person = new Person(Integer.valueOf(HUID), name,
								image, state, table, time);

						friends.add(person);
					}
				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}
	};

	private class FetchReqTask extends AsyncTask<String, Integer, String> {

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
				showUploadSuccess(result);
				groups = getGroups();
				children = getChilds();
				fla = new FriendListAdapter(FriendListActivity.this, groups,
						children, expListView);
				expListView.setAdapter(fla);
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

					JSONArray list = new JSONArray(object.getString("list"));
					int length = list.length();
					for (int i = 0; i < length; i++) {
						JSONObject user = list.getJSONObject(i);

						String HUID = user.getString("huid");
						String image = user.getString("imageUri");
						if (image == null)
							image = "";
						String name = user.getString("name");

						Request req = new Request();
						req.HUID = HUID;
						req.img = image;
						req.name = name;

						requests.add(req);
					}

				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}
	};

	private class AddFriendTask extends AsyncTask<String, Integer, String> {

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

					String message = "Friend successfully added";
					showFinalAlert(message);

				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}
	};

	private class AcceptFriendTask extends AsyncTask<String, Integer, String> {

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
				if (status.equals("ADD")) {

					String message = "Friend successfully added.";
					showFinalAlert(message);

				} else if (status.equals("REMOVE")) {
					String message = "Friend successfully rejected.";
					showFinalAlert(message);
				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

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
}