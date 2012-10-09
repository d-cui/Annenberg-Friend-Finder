package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

public class FriendListActivity extends Activity {
	FriendListAdapter fla;
	ArrayList<Request> requests;
	ArrayList<Friend> friends;

	public void onCreate(Bundle bunny) {
		super.onCreate(bunny);
		// TODO: Server call, update requests + friends
		ExpandableListView expListView = (ExpandableListView) findViewById(android.R.id.list);
		fla = new FriendListAdapter(this, getGroups(), getChilds(), expListView);
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
		for (Friend f : friends) {
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

	public class Friend {
		String name;
		String img;
		String status;
		String table;
		String time;
	}
}
