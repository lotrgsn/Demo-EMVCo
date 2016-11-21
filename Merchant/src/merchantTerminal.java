import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class merchantTerminal {
	static final int PREPORT = 1111;
	static final int PORT = 2222;
	static final int NEXTPORT = 3333;
	static final int TSPPORT = 4444;
	static final String HOST = "localhost";

	public void sendPacket(String[] packet, int port) {
		try {
			Socket socket = new Socket(HOST, port);
			OutputStream output = socket.getOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(output);
			objectOutput.writeObject(packet);
			objectOutput.close();
			output.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[] socketConnection() throws Exception {

		while (true) {

			String[] client_packet = null;
			ServerSocket serverSocket = new ServerSocket(PORT);
			Socket socket = serverSocket.accept();
			ObjectInputStream objectInput = null;

			
			InputStream in = socket.getInputStream();
			objectInput = new ObjectInputStream(in);
			client_packet = (String[]) objectInput.readObject();

//			if (client_packet[0].equals("FROM_CLIENT")) {
//				client_packet[0] = "FROM_MERCHANT";
//				System.out.println("Merchant receved packet from client");
//				sendPacket(client_packet, NEXTPORT);
//				System.out.println("Packet is sent to acquirer");
//				objectInput.close();
//				serverSocket.close();
//			}
			
			if (client_packet[0].equals("FROM_CLIENT")) {
				client_packet[0] = "FROM_MERCHANT";
				System.out.println("Merchant receved packet from client");
				sendPacket(client_packet, TSPPORT);
				System.out.println("Packet is sent to TSP");
				objectInput.close();
				serverSocket.close();
			}

//			else if (client_packet[0].equals("FROM_ACQUIRER")) {
//				client_packet[0] = "FROM_MERCHANT";
//				System.out.println("Merchant received a packet from Acquirer");
//				sendPacket(client_packet, PREPORT);
//				System.out.println("Packet is sent back to client");
//				objectInput.close();
//				serverSocket.close();
//			}
			
			else if (client_packet[0].equals("FROM_TSP")) {
				client_packet[0] = "FROM_MERCHANT";
				System.out.println("Merchant received a packet from TSP");
				sendPacket(client_packet, PREPORT);
				System.out.println("Packet is sent back to client");
				objectInput.close();
				serverSocket.close();
			}
		}
	}

	public static void main(String[] args) {
		merchantTerminal merchant = new merchantTerminal();
		try {
			merchant.socketConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
