import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {

	private static ServerSocket listener;

	/* Application Serveur */

	public static void main(String[] args) throws Exception {

		// Compteur incr�ment� � chaque connexion d'un client au serveur
		int clientNumber = 0;

		// Adresse et port du serveur
		String serverAddress = "127.0.0.1";
		int serverPort = 5000;

		// Cr�ation de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);

		// Association de l'adresse et du port � la connexion
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("Le serveur op�re sur l'adresse %s:%d%n", serverAddress, serverPort);

		try {
			/*
			 * � chaque fois qu'un nouveau client se connecte, on ex�cute la fonction Run()
			 * de l'objet ClientHandler.
			 */
			while (true) {
				// Important: la fct accept() est bloquante : attend qu'un prochain client se
				// connecte
				// Une nouvelle connexion : on incr�mente le compteur clientNumber
				new ClientHandler(listener.accept(), clientNumber++, serverAddress).start();

			}

		} finally {
			// Fermeture de la connexion
			listener.close();
		}
	}

	/*
	 * Une thread qui se charge de traiter la demande de chaque client sur un socket
	 * particulier.
	 */
	private static class ClientHandler extends Thread {
		private Socket socket;
		private int clientNumber;
		private String serverAddress;
		

		public ClientHandler(Socket socket, int clientNumber, String serverAddress) throws IOException {
			this.socket = socket;
			this.clientNumber = clientNumber;
			this.serverAddress = serverAddress;
			System.out.println("Nouvelle connexion avec client#" + clientNumber + " sur " + socket);

		}

		/* Une thread qui se charge d'envoyer au client un message de bienvenue */
		public void run() {

			try {
				
				// Cr�ation d'un canal sortant pour envoyer des messages au client
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				// to read data coming from the client
				Scanner sc = new Scanner(socket.getInputStream());
				DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				FileManager dir = new FileManager(System.getProperty("user.dir"),"Stockage");	// Le client se retrouvera directement dans le dossier Stockage
				// Envoie d'un message au client
				out.writeUTF("Bienvenue sur le serveur! Vous �tes le client #" + clientNumber + ". Veuillez entrer une commande.");
				while (true) {

					String strClient, fileName ="";
					
					LocalDateTime dateTime = LocalDateTime.now();
					DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
					String formattedDateTime = dateTime.format(myFormatObj);
					int port = socket.getPort();
					
					
					while ((strClient=sc.nextLine())!= null) {
						
						System.out.println(
								"[" + serverAddress + ":" + port + " - " + formattedDateTime + "] : " + strClient);
						
						// Switch Case pour les commandes
						String [] command = strClient.split(" ");
						
						switch (command[0]) {
						case "ls":
							dir.ls(out);
							break;
						case "cd":
							
                            
                            if(!command[1].equals("..")) 
                            {
                            Boolean folderExists=false;
							for (File file : dir.listFiles()) {
								if(file.getName().equals(command[1]))
									folderExists=true;
							}
							if(!folderExists) {
								out.writeUTF("Il n'y a pas de dossier "+command[1]);
								break;
							}
								dir = new FileManager(dir,command[1]);
                                out.writeUTF("Vous �tes dans le dossier "+ command[1]);
                            }
                            else {
                            	dir = new FileManager(dir,command[1]);
                                String separator = File.separator.equals("/") ? "/" : "\\\\";
                                String [] pathName = dir.getCanonicalPath().split(separator);
                                out.writeUTF("Vous �tes dans le dossier "+ pathName[pathName.length -1]);
                            }
                            
                            break;
						case "mkdir":
							dir.mkdir(command[1], out); // command[1] == Nom de dossier �crit par le client
							dir = new FileManager(dir,command[1]);
							break;
						case "upload":
							//in.readUTF();
							try {
								dir.saveFile(in, command[1]);
								in.readNBytes(in.available()); // Vider le InputStream
								//out.writeUTF("upload "+command[1]);
							}
							catch(Exception e) {
								System.out.println(e.toString()); // On peut mettre write le fichier n'existe pas
								continue;
							}
							break;
						case "download":
							//in.readUTF();
							try {
							dir.sendFile(command[1],out);
							//out.writeUTF("download "+command[1]);
							}
							catch(Exception e) {
								System.out.println(e.toString());
								continue;
							}
							break;
						case "exit":
							out.writeUTF("Vous avez �t� d�connect� avec succ�s");
							//break;
							return;
						default:
							out.writeUTF("La commande n'a pas �t� reconnue");
							// break;
						}
						
						out.writeUTF("end");
						command[0]= "";
						if(command.length>1)
							command[1]="";
						
						// in.
						//break;
					}	 
				}
				
			} catch (IOException e) {
				System.out.println("Erreur lors du traitement du client# " + clientNumber + ": " + e);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					// Fermeture de la connexion avec le client
					socket.close();
					

					// terminate
					// System.exit(0);

				} catch (IOException e) {
					System.out.println("Erreur lors de la fermeture du socket");
				}
				System.out.println("Connexion avec client# " + clientNumber + " ferm�e");
			}
		}
	}
}
