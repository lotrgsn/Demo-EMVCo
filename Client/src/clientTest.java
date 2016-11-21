import java.awt.Toolkit;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class clientTest {
	static final String HOST = "localhost";
	static final int PORT = 1111;
	static final int NEXTPORT = 2222;
	static final int TSPPORT = 4444;
	String[] final_packet = null;
	String TokenRequestor_ID = "VALIDREQUESTOR1";
	String Token;
	String Expiry_Date;
	
	public void sendPacket(String[] packet, int port) {
	    try {
	    	Socket client_socket = new Socket(HOST, port);
	        OutputStream output = client_socket.getOutputStream();
	        ObjectOutputStream objectOutput = new ObjectOutputStream(output);
	        packet[0] = "FROM_CLIENT";
	        //String[] token_packet = {"USE","233530418", "FROM_CLIENT"};
	        objectOutput.writeObject(packet);
	        System.out.println("Your Request is sent. Please wait...");
	        //objectOutput.flush();
	        //String encode_packet = new String(Hex.encodeHex(output.toByteArray()));
	        objectOutput.close();
	        output.close();
	        client_socket.close();

	    } catch (Exception e) {
	        System.out.println(e.toString());
	    }
	}
	
	public void receivePacket(){
		try{
			ServerSocket serverSocket = new ServerSocket(PORT);
			Socket socket = serverSocket.accept();
			ObjectInputStream objectInput = null;
			InputStream in = socket.getInputStream();
			objectInput = new ObjectInputStream(in);
			final_packet = (String[]) objectInput.readObject();
			
			System.out.println("");
			System.out.println("Here is your receipt:");
			for (int i=0; i<final_packet.length;i++){
				if (final_packet[i] != null){
					System.out.println(final_packet[i]);
				}
			}
			
			Token = final_packet[2];
			TokenRequestor_ID = final_packet[3];
			Expiry_Date = final_packet[4];
			
			serverSocket.close();
			socket.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String receiveNonce(){
		try{
			ServerSocket serverSocket = new ServerSocket(PORT);
			Socket socket = serverSocket.accept();
			ObjectInputStream objectInput = null;
			InputStream in = socket.getInputStream();
			objectInput = new ObjectInputStream(in);
			final_packet = (String[]) objectInput.readObject();
			String nonce;
			
			serverSocket.close();
			socket.close();
			if (final_packet[0] == "FROM_TSP" && final_packet[1] == "NONCE_RESPONSE"){
				nonce = final_packet[2];
			}
			else {
				nonce = "";
			}
			return nonce;	
			
		}catch(Exception e){
			e.printStackTrace();
			return e.toString();
		}
	}
	
	public String getDynamicSecurityCode(){
		return "";
	}

	public static void main(String[] args){
		clientTest client = new clientTest();
		Scanner s = new Scanner(System.in);
		String[] packet = new String[8];
		String nonce = "";
		//String[] Nonce;
		System.out.println("What do you want to do:"
				+ "1. PAY"
				+ "2. ADD");
		String option = s.nextLine();
		s.close();
		
		if (option.equals("PAY")){
			//Request a nonce from Server
			packet[1] = "REQUEST_NONCE";
			client.sendPacket(packet,TSPPORT);
			//Receive Nonce
			nonce = client.receiveNonce();
			
			//Send packet for payment
			//Token Requestor ID, Token, Token Expiry Date, Client Nonce, Dynamic Security Code 
			packet[1] = "USE";
			packet[2] = "1789447862";
			packet[3] = client.TokenRequestor_ID;
			packet[4] = client.Token;
			packet[5] = client.Expiry_Date;
			packet[6] = nonce;
			//Check how to generate DSC
			packet[7] = client.getDynamicSecurityCode();
			
			client.sendPacket(packet,NEXTPORT);
		}
		else if(option.equals("ADD")){
			packet[1] = "ADD";
			packet[2] = "0123456789";
			packet[3] = "0922"; //MMYY
			client.sendPacket(packet,TSPPORT);
		}
		client.receivePacket();
	}
}

