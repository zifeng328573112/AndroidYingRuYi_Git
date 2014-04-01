package joey.present.view;

import com.fx678.zhongyinghuijin.finace.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class AnaltstsWebView extends Activity {
	private WebView 		awebview;
	private Button 			abutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysts);
		awebview = (WebView) findViewById(R.id.analystsWebView);
		// 支持网页有js
		awebview.getSettings().setJavaScriptEnabled(true);

		// awebview.setInitialScale(75);

		WebSettings webSettings = awebview.getSettings();
		// 设置密度
		int screenDensity = getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		switch (screenDensity) {
		case DisplayMetrics.DENSITY_LOW:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			zoomDensity = WebSettings.ZoomDensity.FAR;
			break;
		}
		webSettings.setDefaultZoom(zoomDensity);

		// 点击继续停留在当前的browser中响应，而不是新开一个browser
		awebview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		abutton = (Button) findViewById(R.id.Btn_fanhui);
		abutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();

			}
		});
		webHtml();
	}

	private void webHtml() {
		try {
			awebview.loadUrl("http://gz.tjbxy118.com/yuyue.aspx");
			// awebview.loadUrl("http://weibo.cn/pjpm");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 系统的back回退键重写，不会退处Activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && awebview.canGoBack()) {
			awebview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
