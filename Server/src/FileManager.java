import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * 
 */

/**
 * @author Karim Gargouri, Samia Safaa, Madeleine Tjiu
 *
 */
public class FileManager extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param pathname
	 */
	public FileManager(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param uri
	 */
	public FileManager(URI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param child
	 */
	public FileManager(String parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param child
	 */
	public FileManager(File parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}
	// Fonction ls :: commande permettant d'afficher tous les dossiers et fichiers dans le r�p�rtoire courant de l'utilisateur.
	// Elle ne retourne rien.
	// Elle prend en param�tres un objet de type DataOutputStream.
	public void ls(DataOutputStream out) throws IOException {
		for (File file : listFiles()) {
			String buffer = new String(); 
            buffer+="[";
            if(file.isFile())
            	buffer+="File";
            else
            	buffer+="Folder";
            buffer+="] ";
            buffer+=file.getName();
            out.writeUTF(buffer);
        }
	}
	// Fonction mkdir :: commande permettant la cr�ation d'un dossier au niveau du serveur de stockage.
	// Elle ne retourne rien.
	// Elle prend en param�tres le nom du dossier path de type String et un objet de type DataOutputStream.
	public void mkdir(String path,DataOutputStream out) throws IOException {
		File file = new File(this.getAbsolutePath()+"/"+path); // cr�e un nom de path abstrait de type File object
		if (file.mkdir()) { 
			out.writeUTF("Le dossier " + path +" a �t� cr��"); 
		} else { 											  // Si le nom de dossier exist d�j�, saisir un nouveau nom de dossier 
			out.writeUTF("Un dossier de ce nom existe d�j�. Veuillez choisir un autre nom de dossier.");
		}
	}
	// Cette partie a �t� inspir�e par le code de Carl Ekerot. Lien vers le code https://gist.github.com/CarlEkerot/2693246
		// Fonction sendFile :: Envoie du fichier
		// Elle ne retourne rien
		// Elle prend en param�tres le nom du fichier � envoyer de type String et un objet de type DataOutputStream.
	public void sendFile(String file,DataOutputStream dos ) throws IOException {
		FileInputStream fis = new FileInputStream(this.getAbsolutePath()+"/"+file); // Fichier temporaire qui se comporte comme tube d'envoi de fichier.
		// Un buffer qui recoit des partie des donn�es � transf�rer de tailles maximale de 4096 octets par partie
		File fichier = new File(this.getAbsolutePath()+"/"+file);
		dos.writeLong(fichier.length());
		byte[] buffer = new byte[4096]; 				
		while (fis.read(buffer) > 0) { 												// Tant que le fichier temporaire n'est pas encore tout lu.
			dos.write(buffer); 														// On envoie une partie du fichier par le buffer.
		}
		fis.close();
	}
	// Cette partie a �t� inspir� par le code de Carl Ekerot. Lien vers le code https://gist.github.com/CarlEkerot/2693246.
			// Fonction saveFile :: Enregistrement du fichier
			// Elle ne retourne rien
			// Elle prend en param�tres le nom du fichier � envoyer de type String et un objet de type DataInputStream.
	public void saveFile(DataInputStream dis, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(this.getAbsolutePath()+"/"+fileName); // Fichier temporaire qui se comporte comme tube de r�ception de fichier
		// Un buffer qui recoit des partie des donn�es de tailles maximale de 4096 octets par partie
		long remaining = dis.readLong();
		byte[] buffer = new byte[4096]; 
		int read = 0; 																	  // La taille de la partie des donn�es lu dans le buffer
		while((read=dis.read(buffer, 0, Math.min(buffer.length,(int) remaining)))>0) {    // Tant que le fichier � recevoir n'est pas totalement recu
			remaining -= read;
			fos.write(buffer, 0, read); 												  // On ecrit dans le fichier temporaire de reception
		}
		fos.close();
	}
	// Fonction contains :: V�rifier si le fichier existe.
			// Elle retourne une valeur bool�enne qui indique l'existance du fichier.
			// Elle prend en param�tres le nom du fichier qu'on d�sir trouver de type String.
	public Boolean contains(String fileName) {
		Boolean fileExists=false;
		for (File file : listFiles()) {
			if(file.getName().equals(fileName))
				fileExists=true;
		}
		return fileExists;
	}
}
