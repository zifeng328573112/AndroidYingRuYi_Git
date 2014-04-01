package joey.present.view;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import joey.present.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fx678.zhongyinghuijin.finace.R;

public class ZhiBoDaTing extends Activity {
	private HttpClient 			httpClient = new DefaultHttpClient();
	private HttpResponse 		httpResponse;
	private ImageButton 		backbtn;
	private Button 				denglu;
	private Button 				zhuce;
	private Button 				zhijiejinru;
	private EditText 			user_name;
	private EditText 			pass_word;

	/** 注册和登录信息 */
	private String 				UserName;
	private String 				PassWord;
	private String 				TelPhone;
	private String 				NiCheng;

	/** 注册和登录验证地址 */
	// private String url_dl =
	// "http://www.bycx118.cn:2020/chat/i_checkuser?name="  + UserName + "+&pwd=" + PassWord;
	// private String url_zc = "http://www.bycx118.cn:2020/chat/i_reguser?name="  + UserName + "&pwd=" + PassWord + "&realname=" + NiCheng
	// + "&phonenum=" + TelPhone;

	private Util 				util = new Util();
	/** 返回的xml结构体 */
	private Document 			doc = null;
	// <login>yes</login>
	// <realname>姓名</realname>
	// <group>权限组</group>
	/** 返回值 -- login */
	private String 				login;
	/** 返回值 -- 姓名 */
	private String 				realname;
	/** 返回值 -- 权限组 */
	private String group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zhibodating);
		user_name = (EditText) findViewById(R.id.ed_user);
		pass_word = (EditText) findViewById(R.id.ed_pass);
		init();
	}

	/** 初始化按钮 */
	private void init() {
		backbtn = (ImageButton) findViewById(R.id.backbtn);
		backbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		/** 登录 */
		denglu = (Button) findViewById(R.id.denglu);
		denglu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Thread thread = new Thread() {
					@Override
					public void run() {
						super.run();
						Login_info();
					}
				};
				thread.start();
			}
		});
		/** 注册 */
		zhuce = (Button) findViewById(R.id.zhuce);
		zhuce.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ZhiBoDaTing.this, My_Daliog.class);
				startActivity(intent);
			}
		});
		/** 直接进入 */
		zhijiejinru = (Button) findViewById(R.id.zhijiejinru);
		zhijiejinru.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
	}

	public void Login_info() {
		UserName = user_name.getText().toString();
		PassWord = pass_word.getText().toString();
		Log.i("LOG", UserName);
		Log.i("LOG", PassWord);
		/** 注册和登录验证地址 */
		String url_dl = "http://www.bycx118.cn:2020/chat/i_checkuser?name=" + UserName + "&pwd=" + PassWord;
		HttpGet httpRequest = new HttpGet(url_dl);
		Log.i("LOG", url_dl);
		try {
			httpResponse = httpClient.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				doc = builder.parse(httpResponse.getEntity().getContent());

				NodeList items = doc.getElementsByTagName("info");
				for (int i = 0; i < items.getLength(); i++) {
					Element item 	= (Element) items.item(i);

					login 			= util.getData(item, "login");
					realname 		= util.getData(item, "realname");
					group 			= util.getData(item, "group");

					Log.i("LOG", "-->" + login);
					Log.i("LOG", "-->" + realname);
					Log.i("LOG", "-->" + group);

				}
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
}
