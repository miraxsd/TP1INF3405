import java.io.DataInputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

	private static Socket socket;
		
	/* Application client
	 */
	public static void main (String[] args) throws Exception
	{
		// Address et port du serveur
		System.out.println("Enter IP address :");
		Scanner sc = new Scanner(System.in);
		String serverAddress = sc.next();
		/*String serverAddress = "127.0.0.1";
		String [] checkSA = serverAddress.split("\\.");
		Boolean ipValide = false;
		
		while (ipValide=false)
		{
			if (Integer.parseInt(checkSA[0]>255 || checkSA[1]>255 || checkSA[2]>255 || checkSA[3]>255);
			
		}*/
		System.out.println("Enter port number :");
		int port = sc.nextInt();
		System.out.format(serverAddress + " and " + port);
		sc.close();
		
		// Création d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, port);
		System.out.println("The server is running on " + serverAddress+ ":"+ port);
		// Création d'un canal entrant pour recevoir les messages envoyés par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		// Attente de la réception d'un message envoyé par le serveur sur le canal
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
		// Fermeture de la connexion avec le serveur
		socket.close();
	}
}
