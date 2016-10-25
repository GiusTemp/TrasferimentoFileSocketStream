import java.net.*;
import java.io.*;

public class PutFileClient {

	public static void main(String[] args) throws IOException {
   
		InetAddress addr = null;
		int port = -1;
		
		try{ //check args
			if(args.length == 2){
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
			} else{
				System.out.println("Usage: java PutFileClient serverAddr serverPort");
				System.exit(-1);
			}
		} //try
		catch(Exception e){
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out.println("Usage: java PutFileClient serverAddr serverPort");
			System.exit(-1);
		}
		
		Socket socket = null;
		DataInputStream inFile = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		String nomeDir = null;

		//apro socket
		try{
			socket = new Socket(addr, port);
			socket.setSoTimeout(30000);
			System.out.println("Creata la socket: " + socket);
		}
		catch(Exception e){
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
		}
		
		//prendo stream in/out
		try{
			inSock = new DataInputStream(socket.getInputStream());
			outSock = new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException e){
			System.out.println("Problemi nella creazione degli stream su socket: ");
			e.printStackTrace();
		}

		
		// creazione stream input da tastiera
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Inserisci nome direttorio in cui ci sono file da trasferire");
		File dir = null;
		String hasNext = null;
		
			while ( (nomeDir = stdIn.readLine()) == null);
				
			if( (dir = new File(nomeDir)).exists() && dir.isDirectory() ){
					
					File files[] = dir.listFiles();
					
					for(int i=0; i<files.length; i++){
						//avvio procedura di invio
						if( files[i].isFile() && giusta(files[i].getName()) ){
							String nomeFile = files[i].getName();
							
							//mando nome file ed aspetto esito 
							outSock.writeUTF(nomeFile);
							
							String esito = inSock.readUTF();
							
							//attivo il trasferimento
							if(esito.equalsIgnoreCase("attiva")) {
								outSock.writeLong(files[i].length()); //mando dimensione
							
							//apro file
							inFile = new DataInputStream(new FileInputStream(files[i].getAbsoluteFile()));
							FileUtility.trasferisci_a_byte_file_binario(inFile, outSock);
							
							//significa che sono arrivato alla fine dei file
							//quindi comunico al server che ho finito
							if(i != (files.length-1) && files[i+1].isFile() && giusta(files[i+1].getName()) )
								hasNext = "ok";
							else 
								hasNext = "fine";
							
							outSock.writeUTF(hasNext);
								
							} else if(esito.equalsIgnoreCase("salta file")) {
								System.out.println(nomeFile+ " gia presente su server");
							}
						}
					} //fine loop file
					socket.shutdownOutput();
					socket.shutdownInput();
				}else {
					System.out.println(nomeDir + " non è un direttorio");
					System.exit(-1);
				}
				
			//chiusure...
			stdIn.close();
			inFile.close();
			socket.close();
		
			System.out.println("Client termina esecuzione correttamente");		
	}
	
	//mi dice se inizia con carattere e termina con numero
	public static boolean giusta(String string) {
		
		String lett = "aeiou";
		String num = "0123456789";
		int k = 0;
		for(int i=0; i<lett.length(); i++)
			if ( string.charAt(0) == lett.charAt(i) )
				k++;
				
		for(int i=0; i<num.length(); i++)
			if ( string.charAt(string.length()-1) == num.charAt(i))
				k++;
		return k == 2;
	}
}
