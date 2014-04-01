package joey.present.view;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import joey.present.data.KData;
import joey.present.data.PriceData;
import joey.present.util.Const;
import joey.present.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fx678.zhongyinghuijin.finace.R;

public class KLineView extends Activity {

	// private DatagramSocket client;
	// private DatagramPacket recpacket;
	// private DatagramPacket sendpacket;

	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df2 = new DecimalFormat("0.00");

	private String log;
	private int klinesize = 45;
	/** 本地配置保存对象*/
	private SharedPreferences preferences;
	private ImageButton addBtn;
	private static final String TYPE1M = "1m";
	private static final String TYPE5M = "5m";
	private static final String TYPE15M = "15m";
	private static final String TYPE30M = "30m";
	private static final String TYPE60M = "60m";
	private static final String TYPE1D = "1d";
	private static final String TYPE1W = "1w";
	private static final String TYPE1MON = "1mon";

	private static final long TYPE1MTIME = 60;
	private static final long TYPE5MTIME = 300;
	private static final long TYPE15MTIME = 900;
	private static final long TYPE30MTIME = 1800;
	private static final long TYPE60MTIME = 3600;
	private static final long TYPETIME = 36000000;

	// private ImageButton searchBtn;
	private int nowColor = Color.WHITE;
	private String code;
	private String ex;
	private String name;
	private String type = TYPE1D;
	private long timeforcheck = TYPE1MTIME;
	private TextView detailname;
	private LinearLayout klineLayout;
	/** 当前数据*/
	private PriceData timeNow;

	private List<KData> klineList;

	private KData lastUpdateData = null;

	private float lastClose = 0;// 最后收盘价

	private TextView nowvalue;
	private TextView updownpecent;
	private TextView updownvalue;
	private TextView cjvalue;
	private TextView openvalue;
	private TextView closevalue;
	private TextView highvalue;
	private TextView lowvalue;
	private TextView dayvalue;

	private ImageButton increase;
	private ImageButton narrow;

	private Button indexBtn;
	private Button typedayBtn;
	private Button typeweekBtn;
	private Button typemonBtn;
	// private Button type1mBtn;
	private Button type5mBtn;
	private Button type15mBtn;
	private Button type30mBtn;
	private Button type60mBtn;
	/** 最后选择类别按钮*/
	private Button lastSelectBtn;

	/** 数据取得等待对话框组件*/
	private ProgressDialog progressDialog;

	/** 数据取得线程*/
	private ProgressThread progressThread;

	/** 自动更新线程*/
	private AutoUpdateThread autoUpdateThread;

	/** 自动发送active*/
	private SendActiveThread sendActiveThread;

	private KLineDraw kLineDraw;

	/** 自动更新线程启动标志*/
	private boolean running = false;

	private Util util = new Util();

	private String errorMsg;

	protected void onPause() {
		super.onPause();
		running = false;
		autoUpdateThread = null;
	}

	protected void onResume() {
		super.onResume();
		autoUpdateThread = new AutoUpdateThread(handler);
		running = true;
		autoUpdateThread.start();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.klineview);
		Bundle bundle = getIntent().getExtras();
		code = bundle.getString("code");
		ex = bundle.getString("ex");
		name = bundle.getString("name");
		// searchBtn = (ImageButton) findViewById(R.id.search);
		detailname = (TextView) findViewById(R.id.detailname);
		detailname.setText(name);

		preferences = getSharedPreferences(Const.PREFERENCES_NAME,
				Activity.MODE_PRIVATE);
		addBtn = (ImageButton) findViewById(R.id.addbtn);
		addBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				addDialog();
			}
		});
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
		/** 初始化周期按钮*/
		initButton();

		klineLayout 		= (LinearLayout) findViewById(R.id.klinelayout);
		nowvalue 			= (TextView) findViewById(R.id.nowvalue);
		updownpecent 		= (TextView) findViewById(R.id.updownpecent);
		updownvalue 		= (TextView) findViewById(R.id.updownvalue);
		cjvalue 			= (TextView) findViewById(R.id.cjvalue);
		openvalue 			= (TextView) findViewById(R.id.openvalue);
		closevalue 			= (TextView) findViewById(R.id.closevalue);
		highvalue 			= (TextView) findViewById(R.id.highvalue);
		lowvalue 			= (TextView) findViewById(R.id.lowvalue);
		dayvalue 			= (TextView) findViewById(R.id.dayvalue);
		DisplayMetrics dm 	= new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		kLineDraw 			= new KLineDraw(KLineView.this, dm.widthPixels, dm.widthPixels, ex);
		klineLayout.addView(kLineDraw);
		progressDialog 		= new ProgressDialog(KLineView.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		progressDialog.setTitle("请等待");
		progressDialog.setCancelable(true);
		progressDialog.show();
		progressThread 		= new ProgressThread(handler);
		progressThread.setState(1);
		progressThread.start();
		try {
			/* client = new DatagramSocket();
			 String sendStr = String.format(Const.UDP_PARA + code);
			 byte[] sendBuf = sendStr.getBytes();
			 sendBuf = sendStr.getBytes();
			 InetAddress address = InetAddress.getByName(Const.UDP_IP);
			 sendpacket = new DatagramPacket(sendBuf, sendBuf.length, address,
			 Const.UDP_PORT);
			 client.send(sendpacket);
			 byte[] recBuf = new byte[490];
			 recpacket = new DatagramPacket(recBuf, recBuf.length);*/
			autoUpdateThread = new AutoUpdateThread(handler);
			running = true;
			autoUpdateThread.start();
		} catch (Exception e) {

		}
	}

	private void initButton() {
		increase = (ImageButton) findViewById(R.id.increase);
		increase.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				klinesize -= 9;
				if (klinesize <= 27) {
					klinesize = 27;
				}
				kLineDraw.updateData(klineList, klinesize);
			}

		});
		narrow = (ImageButton) findViewById(R.id.narrow);
		narrow.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				klinesize += 9;
				if (klinesize >= 153) {
					klinesize = 153;
				}
				kLineDraw.updateData(klineList, klinesize);
			}

		});
		typedayBtn = (Button) findViewById(R.id.daytype);
		lastSelectBtn = typedayBtn;
		lastSelectBtn.setBackgroundResource(R.drawable.orangebut);
		typedayBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				type = TYPE1D;
				timeforcheck = TYPETIME;
				if (!btn.equals(lastSelectBtn)) {
					running = false;
					btn.setBackgroundResource(R.drawable.orangebut);
					lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
					progressDialog = new ProgressDialog(KLineView.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("取得数据...");
					progressDialog.setTitle("请等待");
					progressDialog.setCancelable(true);
					progressDialog.show();
					progressThread = new ProgressThread(handler);
					progressThread.setState(1);
					progressThread.start();
					lastSelectBtn = btn;
				}
			}
		});
		typeweekBtn = (Button) findViewById(R.id.weektype);
		typeweekBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				type = TYPE1W;
				timeforcheck = TYPETIME;
				if (!btn.equals(lastSelectBtn)) {
					running = false;
					btn.setBackgroundResource(R.drawable.orangebut);
					lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
					progressDialog = new ProgressDialog(KLineView.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("取得数据...");
					progressDialog.setTitle("请等待");
					progressDialog.setCancelable(true);
					progressDialog.show();
					progressThread = new ProgressThread(handler);
					progressThread.setState(1);
					progressThread.start();
					lastSelectBtn = btn;
				}
			}
		});

		// 月线
		typemonBtn = (Button) findViewById(R.id.montype);
		typemonBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				type = TYPE1MON;
				timeforcheck = TYPETIME;
				try {
					if (!btn.equals(lastSelectBtn)) {
						running = false;
						btn.setBackgroundResource(R.drawable.orangebut);
						lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
						progressDialog = new ProgressDialog(KLineView.this);
						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setMessage("取得数据...");
						progressDialog.setTitle("请等待");
						progressDialog.setCancelable(true);
						progressDialog.show();
						progressThread = new ProgressThread(handler);
						progressThread.setState(1);
						progressThread.start();
						lastSelectBtn = btn;
					}
				} catch (Exception e) {
					Toast.makeText(KLineView.this, "暂无数据", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
	/*	 type1mBtn = (Button) findViewById(R.id.type1m);
		 type1mBtn.setOnClickListener(new OnClickListener() {
		
			 public void onClick(View v) {
				 Button btn = (Button) v;
				 type = TYPE1M;
				 timeforcheck = TYPE1MTIME;
				 if (!btn.equals(lastSelectBtn)) {
					 running = false;
					 btn.setBackgroundResource(R.drawable.orangebut);
					 lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
					 progressDialog = new ProgressDialog(KLineView.this);
					 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					 progressDialog.setMessage("取得数据...");
					 progressDialog.setTitle("请等待");
					 progressDialog.setCancelable(true);
					 progressDialog.show();
					 progressThread = new ProgressThread(handler);
					 progressThread.setState(1);
					 progressThread.start();
					 lastSelectBtn = btn;
				 }
			 }
		 });*/
		type5mBtn = (Button) findViewById(R.id.type5m);
		type5mBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				type = TYPE5M;
				timeforcheck = TYPE5MTIME;
				if (!btn.equals(lastSelectBtn)) {
					running = false;
					btn.setBackgroundResource(R.drawable.orangebut);
					lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
					progressDialog = new ProgressDialog(KLineView.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("取得数据...");
					progressDialog.setTitle("请等待");
					progressDialog.setCancelable(true);
					progressDialog.show();
					progressThread = new ProgressThread(handler);
					progressThread.setState(1);
					progressThread.start();
					lastSelectBtn = btn;
				}
			}
		});
		type15mBtn = (Button) findViewById(R.id.type15m);
		type15mBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				type = TYPE15M;
				timeforcheck = TYPE15MTIME;
				if (!btn.equals(lastSelectBtn)) {
					running = false;
					btn.setBackgroundResource(R.drawable.orangebut);
					lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
					progressDialog = new ProgressDialog(KLineView.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("取得数据...");
					progressDialog.setTitle("请等待");
					progressDialog.setCancelable(true);
					progressDialog.show();
					progressThread = new ProgressThread(handler);
					progressThread.setState(1);
					progressThread.start();
					lastSelectBtn = btn;
				}
			}
		});
		type30mBtn = (Button) findViewById(R.id.type30m);
		type30mBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				type = TYPE30M;
				timeforcheck = TYPE30MTIME;
				if (!btn.equals(lastSelectBtn)) {
					running = false;
					btn.setBackgroundResource(R.drawable.orangebut);
					lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
					progressDialog = new ProgressDialog(KLineView.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("取得数据...");
					progressDialog.setTitle("请等待");
					progressDialog.setCancelable(true);
					progressDialog.show();
					progressThread = new ProgressThread(handler);
					progressThread.setState(1);
					progressThread.start();
					lastSelectBtn = btn;
				}
			}
		});
		type60mBtn = (Button) findViewById(R.id.type60m);
		type60mBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				type = TYPE60M;
				timeforcheck = TYPE60MTIME;
				if (!btn.equals(lastSelectBtn)) {
					running = false;
					btn.setBackgroundResource(R.drawable.orangebut);
					lastSelectBtn.setBackgroundResource(R.drawable.graybluebut);
					progressDialog = new ProgressDialog(KLineView.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("取得数据...");
					progressDialog.setTitle("请等待");
					progressDialog.setCancelable(true);
					progressDialog.show();
					progressThread = new ProgressThread(handler);
					progressThread.setState(1);
					progressThread.start();
					lastSelectBtn = btn;
				}
			}
		});
		indexBtn = (Button) findViewById(R.id.indexSel);
		indexBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				final String[] items = getResources().getStringArray(R.array.typeitem);
				new AlertDialog.Builder(KLineView.this).setTitle("请选择指标").setItems(items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
								case 0:
									kLineDraw.setIndex(Const.INDEX_MACD);
									return;
								case 1:
									kLineDraw.setIndex(Const.INDEX_VOL);
									return;
								case 2:
									kLineDraw.setIndex(Const.INDEX_RSI);
									return;
								case 3:
									kLineDraw.setIndex(Const.INDEX_BOLL);
									return;
								case 4:
									kLineDraw.setIndex(Const.INDEX_KDJ);
									return;
								case 5:
									kLineDraw.setIndex(Const.INDEX_OBV);
									return;
								case 6:
									kLineDraw.setIndex(Const.INDEX_CCI);
									return;
								case 7:
									kLineDraw.setIndex(Const.INDEX_PSY);
									return;
								default:
									;
								}
							}
						}).show();
			}
		});
	}

	/* public void showData() {
		 kLineDraw.setInitFlag(true);
		 kLineDraw.updateData(klineList);
		 nowvalue.setText(timeNow.getPrice_last());
		 updownpecent.setText(timeNow.getPrice_updownrate());
		 updownvalue.setText(timeNow.getPrice_updown());
		 cjvalue.setText(timeNow.getPrice_volume());
		 openvalue.setText(timeNow.getPrice_open());
		 closevalue.setText(timeNow.getPrice_close());
		 highvalue.setText(timeNow.getPrice_high());
		 lowvalue.setText(timeNow.getPrice_low());
		 dayvalue.setText(timeNow.getPrice_quotetime());
	 }*/

	public void showNew() {
		progressDialog.dismiss();
		if (lastClose != 0) {
			float updown = util.getFloat(timeNow.getPrice_last()) - lastClose;
			float updownrate = updown / lastClose * 100;
			if (Const.WH4.equals(ex) && !Const.USD.equals(timeNow.getPrice_code()) && !Const.USDJPY.equals(timeNow.getPrice_code())) {
				timeNow.setPrice_updown(String.valueOf(df4.format(updown)));
			} else {
				timeNow.setPrice_updown(String.valueOf(df2.format(updown)));
			}
			timeNow.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
			timeNow.setPrice_lastclose(String.valueOf(lastClose));
		}
		// 详细数据更新
		if ((util.getFloat(timeNow.getPrice_last()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			nowvalue.setTextColor(Color.BLACK);
			nowvalue.setBackgroundColor(Color.RED);
			nowColor = Color.RED;
			updownpecent.setTextColor(Color.RED);
			updownvalue.setTextColor(Color.RED);
		} else {
			nowvalue.setBackgroundColor(Color.GREEN);
			nowvalue.setTextColor(Color.BLACK);
			nowColor = Color.RED;
			updownpecent.setTextColor(Color.GREEN);
			updownvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_last()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			nowvalue.setTextColor(Color.WHITE);
			nowColor = Color.WHITE;
			updownpecent.setTextColor(Color.WHITE);
			updownvalue.setTextColor(Color.WHITE);
		}
		nowvalue.setText(timeNow.getPrice_last());
		updownpecent.setText(timeNow.getPrice_updownrate() + "%");
		updownvalue.setText(timeNow.getPrice_updown());
		cjvalue.setTextColor(Color.YELLOW);
		String turnovertmp = timeNow.getPrice_turnover();
		float turnovertmpvalue = util.getFloat(turnovertmp) / 10000;
		turnovertmpvalue = (float) Math.round(turnovertmpvalue * 100) / 100;
		cjvalue.setText(String.valueOf(turnovertmpvalue) + "万");
		if ((util.getFloat(timeNow.getPrice_open()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			openvalue.setTextColor(Color.RED);
		} else {
			openvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_open()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			openvalue.setTextColor(Color.WHITE);
		}
		openvalue.setText(timeNow.getPrice_open());
		closevalue.setText(String.valueOf(lastClose));
		if ((util.getFloat(timeNow.getPrice_high()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			highvalue.setTextColor(Color.RED);
		} else {
			highvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_high()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			highvalue.setTextColor(Color.WHITE);
		}
		highvalue.setText(timeNow.getPrice_high());
		if ((util.getFloat(timeNow.getPrice_low()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			lowvalue.setTextColor(Color.RED);
		} else {
			lowvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_low()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			lowvalue.setTextColor(Color.WHITE);
		}
		lowvalue.setText(timeNow.getPrice_low());
		dayvalue.setText(util.formatTimeYD(timeNow.getPrice_quotetime()));

		if (klineList != null && klineList.size() > 0) {
			KData temp = null;
			if (lastUpdateData != null) {
				temp = lastUpdateData;
			} else {
				temp = klineList.get(klineList.size() - 1);
			}
			if (!temp.getK_close().equals(timeNow.getPrice_last())) {

				if (util.getFloat(timeNow.getPrice_quotetime()) > util
						.getFloat(temp.getK_timeLong())) {

					// 最后一根K线 暂时取消
					// KData lastPoint = new KData();
					// lastPoint.setK_close(timeNow.getPrice_last());

					/* if (!TYPE1D.equals(type) && !TYPE1W.equals(type) && !TYPE1MON.equals(type)) {
						 lastPoint.setK_date(util.formatTimeSec(String.valueOf(Long.valueOf(temp.getK_timeLong()) + TYPE5MTIME)));
						 lastPoint.setK_timeLong(String.valueOf(Long.valueOf(temp.getK_timeLong()) + TYPE5MTIME));
					 } else {
						 lastPoint.setK_date("现价 现价");
						 lastPoint.setK_timeLong(String.valueOf(Long.valueOf(temp.getK_timeLong()) + TYPE5MTIME));
					 }*/

					/* // temp.setK_timeLong(timeNow.getPrice_quotetime());
					 lastPoint.setK_open(temp.getK_open());
					 if (util.getFloat(timeNow.getPrice_last()) > util.getFloat(temp.getK_high())) {
						 lastPoint.setK_high(timeNow.getPrice_last());
					 } else {
						 lastPoint.setK_high(temp.getK_high());
					 }
					 if (util.getFloat(timeNow.getPrice_last()) < util.getFloat(temp.getK_low())) {
						 lastPoint.setK_low(timeNow.getPrice_last());
					 } else {
						 lastPoint.setK_low(temp.getK_low());
					 }
					 klineList.add(lastPoint);
					 lastUpdateData = lastPoint;*/
				} else {
					temp.setK_close(timeNow.getPrice_last());
					// 这里不是最后一根K线的数据
					// temp.setK_date(util.formatTimeSec(timeNow
					// .getPrice_quotetime()));
					// temp.setK_timeLong(timeNow.getPrice_quotetime());
					temp.setK_open(temp.getK_open());
					if (util.getFloat(timeNow.getPrice_last()) > util.getFloat(temp.getK_high())) {
						temp.setK_high(timeNow.getPrice_last());
					} else {
						temp.setK_high(temp.getK_high());
					}
					if (util.getFloat(timeNow.getPrice_last()) < util.getFloat(temp.getK_low())) {
						temp.setK_low(timeNow.getPrice_last());
					} else {
						temp.setK_low(temp.getK_low());
					}
					lastUpdateData = temp;
				}
			}
			/* klineList.add(lastPoint);
			 temp.setK_volume(timeNow.getPrice_volume());
			 temp.setK_total(timeNow.getPrice_total());
			 if (lastUpdateData != null) {
			 	if (util.getFloat(timeNow.getPrice_quotetime()) - util.getFloat(lastUpdateData.getK_timeLong()) > timeforcheck) {
			 		lastUpdateData.setK_close(timeNow.getPrice_last());
			 		lastUpdateData.setK_date(util.formatTimeSec(timeNow.getPrice_quotetime()));
			 		lastUpdateData.setK_timeLong(timeNow.getPrice_quotetime());
			 		lastUpdateData.setK_high(timeNow.getPrice_high());
			 		lastUpdateData.setK_low(timeNow.getPrice_low());
			 		lastUpdateData.setK_open(timeNow.getPrice_open());
			 		lastUpdateData.setK_volume(timeNow.getPrice_volume());
			 		lastUpdateData.setK_total(timeNow.getPrice_total());
			 // klineList.add(lastUpdateData);
			 	} else {
			 	}
			 } else {
			 	lastUpdateData = new KData();
			 	lastUpdateData.setK_close(timeNow.getPrice_last());
			 	lastUpdateData.setK_date(util.formatTimeSec(timeNow.getPrice_quotetime()));
			 	lastUpdateData.setK_timeLong(timeNow.getPrice_quotetime());
			 	lastUpdateData.setK_high(timeNow.getPrice_high());
			 	lastUpdateData.setK_low(timeNow.getPrice_low());
			 	lastUpdateData.setK_open(timeNow.getPrice_open());
			 	lastUpdateData.setK_volume(timeNow.getPrice_volume());
			 	lastUpdateData.setK_total(timeNow.getPrice_total());
			 // klineList.add(lastUpdateData);
			 }*/
			kLineDraw.setInitFlag(true);
			kLineDraw.updateData(klineList, klinesize);
		}
	}

	private synchronized void updateData(String code) {
		lastUpdateData = null;
		String listDataURL = Const.URL_KDATA;
		listDataURL = listDataURL.replaceFirst(Const.URL_EX, ex);
		listDataURL = listDataURL.replaceFirst(Const.URL_CODE, code);
		listDataURL = listDataURL.replaceFirst(Const.URL_TYPE, type);
		listDataURL = listDataURL.replaceFirst(Const.URL_COUNT, "180");
		klineList = util.getKData(listDataURL);
		String timeNowURL = Const.URL_NOW;
		timeNowURL = timeNowURL.replaceFirst(Const.URL_EX, ex);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_CODE, code);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowURL = timeNowURL.replaceFirst(Const.URL_COUNT, "3");
		timeNow = util.getTimeNow(timeNowURL, ex);
		lastClose = util.getFloat(timeNow.getPrice_lastclose());
		if (Const.AUT_D.equals(code) || Const.AGT_D.equals(code)) {
			lastClose = util.getFloat(timeNow.getPrice_lastsettle());
		}
		if (lastClose == 0) {
			lastClose = util.getFloat(timeNow.getPrice_lastclose());
			if (Const.AUT_D.equals(code) || Const.AGT_D.equals(code)) {
				lastClose = util.getFloat(timeNow.getPrice_lastsettle());
			}
			kLineDraw.setLastClose(lastClose);
		}
		kLineDraw.setCode(code);
		running = true;
	}

	// 自动发送active
	/** Nested class that performs progress calculations (counting) */
	private class SendActiveThread extends Thread {
		private long waitTime = 5000;

		public void run() {
			while (running) {
				try {
					SendActiveThread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("total", 7);
				msg.setData(b);
				errorMsg = "";
				handler.sendMessage(msg);
			}
		}

	}

	// 自动更新线程
	/** Nested class that performs progress calculations (counting) */
	private class AutoUpdateThread extends Thread {
		Handler mHandler;
		final static long AUTO1 = 10000;
		private long waitTime = 10000;

		AutoUpdateThread(Handler h) {
			mHandler = h;
		}

		public void setAutoTime(String autoTime) {
			if ("1".equals(autoTime)) {
				waitTime = AUTO1;
			}
		}

		public void run() {
			while (running) {
				try {
					AutoUpdateThread.sleep(waitTime);
				} catch (InterruptedException e) {

				}
				progressThread = new ProgressThread(handler);
				progressThread.setState(2);
				progressThread.start();
				// Message msg = mHandler.obtainMessage();
				// Bundle b = new Bundle();
				// b.putInt("total", 4);
				// msg.setData(b);
				// errorMsg = "";
				// mHandler.sendMessage(msg);
			}
		}
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
				try {
					updateData(code);
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 1);
					msg.setData(b);
					errorMsg = "";

					mHandler.sendMessage(msg);
					return;
				} catch (Exception e) {
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 2);
					msg.setData(b);
					errorMsg = "无法取得数据！请稍后再试。";
					mHandler.sendMessage(msg);
					return;
				}
			} else if (2 == mState) {
				try {
					updateData(code);
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 4);
					msg.setData(b);
					errorMsg = "";
					progressDialog.dismiss();
					mHandler.sendMessage(msg);
					return;
				} catch (Exception e) {
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 2);
					msg.setData(b);
					errorMsg = "无法取得数据！请稍后再试。";
					mHandler.sendMessage(msg);
					return;
				}
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
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {

				int total = msg.getData().getInt("total");
				if (total == 1) {
					// sendActiveThread = new SendActiveThread();
					// sendActiveThread.start();
					showNew();
				}
				if (total == 2) {
					if (!"".equals(errorMsg)) {
						// errorDialog("异常", errorMsg);
					}
				}
				if (total == 3 && running) {
					Toast.makeText(getApplicationContext(), "已添加到我的自选中。", 4000).show();
				}
				if (total == 4 && running) {
					showNew();
				}
				if (total == 6 && running) {
					Toast.makeText(getApplicationContext(), "我的自选中已有此项。", 4000).show();
				}
				if (total == 7 && running) {
					nowvalue.setTextColor(nowColor);
					nowvalue.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		};

		public void handleMessage(Message msg) {

			int total = msg.getData().getInt("total");
			if (total == 1) {
				// sendActiveThread = new SendActiveThread();
				// sendActiveThread.start();
				showNew();
			}
			if (total == 2) {
				if (!"".equals(errorMsg)) {
					// errorDialog("异常", errorMsg);
				}
			}
			if (total == 3 && running) {
				Toast.makeText(getApplicationContext(), "已添加到我的自选中。", 4000).show();
			}
			if (total == 4 && running) {
				showNew();
			}
			if (total == 6 && running) {
				Toast.makeText(getApplicationContext(), "我的自选中已有此项。", 4000).show();
			}
		}
	};

	protected void addDialog() {
		AlertDialog.Builder builder = new Builder(KLineView.this);
		builder.setMessage("确认要添加到我的自选吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Thread t = new Thread() {
					public void run() {

						SharedPreferences.Editor editor = preferences.edit();
						String opTemp 		= preferences.getString(Const.PREF_OPTIONAL, "");
						String opNameTemp 	= preferences.getString(Const.PREF_OPTIONAL_NAME, "");
						String exTemp 		= preferences.getString(Const.PREF_EX, "");
						if ("".equals(opTemp)) {
							editor.putString(Const.PREF_OPTIONAL, code);
							editor.putString(Const.PREF_OPTIONAL_NAME, name);
							editor.putString(Const.PREF_EX, ex);
						} else {
							String[] tmp = opTemp.split(",");
							for (int i = 0; i < tmp.length; i++) {
								if (code.equals(tmp[i])) {
									Message msg = handler.obtainMessage();
									Bundle b = new Bundle();
									b.putInt("total", 6);
									msg.setData(b);
									handler.sendMessage(msg);
									return;
								}
							}
							opTemp = opTemp + "," + code;
							exTemp = exTemp + "," + ex;
							opNameTemp = opNameTemp + "," + name;
							editor.putString(Const.PREF_OPTIONAL, opTemp);
							editor.putString(Const.PREF_EX, exTemp);
							editor.putString(Const.PREF_OPTIONAL_NAME, opNameTemp);
						}
						editor.commit();
						Message msg = handler.obtainMessage();
						Bundle b = new Bundle();
						b.putInt("total", 3);
						msg.setData(b);
						handler.sendMessage(msg);
					}
				};
				t.start();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
