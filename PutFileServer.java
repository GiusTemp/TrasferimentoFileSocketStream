import java.io.*;
import java.net.*;


public class PutFileServer {
	public static final int PORT = 54321; // porta default per server

	public static void main(String[] args) throws IOException {
		// Porta sulla quale ascolta il server
		int port = -1;
		ServerSocket serverSocket = null;
		
		/* controllo argomenti */
		try {
			if (args.length == 1) {
				port = Integer.parseInt(args[0]);
				// controllo che la porta sia nel range consentito 1024-65535
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
					System.exit(1);
				}
			} else if (args.length == 0) {
				port = PORT;
			} else {
				System.out
					.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
				System.exit(1);
			}
		} //try
		catch (Exception e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out
				.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
			System.exit(1);
		}

		/* preparazione socket e in/out stream */
		try {
			
			try {
				serverSocket = new ServerSocket(port);
				serverSocket.setReuseAddress(true);
				System.out.println("PutFileServerSeq: avviato ");
				System.out.println("Creata la server socket: " + serverSocket);
			}
			catch (Exception e) {
				System.err.println("Problemi nella creazione della server socket: ");
				e.printStackTrace();
				System.exit(2);
			}
		
			//ciclo infinito server
			while (true) {
				Socket clientSocket = null;
				
				System.out.println("\nIn attesa di richieste...");
				try {
					clientSocket = serverSocket.accept();
					System.out.println("Connessione accettata: " + clientSocket);
					new PutFileServerThread(clientSocket).start(); //creo figlio che si occupa del Client
				}
				catch (Exception e) {
					System.err.println("Problemi nella accettazione della connessione: "
							+ e.getMessage());
					e.printStackTrace();
				}
			}//loop
		} catch (Exception e) {
			serverSocket.close();
		}
		
	} //main
}
