import java.io.DataOutputStream;
import java.io.File;
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
		File file = new File(path); // crée un nom de path abstrait de type File object
		if (file.mkdir()) { 
			out.writeUTF("Le dossier " + file +" a été créé"); 
		} else { // Si le nom de dossier exist déjà, saisir un nouveau nom de dossier 
			out.writeUTF("Un fichier de ce nom existe déjà. Veuillez choisir un autre nom de dossier.");
		}
	}
	
}
