package joey.present.view;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import joey.present.data.New_List_Data;
import joey.present.util.Const;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.fx678.zhongyinghuijin.finace.R;

public class InforDetailView_2 extends Activity {
	// private TextView autherText;
	// 数据取得用线程
	private ProgressThread progressThread;
	// 数据取得用dialog
	private ProgressDialog progressDialog;
	// 详细内容地址
	private String url;
	// 详细内容标题组件
	private TextView title;
	// 详细内容发表时间组件
	private TextView time;
	// 详细内容组件
	// private TextView detail;
	private WebView detail;
	// 详细内容标题
	private String titleValue;
	// 详细内容发表时间
	private String timeValue;
	// 详细内容
	private String detailValue;
	// 详细内容
	// private String author;
	// 异常语句
	private String errorMsg = "";
	// 放大缩小组件
	private ZoomControls zoomControls;
	// 论坛监听按钮
	private Button btn_i;
	// 字体大小
	private float size;
	// 字体大小默认值
	private float sizeDef;
	private String titleString;

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infordetailview_2);

		Bundle bundle = getIntent().getExtras();
		url = bundle.getString("link");
		titleString = bundle.getString("title");
		Log.i("LOG", "地址 : " + url);
		title = (TextView) findViewById(R.id.newsDetailTitle);
		time = (TextView) findViewById(R.id.newsDetailTime);
		detail = (WebView) findViewById(R.id.newsDetail);
		// autherText = (TextView) findViewById(R.id.author);
		// 放大缩小控件
		// zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		// detail.setMovementMethod(ScrollingMovementMethod.getInstance());
		// size = detail.getTextSize() - 2;
		// sizeDef = detail.getTextSize();
		// detail.setTextSize(sizeDef - 4);
		//
		time.setMovementMethod(ScrollingMovementMethod.getInstance());
		size = time.getTextSize() - 2;
		sizeDef = time.getTextSize();
		time.setTextSize(sizeDef - 4);
		//
		title.setMovementMethod(ScrollingMovementMethod.getInstance());
		size = title.getTextSize() - 2;
		sizeDef = title.getTextSize();
		title.setTextSize(sizeDef - 4);

		// 头条返回实现
		Button btn = (Button) findViewById(R.id.Btn_fanhui);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				InforDetailView_2.this.finish();
			}
		});

		// 放大缩小控件的监听实现
		// zoomControls.setOnZoomInClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// if (size <= sizeDef + 8) {
		// size = size + 2;
		// detail.setTextSize(size);
		// }
		//
		// }
		//
		// });
		// zoomControls.setOnZoomOutClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// if (size > sizeDef - 4) {
		// size = size - 2;
		// detail.setTextSize(size);
		// }
		// }
		//
		// });

		// ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		// backBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// InforDetailView.this.finish();
		// }
		//
		// });
		// detail.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);

		progressDialog = new ProgressDialog(InforDetailView_2.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		progressDialog.setTitle("请等待");
		progressDialog.setCancelable(true);
		progressDialog.show();
		progressThread = new ProgressThread(handler);
		progressThread.start();

	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			if ("".equals(errorMsg)) {
				title.setText(titleString);
				time.setText(timeValue);
				detail.loadDataWithBaseURL(null, detailValue, "text/html",
						"utf-8", null);

			} else {
				errorDialog("异常", errorMsg);
			}
		}
	};

	// 取得详细数据
	public void getDetail() throws Exception {
		// Document doc = null;
		// try {
		// URL inforUrl = new URL(url);
		// URLConnection ucon = inforUrl.openConnection();
		// DocumentBuilderFactory factory = DocumentBuilderFactory
		// .newInstance();
		// DocumentBuilder builder;
		// builder = factory.newDocumentBuilder();
		// doc = builder.parse(ucon.getInputStream());
		// } catch (Exception e) {
		// throw e;
		// }
		// titleValue = doc.getElementsByTagName("from").item(0).getFirstChild()
		// .getNodeValue();
		// timeValue = doc.getElementsByTagName("time").item(0).getFirstChild()
		// .getNodeValue();
		// // author =
		// doc.getElementsByTagName("author").item(0).getFirstChild()
		// // .getNodeValue();
		// detailValue = doc.getElementsByTagName("content").item(0)
		// .getFirstChild().getNodeValue();
		//
		// Log.i("LOG", "from : " + titleValue);
		// Log.i("LOG", "time : " + timeValue);
		// Log.i("LOG", "content : " + detailValue);

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputStream input = ucon.getInputStream();
		Document doc = builder.parse(input);
		NodeList items = doc.getElementsByTagName("news");
		Element item = (Element) items.item(0);
		titleValue = getData(item, "from");
		timeValue = getData(item, "time");
		detailValue = getData(item, "content");
		Log.i("LOG", "from : " + titleValue);
		Log.i("LOG", "time : " + timeValue);
		Log.i("LOG", "content : " + detailValue);
	}

	protected void errorDialog(String title, String msg) {

		AlertDialog.Builder builder = new Builder(InforDetailView_2.this);
		builder.setMessage(msg);
		builder.setTitle(title);

		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;

		@SuppressWarnings("unused")
		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			try {
				getDetail();
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				msg.setData(b);
				errorMsg = "";
				mHandler.sendMessage(msg);
			} catch (Exception e) {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				msg.setData(b);
				errorMsg = "无法取得数据！请稍后再试。";
				mHandler.sendMessage(msg);
			}

		}

	}

	public String getData(Element item, String key) {

		String ret = "";
		if (item.getElementsByTagName(key) != null) {
			if (item.getElementsByTagName(key).item(0) != null) {
				if (item.getElementsByTagName(key).item(0).getFirstChild() != null) {
					ret = item.getElementsByTagName(key).item(0)
							.getFirstChild().getNodeValue();
				}
			}
		}

		return ret;
	}
}
