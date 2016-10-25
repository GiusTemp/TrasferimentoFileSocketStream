import java.io.*;
import java.net.*;

public class PutFileServerThread extends Thread{

	private Socket socket = null;

	public PutFileServerThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	public void run() {

		DataOutputStream outFile = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		
		//stream I/O e ricezione nome file
		String nomeFile = null;
		String condizione = "ok";
		File dir = new File(".");
		
		try {
			inSock = new DataInputStream(socket.getInputStream());
			outSock = new DataOutputStream(socket.getOutputStream());
			
			while( !condizione.equalsIgnoreCase("fine")){
				nomeFile = inSock.readUTF();
				if( !contiene(dir, nomeFile) ) {
					outSock.writeUTF("attiva"); //mando accettazione richiesta
				
					long dimByte = inSock.readLong(); //prendo la dimensione
					outFile = new DataOutputStream(new FileOutputStream(nomeFile));
				
					//trasferimento
					int buf = 0;
					for(int i = 0; i < dimByte; i++){
						buf = inSock.read();
						outFile.write(buf);
					}
					outFile.flush();
					
					//vedo se ho finito in base a ciò che mi dice il client
					condizione = inSock.readUTF();
					
					if(condizione.equalsIgnoreCase("fine")) { //chiudo tutto tanto so che ho finito
						socket.shutdownInput();
						socket.shutdownOutput();
						socket.close();
						outFile.close();
					}
				}else 
					outSock.writeUTF("salta file");
				}
			
			}//fine loop
		
			
		catch(IOException ste){
			System.out.println("errore in stream ");
			ste.printStackTrace();
		}
	}

	//mi dice se gia contiene quel file
	public static boolean contiene(File dir, String file){
			for(File x: dir.listFiles())
				if(x.getName().equals(file))
					return true;
			return false;
	}
}
