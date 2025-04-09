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
				System.out.println("Connected to peer!");

				ObjectOutputStream out = new ObjectOutputStream(peerSocket.getOutputStream());
				out.flush();
				ObjectInputStream in = new ObjectInputStream(peerSocket.getInputStream());
				
				String message;
				while (true) {
					System.out.print("Hello, please input a sentence: ");
					//read a sentence from the standard input
					message = bufferedReader.readLine();
					//Send the sentence to the server
					sendMessage(message, out);
					//Receive the upperCase sentence from the server
					String MESSAGE = (String)in.readObject();
					//show the message to the user
					System.out.println("Receive message: " + MESSAGE);
			
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
		private String MESSAGE;    //uppercase message send to the client
		private Socket connection;
        	private ObjectInputStream in;	//stream read from the socket
        	private ObjectOutputStream out;    //stream write to the socket
		private int no = 1; 		//The index number of the client

        	public Handler(Socket connection) {
            		this.connection = connection;
	    		this.no = no;
        	}

        public void run() {
 		try{
			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			try{
				while(true)
				{
					//receive the message sent from the client
					message = (String)in.readObject();
					//show the message to the user
					System.out.println("Receive message: " + message + " from client " + no);
					//Capitalize all letters in the message
					MESSAGE = message.toUpperCase();
					//send MESSAGE back to the client
					sendMessage(MESSAGE, out);
				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException ioException){
			System.out.println("Disconnect with Client " + no);
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				connection.close();
			}
			catch(IOException ioException){
				System.out.println("Disconnect with Client " + no);
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
			System.out.println("Send message: " + msg + " to Client");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

}
