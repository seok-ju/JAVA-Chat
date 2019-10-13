package Info;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import chatting.Lobby;

public class Manager {
	ExecutorService executorService = null;
	
	Socket soc = null;
	
	ArrayList<Person> person = null;
	ArrayList<Info> info = null;
	
	BufferedReader br = null;
	PrintWriter pw = null;
	
	public Manager(Socket soc, ArrayList<Person> person, ArrayList<Info> info, ExecutorService executorService) {
		this.soc = soc;
		this.person = person;
		this.info = info;
		this.executorService = executorService;
		
		try {
			pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);
			pw.println("1. �α���           2. ȸ������");
		} catch (IOException e) {}
	}

	public void doJoin() {
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);
			
			boolean op = true;
			String id = "";
			while(op) {
				pw.println("[ȸ������]");
				pw.println("���̵� �Է�");
				id = br.readLine();
				for(int i=0; i<info.size(); i++) {
					if(id.equals(info.get(i).getId())) {
						pw.println("���̵� �ߺ�");
						break;
					}else if(i==info.size()-1) op=false;
				}if(info.size()==0) break;
			}
			pw.println("�̸� �Է�");
			String name = br.readLine();
			pw.println("��й�ȣ �Է�");
			String pwd = br.readLine();
			
			join(id,pwd,name);

		} catch (IOException e) {}
	}
	
	private synchronized void join(String id, String pwd, String name) throws FileNotFoundException, IOException {
		info.add(new Info(id, pwd));
		person.add(new Person(name, id));
		new MyFile().savePerson(person);
		new MyFile().saveInfo(info);
		Thread.yield();
	}
	
	public void doLogin() {
		boolean op = true;
		String id, pwd;
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);
			while(op) {
				pw.println("[�α���]");
				pw.println("���̵� �Է�");
				id = br.readLine();
				pw.println("��й�ȣ �Է�");
				pwd = br.readLine();
				
				op = login(id, pwd);
				if(op)pw.println("�ٽ� �Է��ϼ���");

			}
		} catch (IOException e) {}
	}
	
	private synchronized boolean login(String id, String pwd) {
		for(int i=0; i<info.size(); i++) {
			if(id.equals(info.get(i).getId())) {
				String name = person.get(i).getName();
				if(pwd.equals(info.get(i).getPw())) {
					if(!info.get(i).isStat()) {
						info.get(i).setStat(true);
						person.get(i).setSoc(soc);
						goLobby();
						pw.println(name + "(" + info.get(i).getId() + ")" + "�� ȯ���մϴ�");
						return false;
					}else pw.println("�̹� �������Դϴ�");
				}
			}
		}
		return true;
	}
	
	private void goLobby() {
		executorService.execute(new Lobby(soc, person, info));
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
		
		int poolSize = threadPoolExecutor.getPoolSize();//������ Ǯ ������ ���
		String threadName = Thread.currentThread().getName();//������ Ǯ�� �ִ� �ش� ������ �̸� ���
		
		System.out.println("[�� ������ ����:" + poolSize + "] �۾� ������ �̸�: "+threadName);
	}
}