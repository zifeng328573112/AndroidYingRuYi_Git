package joey.present.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import joey.present.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
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

public class My_Daliog extends Activity {
	/** 数据取得对话框 */
	private ProgressDialog progressDialog;

	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse httpResponse;
	private EditText name, pass, nicheng, tel;
	private Button btn;
	private ImageButton backbtn;

	/** 注册和登录信息 */
	private String UserName;
	private String PassWord;
	private String TelPhone;
	private String NiCheng;

	// private String url_zc = "http://www.bycx118.cn:2020/chat/i_reguser?name="
	// + UserName + "&pwd=" + PassWord + "&realname=" + NiCheng
	// + "&phonenum=" + TelPhone;

	private Util util = new Util();

	/** 返回的xml结构体 */
	private Document doc = null;
	/** 返回的值 */
	private String rescode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_dialog);

		init();
	}

	private void init() {
		backbtn = (ImageButton) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});

		name = (EditText) findViewById(R.id.ed_user);
		pass = (EditText) findViewById(R.id.ed_pass);
		nicheng = (EditText) findViewById(R.id.ed_nicheng);
		tel = (EditText) findViewById(R.id.ed_tel);

		btn = (Button) findViewById(R.id.zhuce);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				LoginIn();
			}

		});

	}

	private void LoginIn() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				Message msg = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("code", "1");
				msg.setData(b);
				handler.sendMessage(msg);
				Login_info();

			}
		};
		thread.start();
	}

	public void Login_info() {

		UserName = name.getText().toString();
		PassWord = pass.getText().toString();
		NiCheng = nicheng.getText().toString();
		TelPhone = tel.getText().toString();
		String url_zc = "http://www.bycx118.cn:2020/chat/i_reguser?name="
				+ UserName + "&pwd=" + PassWord + "&realname=" + NiCheng
				+ "&phonenum=" + TelPhone;
		// http://www.bycx118.cn:2020/chat/i_checkuser?name=111&pwd=111
		HttpGet httpRequest = new HttpGet(url_zc);
		Log.i("LOG", "UserName " + UserName);
		Log.i("LOG", "PassWord " + PassWord);
		Log.i("LOG", "NiCheng " + NiCheng);
		Log.i("LOG", "TelPhone " + TelPhone);
		Log.i("LOG", "url_zc " + url_zc);
		try {
			httpResponse = httpClient.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				doc = builder.parse(httpResponse.getEntity().getContent());
				// 得到 "根节点" -- 取单节点的方法
				Element root = doc.getDocumentElement();
				rescode = root.getFirstChild().getNodeValue();
				Log.i("LOG", "中盈 : " + rescode);

				Message msg = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("code", rescode);
				msg.setData(b);
				handler.sendMessage(msg);

				// NodeList items = doc.getElementsByTagName("info");

				// for (int i = 0; i < items.getLength(); i++) {
				// Element item = (Element) items.item(i);
				//
				// rescode = util.getData(item, "login");
				//
				// // Log.i("LOG", "中盈 : " + rescode);
				//
				// }
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			String code = msg.getData().getString("code");
			if ("ok".equals(code)) {
				progressDialog.dismiss();
				AlertDialog.Builder builder = new Builder(My_Daliog.this);
				builder.setMessage("注册成功");
				builder.setTitle("提示");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setClass(My_Daliog.this,
										ZhiBoDaTing.class);
								startActivity(intent);
							}
						});
				builder.create().show();
			}
			if ("no".equals(code)) {
				progressDialog.dismiss();
				AlertDialog.Builder builder = new Builder(My_Daliog.this);
				builder.setMessage("注册失败");
				builder.setTitle("提示");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				builder.create().show();
			}
			if ("1".equals(code)) {
				progressDialog = new ProgressDialog(My_Daliog.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("取得数据...");
				progressDialog.setTitle("请等待");
				progressDialog.setCancelable(true);
				progressDialog.show();
			}

		}
	};

}
