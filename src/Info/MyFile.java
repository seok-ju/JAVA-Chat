package Info;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyFile {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	Date time = new Date();
	
	private String now = format.format(time);
	private String name = " "; 
	
	File f = new File("info.dat");
	File f2 = new File("person.dat");
	File f3 = null;
	
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	
	BufferedWriter bw = null;
	
	ArrayList<Info> arrI = new ArrayList<Info>();
	ArrayList<Person> arrP = new ArrayList<Person>();
	
	public void addName(String name) {
		this.name += name + " ";
	}
	
	public ArrayList<Info> loadInfo() {
		if(f.exists()) {
				try {
					ois = new ObjectInputStream(new FileInputStream(f));
					while(true) {
						Info tmp = (Info)ois.readObject();
						arrI.add(tmp);
					}
				} catch (ClassNotFoundException e) {
				} catch (IOException e) {
				} finally {
					try {
						ois.close();
					} catch (IOException e) {
					}					
				}			
			return arrI;
		}		
		return arrI;
	}
	
	public ArrayList<Person> loadPerson() {
		if(f2.exists()) {
			try {
				ois = new ObjectInputStream(new FileInputStream(f2));
				while(true) {
					Person tmp = (Person)ois.readObject();
					arrP.add(tmp);
				}
			} catch (ClassNotFoundException e) {
			} catch (IOException e) {
			} finally {
				try {
					ois.close();
				} catch (IOException e) {
				}					
			}			
		return arrP;
	}		
	return arrP;
	}
	
	public void saveInfo(ArrayList<Info> arr) throws FileNotFoundException, IOException {
		oos = new ObjectOutputStream(new FileOutputStream(f));
		for(int i=0; i<arr.size(); i++) {
			oos.writeObject(arr.get(i));
			oos.flush();
		}
		oos.close();
	}
	
	public void savePerson(ArrayList<Person> arr) throws FileNotFoundException, IOException {
		oos = new ObjectOutputStream(new FileOutputStream(f2));
		for(int i=0; i<arr.size(); i++) {
			oos.writeObject(arr.get(i));
			oos.flush();
		}
		oos.close();
	}
	
	public void saveChat(String con) {
		f3 = new File(now + "(" + name + ").txt");
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f3)));
			bw.write(con);

			bw.close();
			
		} catch (FileNotFoundException e) {}
		catch (IOException e) {}
	}
	
}
