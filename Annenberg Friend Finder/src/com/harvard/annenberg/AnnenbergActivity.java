package com.harvard.annenberg;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
	float mx;
	float my;
	float topLeftX;
	float topLeftY;
	int imgWidth;
	int imgHeight;
	int screenWidth;
	int screenHeight;

	float beginTapX;
	float beginTapY;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mx = 0.0f;
		my = 0.0f;
		topLeftX = 0.0f;
		topLeftY = 0.0f;
		setContentView(R.layout.annenberg_layout);
		DisplayMetrics m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		annenbergImg = (ImageView) this.findViewById(R.id.annenberg__img);
		imgWidth = this.getResources().getDrawable(R.drawable.annenberglayout)
				.getIntrinsicWidth() + 50;
		imgHeight = this.getResources().getDrawable(R.drawable.annenberglayout)
				.getIntrinsicHeight() + 50;
		screenWidth = m.widthPixels;
		screenHeight = m.heightPixels;
		annenbergImg.scrollBy(-imgWidth / 2 + screenWidth / 2, -imgHeight / 2
				+ screenHeight / 2);
		annenbergImg.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent event) {

				float curX, curY;

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					mx = event.getX();
					my = event.getY();
					beginTapX = mx;
					beginTapY = my;
					break;
				case MotionEvent.ACTION_MOVE:
					curX = event.getX();
					curY = event.getY();
					annenbergImg.scrollBy((int) (mx - curX), (int) (my - curY));
					topLeftX += (mx - curX);
					topLeftY += (my - curY);
					mx = curX;
					my = curY;
					break;
				case MotionEvent.ACTION_UP:

					curX = event.getX();
					curY = event.getY();
					annenbergImg.scrollBy((int) (mx - curX), (int) (my - curY));
					topLeftX += (mx - curX);
					topLeftY += (my - curY);
					if (topLeftX < 0) {
						annenbergImg.scrollBy((int) -topLeftX, 0);
						topLeftX = 0;
					}
					if (topLeftY < 0) {
						annenbergImg.scrollBy(0, (int) -topLeftY);
						topLeftY = 0;
					}
					if (topLeftY + screenHeight > imgHeight) {
						annenbergImg.scrollBy(0, (int) (imgHeight
								- screenHeight - topLeftY));
						topLeftY = imgHeight - screenHeight;
					}
					if (topLeftX + screenWidth > imgWidth) {
						annenbergImg.scrollBy(
								(int) (imgWidth - screenWidth - topLeftX), 0);
						topLeftX = imgWidth - screenWidth;
					}
					if (Math.abs(beginTapX - curX) < 10
							&& Math.abs(beginTapY - curY) < 10) {
						int absoluteX = (int) (topLeftX + curX);
						int absoluteY = (int) (topLeftY + curY);
						int tableId = 0;
						if (absoluteX > imgWidth || absoluteY > imgHeight) {
							break;
						}
						if (absoluteX < 250 && absoluteX > 80) {
							// A column
						} else if (absoluteX < 450 && absoluteX > 280) {
							// B column
							tableId += 17;
						} else if (absoluteX < 650 && absoluteX > 480) {
							// C column
							tableId += 34;
						} else {
							break;
						}

						if (absoluteY < 85 && absoluteY > 35) {
							tableId += 1;
						} else if (absoluteY < 165 && absoluteY > 115) {
							tableId += 2;
						} else if (absoluteY < 235 && absoluteY > 185) {
							tableId += 3;
						} else if (absoluteY < 335 && absoluteY > 285) {
							tableId += 4;
						} else if (absoluteY < 410 && absoluteY > 360) {
							tableId += 5;
						} else if (absoluteY < 510 && absoluteY > 460) {
							tableId += 6;
						} else if (absoluteY < 585 && absoluteY > 535) {
							tableId += 7;
						} else if (absoluteY < 660 && absoluteY > 610) {
							tableId += 8;
						} else if (absoluteY < 735 && absoluteY > 685) {
							tableId += 9;
						} else if (absoluteY < 845 && absoluteY > 795) {
							tableId += 10;
						} else if (absoluteY < 925 && absoluteY > 875) {
							tableId += 11;
						} else if (absoluteY < 1005 && absoluteY > 955) {
							tableId += 12;
						} else if (absoluteY < 1075 && absoluteY > 1025) {
							tableId += 13;
						} else if (absoluteY < 1175 && absoluteY > 1125) {
							tableId += 14;
						} else if (absoluteY < 1250 && absoluteY > 1200) {
							tableId += 15;
						} else if (absoluteY < 1330 && absoluteY > 1280) {
							tableId += 16;
						} else if (absoluteY < 1390 && absoluteY > 1340) {
							tableId += 17;
						} else {
							break;
						}

						// DO SOMETING WITH TABLE CLICK

					}
					break;
				}

				return true;
			}
		});

	}
}
