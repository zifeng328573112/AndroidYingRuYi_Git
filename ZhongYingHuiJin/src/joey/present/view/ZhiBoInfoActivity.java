package joey.present.view;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import joey.present.data.ZhiBoChatBean;
import joey.present.util.ConnectNet;
import joey.present.util.Util;
import joey.present.util.ZhiBoCallWeb;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fx678.zhongyinghuijin.finace.R;

public class ZhiBoInfoActivity extends Activity implements OnClickListener {
	
	private static final String 		TAG = "ZhiBoInfoActivity";
	public static final int 			SUCCESS = 0;//成功
	public static final int 			NO = 1;//失败
	public static final int 			REFRESH = 2;//刷新
	private int 						refreshTime = 0; //时长
	private Boolean 					isadapter = false;
	private ProgressDialog 				chatProgressDialog;

	private List<ZhiBoChatBean> 		chatMessageList = new ArrayList<ZhiBoChatBean>();
	private Util 						chatUtil = new Util();
	Timer 								chatTimer = new Timer();  //时长

	private ZhiBoChatAdapter 			chatAdapter;

	private RelativeLayout				chatRlBottom;
	private TextView 					chatTitle;
	private EditText 					chatInput;
	private ListView 					chatListView;
	private Button 						chatRightbtn;
	private Button 						chatSendButton;
	private ImageButton 				chatBackbtn;

	private String 						chatContString;
	private String 						chatRoomId;// 取值roomId
	private String 						chatRoomName;// 取值roomName
	private String 						chatStatus;// 取值status
	private String 						chatLogin;// 取值login
	private String 						chatRealname;// 取值realname
	private String 						chatGroup;// 取值group
	private String 						chatUserpic;// 取值userpic
	private String 						chatUserId;// 取值chatUserId
	private String 						chatString;
	private String 						chatname;
	private String 						chatcontent;
	private String 						chatyhId;
	private String 						chatCount = "20";//count 每页条数 如20 
	
	/**
	 * 启动线程
	 */
	private Handler chatHoseAppHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				try {
					if (ConnectNet.isConnectingToInternet(ZhiBoInfoActivity.this)) {
						if (!"".equals(chatMessageList)) {
							chatAdapter = new ZhiBoChatAdapter(getApplicationContext(), chatMessageList);
							chatListView.setAdapter(chatAdapter);
							Log.i(TAG, "chatListView++++++>>>" + chatListView);
							chatAdapter.notifyDataSetChanged();
							if ("True".equals(chatStatus)) {
								Toast.makeText(getApplicationContext(), "获取数据成功", Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "获取数据列表失败！", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getApplicationContext(), "网络连接失败,请稍后重试...", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case NO:
				Toast.makeText(getApplicationContext(), "当前没有可被刷新内容", Toast.LENGTH_LONG).show();
				break;
				
			case REFRESH:
				chatRefresh();
				refreshTime ++;
				break;
				
			default:
				break;
			}
			super.handleMessage(msg);
		};
	};

	/**
	 * 创建activity
	 */
	@Override    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		initView();
		EdiText();
		showTextView();
		
		new Thread(){
			public void run() {
				chatRefresh();
			};
		}.start();
		
		  chatTimer.schedule(new TimerTask() {  
              
	            @Override  
	            public void run() {  
	            	chatHoseAppHandler.sendEmptyMessage(REFRESH);  
	            }  
	        }, 0, 100000);//延时0秒后每隔1分钟刷新一次。  
	}

	
	/**
	 * 初始化View
	 */
	@SuppressLint({ "NewApi", "ResourceAsColor" })
	private void initView() {
		setContentView(R.layout.zhibo_info);
		
		chatBackbtn 		= (ImageButton) findViewById(R.id.chat_backbtn);
		chatRightbtn 		= (Button) findViewById(R.id.chat_right_btn);
		chatSendButton 		= (Button) findViewById(R.id.chat_conversation_send);
		chatListView 		= (ListView) findViewById(R.id.chat_info_listview);
		chatInput 			= (EditText) findViewById(R.id.chat_input);
		chatTitle	 		= (TextView) findViewById(R.id.chat_title);
		chatRlBottom        = (RelativeLayout)findViewById(R.id.rl_bottom);
		/* 给Button添加监听事件 */
		chatBackbtn.setOnClickListener(this);
		chatRightbtn.setOnClickListener(this);
		chatSendButton.setOnClickListener(this);

		Intent intent 		= getIntent();// 获取到的值
		chatRoomId 			= intent.getStringExtra("roomId");
		chatRoomName 		= intent.getStringExtra("roomName");
		chatStatus 			= intent.getStringExtra("status");
		/* 这是从登录页面传过来的 */
		chatLogin 			= intent.getStringExtra("login");
		chatRealname 		= intent.getStringExtra("realname");
		chatGroup 			= intent.getStringExtra("group");
		chatUserpic 		= intent.getStringExtra("userpic");
		chatUserId 			= intent.getStringExtra("chatUserId");
		Log.i(TAG, "chatLogin:" 	+ chatLogin + "\n" + 
					"chatRealname:" + chatRealname + "\n" + 
					"chatGroup:" 	+ chatGroup + "\n" + 
					"chatUserpic:" 	+ chatUserpic + "\n" + 
					"chatUserId:" 	+ chatUserId);
	}

	/***
	 * 设置title
	 */
	public void showTextView() {
		chatTitle.setText(chatRoomName);
		chatProgressDialog = new ProgressDialog(ZhiBoInfoActivity.this);
		chatProgressDialog.setMessage("正在获取" + chatRoomName + "列表");
		chatProgressDialog.setTitle("请稍候");
		chatProgressDialog.show();
	}

	/**
	 * 编辑框（暂时离开）不可编辑
	 * 
	 * chatSendButton.getBackground().setAlpha(10);//0~255透明度值 
	 */
	public void EdiText() {
		if ("false".equals(chatStatus)) {
		} else if ("False".equals(chatStatus)) {
			if (ConnectNet.isConnectingToInternet(ZhiBoInfoActivity.this)) {
				Toast.makeText(ZhiBoInfoActivity.this, "房主暂时离开，不能进行评论", Toast.LENGTH_LONG).show();
			}
			chatSendButton.setClickable(false);//button不可点击 
			chatInput.setCursorVisible(false);// 隐藏光标
			chatInput.setFocusableInTouchMode(false);// 禁止弹出软键盘
			chatInput.setFilters(new InputFilter[] { new InputFilter() {// editext不可编辑

				@Override
				public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
					return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
				}
			} });
		}
	}

	/**
	 * 设置点击事件
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.chat_backbtn:// 返回
			ZhiBoInfoActivity.this.finish();
			/* 停止刷新*/
			chatTimer.cancel();
			/* 如果软键盘弹出就关闭*/
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        imm.hideSoftInputFromWindow(chatInput.getWindowToken(),0);
			break;

		case R.id.chat_right_btn:// 刷新
			 if (!ConnectNet.isFastDoubleClick()) {  
				chatProgressDialog = new ProgressDialog(ZhiBoInfoActivity.this);
				chatProgressDialog.setMessage("正在刷新" + chatRoomName + "列表");
				chatProgressDialog.setTitle("请稍候");
				chatProgressDialog.show();
				 new Thread(){
					 public void run() {
						 try {
							Thread.sleep(500);
							chatRefresh();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					 };
				 }.start();
			 } else {
				 Toast.makeText(ZhiBoInfoActivity.this, "不可连续刷新", Toast.LENGTH_SHORT).show(); 
			 }
			break;

		case R.id.chat_conversation_send:// 发送
			send();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 发送
	 */
	private void send() {
		chatContString = chatInput.getText().toString().trim();
		if (chatContString.length() > 0) {
			ZhiBoChatBean chatBean = new ZhiBoChatBean();
			
			chatBean.setComMeg(false);
			chatBean.setContent(chatContString);
			chatBean.setUserName(chatRealname);
			chatBean.setUserpic(chatUserpic);
			chatBean.setUserId(chatUserId);
			chatMessageList.add(chatBean);
			Log.i(TAG, "chatBean++++++>>>" + chatBean);
			chatInput.setText("");
			chatAdapter.notifyDataSetChanged();
			chatListView.setSelection(chatListView.getCount() - 1);
			getInfo();
		}else {
			Toast.makeText(getApplicationContext(), "不能发送空消息", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 发送消息
	 */
	private void getInfo() {
		new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("hallid",   chatRoomId);
					map.put("f", 	    chatUserId);
					map.put("username", chatRealname);
					map.put("userpic",  chatUserpic);
					map.put("content",  chatContString);
					ZhiBoCallWeb callWeb = new ZhiBoCallWeb();
					chatString 				 = callWeb.sendMessge(map, "UTF-8");// 提交数据
					Log.i(TAG, "string++++++>>>" + chatString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 回复
	 */
	public void chatReturn(int position) {
		if ("True".equals(chatStatus)) {
			chatInput.setFocusable(true);
			chatInput.setFocusableInTouchMode(true);
			chatInput.requestFocus();
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.showSoftInput(chatInput, InputMethodManager.HIDE_NOT_ALWAYS);
			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

			chatInput.setText("【" + chatMessageList.get(position).getUserName() + ":" + chatMessageList.get(position).getContent() + "】");
			chatInput.setSelection(chatInput.length());
		}
	}

	/**
	 * 刷新
	 */
	public void chatRefresh() {
		new Thread(new MarketAppItemRun()).start();
	}

	/**
	 * 获取消息列表
	 * @author Administrator
	 * 
	 */
	private class MarketAppItemRun implements Runnable {

		public void run() {
			try {
				String path = "http://42.121.54.222:2025/chat/i_messagelist?";
				Map<String, String> map = new HashMap<String, String>();
				map.put("hallid", chatRoomId);
				map.put("userid", chatUserId);
				map.put("count",  chatCount);
				map.put("page",   "1");
				ZhiBoCallWeb callWeb 	= new ZhiBoCallWeb();
				chatMessageList 			= callWeb.getMessageList(path, map);
				Log.i(TAG, "chatMessageList++++++>>>" + chatMessageList);
				
				if (chatListView.equals(chatMessageList)) {
					Message msg = new Message();
					msg.what = NO;
					chatHoseAppHandler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = SUCCESS;
					chatHoseAppHandler.sendMessage(msg);
					chatProgressDialog.dismiss();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class ViewHolder {
		public TextView 		chatContent;
		public TextView 		chatUserName;
		public ImageView 		chatUserHead;
		public Button 			chatReturn;
		public String 			chatId;
	}

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	public class ZhiBoChatAdapter extends BaseAdapter {

		private List<ZhiBoChatBean> 		chatBean_a;
		private Context 					chatText;
		private LayoutInflater 				chatInflater;

		public ZhiBoChatAdapter(Context context, List<ZhiBoChatBean> chatBean) {
			chatText 		= context;
			this.chatBean_a = chatBean;
			chatInflater 	= LayoutInflater.from(chatText);
		}

		public int getCount() {
			return chatBean_a.size();
		}

		public Object getItem(int position) {
			return chatBean_a.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public int getItemViewType(int position) {
			ZhiBoChatBean entity = chatBean_a.get(position);

			if (entity.getIsComMeg()) {
				return IMsgViewType.IMVT_COM_MSG;
			} else {
				return IMsgViewType.IMVT_TO_MSG;
			}
		}

		public int getViewTypeCount() {
			return 2;
		}

		/**
		 * 往控件里面填充数据
		 */
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			Intent intent 		= getIntent();
			String UserId 		= intent.getStringExtra("chatUserId");
			Log.i("temp", "----------------" + chatUserId);

			ZhiBoChatBean bean 	= chatBean_a.get(position);
			chatname 			= bean.getUserName();
			chatcontent 		= bean.getContent();
			String chatId 		= bean.getUserId();
			Log.i("temp", "----------------" + chatId);

			ViewHolder viewHolder = null;
			View chatView = null;
			if (!UserId.equals(chatId)) {
				chatView = chatInflater.inflate(R.layout.zhibo_item_msg_text_left, parent, false);
			} else {
				chatView = chatInflater.inflate(R.layout.zhibo_item_msg_text_right, parent, false);
			}
			if (chatView.getTag() == null) {
				viewHolder 					= new ViewHolder();
				viewHolder.chatContent 		= (TextView) chatView.findViewById(R.id.chat_content);
				viewHolder.chatUserName 	= (TextView) chatView.findViewById(R.id.chat_userName);
				viewHolder.chatUserHead 	= (ImageView) chatView.findViewById(R.id.chat_userhead);
				viewHolder.chatReturn 		= (Button) chatView.findViewById(R.id.chat_return);
				viewHolder.chatId 			= chatId;		
				chatView.setTag(viewHolder);
			} else {
				viewHolder 					= (ViewHolder) chatView.getTag();
			}
			
			String path = "http://42.121.54.222:2025" + bean.getUserpic();
			loadImag(path, viewHolder.chatUserHead);// 显示图片
			viewHolder.chatUserName.setText(chatname);// 名字
			viewHolder.chatContent.setText(chatcontent);// 显示内容
			Log.i("temp", position + ",chatcontent->" + chatcontent + ",chatname->" + chatname);
  
			if (!UserId.equals(chatId)) {
				try {
					viewHolder.chatReturn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View view) { 
							Log.i("temp", "position->" + position);
							chatReturn(position);
						}
					});
				} catch (Exception e) {
				}
			}
			return chatView;
		}

		/**
		 * 设置图片路径
		 * @param path
		 * @param imag
		 * @return
		 */
		public Bitmap loadImag(String path, ImageView imag) {
			new ImagAsyncTask(imag).execute(path);
			return null;
		}

		/**
		 * 异步加载图片
		 * @author Administrator
		 */
		public class ImagAsyncTask extends AsyncTask<String, Void, Bitmap> {

			private ImageView chatUserHead;

			/**
			 * view 控件的构造函数
			 * @param imag
			 */
			public ImagAsyncTask(ImageView imag) {
				chatUserHead = imag;
			}
			
			/**
			 * 从服务器上获取图片
			 * @param path图片的网络地址
			 * @return 位图
			 * @throws Exception
			 */
			public Bitmap getImage(final String path) {
				try {
					URL url 				= new URL(path);
					HttpURLConnection conn 	= (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() == 200) {
						InputStream is 		= conn.getInputStream();
						Bitmap bitmap 		= BitmapFactory.decodeStream(is);
						return bitmap;
					}
				} catch (Exception e) {
				}
				return null;
			}

			/**
			 * 处理下载图片
			 */
			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap bitmap 	= null;
				try {
					String url 	= params[0];
					bitmap 		= getImage(url);
				} catch (Exception e) {
				}
				return bitmap;
			}

			/**
			 * 将图片返回的值设置给view控件
			 */
			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				chatUserHead.setImageBitmap(result);
			}
		}
	}
}