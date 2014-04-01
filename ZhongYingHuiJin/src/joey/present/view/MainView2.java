package joey.present.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.fx678.zhongyinghuijin.finace.R;
import com.umeng.analytics.MobclickAgent;

public class MainView2 extends Activity {

	Button btn01;
	Button btn02;

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainview2);

		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);

		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_01:
			Intent in = new Intent(MainView2.this, TransView.class);
			startActivity(in);
			break;
		case R.id.about_02:
			Intent in2 = new Intent(MainView2.this, MainView.class);
			startActivity(in2);
			break;
		default:
			break;
		}
	}

}
