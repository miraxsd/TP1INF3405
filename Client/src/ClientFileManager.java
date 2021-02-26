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
 * @author Karim
 *
 */
public class ClientFileManager extends File {

	/**
	 * @param pathname
	 */
	public ClientFileManager(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param uri
	 */
	public ClientFileManager(URI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param child
	 */
	public ClientFileManager(String parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param child
	 */
	public ClientFileManager(File parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}
	//Inspir� du code de https://gist.github.com/CarlEkerot/2693246
		public void sendFile(String file,DataOutputStream dos ) throws IOException {
			FileInputStream fis = new FileInputStream(this.getAbsolutePath()+"/"+file); // Fichier temporaire qui se comporte comme tube d'envoi de fichier
			// Un buffer qui recoit des partie des donn�es � transf�rer de tailles maximale de 4096 octets par partie
			File fichier = new File(this.getAbsolutePath()+"/"+file);
			dos.writeLong(fichier.length());
			byte[] buffer = new byte[4096]; 				
			while (fis.read(buffer) > 0) { // Tant que le fichier temporaire n'est pas encore tout lu
				dos.write(buffer); // On envoie une partie du fichier par le buffer
			}
			fis.close();
		}
		//Inspir� du code de https://gist.github.com/CarlEkerot/2693246 et de https://zetcode.com/java/filesize/ pour le filesize
		public void saveFile(DataInputStream dis, String fileName) throws IOException {
			FileOutputStream fos = new FileOutputStream(this.getAbsolutePath()+"/"+fileName); // Fichier temporaire qui se comporte comme tube de r�ception de fichier
			long remaining = dis.readLong();
			// Un buffer qui recoit des partie des donn�es de tailles maximale de 4096 octets par partie
			byte[] buffer = new byte[4096]; 
			int read = 0; // La taille de la partie des donn�es lu dans le buffer
			while((read=dis.read(buffer, 0, Math.min(buffer.length,(int) remaining)))>0) { // Tant que le fichier � recevoir n'est pas totalement recu
						remaining -= read;
				fos.write(buffer, 0, read); // On ecrit dans le fichier temporaire de reception
			}
			fos.close();
		}
}
