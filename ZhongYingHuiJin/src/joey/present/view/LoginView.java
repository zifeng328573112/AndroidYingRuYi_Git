package joey.present.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.fx678.zhongyinghuijin.finace.R;
public class LoginView extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loginview);
		
		
		}
}
