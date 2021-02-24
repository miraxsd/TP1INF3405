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
		// Address et port du serveur
		System.out.println("Entrez adresse IP du serveur:");
		
		String serverAddress = sc.next();

		while (!ipValide(serverAddress)) {
			System.out.println("Adress IP invalide. Veuillez entrer un autre adresse IP valide:");
			serverAddress = sc.next();
		}
		System.out.println("Entrez num�ro de port du serveur :");
		int port = 0;
		while ((port < 5000) || (port > 5050)) { //il y un probl�me ici si on rentre un port invalid. 
			try {
				port = sc.nextInt();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Num�ro de port invalide. Veuillez enter un num�ro de port entre 5000 and 5050 :");
			}
			if (port < 5000 || port > 5050)
				System.out.println("Num�ro de port invalide. Veuillez enter un num�ro de port entre 5000 and 5050 :");
		}
		
		if (socket != null)
			socket.close();
		
		Scanner kb = new Scanner(System.in);
		// 1. Cr�ation d'une nouvelle connexion avec le serveur
		//String serverAddress="127.0.0.1";
		//int port=5000;
		socket = new Socket(serverAddress, port);
		System.out.println("Le serveur op�re sur l'adresse " + serverAddress + ":" + port);

		// Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Attente de la r�ception d'un message envoy� par le serveur sur le canal
		String messageFromServer = in.readUTF();
		System.out.println(messageFromServer);

		// Cr�ation d'un canal sortant pour �crire au serveur
		
		
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		PrintStream ps = new PrintStream(socket.getOutputStream());
		String strKb;

		// Saisir entr�e du client au clavier
		
			while ((strKb = kb.nextLine()) != null) {

			// Transmettre message au serveur
			
			ps.println(strKb);
			
			// Si le client veut Upload un fichier au serveur
			if(in.readUTF().equals("upload")) {
				ClientFileManager.sendFile(in.readUTF(),out);
			}
			// Recevoir message du serveur
			readMessagesFromServer(in);
		}

		// Fermeture de la connexion avec le serveur
		socket.close();
		// close connections with read/write
		//out.close();
		ps.close();
		// }catch(java.net.BindException e) { socket.close();}
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
				if(in.readUTF().equals("download")) {
					ClientFileManager.saveFile(in, in.readUTF());
				}
					
				System.out.println(messageFromServer);
			}
		} catch (java.io.EOFException ignore) {
		}
		;

	}

}
