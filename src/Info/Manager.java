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
			pw.println("1. 로그인           2. 회원가입");
		} catch (IOException e) {}
	}

	public void doJoin() {
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);
			
			boolean op = true;
			String id = "";
			while(op) {
				pw.println("[회원가입]");
				pw.println("아이디 입력");
				id = br.readLine();
				for(int i=0; i<info.size(); i++) {
					if(id.equals(info.get(i).getId())) {
						pw.println("아이디 중복");
						break;
					}else if(i==info.size()-1) op=false;
				}if(info.size()==0) break;
			}
			pw.println("이름 입력");
			String name = br.readLine();
			pw.println("비밀번호 입력");
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
				pw.println("[로그인]");
				pw.println("아이디 입력");
				id = br.readLine();
				pw.println("비밀번호 입력");
				pwd = br.readLine();
				
				op = login(id, pwd);
				if(op)pw.println("다시 입력하세요");

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
						pw.println(name + "(" + info.get(i).getId() + ")" + "님 환영합니다");
						return false;
					}else pw.println("이미 접속중입니다");
				}
			}
		}
		return true;
	}
	
	private void goLobby() {
		executorService.execute(new Lobby(soc, person, info));
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
		
		int poolSize = threadPoolExecutor.getPoolSize();//스레드 풀 사이즈 얻기
		String threadName = Thread.currentThread().getName();//스레드 풀에 있는 해당 스레드 이름 얻기
		
		System.out.println("[총 스레드 개수:" + poolSize + "] 작업 스레드 이름: "+threadName);
	}
}