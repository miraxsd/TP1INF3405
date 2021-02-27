import java.io.PrintStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	private static Socket socket;

	/*
	 * Application client
	 */
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		ClientFileManager dir = new ClientFileManager(System.getProperty("user.dir"),"WorkSpace");
		// Address et port du serveur
		int port=0;
		System.out.println("Entrez adresse IP du serveur:");
		Scanner kb = new Scanner(System.in);
		String serverAddress = sc.next();
		while(true) {
		while (!ipValide(serverAddress)) {
			System.out.println("Adresse IP invalide. Veuillez entrer un autre adresse IP valide:");
			serverAddress = sc.next();
		}
		// Lire le port
			port=readPort(sc);
			
		if (socket != null)
			socket.close();
		

		// 1. Cr�ation d'une nouvelle connexion avec le serveur
		//String serverAddress="127.0.0.1";
		//int port=5000;
		
		try {
		socket = new Socket(serverAddress, port);
		//Attendre que le client soit connect� au serveur
		//Si la connection est timed out redemander au client d'entrer une nouvelle adresse du serveur
		synchronized (socket){
		socket.notify();}}
		catch(java.net.ConnectException e) {
			System.out.println("Impossible de se connecter � ce serveur");
			serverAddress="";
			port=0;
			continue;
		}
		break;
		}
		System.out.println("Le serveur opere sur l'adresse " + serverAddress + ":" + port);

		// Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Attente de la r�ception d'un message envoy� par le serveur sur le canal
		String messageFromServer = in.readUTF();
		System.out.println(messageFromServer);

		// Cr�ation d'un canal sortant pour �crire au serveur
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		PrintStream ps = new PrintStream(socket.getOutputStream());
		String strKb="";

		// Saisir entr�e du client au clavier
		
			while ((strKb = kb.nextLine()) != null) {

			// Transmettre message au serveur
			in.readNBytes(in.available());// Vider le InputStream
			String [] command = strKb.split(" ");
			switch (command[0]) {
			case "upload":
				// Si le client veut Upload un fichier au serveur
				if(dir.contains(command[1])) { // N'envoyer le fichier que s'il existe
					ps.println(strKb); // S'il existe envoyer la commande upload au serveur
					dir.sendFile( command[1],out);
					System.out.println("Le fichier "+command[0]+" � bien �t� t�l�vers�.");
					}
				else 
					System.out.println("le fichier "+command[1]+" n'existe pas");
				break;
			case "download":
				ps.println(strKb);
				if(in.readUTF().equals("ready")) {
				dir.saveFile(in,command[1]);
				System.out.println("Le fichier "+command[1]+" a bien �t� t�l�charg�.");
				}
				else
					System.out.println("Aucun fichier nomm� "+command[1]+" n'existe dans le dossier actuel du serveur");
				in.readNBytes(in.available());// Vider le InputStream
				break;
			default:
			ps.println(strKb);
			// Recevoir message du serveur	
			readMessagesFromServer(in);
			break;
			}
			strKb="";
			command[0]= "";
			if(command.length>1) // Si la commande a un deuxieme argument on le r�initialise
				command[1]="";	
		}

		// Fermeture de la connexion avec le serveur
		socket.close();
		// close connections with read/write
		ps.close();
		kb.close();
		sc.close();
	}

	public static boolean ipValide(String serverAddress) {
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
				String messageFromServer = "";
				if ((messageFromServer = in.readUTF()).equals("end"))
					break;
				System.out.println(messageFromServer);
			}
		} catch (java.io.EOFException ignore) {
		};
	}
	// Inspir� de https://stackoverflow.com/questions/17985575/checking-if-input-from-scanner-is-int-with-while-loop-java
	public static int readPort(Scanner sc) {
			int port=0;
			System.out.println("Entrez num�ro de port du serveur :");
			if (sc.hasNextInt()) {
			    port = sc.nextInt();
			} else {
			    sc.next();   
			    port = 0;
			}
			while (port > 5050 || port < 5000) {
			    System.out.print("Num�ro de port invalide. Veuillez enter un num�ro de port entre 5000 et 5050 :");
			    if (sc.hasNextInt()) {
			        port = sc.nextInt();
			     } else {
			       String buffer = sc.next();
			       port = 0;
			    }
			}
			return port;
	}

}
