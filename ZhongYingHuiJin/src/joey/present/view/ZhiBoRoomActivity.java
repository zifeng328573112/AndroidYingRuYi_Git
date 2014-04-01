package joey.present.view;

import java.util.ArrayList;

import joey.present.data.ChatRoomBean;
import joey.present.util.ConnectNet;
import joey.present.util.Const;
import joey.present.util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fx678.zhongyinghuijin.finace.R;

public class ZhiBoRoomActivity extends Activity implements OnClickListener, OnItemClickListener {
	private ImageButton 			backbtn;
	private Button    				chatRightRefresh,tuichu;
	private ListView 				zhibo_room_listview;
	private Util 					util = new Util();
	private ArrayList<ChatRoomBean> chatRoomBean;
	
	private String 					login;
	private String 					realname;
	private String 					group;
	private String 					userpic;
	private String 					chatUserId;
	private ProgressDialog 			progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		progressDialog = new ProgressDialog(ZhiBoRoomActivity.this);
		progressDialog.setMessage("正在获取房间列表");
		progressDialog.setTitle("请稍候");
		progressDialog.show();
		
		initView();
		if (ConnectNet.isConnectingToInternet(ZhiBoRoomActivity.this)) {
			initData();
		} else {
			progressDialog.dismiss();
			Toast.makeText(getApplicationContext(), "网络连接失败,请稍后重试...", Toast.LENGTH_LONG).show();
		}
	}

	private void initView() {
		setContentView(R.layout.zhibo_room);
		backbtn 			= (ImageButton) findViewById(R.id.backbtn);
		zhibo_room_listview = (ListView) findViewById(R.id.zhibo_room_listview);
		chatRightRefresh    = (Button)findViewById(R.id.chat_right_refresh);
		tuichu = (Button)findViewById(R.id.tuichu);//便于测试，需求没有要求退出登录
		
		backbtn.setOnClickListener(this);
		zhibo_room_listview.setOnItemClickListener(this);
		chatRightRefresh.setOnClickListener(this);
		tuichu.setOnClickListener(this);//便于测试，需求没有要求退出登录
		
		Intent intent 		= getIntent();
		login 				= intent.getStringExtra("login");
		realname 			= intent.getStringExtra("realname");
		group 				= intent.getStringExtra("group");
		userpic 			= intent.getStringExtra("userpic");
		chatUserId 			= intent.getStringExtra("userid");
		Log.i("temp", "login:(" + login + 
				"),realname:(" + realname + 
				"),group:(" +  group + 
				"),userpic:(" + userpic + 
				"),chatUserId:(" + chatUserId + ")");
	}

	private void initData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = "http://42.121.54.222:2025/chat/i_roomlist";
				try {
					Message msg = handler.obtainMessage();
					msg.obj 	= util.getZhiBoRoom(url);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			chatRoomBean = (ArrayList<ChatRoomBean>) msg.obj;
			zhibo_room_listview.setAdapter(new MyAdapter(ZhiBoRoomActivity.this, chatRoomBean));
		};
	};
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backbtn:
			ZhiBoRoomActivity.this.finish();
			break;
			
		case R.id.chat_right_refresh://刷新
			progressDialog = new ProgressDialog(ZhiBoRoomActivity.this);
			progressDialog.setMessage("正在刷新房间列表");
			progressDialog.setTitle("请稍候");
			progressDialog.show();
			if (ConnectNet.isConnectingToInternet(ZhiBoRoomActivity.this)) {
				initData();
			} else {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "网络连接失败,请稍后重试...", Toast.LENGTH_LONG).show();
			}
			break;

		case R.id.tuichu://便于测试，需求没有要求退出登录
			clearData();
			break;
		default:
			break;
		}
	}
	
	public void clearData() { //便于测试，需求没有要求退出登录
		SharedPreferences preferences = getSharedPreferences(Const.CHATLOGIN, Context.MODE_PRIVATE);
		preferences.edit().clear().commit(); 
		ZhiBoRoomActivity.this.finish();
   } 

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}
	
	class Holder {
		public TextView 	paiming, 
							mingcheng, 
							guandian, 
							renqi, 
							zhuangtai;
		public Button 		caozuo;
	}

	class MyAdapter extends BaseAdapter {
		private Context 				context;
		private ArrayList<ChatRoomBean> chatRoomBean;
		private LayoutInflater 			inflater;

		public MyAdapter(Context context, ArrayList<ChatRoomBean> chatRoomBean) {
			this.context 		= context;
			this.chatRoomBean 	= chatRoomBean;
			inflater 			= LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return chatRoomBean == null ? 0 : chatRoomBean.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = new Holder();
			if (convertView == null) {
				convertView 		= inflater.inflate(R.layout.zhibo_listview_item, null);
				holder.paiming 		= (TextView) convertView.findViewById(R.id.paiming);
				holder.mingcheng 	= (TextView) convertView.findViewById(R.id.mingcheng);
				holder.guandian 	= (TextView) convertView.findViewById(R.id.guandian);
				holder.renqi 		= (TextView) convertView.findViewById(R.id.renqi);
				holder.zhuangtai 	= (TextView) convertView.findViewById(R.id.zhuangtai);
				holder.caozuo 		= (Button) convertView.findViewById(R.id.caozuo);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.paiming.setText(position + 1 + "");
			holder.mingcheng.setText(chatRoomBean.get(position).getRoomname());
			holder.guandian.setText(chatRoomBean.get(position).getDkview());
			holder.renqi.setText(chatRoomBean.get(position).getPopularity());
			holder.caozuo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ZhiBoRoomActivity.this, ZhiBoInfoActivity.class);
					intent.putExtra("roomId", 		chatRoomBean.get(position).getRoomid());
					intent.putExtra("roomName", 	chatRoomBean.get(position).getRoomname());
					intent.putExtra("status", 		chatRoomBean.get(position).getStatus());
					intent.putExtra("login", 		login);
					intent.putExtra("realname", 	realname);
					intent.putExtra("group", 		group);
					intent.putExtra("userpic", 		userpic);
					intent.putExtra("chatUserId", 	chatUserId);
					if ("盈如意语音视频直播".equals(chatRoomBean.get(position).getRoomname())) {
						if ("特权会员".equals(group)) {
							startActivity(intent);
						} else {
							AlertDialog.Builder builder = new Builder(ZhiBoRoomActivity.this);
							builder.setMessage("只有特权会员才能访问本直播间！");
							builder.setTitle("提示");
							builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int arg1) {
									dialog.dismiss();
								}
							});
							builder.create().show();
						}
					} else {
						startActivity(intent);
					}
				}
			});
			
			String state = "";
			String caozuo_state = "";
			if (chatRoomBean.get(position).getStatus().equals("True")) {
				state 		 = "直播中";
				caozuo_state = "进入直播";
				holder.caozuo.setTextColor(Color.WHITE);
				holder.caozuo.setBackgroundResource(R.drawable.redbgpress);
//				holder.caozuo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//添加下划线 
			} else {
				state = "暂时离开";
				caozuo_state = "暂时离开";
				holder.caozuo.setTextColor(Color.WHITE);
				holder.caozuo.setBackgroundResource(R.drawable.bluebg);
				//holder.caozuo.setClickable(false);//设置点击事件
			}
			holder.zhuangtai.setText(state);
			holder.caozuo.setText(caozuo_state);
			
			return convertView;
		}
	}
}
