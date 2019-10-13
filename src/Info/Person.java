package Info;

import java.io.Serializable;
import java.net.Socket;

public class Person implements Serializable{
	private String name;
	private String id;
	private transient Socket soc;
	private boolean chat;
	
	public Person(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Socket getSoc() {
		return soc;
	}
	public void setSoc(Socket soc) {
		this.soc = soc;
	}

	public boolean isChat() {
		return chat;
	}

	public void setChat(boolean chat) {
		this.chat = chat;
	}
	
	
}
