package chatting;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{
	InetAddress ip;
	Socket soc;
	Scanner sc;
	BufferedReader br;
	PrintWriter pw;

	public Client() {
		try {
			ip = InetAddress.getByName("192.168.0.51");
			soc = new Socket(ip, 55555);
			
			System.out.println("서버연결");
			sc = new Scanner(System.in);
			pw = new PrintWriter(soc.getOutputStream(),true);
			Thread tr = new Thread(this);
			tr.start();
			
			String msg = "";
			while((msg = sc.nextLine()) != null) {
				pw.println(msg);				
			}
		} catch (Exception e) {}
		finally {
			pw.close();
			try {
				soc.close();
			} catch (IOException e) {}
		}
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			String reciveMsg = "";
			while(true) {
				reciveMsg = br.readLine();
				System.out.println(reciveMsg);
			}
		} catch (IOException e) {}
	finally {
		try {
			br.close();
		} catch (IOException e) {}
	}
		}
	
	public static void main(String[] args) {
		new Client();
	}
}