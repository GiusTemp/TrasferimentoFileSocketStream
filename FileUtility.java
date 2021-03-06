import java.io.*;

public class FileUtility {
	static protected void trasferisci_a_byte_file_binario(DataInputStream src,
			DataOutputStream dest) throws IOException {
	
		// ciclo di lettura da sorgente e scrittura su destinazione
	    int buffer;    
	    try {
	    	// esco dal ciclo all lettura di un valore negativo -> EOF
	    	// N.B.: la funzione consuma l'EOF
	    	while ((buffer=src.read()) >= 0) {
	    		dest.write(buffer);
	    	}
	    	dest.flush();
	    }
	    catch (EOFException e) {
	    	e.printStackTrace();
	    }
	}
}
