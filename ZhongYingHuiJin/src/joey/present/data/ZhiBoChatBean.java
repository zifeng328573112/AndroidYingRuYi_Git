package joey.present.data;

import android.util.Log;

public class ZhiBoChatBean {
	private int 			count;// count
	private String 			id;
	private String 			time;
	private String 			userId;// 用户身份唯一id
	private String 			hallId;// 房间唯一id
	private String 			f;// 用户身份唯一标识
	private String 			userName;// 用户姓名
	private String 			userpic;// 用户头像
	private String 			content;// 内容
	private boolean 		isComMeg = true;

	public ZhiBoChatBean() {
		super();
	}

	public ZhiBoChatBean(int count, String id, String time, String userId, String hallId, String f, String userName, String userpic, String content, boolean isComMeg) {
		super();
		this.count = count;
		this.id = id;
		this.time = time;
		this.userId = userId;
		this.hallId = hallId;
		this.f = f;
		this.userName = userName;
		this.userpic = userpic;
		this.content = content;
		this.isComMeg = isComMeg;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHallId() {
		return hallId;
	}

	public void setHallId(String hallId) {
		this.hallId = hallId;
	}

	public String getF() {
		return f;
	}

	public void setF(String f) {
		this.f = f;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean getIsComMeg() {
		return isComMeg;
	}

	public void setComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	@Override
	public String toString() {
		return "ZhiBoChatBean [count=" + count + ", id=" + id + ", time=" + time + ", userId=" + userId + ", hallId=" + hallId + ", f=" + f + ", userName=" + userName + ", userpic=" + userpic
				+ ", content=" + content + ", isComMeg=" + isComMeg + "]";
	}

}
