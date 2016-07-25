import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	ArrayList<PrintWriter> clientOutputStream = new ArrayList<PrintWriter>();
	HashSet<String> usernames = new HashSet<String>();
	Hashtable<String, String> usernames_data = new Hashtable<String, String>(); 

	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	String username, message;

	public class ClientHandler implements Runnable {
		public void run() {
			try{
				while(true) {
					receive();
					
				}
			} catch(Exception e) { e.printStackTrace(); }
		}
	}

	public static void main(String[] args) {
		new Server().go();
	}

	public void go() {
		try{
			ServerSocket serverSock = new ServerSocket(4500);
			while(true) {
				Socket clientSocket = serverSock.accept();
				reader = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
	      		writer = new PrintWriter( clientSocket.getOutputStream() );
	      		clientOutputStream.add(writer);
				System.out.print("New User Joined ....");

				setUsername();				
				Thread t = new Thread( new ClientHandler() );
				t.setName(username);
				t.start();
			}
		} catch(Exception e) { e.printStackTrace(); }
	}

	public void setUsername(){
		try{
			while( (message = reader.readLine()) != null ){
				username = message;
				if( usernames.contains(username) ){
					send("false");
				}
				else
					break;
			}
			usernames.add(username);
			System.out.println(" Connection Established with " + username + " ...");
			send(username);
		} catch(Exception e) { e.printStackTrace(); }
	}

	public void send(String message) {
      try{
        writer.println(message);
        writer.flush();
      } catch(Exception e) { e.printStackTrace(); }
    }

    public void receive() {
      try{
        while( (message = reader.readLine()) != null ) {
        	message = "<" + Thread.currentThread().getName() + ">: " + message;
			System.out.println( message );
        	tellEveryone(message);
		}
      } catch(Exception e) { e.printStackTrace(); }
    }

	public void tellEveryone(String message) {
		Iterator it = clientOutputStream.iterator();
		while( it.hasNext() ) {
			try{
				PrintWriter writer = (PrintWriter)it.next();
				writer.println(message);
				writer.flush();
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
}