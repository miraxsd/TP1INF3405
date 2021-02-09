import java.io.DataInputStream;
import java.io.DataOutputStream;
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
		
		while (!ipvalide(serverAddress))
		{
			System.out.println("IP adress invalid. Please enter another IP adress :");
			serverAddress=sc.next();
		}
		System.out.println("Enter port number :");
		int port=0;
		while((port<5000) || (port>5050)) {
			try {
				port = sc.nextInt();
			}catch(java.util.InputMismatchException e) {
				System.out.println("Invalid port. Please enter another port between 5000 and 5050 :");
			}
			if(port<5000 || port>5050)
				System.out.println("Invalid port. Please enter another port between 5000 and 5050 :");
		}
		System.out.format(serverAddress + " and " + port);
		
		if(socket!=null)
			socket.close();
		
		// Création d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, port);
		System.out.println("The server is running on " + serverAddress+ ":"+ port);
		// Création d'un canal entrant pour recevoir les messages envoyés par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		// Création d'un canal sortant pour envoyer des messages au serveur
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String commande="";
		//do{
		// Envoie d'un message au serveur
		
		//while(commande!="exit");*/ 
		// Attente de la réception d'un message envoyé par le serveur sur le canal
		String helloMessageFromServer="";
		//helloMessageFromServer = in.readUTF();
		try {
		do{
			/*if(sc.hasNext()) {
			commande = sc.next(); 
			out.writeUTF(commande);}*/
			helloMessageFromServer = in.readUTF();
			System.out.println(helloMessageFromServer);
		}
		while(!helloMessageFromServer.isBlank() /*&& commande.compareTo("exit")!=0*/);
		}catch(java.io.EOFException ignore) {};
		//System.out.println(helloMessageFromServer);
		in.close();
		out.close();
		
		// Fermeture de la connexion avec le serveur
		
		sc.close();
		socket.close();
		
		//}catch(java.net.BindException e) { socket.close();}
	}
	public static boolean ipvalide (String serverAddress) {
		if(serverAddress.endsWith(".")) 
			return false;
	
		String [] checkSA = serverAddress.split("\\.");
		
		int ipLength = checkSA.length; 
		int compteur =0;
		
		if(ipLength !=4)
			return false;
		while(compteur < 4) {
			try {
				if (Integer.parseInt(checkSA[compteur])<=255 && Integer.parseInt(checkSA[compteur])>=0 ) 
					compteur++;
				else 
					return false;
			}catch(NumberFormatException e) {
				return false;
			}		
		}
		return true;
	}	
}






