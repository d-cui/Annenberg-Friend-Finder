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
	float mx, my;
	float topLeftX = 0;
	float topLeftY = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.annenberg_layout);

		annenbergImg = (ImageView) this.findViewById(R.id.annenberg__img);

		annenbergImg.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent event) {

				float curX, curY;

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					mx = event.getRawX();
					my = event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					curX = event.getRawX();
					curY = event.getRawY();
					annenbergImg.scrollBy((int) (mx - curX), (int) (my - curY));
					topLeftX += (mx - curX);
					topLeftY += (my - curY);
					mx = curX;
					my = curY;
					break;
				case MotionEvent.ACTION_UP:
					curX = event.getRawX();
					curY = event.getRawY();
					annenbergImg.scrollBy((int) (mx - curX), (int) (my - curY));
					topLeftX += (mx - curX);
					topLeftY += (my - curY);
					break;
				}

				return true;
			}
		});

	}
}
