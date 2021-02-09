import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;

public class Server {

	private static ServerSocket listener;
	
	/* Application Serveur */
	
	public static void main(String[] args) throws Exception
	{
	
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
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		
		try
		{
			/* � chaque fois qu'un nouveau client se connecte, on ex�cute la fonction Run() de l'objet
			 * ClientHandler.
			 */
			while(true) 
			{
				// Important: la fct accept() est bloquante : attaned qu<un prochain client se connecte
				// Une nouvelle connexion : on incr�mente le compteur clientNumber
				new ClientHandler(listener.accept(), clientNumber++).start();
				
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
		
		public ClientHandler(Socket socket, int clientNumber)
		{
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New connection with client#"+ clientNumber + " at "+ socket);
			
		}
		/* Une thread qui se charge d'envoyer au client un message de bienvenue*/
		public void run()
		{
			try
			{
				// Cr�ation d'un canal sortant pour envoyer des messages au client
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				// Envoie d'un message au client
				out.writeUTF("Hellow from server - you are client#" + clientNumber);
				//out.writeUTF(Server.executeCommand("dir"));
				File dir = new File(System.getProperty("user.dir"));
				//Stream<Path> fichiersCourant=Files.list(Paths.get(""));
				//out.writeUTF(Files.newDirectoryStream(Paths.get("")).toString());
				for (String nomFichier : dir.list()) {
		            out.writeUTF(nomFichier);
		        }
				// Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le serveur
				DataInputStream in = new DataInputStream(socket.getInputStream());
				// Attente de la r�ception d'un message envoy� par le serveur sur le canal
				String commande;
				do{
					// Reception du message du client
					commande = in.readUTF(); 
				}
					while(commande!="exit");
				
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
				}
				catch (IOException  e)
				{
					System.out.println("Couldn't close a socket, what's going on?");
				}
				System.out.println("Connection with client# " + clientNumber + " closed");
			}
		}
	}
	/*public static String executeCommand(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return output.toString();
    }*/
}
