package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonAdapter extends ArrayAdapter {
	ArrayList<Person> people;

	public PersonAdapter(Context context, ArrayList<Person> objects) {
		super(context, R.layout.person_row, R.id.person_name, objects);
		people = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		((TextView) view.findViewById(R.id.person_name)).setText(people.get(
				position).getName());
		((TextView) view.findViewById(R.id.person_status)).setText(people.get(
				position).getStatus());
		((TextView) view.findViewById(R.id.person_table)).setText(people.get(
				position).getTable());
		((TextView) view.findViewById(R.id.person_time)).setText(people.get(
				position).getTime());
		ImageView i = ((ImageView) view.findViewById(R.id.person_image));
		String image = people.get(position).getImg();
		if (image.equals("") == false) {
			Uri u = Uri.parse(people.get(position).getImg());
			i.setImageURI(u);
		}

		return view;
	}
}
