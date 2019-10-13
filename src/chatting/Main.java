package chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import Info.Info;
import Info.Manager;
import Info.MyFile;
import Info.Person;

class Mytmp extends Thread{
	Socket soc = null;
	Manager man = null;
	
	ArrayList<Person> person = null;
	ArrayList<Info> info = null;
	
	BufferedReader br = null;
	
	ExecutorService executorService = null; 
	
	public Mytmp(Socket soc, ArrayList<Person> person, ArrayList<Info> info, ExecutorService executorService) {
		this.soc = soc;
		this.person = person;
		this.info = info;
		this.executorService = executorService;
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			
			man = new Manager(soc, person, info, executorService);
			
			String ans = br.readLine();
			
			if(ans.equals("1")) {
				man.doLogin();
			}
			else if(ans.equals("2")) {
				man.doJoin();
				man.doLogin();
			}

		} catch (IOException e) {}
	}	
}

public class Main {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		ServerSocket serverSocket = null;
		Socket soc = null;

		MyFile file = new MyFile();

		ArrayList<Person> person = null;
		ArrayList<Info> info = null;

		try {
			person = file.loadPerson();
			info = file.loadInfo();
		} catch (Exception e) {}

		try {
			for(int i=0; i<info.size();i++) {
				person.get(i).setChat(false);
				info.get(i).setStat(false);
//				System.out.println(info.get(i));
			}
			System.out.println("Info : " + info.size() + "ÆÞ½¼ : " + person.size());
			serverSocket = new ServerSocket(55555);
			System.out.println("Æ÷Æ®¿­¸²....");
			boolean op = true;
		while(op) {
			soc = serverSocket.accept();
			System.out.println(soc.toString());
			new Mytmp(soc, person, info, executorService).start();
			}
		}catch (Exception e) {e.printStackTrace();}
		finally {
			try {
				file.saveInfo(info);
				file.savePerson(person);
				if(serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
					System.out.println("¼­¹öÁ¾·á");
				}
			} catch (Exception e) {}
		}		
	}
}