package joey.present.view;

import com.google.code.microlog4android.config.PropertyConfigurator;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import com.fx678.zhongyinghuijin.finace.R;
public class LogoView extends Activity {

	//启动图片组件
	private ImageView logoImage;
	//主画面handler
	private Handler handler;
	//透明值
	private int alpha = 255;
	private int reflash = 0;
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		PropertyConfigurator.getConfigurator(this).configure();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.logoview);
		initView();
		new Thread(new Runnable() {
            public void run() {
                
                while (reflash < 2) {
                    try {
                        if (reflash == 0) {
                            Thread.sleep(1000);
                            reflash = 1;
                        } else {
                            Thread.sleep(50);
                        }
                        updateLogo();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
	}

	private void initView() {
		logoImage = (ImageView) findViewById(R.id.logoImageView);
		logoImage.setAlpha(alpha);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				logoImage.setAlpha(alpha);
				logoImage.invalidate();
			}
		};
	}

	public void updateLogo() {
		alpha -= 5;
		if (alpha <= 0) {
			reflash = 2;
			Intent in = new Intent(this, StartView.class);
			Bundle bundle = new Bundle();
			bundle.putString("started", "");
			in.putExtras(bundle);
			startActivity(in);
			finish();
		}
		handler.sendMessage(handler.obtainMessage());
	}
}