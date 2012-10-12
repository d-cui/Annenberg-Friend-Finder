package com.harvard.annenberg;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class EveryoneListActivity extends Activity {
	public void onCreate(Bundle bunny) {
		super.onCreate(bunny);
		setContentView(R.layout.all_list_layout);
		ListView l = (ListView) findViewById(R.id.allList);
		ArrayList<Person> people = new ArrayList<Person>();
		// TODO: server call fetch all Active
		PersonAdapter p = new PersonAdapter(this, people);
		l.setAdapter(p);
	}
}
