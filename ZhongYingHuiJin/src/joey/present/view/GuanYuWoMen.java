package joey.present.view;

import com.fx678.zhongyinghuijin.finace.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class GuanYuWoMen extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guanyuwomen);
	}
}
