package joey.present.data;

public class ZhiBoMessageBean {
	public String info;
	public String next;
	public String last;
	public String messages;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "ZhiBoMessageBean [info=" + info + ", next=" + next + ", last=" + last + ", messages=" + messages + "]";
	}

}
