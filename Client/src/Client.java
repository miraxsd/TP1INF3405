import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

	private static Socket socket;

	/*
	 * Application client
	 */
	public static void main(String[] args) throws Exception {
		// Address et port du serveur
		System.out.println("Enter IP address :");
		Scanner sc = new Scanner(System.in);
		String serverAddress = sc.next();

		while (!ipvalide(serverAddress)) {
			System.out.println("IP adress invalid. Please enter another IP adress :");
			serverAddress = sc.next();
		}
		System.out.println("Enter port number :");
		int port = 0;
		while ((port < 5000) || (port > 5050)) {
			try {
				port = sc.nextInt();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Invalid port. Please enter another port between 5000 and 5050 :");
			}
			if (port < 5000 || port > 5050)
				System.out.println("Invalid port. Please enter another port between 5000 and 5050 :");
		}

		if (socket != null)
			socket.close();
		// 1. Création d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, port);
		System.out.println("The server is running on " + serverAddress + ":" + port);

		// Création d'un canal entrant pour recevoir les messages envoyés par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Attente de la réception d'un message envoyé par le serveur sur le canal
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);

		// Création d'un canal sortant pour écrire au serveur
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		// Read data coming from the server
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(socket.getInputStream()));
		
		String strKb;

		// repeat as long as exit
		// is not typed at client
		while (!(strKb = sc.next()).equals("exit")) {

			// send to the server
			out.writeUTF(strKb);

			// receive from the server
			readMessagesFromServer(in);
		}

		// Fermeture de la connexion avec le serveur
		socket.close();
		// close connections with read/write
		out.close();
		// br.close();
		// }catch(java.net.BindException e) { socket.close();}
		sc.close();
	}

	public static boolean ipvalide(String serverAddress) {
		if (serverAddress.endsWith("."))
			return false;

		String[] checkSA = serverAddress.split("\\.");

		int ipLength = checkSA.length;
		int compteur = 0;

		if (ipLength != 4)
			return false;
		while (compteur < 4) {
			try {
				if (Integer.parseInt(checkSA[compteur]) <= 255 && Integer.parseInt(checkSA[compteur]) >= 0)
					compteur++;
				else
					return false;
			} catch (NumberFormatException e) {
				return false;
			}

		}

		return true;
	}

	public static void readMessagesFromServer(DataInputStream in) throws java.io.IOException {
		try {
			while (true) {
				String helloMessageFromServer = "";
				if ((helloMessageFromServer = in.readUTF()).equals("end"))
					break;
				System.out.println(helloMessageFromServer);
			}
		} catch (java.io.EOFException ignore) {
		}
		;

	}

}
