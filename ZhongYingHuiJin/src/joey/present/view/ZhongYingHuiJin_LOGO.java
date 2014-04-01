package joey.present.view;

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
import android.widget.ImageButton;

import com.fx678.zhongyinghuijin.finace.R;

public class ZhongYingHuiJin_LOGO extends Activity {
	private ImageButton 	backbtn;
	private WebView 		wbLogo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.logo);
		wbLogo = (WebView) findViewById(R.id.gywm_wb);
		wbLogo.getSettings().setJavaScriptEnabled(true);

		// wbLogo.setInitialScale(75);

		WebSettings webSettings = wbLogo.getSettings();
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

		/** 页面缩放功能 */
		// wbLogo.getSettings().setSupportZoom(true);
		// wbLogo.getSettings().setBuiltInZoomControls(true);
		// wbLogo.getSettings().setUseWideViewPort(true);
		// wbLogo.getSettings().setLoadWithOverviewMode(true);

		// 点击继续停留在当前的browser中响应，而不是新开一个browser
		wbLogo.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webHtml();

		backbtn = (ImageButton) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ZhongYingHuiJin_LOGO.this.finish();

			}
		});

	}

	private void webHtml() {
		try {
			wbLogo.loadUrl("http://gz.tjbxy118.com/about.html");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 系统的back回退键重写，不会退处Activity

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wbLogo.canGoBack()) {
			wbLogo.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
