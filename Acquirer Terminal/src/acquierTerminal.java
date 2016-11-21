import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class acquierTerminal {
	static final String token_requestor_id = "VALIDREQUESTOR1";
	static final String HOST = "localhost";
	static final int PREPORT = 2222;
	static final int PORT = 3333;
	static final int NEXTPORT = 4444;

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

			if (client_packet[0].equals("FROM_MERCHANT")) {
				System.out.println("Acquirer received a packet from Merchant");
				String[] acquirer_packet = new String[client_packet.length+1];
				for (int i=0;i<client_packet.length;i++){
					acquirer_packet[i] = client_packet[i];
				}
				acquirer_packet[0] = "FROM_ACQUIRER";
				acquirer_packet[3] = token_requestor_id;
				sendPacket(acquirer_packet, NEXTPORT);
				System.out.println("Packet is sent to TSP");

				objectInput.close();
				serverSocket.close();
			}

			else if (client_packet[0].equals("FROM_TSP")) {
				System.out.println("Acquirer recevied a packet from TSP");
				client_packet[0] = "FROM_ACQUIRER";
				sendPacket(client_packet, PREPORT);
				System.out.println("Packet is sent back to Merchant");
				objectInput.close();
				serverSocket.close();
			}
		}
	}
	
	public static void main(String[] args){
		acquierTerminal acquirer = new acquierTerminal();
		try{
			acquirer.socketConnection();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
