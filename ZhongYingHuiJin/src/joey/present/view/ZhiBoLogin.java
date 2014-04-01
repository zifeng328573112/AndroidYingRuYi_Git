package joey.present.view;

import java.security.MessageDigest;

import joey.present.data.LoginBean;
import joey.present.util.Const;
import joey.present.util.Util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fx678.zhongyinghuijin.finace.R;

public class ZhiBoLogin extends Activity implements OnClickListener {
	private ImageButton 			backbtn;
	private EditText 				zhibo_account, zhibo_password;
	private Button 					zhibo_login;
	private String 					account, password;
	private Util 					util = new Util();
	private ProgressDialog 			progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
	}

	private void initView() {
		setContentView(R.layout.zhibo_login);
		backbtn 		= (ImageButton) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(this);
		zhibo_login 	= (Button) findViewById(R.id.zhibo_login);
		zhibo_login.setOnClickListener(this);
		zhibo_account 	= (EditText) findViewById(R.id.zhibo_account);
		zhibo_password 	= (EditText) findViewById(R.id.zhibo_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backbtn:
			ZhiBoLogin.this.finish();
			break;
		case R.id.zhibo_login:
			account 		= zhibo_account.getText().toString().trim();
			password 		= zhibo_password.getText().toString().trim();
			progressDialog 	= new ProgressDialog(ZhiBoLogin.this);
			progressDialog.setMessage("正在登录...");
			progressDialog.setTitle("请稍候");
			progressDialog.show();
			login();
			break;
		default:
			break;
		}
	}

	private void login() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = "http://42.121.54.222:2025/chat/i_checkuser?name=" + account + "&pwd=" + MD5(password) + "";
				Log.i("temp", "http://42.121.54.222:2025/chat/i_checkuser?name=" + account + "&pwd=" + MD5(password) + "");
				try {
					Message msg = handler.obtainMessage();
					msg.obj 	= util.getZhiBoInfo(url);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			LoginBean loginBean = (LoginBean) msg.obj;
			String code 		= loginBean.getLogin();
			String message 		= null;
			if ("100".equals(code)) {
				progressDialog.dismiss();
				message = "登录成功";
				Intent intent = new Intent(ZhiBoLogin.this, ZhiBoRoomActivity.class);
				intent.putExtra("login", loginBean.getLogin());
				intent.putExtra("realname", loginBean.getRealname());
				intent.putExtra("group", loginBean.getGroup());
				intent.putExtra("userpic", loginBean.getUserpic());
				intent.putExtra("userid", loginBean.getUserid());
				startActivity(intent);
				ZhiBoLogin.this.finish();
				
				/* 将登录数据保存在SharedPreferences中*/
				SharedPreferences preferences = getSharedPreferences(Const.CHATLOGIN, Activity.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("login", loginBean.getLogin());
				editor.putString("realname", loginBean.getRealname());
				editor.putString("group", loginBean.getGroup());
				editor.putString("userpic", loginBean.getUserpic());
				editor.putString("userid", loginBean.getUserid());
				editor.commit();
			} else if ("0".equals(code)) {
				progressDialog.dismiss();
				message = "密码错误";
			} else if ("-1".equals(code)) {
				progressDialog.dismiss();
				message = "用户名不存在";
			} else if ("-2".equals(code)) {
				progressDialog.dismiss();
				message = "过期";
			}
			Toast.makeText(ZhiBoLogin.this, message, Toast.LENGTH_LONG).show();
		};
	};

	// MD5加密，32位
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray 		= str.toCharArray();
		byte[] byteArray 		= new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] 		= (byte) charArray[i];
		}
		byte[] md5Bytes 		= md5.digest(byteArray);
		StringBuffer hexValue 	= new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
