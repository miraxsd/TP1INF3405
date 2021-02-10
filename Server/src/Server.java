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
	
	public static void main(String[] args) throws Exception
	{
	
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
		
		try
		{
			/* À chaque fois qu'un nouveau client se connecte, on exécute la fonction Run() de l'objet
			 * ClientHandler.
			 */
			while(true) 
			{
				// Important: la fct accept() est bloquante : attend qu'un prochain client se connecte
				// Une nouvelle connexion : on incrémente le compteur clientNumber
				new ClientHandler(listener.accept(), clientNumber++, serverAddress, serverPort).start();
				
			}
			
		}
		finally
		{
			// Fermeture de la connexion
			listener.close();
		}
	}

	/* Une thread qui se charge de traiter la demande de chaque client
	 * sur un socket particulier.
	 */
	private static class ClientHandler extends Thread
	{
		private Socket socket;
		private int clientNumber;
		private String serverAddress;
		private int serverPort;
	
  
		public ClientHandler(Socket socket, int clientNumber, String serverAddress, int serverPort) throws IOException
		{
			this.socket = socket;
			this.clientNumber = clientNumber;
			this.serverAddress = serverAddress;
			System.out.println("New connection with client#"+ clientNumber + " at "+ socket);
			       
		}
		/* Une thread qui se charge d'envoyer au client un message de bienvenue*/
		public void run()
		{
			
			try
			{
		        // to send data to the client 
		        PrintStream ps = new PrintStream(socket.getOutputStream()); 
		  
		        // to read data coming from the client 
		        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
		        //DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		        
		        // to read data from the keyboard 
		        BufferedReader kb = new BufferedReader(new InputStreamReader(System.in)); 
				while (true) {
					
					// Création d'un canal sortant pour envoyer des messages au client
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					
					// Envoie d'un message au client
					out.writeUTF("Hello from server - you are client #" + clientNumber+". What would you like to do?");
					
					String strCl, strKb;
					LocalDateTime dateTime = LocalDateTime.now();
					DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
					String formattedDateTime = dateTime.format(myFormatObj);
					int port = socket.getPort();
					while ((strCl = br.readLine()) != null) { 
						
		                System.out.println("[" + serverAddress +":"+ port +" - "+ formattedDateTime + "] : "+ strCl); 
		                
		                
		                ps.println("Command executed");
		                
		               
		            }							
				}
				
			}
			catch (IOException  e)
			{
				System.out.println("Error handling client# " + clientNumber + ": "+ e);
			}
			finally
			{
				try
				{
					// Fermeture de la connexion avec le client
					socket.close();
					
					// terminate
					System.exit(0);
		
				}
				catch (IOException  e)
				{
					System.out.println("Couldn't close a socket, what's going on?");
				}
				System.out.println("Connection with client# " + clientNumber + " closed");
			}
		}
	}
	public static Set<String> listFilesUsingFileWalk(String dir, int depth) throws IOException {
	    try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
	        return stream
	          .filter(file -> !Files.isDirectory(file))
	          .map(Path::getFileName)
	          .map(Path::toString)
	          .collect(Collectors.toSet());
	    }
	}
}
