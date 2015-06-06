package com.example.guesturedemo;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {

	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	public static String TAG = "guesturedemo";
	DisplayMetrics diaplym;
	public static int img = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView image = (ImageView) findViewById(R.id.image);

		image.setOnTouchListener(this);
		// diaplym = getResources().getDisplayMetrics();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		ImageView image = (ImageView) v;
		image.setScaleType(ScaleType.MATRIX);

		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			// Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			// Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				// Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
			mode = NONE;
//			matrix = new Matrix();
//			savedMatrix = new Matrix();
//			image.setScaleType(ScaleType.FIT_XY);
//			if (img == 0) {
//				img = 1;
//				image.setImageResource(R.drawable.newpic_n);
//
//			} else {
//				img = 0;
//				image.setImageResource(R.drawable.newpic);
//
//			}

		case MotionEvent.ACTION_POINTER_UP:
			mode = DRAG;
			// Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				// Log.d(TAG, "newDist=" + newDist);

				if (newDist > 10f) {
					// matrix.set(savedMatrix);
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					// float cm = getRealCm(newDist, diaplym);
					// Log.d(TAG, String.valueOf(cm));
					// matrix.postScale(scale, scale, mid.x, mid.y);

					matrix.postScale(scale, scale, mid.x, mid.y);

				}
			}
			break;
		}

		image.setImageMatrix(matrix);
		return true; // indicate event was handled
	}

	public static float getRealCm(float pixels, DisplayMetrics displayMetrics) {
		float dpi = (float) displayMetrics.densityDpi;

		float inches = pixels / dpi;

		return inches * 2.54f; // inches to cm
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
