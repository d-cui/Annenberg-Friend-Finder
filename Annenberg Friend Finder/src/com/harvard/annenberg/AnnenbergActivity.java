package com.harvard.annenberg;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnnenbergActivity extends Activity {
	
	private ImageView annenbergImg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		WebView webview = new WebView(this);
		setContentView(webview);

		String data = "<body>" + "<img src=\"annenberglayout.png\"/></body>";

		webview.loadDataWithBaseURL("file:///android_asset/", data,
				"text/html", "utf-8", null);
		webview.getSettings().setBuiltInZoomControls(false);
		webview.getSettings().setSupportZoom(false);
		webview.setOnTouchListener(clickListener);
	}

	OnTouchListener clickListener = new OnTouchListener()
	{

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
		
	};
}
