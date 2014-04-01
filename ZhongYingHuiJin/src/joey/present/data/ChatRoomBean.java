package joey.present.data;

public class ChatRoomBean {
	public String roomname;
	public String roomid;
	public String dkview;
	public String popularity;
	public String status;

	public String getRoomname() {
		return roomname;
	}

	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}

	@Override
	public String toString() {
		return "ChatRoomBean [roomname=" + roomname + ", roomid=" + roomid + ", dkview=" + dkview + ", popularity=" + popularity + ", status=" + status + "]";
	}

	public String getRoomid() {
		return roomid;
	}

	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}

	public String getDkview() {
		return dkview;
	}

	public void setDkview(String dkview) {
		this.dkview = dkview;
	}

	public String getPopularity() {
		return popularity;
	}

	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
