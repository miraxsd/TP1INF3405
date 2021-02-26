import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.Arrays;
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
		System.out.println("Entrez adresse IP du serveur:");
		
		String serverAddress = sc.next();

		while (!ipValide(serverAddress)) {
			System.out.println("Adress IP invalide. Veuillez entrer un autre adresse IP valide:");
			serverAddress = sc.next();
		}
		System.out.println("Entrez numéro de port du serveur :");
		int port = 0;
		while ((port < 5000) || (port > 5050)) { //il y un problème ici si on rentre un port invalid. 
			try {
				port = sc.nextInt();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Numéro de port invalide. Veuillez enter un numéro de port entre 5000 and 5050 :");
			}
			if (port < 5000 || port > 5050)
				System.out.println("Numéro de port invalide. Veuillez enter un numéro de port entre 5000 and 5050 :");
		}
		
		if (socket != null)
			socket.close();
		
		Scanner kb = new Scanner(System.in);
		// 1. Création d'une nouvelle connexion avec le serveur
		//String serverAddress="127.0.0.1";
		//int port=5000;
		socket = new Socket(serverAddress, port);
		System.out.println("Le serveur opère sur l'adresse " + serverAddress + ":" + port);

		// Création d'un canal entrant pour recevoir les messages envoyés par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Attente de la réception d'un message envoyé par le serveur sur le canal
		String messageFromServer = in.readUTF();
		System.out.println(messageFromServer);

		// Création d'un canal sortant pour écrire au serveur
		
		
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		PrintStream ps = new PrintStream(socket.getOutputStream());
		String strKb="";

		// Saisir entrée du client au clavier
		
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
					}
				else 
					System.out.println("le fichier "+command[1]+" n'existe pas");
				break;
			case "download":
				ps.println(strKb);
				if(in.readUTF().equals("ready")) {
				dir.saveFile(in,command[1]);
				}
				else
					System.out.println("Aucun fichier nommé "+command[1]+" n'existe dans le dossier actuel du serveur");
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
			if(command.length>1) // Si la commande a un deuxieme argument on le réinitialise
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

}
