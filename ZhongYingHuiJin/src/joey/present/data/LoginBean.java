package joey.present.data;

public class LoginBean {
	public String login;
	public String realname;
	public String group;
	public String enddate;
	public String userpic;
	public String userid;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "LoginBean [login=" + login + ", realname=" + realname + ", group=" + group + ", enddate=" + enddate + ", userpic=" + userpic + ", userid=" + userid + "]";
	}

}
