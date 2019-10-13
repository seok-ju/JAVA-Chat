package chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import Info.MyFile;
import Info.Person;

public class Room extends Thread{
	private Socket soc = null;
	ArrayList<PrintWriter> arr = null;
	ArrayList<Person> p = null;
	ArrayList<String> con = null;
	
	BufferedReader br = null;
	PrintWriter pw = null;
	
	private String name = null;
	
	MyFile file = null;
	
	private int num = 0;
	
	public Room(String name, Socket soc, ArrayList<PrintWriter> arr, ArrayList<Person> p, ArrayList<String> con, MyFile file, int i) {
		this.name = name;
		this.soc = soc;
		this.arr = arr;
		this.p = p;
		this.con = con;
		this.file = file;
		this.num = i;
	}
	
	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			pw = new PrintWriter(soc.getOutputStream(), true);
			pw.println("채팅방에 입장하셨습니다");
			pw.println("[퇴장은 \"/나가기\" 입력하세요!]");

			synchronized (file) {
				file.addName(name);
			}
			
			while(true) {
				String readLine = br.readLine();
				synchronized (con) {
					con.add(name +" >> " + readLine);
				}
				if(!readLine.equals("/나가기"))
					broadcast(name + " >> " + readLine);
				else {
					broadcast(name + "님 퇴장하셨습니다");
					synchronized (p) {
						for(int i=0; i<p.size(); i++) {
							if(soc.equals(p.get(i).getSoc())) {
								p.get(i).setChat(false);
							}
						}						
					}
					arr.set(num, new PrintWriter("null"));
					break;
				}
			}
			synchronized (con) {
				String tmp = "";
				for(int i=0; i<con.size(); i++) {
					tmp += con.get(i)+"\n";
					file.saveChat(tmp);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcast(String msg) {
		synchronized (arr) {
			for(PrintWriter e : arr) {
				e.println(msg);
			}
		}
	}
}
