package chatting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import Info.Info;
import Info.MyFile;
import Info.Person;

public class Lobby extends Thread{

	Socket soc = null;
	MyFile file = null;
	
	ArrayList<Info> info = null;
	ArrayList<Person> person = null;
	ArrayList<PrintWriter> arr = null; // �� ���� ���
	ArrayList<Socket> socket = null; // �� ���� ���� 
	ArrayList<String> name = null; // �� ���� �ο��̸�
	ArrayList<String> con = null; // �� ���� ��ȭ����
	
	BufferedReader br = null;
	PrintWriter pw = null;
	
	private boolean chat = false;

	public Lobby(Socket soc, ArrayList<Person> p, ArrayList<Info> info) {
		this.soc = soc;
		this.person = p;
		this.info = info;
	}

	@Override
	public void run() {

		String request ="";
		
		while(true) {
			synchronized (person) {
				for(int i=0; i<person.size(); i++) {
					if(soc.equals(person.get(i).getSoc())) {
						chat = person.get(i).isChat();
					}
				}
			}
			
			if(!chat) {
				request = choice();
				
				if(request.equals("join")) {
					doJoin();
				}
				
				else if(request.equals("list")){
					showList();
				}
				
				else if(request.equals("logout")) {
					logout();
					break;
				}		
			}			
		}
	}

	private String choice() {
		String menu = "";
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);	

			pw.println("[��ɾ� �ȳ�]");
			pw.println("ä�ù� �̿� : join \t ������ ��ȸ : list  \t ���� : logout");
			menu = br.readLine();

		} catch (IOException e) {}

		return menu;
	}
	
	private void doJoin() {
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);	
			
			name = new ArrayList<String>();
			synchronized (person) {
				for(int i=0; i<person.size(); i++) {
					if(soc.equals(person.get(i).getSoc())) {
						name.add(person.get(i).getName());
						person.get(i).setChat(true);
					}
				}
			}
			String request = "";
			
			file = new MyFile();
			con = new ArrayList<String>();
			arr = new ArrayList<PrintWriter>();
			arr.add(pw);
			socket = new ArrayList<Socket>();
			socket.add(soc);
			boolean op = true;
			while(op) {
				pw.println("��ȭ�ϰ� ���� ���(�̸� �Ǵ� ���̵�)�� �Է��ϼ���(�Ϸ�� \"/����\") ");
				request = br.readLine();
				if(!request.equals("/����")) {
					synchronized (person) {
						int cnt = 0;
						for(int i=0; i<person.size(); i++) {
							if(person.get(i).getName().equals(request) || info.get(i).getId().equals(request)){
								cnt++;
								if(info.get(i).isStat() && !person.get(i).isChat()) {
									person.get(i).setChat(true);
									socket.add(person.get(i).getSoc());
									name.add(person.get(i).getName());
									arr.add(new PrintWriter(new OutputStreamWriter
											(person.get(i).getSoc().getOutputStream()),true));
								}else if(info.get(i).isStat()){pw.println(request + "���� ä�����Դϴ�");
								}else {pw.println(request + "���� �������� �ƴմϴ�");}
						}
					}if(cnt == 0) pw.println(request + "��(��) ���� �̸��Դϴ�");
					}
				}else {op = false;}		
			}for(int i=1; i<arr.size(); i++) {
				arr.get(i).println(name.get(0) + "���� �ʴ��ϼ̽��ϴ�.");
				arr.get(i).println("[���� �ѹ� �����ּ���]");
				
			}for(int i=0; i<socket.size(); i++) {
				new Room(name.get(i), socket.get(i), arr, person, con, file, i).start();
			}

//			br.close();
//			pw.close();

	} catch (IOException e) {}
	}
	
	private void showList() {
		try {
			pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);
			
			int cnt=0;
			for(int i=0; i<person.size(); i++) {
				if(info.get(i).isStat()) {
					if(person.get(i).isChat())
						pw.println(++cnt + ". " + person.get(i).getName() + "(" + info.get(i).getId() + ")" + "[ä����]");
					else pw.println(++cnt + ". " + person.get(i).getName() + "(" + info.get(i).getId() + ")");					
				}
			}
			pw.println("�� ������ �� : " + cnt +"��");
			
		} catch (IOException e) {}
	}
	
	private void logout() {
		synchronized (info) {
			for(int i=0; i<person.size(); i++) {
				if(soc.equals(person.get(i).getSoc())) {
					for(int j=0; j<info.size(); j++) {
						if(person.get(i).getId().equals(info.get(j).getId())) {
							info.get(j).setStat(false);
							pw.println(person.get(i).getName()+"�� �α׾ƿ��ϼ̽��ϴ�");
							try {
								new MyFile().saveInfo(info);
							} catch (FileNotFoundException e) {
							} catch (IOException e) {}
						}
					}
				}
			}				
		}
	}
}
