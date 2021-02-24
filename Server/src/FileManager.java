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
	public void ls(DataOutputStream out) throws IOException {
		for (String nomFichier : list()) {
            out.writeUTF(nomFichier);
        }
	}
	
	public void mkdir(String path,DataOutputStream out) throws IOException {
		File file = new File(this.getAbsolutePath()+"/"+path); // crée un nom de path abstrait de type File object
		if (file.mkdir()) { 
			out.writeUTF("Le dossier " + path +" a été créé"); 
		} else { // Si le nom de dossier exist déjà, saisir un nouveau nom de dossier 
			out.writeUTF("Un fichier de ce nom existe déjà. Veuillez choisir un autre nom de dossier.");
		}
	}
	public void sendFile(String file,DataOutputStream dos ) throws IOException {
		FileInputStream fis = new FileInputStream(this.getAbsolutePath()+"/"+file); // Fichier temporaire qui se comporte comme tube d'envoi de fichier
		// Un buffer qui recoit des partie des données à transférer de tailles maximale de 4096 octets par partie
		byte[] buffer = new byte[4096]; 				
		while (fis.read(buffer) > 0) { // Tant que le fichier temporaire n'est pas encore tout lu
			dos.write(buffer); // On envoie une partie du fichier par le buffer
		}
		fis.close();
	}
	public void saveFile(DataInputStream dis, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(this.getAbsolutePath()+"/"+fileName); // Fichier temporaire qui se comporte comme tube de réception de fichier
		// Un buffer qui recoit des partie des données de tailles maximale de 4096 octets par partie
		byte[] buffer = new byte[4096]; 
		int read = 0; // La taille de la partie des données lu dans le buffer
		while((read=dis.read(buffer, 0, buffer.length))!=0) { // Tant que le fichier à recevoir n'est pas totalement recu
			fos.write(buffer, 0, read); // On ecrit dans le fichier temporaire de reception
		}
		fos.close();
	}
}
