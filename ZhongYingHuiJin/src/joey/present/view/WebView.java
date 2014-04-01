package joey.present.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.fx678.zhongyinghuijin.finace.R;
public class WebView extends Activity {
	android.webkit.WebView webview;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);
		webview = (android.webkit.WebView) findViewById(R.id.webview);
		button = (Button) findViewById(R.id.Btn_fanhui);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});
		webHtml();
	}

	// 内嵌游览器WebView
	private void webHtml() {
		try {
			webview.loadUrl("http://my.fx678.com/");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
