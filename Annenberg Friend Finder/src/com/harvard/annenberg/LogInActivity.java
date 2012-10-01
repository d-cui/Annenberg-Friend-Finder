package com.harvard.annenberg;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
public class LogInActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in);
		Button logInButton = (Button) findViewById(R.id.login_go);
		logInButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				boolean validLogin = true;
				if(validLogin){
					
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in, menu);
		return true;
	}
}
