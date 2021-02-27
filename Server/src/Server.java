import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class Server {

	private static ServerSocket listener;

	/* 
	 * Application serveur 
	 */

	public static void main(String[] args) throws Exception {

		
		// 1. Initialisation des variables globales
		int clientNumber = 0; // Compteur qui s'incr�mente avec chaque nouvelle connexion au serveur
		String serverAddress = "127.0.0.1"; // Adresse du serveur
		int serverPort = 5000; // Port du serveur

		// 2. Cr�ation du socket pour communiquer avec les clients
		listener = new ServerSocket(); 
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);

		// 3. Association (bind) de l'adresse et du port au serveur
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("Le serveur op�re sur l'adresse %s:%d%n", serverAddress, serverPort);

		try {
			/*
			 * 4. Lors de chaque nouvelle connexion client, on exécute la fonction Run()
			 * de l'objet ClientHandler.
			 */
			while (true) {
				/*
				 * La fonction accept() reste bloquée en attendant qu'une application client fasse
				 * un requète de connexion. Le compteur clientNumber est incrémenté de 1 avec chaque
				 * nouvelle connexion. 
				 */
				
				new ClientHandler(listener.accept(), clientNumber++, serverAddress).start();

			}

		} finally {
			// 5. Fermeture de la connexion
			listener.close();
		}
	}

	/*
	 * 6. ClientHandler est une thread qui se charge de traiter les demandes de plusieurs clients 
	 * simultanément, sur des ports diffèrents.
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

		// 7. Run() est une thread qui permet d'avoir une communication bidirectionnelle avec le client
		public void run() {

			try {
				
				// Canal sortant pour envoyer des messages au client
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				// Canaux entrant pour recevoir les messages du client
				Scanner sc = new Scanner(socket.getInputStream());
				DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				FileManager dir = new FileManager(System.getProperty("user.dir"),"Stockage");	// Le client se retrouvera directement dans le dossier Stockage
				// Envoie d'un message au client
				out.writeUTF("Bienvenue sur le serveur! Vous �tes le client #" + clientNumber + ". Veuillez entrer une commande.");
				while (true) {

					String strClient = "";
					
					LocalDateTime dateTime = LocalDateTime.now();
					DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
					String formattedDateTime = dateTime.format(myFormatObj);
					int port = socket.getPort();
					
					
					while ((strClient=sc.nextLine())!= null) {
						in.readNBytes(in.available());// Vider le InputStream
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
							if(!dir.contains(command[1])) {
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
							dir.mkdir(command[1], out); // command[1] == Nom de dossier écrit par le client
							break;
						case "remove":
							if(!dir.contains(command[1])) {
								out.writeUTF("Il n'y a pas de dossier ou de fichier nomm� "+command[1]);
								break;
							}
							FileManager file = new FileManager(dir,command[1]);
							 if(file.isFile())
								 out.writeUTF("Le fichier "+ command[1] +" a �t� effac�");
					         else
					        	 out.writeUTF("Le dossier "+ command[1] +" a �t� effac�");
							file.delete();
							break;
						case "upload":
								dir.saveFile(in, command[1]);
								in.readNBytes(in.available()); // Vider le InputStream
							break;
						case "download":
							if(dir.contains(command[1])) {
								out.writeUTF("ready");
								dir.sendFile(command[1],out);}
							break;
						case "exit":
							out.writeUTF("Vous avez �t� d�connect� avec succ�s");
							return;
						default:
							out.writeUTF("La commande n'a pas �t� reconnue");
						}
						
						out.writeUTF("end");
						command[0]= "";
						if(command.length>1) // Si la commande a un deuxieme argument on le réinitialise
							command[1]="";
					}	 
				}
				
			} catch (IOException e) {
				System.out.println("Erreur lors du traitement du client# " + clientNumber + ": " + e);
			} finally {
				try {
					// Fermeture de la connexion avec le client
					socket.close();
					

					// terminate
					// System.exit(0);
					// sc.close();
				} catch (IOException e) {
					System.out.println("Erreur lors de la fermeture du socket");
				}
				System.out.println("Connexion avec client# " + clientNumber + " ferm�e");
			}
		}
	}
}
