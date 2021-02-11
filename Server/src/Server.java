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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {

	private static ServerSocket listener;

	/* Application Serveur */

	public static void main(String[] args) throws Exception {

		// Compteur incrémenté à chaque connexion d'un client au serveur
		int clientNumber = 0;

		// Adresse et port du serveur
		String serverAddress = "127.0.0.1";
		int serverPort = 5000;

		// Création de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);

		// Association de l'adresse et du port à la connexion
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);

		try {
			/*
			 * À chaque fois qu'un nouveau client se connecte, on exécute la fonction Run()
			 * de l'objet ClientHandler.
			 */
			while (true) {
				// Important: la fct accept() est bloquante : attend qu'un prochain client se
				// connecte
				// Une nouvelle connexion : on incrémente le compteur clientNumber
				new ClientHandler(listener.accept(), clientNumber++, serverAddress, serverPort).start();

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
		private int serverPort;

		public ClientHandler(Socket socket, int clientNumber, String serverAddress, int serverPort) throws IOException {
			this.socket = socket;
			this.clientNumber = clientNumber;
			this.serverAddress = serverAddress;
			System.out.println("New connection with client#" + clientNumber + " at " + socket);

		}

		/* Une thread qui se charge d'envoyer au client un message de bienvenue */
		public void run() {

			try {
				// to send data to the client
				PrintStream ps = new PrintStream(socket.getOutputStream());
				// Création d'un canal sortant pour envoyer des messages au client
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				// to read data coming from the client
				 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				FileManager dir = new FileManager(System.getProperty("user.dir"));
				// to read data from the keyboard
				//BufferedReader kb = new BufferedReader(new InputStreamReader(Socket.in));
				// Envoie d'un message au client
				out.writeUTF("Hello from server - you are client #" + clientNumber + ". What would you like to do?");
				while (true) {

					String strClient="";
					
					LocalDateTime dateTime = LocalDateTime.now();
					DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
					String formattedDateTime = dateTime.format(myFormatObj);
					int port = socket.getPort();

					while ((strClient = in.readUTF()) != null) {
						System.out.println(
								"[" + serverAddress + ":" + port + " - " + formattedDateTime + "] : " + strClient);
						// Switch Case pour les commandes
						String command = strClient.split(" ")[0];
						switch (command) {
						case "ls":
							dir.ls(out);
							break;
						case "cd":
							in.readUTF();
							out.writeUTF("Je suis cd qu'est ce que vous voulez de moi?");
							break;
						case "mkdir":
							in.readUTF();
							// create an abstract pathname de type File object
							String path = ""; // il faut modifier cette ligne pour prendre le nom du fichier choisi par
												// l'utilisitateur
							File file = new File(path);
							if (file.mkdir()) {
								System.out.println("Directory is created");
							} else {
								System.out.println("Directory can't be created");
							}
							break;
						case "upload":
							in.readUTF();
							out.writeUTF("Je suis upload qu'est ce que vous voulez de moi?");
							break;
						case "download":
							in.readUTF();
							out.writeUTF("Je suis download qu'est ce que vous voulez de moi?");
							break;
						case "exit":
							out.writeUTF("Vous avez été déconnecté avec succès");
							break;
						default:
							out.writeUTF("La commande n'a pas été reconnue");
							// break;
						}
						// ps.println("Command executed");

						out.writeUTF("end");
						command = "";
						// in.
						break;
					}
				}

			} catch (IOException e) {
				System.out.println("Error handling client# " + clientNumber + ": " + e);
			} finally {
				try {
					// Fermeture de la connexion avec le client
					socket.close();

					// terminate
					// System.exit(0);

				} catch (IOException e) {
					System.out.println("Couldn't close a socket, what's going on?");
				}
				System.out.println("Connection with client# " + clientNumber + " closed");
			}
		}
	}
}
