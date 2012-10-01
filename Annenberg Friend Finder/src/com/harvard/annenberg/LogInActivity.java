package com.harvard.annenberg;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LogInActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in);
		// COMMENT
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in, menu);
		return true;
	}
}
