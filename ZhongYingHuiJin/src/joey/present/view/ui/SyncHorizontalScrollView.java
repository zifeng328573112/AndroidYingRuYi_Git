﻿package joey.present.view.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;

public class SyncHorizontalScrollView extends HorizontalScrollView {
	private View mView;

	public SyncHorizontalScrollView(Context context) {
		super(context);
	}

	public SyncHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mView != null) {
			mView.scrollTo(l, t);
		}
	}
	public void setScrollView(View view) {
		mView = view;
	}
}
