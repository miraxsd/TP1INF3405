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
		ClientFileManager dir = new ClientFileManager(System.getProperty("user.dir"), "WorkSpace");

		// 1. Demander au client d'entrer l'address et port du serveur
		int port = 0;
		System.out.println("Entrez adresse IP du serveur:");
		Scanner kb = new Scanner(System.in);
		String serverAddress = sc.next();
		while (true) {
			// Tant que l'adresse IP n'est pas valide demander d'entrer une autre adresse.
			while (!ipValide(serverAddress)) {
				System.out.println("Adresse IP invalide. Veuillez entrer un autre adresse IP valide:");
				serverAddress = sc.next();
			}
			// Lire le port entré
			port = readPort(sc);

			if (socket != null)
				socket.close();

			// 1. Création d'une nouvelle connexion avec le serveur
			
			try {
				socket = new Socket(serverAddress, port);
				// Attendre que le client soit connecté au serveur
				// Si la connection est 'timed out', demander au client d'entrer une nouvelle
				// adresse du serveur
				synchronized (socket) {
					socket.notify();
				}
			} catch (java.net.ConnectException e) {
				System.out.println("Impossible de se connecter à ce serveur");
				serverAddress = "";
				port = 0;
				continue;
			}
			break;
		}
		System.out.println("Le serveur opère sur l'adresse " + serverAddress + ":" + port);

		// Création d'un canal entrant pour recevoir les messages envoyés par le
		// serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Attente de la réception d'un message envoyé par le serveur sur le canal
		String messageFromServer = in.readUTF();
		System.out.println(messageFromServer);

		// Création d'un canal sortant pour écrire au serveur
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		PrintStream ps = new PrintStream(socket.getOutputStream());
		String strKb = "";

		// Saisir entrée du client au clavier

		while ((strKb = kb.nextLine()) != null) {

			// Transmettre message au serveur
			ps.println(strKb);
			in.readNBytes(in.available());// Vider le InputStream
			String[] command = strKb.split(" ");
			switch (command[0]) {
			case "upload":
				// Si le client veut télécharger un fichier au serveur
				if (dir.contains(command[1])) { // N'envoyer le fichier que s'il existe
					out.writeUTF("ready"); // Informe le serveur que le fichier demandé existe
					dir.sendFile(command[1], out);
					System.out.println("Le fichier " + command[1] + " a bien été téléversé.");
				} else {
					out.writeUTF("sorry");
					System.out.println("Le fichier " + command[1] + " n'existe pas");
				}
				break;
			case "download":

				if (in.readUTF().equals("ready")) {
					dir.saveFile(in, command[1]);
					System.out.println("Le fichier " + command[1] + " a bien été téléchargé.");
				} else
					System.out.println(
							"Aucun fichier nommé " + command[1] + " n'existe dans le dossier actuel du serveur.");
				in.readNBytes(in.available());// Vider le InputStream
				break;
			default:
				// Recevoir message du serveur
				readMessagesFromServer(in);
				break;
			}
			strKb = "";
			command[0] = "";
			if (command.length > 1) // Si la commande a un deuxième argument, on le réinitialise
				command[1] = "";
		}

		// Fermeture de la connexion avec le serveur
		socket.close();
		// Fermeture des connexions pour lire et écrire
		ps.close();
		kb.close();
		sc.close();
	}
	
	// Fonction qui vérifie le format de l'adresse IP entrée par l'utilisateur
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
		}
		;
	}

	// Inspiré de
	// https://stackoverflow.com/questions/17985575/checking-if-input-from-scanner-is-int-with-while-loop-java
	public static int readPort(Scanner sc) {
		int port = 0;
		System.out.println("Entrez numéro de port du serveur :");
		if (sc.hasNextInt()) {
			port = sc.nextInt();
		} else {
			sc.next();
			port = 0;
		}
		while (port > 5050 || port < 5000) {
			System.out.print("Numéro de port invalide. Veuillez enter un numéro de port entre 5000 et 5050 :");
			if (sc.hasNextInt()) {
				port = sc.nextInt();
			} else {
				sc.next(); // jeter l'entrée
				port = 0;
			}
		}
		return port;
	}

}
