package Info;

import java.io.Serializable;

public class Info implements Serializable{
	private String id;
	private String pw;
	private boolean stat;
	
	public Info(String id, String pw) {
		this.id = id;
		this.pw = pw;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public boolean isStat() {
		return stat;
	}

	public void setStat(boolean stat) {
		this.stat = stat;
	}

	@Override
	public String toString() {
		return "id=" + id + ", pw=" + pw + ", stat=" + stat;
	}
	
	
}
