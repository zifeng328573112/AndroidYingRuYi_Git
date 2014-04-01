package joey.present.view;

import joey.present.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fx678.zhongyinghuijin.finace.R;

public class Login_CeLue extends Activity {
	private EditText 			ed_user, ed_pass;
	private Button 				loginButton;
	private SharedPreferences 	preferences;
	private String 				username, password;
	private Util 				util = new Util();
	private String 				reCode;
	private TextView 			tel_service;
	// 数据取得等待对话框组件
	private ProgressDialog 		progressDialog;
	// 数据取得线程
	private ProgressThread 		progressThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_celue);
		ed_user = (EditText) findViewById(R.id.ed_user);
		ed_pass = (EditText) findViewById(R.id.ed_pass);
		loginButton = (Button) findViewById(R.id.denglu);
		preferences = getSharedPreferences("jiaoyicelue", Activity.MODE_PRIVATE);
		ImageButton backbtn = (ImageButton) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});

		tel_service = (TextView) findViewById(R.id.tel_service);
		tel_service.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_DIAL);
				i.setData(Uri.parse("tel:400-6859-118"));
				startActivity(i);

			}
		});
		loginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				username = ed_user.getText().toString();
				password = ed_pass.getText().toString();

				if ("".equals(username)) {
					errorDialog("提示", "用户名不能为空!");
				} else if ("".equals(password)) {
					errorDialog("提示", "密码不能为空!");
				} else {
					// 异步版本检查
					if (isNetworkConnected(Login_CeLue.this)) {
						progressDialog = new ProgressDialog(Login_CeLue.this);
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setMessage("正在验证...");
						progressDialog.setTitle("请等待");
						progressDialog.setCancelable(true);
						progressDialog.show();
						progressThread = new ProgressThread(handler);
						progressThread.setState(1);
						progressThread.start();
					} else {
						showNoConn();
					}

				}
			}
		});

	}

	private void init() {
		username = ed_user.getText().toString().trim();
		password = ed_pass.getText().toString().trim();
		reCode = util
				.checkJinPing("http://www.tjbxy118.com/jinping/yz.aspx?username="
						+ username + "&password=" + password);

	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Dialog提示
	 */
	private void showNoConn() {
		new AlertDialog.Builder(Login_CeLue.this).setTitle("注意：")
				.setCancelable(false).setMessage("无法加载数据,请检查网络连接")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//

					}
				}).create().show();
	}

	protected void errorDialog(String title, String msg) {

		AlertDialog.Builder builder = new Builder(Login_CeLue.this);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private class ProgressThread extends Thread {
		Handler mHandler;
		int mState;

		@SuppressWarnings("unused")
		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			if (1 == mState) {
				init();
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("total", 1);
				msg.setData(b);
				mHandler.sendMessage(msg);
				return;
			}

		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
		}

	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			int total = msg.getData().getInt("total");
			if (total == 1) {
				if ("1".equals(reCode)) {
					progressDialog.dismiss();
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("OK", "1");
					editor.commit();
					Intent in = new Intent(Login_CeLue.this,
							tradingstrategyNewView.class);
					startActivity(in);
					Login_CeLue.this.finish();
				}
				if ("2".equals(reCode)) {
					progressDialog.dismiss();
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("OK", "1");
					editor.commit();
					Intent in = new Intent(Login_CeLue.this,
							tradingstrategyNewView.class);
					startActivity(in);
					Login_CeLue.this.finish();
				}
				if ("3".equals(reCode)) {
					progressDialog.dismiss();
					errorDialog("提示", "失效用户");
				}
				if ("4".equals(reCode)) {
					progressDialog.dismiss();
					errorDialog("提示", "登录失败或者用户名密码错误");
				}
			}

		}
	};
}
