package joey.present.view;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.fx678.zhongyinghuijin.finace.R;
import com.umeng.analytics.MobclickAgent;

import joey.present.data.LoginBean;
import joey.present.util.Const;
import joey.present.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class StartView extends Activity {
	protected static final String TAG = "temp";
	// private ImageButton searchBtn;
	private ImageButton goldmkbtn;
	private ImageButton globalbtn;
	private ImageButton newsbtn;
	private ImageButton mychoicebtn;
	private ImageButton tradingstrategybtn;
	private ImageButton onlinetradingbtn;
	private ImageButton customerservicebtn;
	private ImageButton sethelpbtn;
	private ImageButton exitsoftbtn;
	private String version = "";
	private Util util = new Util();

	private DatagramSocket client;
	private DatagramPacket recpacket;
	private DatagramPacket sendpacket;

	private SharedPreferences preferences;

	public void onDestroy() {
		super.onDestroy();
		// stopService(new Intent(this, NewsOnTopService.class));
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		// Thread t = new Thread() {
		// public void run() {
		// try {
		// client.close();
		// client = new DatagramSocket();
		// String sendStr = String.format(Const.UDP_PARA + "message");
		// byte[] sendBuf = sendStr.getBytes();
		// sendBuf = sendStr.getBytes();
		// InetAddress address = InetAddress.getByName(Const.UDP_IP);
		// sendpacket = new DatagramPacket(sendBuf, sendBuf.length,
		// address, Const.UDP_PORT);
		// client.send(sendpacket);
		// byte[] recBuf = new byte[200];
		// recpacket = new DatagramPacket(recBuf, recBuf.length);
		//
		// client.receive(recpacket);
		// String str = new String(recpacket.getData(), 0, 200);
		// Message msg = handler.obtainMessage();
		// Bundle b = new Bundle();
		// b.putString("message", str);
		// msg.setData(b);
		// handler.sendMessage(msg);
		// } catch (Exception e) {
		//
		// }
		//
		// }
		// };
		// t.start();
	}

	public void onPause() {
		super.onPause();
		// client.close();
		MobclickAgent.onPause(this);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.startview);
		// Network();
		initView();

		// 异步版本检查
		if (isNetworkConnected(this)) {
			checkVersion();
			// startService(new Intent(StartView.this, ImportService.class));
		} else {
			showNoConn();
		}

		// startService(new Intent(StartView.this, NewsOnTopService.class));
		preferences = getSharedPreferences("jiaoyicelue", Activity.MODE_PRIVATE);

	}

	private void initView() {
		// searchBtn = (ImageButton) findViewById(R.id.search);
		// searchBtn.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// SearchKeyboardDlg dlg = new SearchKeyboardDlg(StartView.this);
		// dlg.show();
		// }
		// });
		tradingstrategybtn = (ImageButton) findViewById(R.id.tradingstrategybtn);
		tradingstrategybtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(StartView.this, "尽请期待...", 0).show();
				/* 将数据从SharedPreferences中取出来*/
				SharedPreferences preferences = getSharedPreferences(Const.CHATLOGIN, Context.MODE_PRIVATE);
				String login 	= preferences.getString("login", "");
				String realname = preferences.getString("realname", "");
				String group 	= preferences.getString("group", "");
				String userpic 	= preferences.getString("userpic", "");
				String userid 	= preferences.getString("userid", "");

				if (userid == null || "".equals(userid)) {
					Intent intent = new Intent(StartView.this, ZhiBoLogin.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(StartView.this, ZhiBoRoomActivity.class);
					intent.putExtra("login", login);
					intent.putExtra("realname", realname);
					intent.putExtra("group", group);
					intent.putExtra("userpic", userpic);
					intent.putExtra("userid", userid);
					startActivity(intent);
				}
			}
		});
		goldmkbtn = (ImageButton) findViewById(R.id.goldmkbtn);
		goldmkbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.icongoldmarket);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.icongoldmarket);
					Intent in = new Intent(StartView.this, SelectView.class);
					Bundle bundle = new Bundle();
					bundle.putString("started", "gold");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		globalbtn = (ImageButton) findViewById(R.id.globalbtn);
		globalbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.newgoldanalyst);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.newgoldanalyst);
					// Intent in = new Intent(StartView.this, ZhiBoDaTing.class);
					Intent intent = new Intent(StartView.this, AnaltstsWebView.class);
					Bundle bundle = new Bundle();
					bundle.putString("started", "global");
					intent.putExtras(bundle);
					startActivity(intent);

				}
				return false;
			}
		});
		newsbtn = (ImageButton) findViewById(R.id.newsbtn);
		newsbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.newnew);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.newnew);
					// 直接跳转到NewsView界面
					Intent in = new Intent(StartView.this, NewsView.class);
					// Intent in = new Intent(StartView.this, MainTab.class);
					Bundle bundle = new Bundle();
					bundle.putString("selected", "newstab");
					bundle.putString("btn", "0");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		mychoicebtn = (ImageButton) findViewById(R.id.mychoicebtn);
		mychoicebtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.iconmychoice);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.iconmychoice);
					Intent in = new Intent(StartView.this, ZhongYingHuiJin_LOGO.class);
					Bundle bundle = new Bundle();
					bundle.putString("selected", "mychoicetab");
					bundle.putString("btn", "0");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		sethelpbtn = (ImageButton) findViewById(R.id.sethelpbtn);
		sethelpbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.iconsethelp);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.iconsethelp);
					// Intent in = new Intent(StartView.this,
					// tradingstrategyNewView.class);
					Intent in = new Intent(StartView.this, MoNiDaSai_AnaltstsWebView.class);
					// Intent in = new Intent(StartView.this, MainTab.class);
					Bundle bundle = new Bundle();
					// bundle.putString("selected", "newstab");
					// bundle.putString("btn", "1");
					// in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		onlinetradingbtn = (ImageButton) findViewById(R.id.onlinetradingbtn);
		onlinetradingbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.iconcalendar);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.iconcalendar);
					Intent in = new Intent(StartView.this, CalendarView.class);
					startActivity(in);
					// Intent intent= new Intent(Intent.ACTION_VIEW);
					// intent.setData(Uri.parse("http://wap.fx678.com"));
					// startActivity(intent);
				}
				return false;
			}
		});
		customerservicebtn = (ImageButton) findViewById(R.id.customerservicebtn);
		customerservicebtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.iconcustomerservice);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.iconcustomerservice);
					Intent intent = new Intent(StartView.this, MainView.class);
					startActivity(intent);
				}
				return false;
			}
		});
		sethelpbtn = (ImageButton) findViewById(R.id.sethelpbtn);
		sethelpbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.iconsethelp);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.iconsethelp);
					Intent in = new Intent(StartView.this, MoNiDaSai_AnaltstsWebView.class);
					startActivity(in);
				}
				return false;
			}
		});
		exitsoftbtn = (ImageButton) findViewById(R.id.exitsoftbtn);
		exitsoftbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.icontradingstrategy);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.icontradingstrategy);
					// Intent in = new Intent(StartView.this,
					// tradingstrategyNewView.class);
					// startActivity(in);

					String login_ok = preferences.getString("OK", "");
					if ("1".equals(login_ok)) {
						Intent in = new Intent(StartView.this, tradingstrategyNewView.class);
						startActivity(in);
					} else {
						Intent in = new Intent(StartView.this, Login_CeLue.class);
						startActivity(in);
					}

				}
				return false;
			}
		});
	}

	// 退出确认对话框
	protected void exitDialog() {
		AlertDialog.Builder builder = new Builder(StartView.this);
		builder.setMessage("确认要退出盈如意吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String message = msg.getData().getString("message");
			Toast.makeText(getApplicationContext(), message, 3000).show();
		}
	};

	private void checkVersion() {
		// TODO Auto-generated method stub

		MyTask mytask = new MyTask();
		mytask.execute(null, null, null);
	}

	class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			version = util.checkVersion("http://m.fx678.com/Upgrade.aspx?ver=YINGRUYI_ANDROID_V1.0.1");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String v = version.substring(0, 1);
			if ("1".equals(v)) {
				AlertDialog.Builder builder = new Builder(StartView.this);
				builder.setMessage("发现新版本，需要下载最新版吗？");
				builder.setTitle("提示");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				builder.setNegativeButton("不更新", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			} else if ("2".equals(v)) {
				AlertDialog.Builder builder = new Builder(StartView.this);
				builder.setMessage("请更新到最新版使用！");
				builder.setTitle("提示");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				builder.create().show();
			}
		}
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
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
		// TODO Auto-generated method stub
		new AlertDialog.Builder(StartView.this).setTitle("注意：").setCancelable(false).setMessage("无法加载数据,请检查网络连接").setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//
			}
		}).create().show();
	}

}
