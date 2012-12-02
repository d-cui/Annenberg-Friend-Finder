package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class FriendListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ExpandableListView mExpandableListView;
	private ArrayList<HashMap<String, String>> groups;
	private ArrayList<ArrayList<HashMap<String, String>>> children;
	private int[] groupStatus;

	public FriendListAdapter(Context context,
			ArrayList<HashMap<String, String>> groups,
			ArrayList<ArrayList<HashMap<String, String>>> child,
			ExpandableListView expListView) {
		mContext = context;
		mExpandableListView = expListView;

		this.groups = groups;
		this.children = child;

		groupStatus = new int[groups.size()];

		for (int i = 0; i < groupStatus.length; i++)
			groupStatus[i] = 0;

		setListEvent();
	}

	private void setListEvent() {

		mExpandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					public void onGroupExpand(int arg0) {
						// TODO Auto-generated method stub
						groupStatus[arg0] = 1;
					}
				});

		mExpandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {

					public void onGroupCollapse(int arg0) {
						// TODO Auto-generated method stub
						groupStatus[arg0] = 0;
					}
				});
	}

	public long getChildId(int arg0, int arg1) {
		return 0;
	}

	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {

		if (arg0 == 0) {
			RequestChildHolder childHolder;
			// if (arg3 == null) {
			// Friend Request
			arg3 = LayoutInflater.from(mContext).inflate(
					R.layout.friend_req_row, null);

			childHolder = new RequestChildHolder();
			childHolder.HUID = (TextView) arg3.findViewById(R.id.request_huid);
			childHolder.name = (TextView) arg3.findViewById(R.id.request_name);
			childHolder.img = (ImageView) arg3.findViewById(R.id.request_image);
			arg3.setTag(childHolder);
			// } else {
			// childHolder = (RequestChildHolder) arg3.getTag();
			// }

			childHolder.name.setText(children.get(arg0).get(arg1).get("name"));
			childHolder.HUID.setText("HUID: "
					+ children.get(arg0).get(arg1).get("HUID"));

			String imgUri = children.get(arg0).get(arg1).get("img");

			if (!imgUri.equals(""))
				childHolder.img.setImageURI(Uri.parse(children.get(arg0)
						.get(arg1).get("img")));
			return arg3;
		}
		PersonChildHolder childHolder;
		// if (arg3 == null) {
		arg3 = LayoutInflater.from(mContext).inflate(R.layout.person_row, null);

		childHolder = new PersonChildHolder();
		childHolder.name = (TextView) arg3.findViewById(R.id.person_name);
		childHolder.img = (ImageView) arg3.findViewById(R.id.person_image);
		childHolder.status = (TextView) arg3.findViewById(R.id.person_status);
		// childHolder.table = (TextView) arg3.findViewById(R.id.person_table);
		// childHolder.time = (TextView) arg3.findViewById(R.id.person_time);
		arg3.setTag(childHolder);
		// } else {
		// childHolder = (PersonChildHolder) arg3.getTag();
		// }
		childHolder.name.setText(children.get(arg0).get(arg1).get("name"));
		int statusID = Integer.parseInt(children.get(arg0).get(arg1)
				.get("status"));
		String status = "";

		if (statusID == 1) {
			status = "Not at Annenberg";
		} else if (statusID == 2) {
			status = "In line";
		} else {
			status = "Eating";
		}

		String imgUri = children.get(arg0).get(arg1).get("img");

		if (!imgUri.equals(""))
			childHolder.img.setImageURI(Uri.parse(children.get(arg0).get(arg1)
					.get("img")));

		int tableID = Integer.parseInt(children.get(arg0).get(arg1)
				.get("table"));
		String table = "" + ((tableID - 1) % 17 + 1);
		if (tableID > 17 && tableID <= 34)
			table += "B";
		else if (tableID > 34)
			table += "C";
		else if (tableID == 0)
			table = "N/A";
		else
			table += "A";
		// childHolder.table.setText("Table: " + table + " ");
		String time = children.get(arg0).get(arg1).get("time");
		if (time.equals("null")) {
			time = "None";
		} else {
			StringTokenizer st = new StringTokenizer(time);
			st.nextToken();
			time = st.nextToken();
		}

		if (status.equals("Eating")) {
			status = "Table " + table + " since " + time;
		}
		// childHolder.time.setText("Last check-in: " + time);

		childHolder.status.setText(status);
		return arg3;

	}

	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return children.get(arg0).size();
	}

	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return groups.get(arg0);
	}

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
		GroupHolder groupHolder;
		if (arg2 == null) {
			arg2 = LayoutInflater.from(mContext).inflate(R.layout.category_row,
					null);
			groupHolder = new GroupHolder();
			groupHolder.img = (ImageView) arg2.findViewById(R.id.indicator);
			groupHolder.title = (TextView) arg2.findViewById(R.id.category);
			arg2.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) arg2.getTag();
		}
		if (groupStatus[arg0] == 0) {
			groupHolder.img.setImageResource(R.drawable.expand);
		} else {
			groupHolder.img.setImageResource(R.drawable.collapse);
		}
		groupHolder.title.setText(groups.get(arg0).get("category"));

		return arg2;
	}

	class GroupHolder {
		ImageView img;
		TextView title;
	}

	class RequestChildHolder {
		ImageView img;
		TextView name;
		TextView HUID;
	}

	class PersonChildHolder {
		ImageView img;
		TextView name;
		TextView status;
		TextView table;
		TextView time;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	public Object getChild(int groupPosition, int childPosition) {
		Log.v("getChild", "If this is ever called, we're in deep shit.");
		return null;
	}

}
