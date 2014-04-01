package joey.present.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import com.fx678.zhongyinghuijin.finace.R;

public class Item_LOGO extends Activity {
	private ImageButton backbtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.item_logo);
		backbtn = (ImageButton) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Item_LOGO.this.finish();
			}
		});
	}
}
