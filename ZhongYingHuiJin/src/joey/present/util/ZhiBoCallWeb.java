package joey.present.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import joey.present.data.ZhiBoChatBean;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class ZhiBoCallWeb {
	private List<ZhiBoChatBean> messageList = new ArrayList<ZhiBoChatBean>();

	public ZhiBoCallWeb() {
	}

	// 消息列表
	public List<ZhiBoChatBean> getMessageList(String path, Map<String, String> map) {
		try {
			StringBuilder buffer = new StringBuilder();
			for (Entry<String, String> entry : map.entrySet()) {
				buffer.append(entry.getKey());
				buffer.append("=");
				buffer.append(entry.getValue());
				buffer.append("&");
			}
			buffer.deleteCharAt(buffer.length() - 1);
			byte[] entry = buffer.toString().getBytes();
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);// 允许对外输出数据
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", entry.length + "");

			OutputStream os = conn.getOutputStream();// 得到输出流
			os.write(entry);
			int responseCode = conn.getResponseCode();// 得到返回码
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();// 得到输入流
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(is, "UTF-8");
				int eventType = parser.getEventType();
				ZhiBoChatBean message = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
					case XmlPullParser.START_TAG:
						if ("message".equals(parser.getName())) {
							message = new ZhiBoChatBean();
						}
						if ("id".equals(parser.getName())) {
							message.setId(parser.nextText());
						}
						if ("time".equals(parser.getName())) {
							message.setTime(parser.nextText());
						}
						if ("userid".equals(parser.getName())) {
							message.setUserId(parser.nextText());
						}
						if ("username".equals(parser.getName())) {
							message.setUserName(parser.nextText());
						}
						if ("userpic".equals(parser.getName())) {
							message.setUserpic(parser.nextText());
						}
						if ("content".equals(parser.getName())) {
							message.setContent(parser.nextText());
						}
						break;

					case XmlPullParser.END_TAG:
						if ("message".equals(parser.getName())) {
							messageList.add(message);
						}
						break;

					default:
						break;
					}
					eventType = parser.next();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messageList;
	}

	// 解析
	@SuppressWarnings("unused")
	private String getresultCodeAndMessageState(InputStream is) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		byte[] data = bos.toByteArray();
		String substring = new String(data);
		return substring;
	}

	/**
	 * 提交数据到服务器上
	 */
	private static String PATH = "http://42.121.54.222:2025/chat/i_sendmessages?";
	private static URL url = null;
	static {
		try {
			url = new URL(PATH);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static String sendMessge(Map<String, String> params, String encode) {
		StringBuffer buffer = new StringBuffer();
		if (params.isEmpty() || params != null) {
			try {
				for (Map.Entry<String, String> enty : params.entrySet()) {
					buffer.append(enty.getKey());
					buffer.append("=");
					buffer.append(URLEncoder.encode(enty.getValue(), encode));
					buffer.append("&");
				}
				buffer.deleteCharAt(buffer.length() - 1);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				urlConnection.setConnectTimeout(3000);
				urlConnection.setRequestMethod("POST");
				byte[] data = buffer.toString().getBytes();
				Log.i("temp", "data++++++>>>" + data);
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				urlConnection.addRequestProperty("Content-Length", String.valueOf(data.length));
				Log.i("temp", "urlConnection++++++>>>" + urlConnection);
				OutputStream outputStream = urlConnection.getOutputStream();
				outputStream.write(data);
				int responseCode = urlConnection.getResponseCode();
				if (responseCode == 200) {
					return changeInputStream(urlConnection.getInputStream(), encode);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	private static String changeInputStream(InputStream inputStream, String encode) {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len;
		String resulr = "";
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					arrayOutputStream.write(data, 0, len);
				}
				resulr = new String(arrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resulr;
	}
}
