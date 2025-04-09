import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

// Patrick Kallenbach - CNT4007 Project 3

public class chat {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Please input a port number.");
		}
		else {
			int port = Integer.parseInt(args[0]);
			ServerSocket listener = new ServerSocket(port);
			Socket peerSocket;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			int peerPort = Integer.parseInt(bufferedReader.readLine());

			peerSocket = new Socket("localhost", peerPort);

			try {
				new Handler(listener.accept()).start();
				System.out.println("Connected to peer! Start chatting");

				ObjectOutputStream out = new ObjectOutputStream(peerSocket.getOutputStream());
				out.flush();
				
				String message;
				while (true) {
					//read a sentence from the standard input
					message = bufferedReader.readLine();
					//Send the sentence to the server
					sendMessage(message, out);
				}
			} finally {
				listener.close();
				peerSocket.close();
			} 
		}

	}

	/**
     	* A handler thread class.  Handlers are spawned from the listening
     	* loop and are responsible for dealing with a single client's requests.
     	*/
    	private static class Handler extends Thread {
        	private String message;    //message received from the client
		private Socket connection;
        	private ObjectInputStream in;	//stream read from the socket

        	public Handler(Socket connection) {
            		this.connection = connection;
        	}

        public void run() {
 		try{
			//initialize Input and Output streams
			in = new ObjectInputStream(connection.getInputStream());
			try{
				while(true)
				{
					//receive the message sent from the client
					message = (String)in.readObject();
					//show the message to the user
					System.out.println("Chat: " + message);
				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException ioException){
			System.out.println("Disconnect with Peer");
		}
		finally{
			//Close connections
			try{
				in.close();
				connection.close();
			}
			catch(IOException ioException){
				System.out.println("Disconnect with Peer");
			}
		}
	}


    }
	//send a message to the output stream
	public static void sendMessage(String msg, ObjectOutputStream out)
	{
		try{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

}
