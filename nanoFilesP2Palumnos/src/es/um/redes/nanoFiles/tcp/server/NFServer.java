package es.um.redes.nanoFiles.tcp.server;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServer implements Runnable {

	public static int PORT = 0;
	private ServerSocket serverSocket = null;
	

	public NFServer() throws IOException {
		/*
		 * DONE: (Boletín SocketsTCP) Crear una direción de socket a partir del puerto
		 * especificado (PORT)
		 */
		serverSocket = new ServerSocket();
		/*
		 * DONE: (Boletín SocketsTCP) Crear un socket servidor y ligarlo a la dirección
		 * de socket anterior
		 */
		serverSocket.bind(new InetSocketAddress(PORT));
		PORT = serverSocket.getLocalPort();
		
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación.
	 * 
	 */
	
	public void test() {
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"[fileServerTestMode] Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("[fileServerTestMode] NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}
		
		
		
		while (true) {
			try {
				/*
				 * DONE: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
				 * otros peers que soliciten descargar ficheros.
				 */
				Socket socketCliente = serverSocket.accept();
				
				/*
				 * DONE: (Boletín SocketsTCP) Tras aceptar la conexión con un peer cliente, la
				 * comunicación con dicho cliente para servir los ficheros solicitados se debe
				 * implementar en el método serveFilesToClient, al cual hay que pasarle el
				 * socket devuelto por accept.
				 */
				
				DataInputStream dis = new DataInputStream(socketCliente.getInputStream());
				DataOutputStream dos = new DataOutputStream(socketCliente.getOutputStream());
				
				// --- probar entero ---
				int numero_recibido = dis.readInt();
				System.out.println("* servidor recibió el entero: " + numero_recibido + ".");
				dos.writeInt(numero_recibido);
				
				
				
			// --- probar PeerFiles solo ---
			    System.out.println("* servidor está esperando un PeerMessage...");
				serveFilesToClient(socketCliente);
					
					
				System.out.println("*prueba finalizada del servidor. cierro socket uwu\n");
				socketCliente.close();
					
			}
			catch (IOException e) {
				System.out.println("Server exception: " + e.getMessage());
			}
				
				
				
		}
			
	}
	
	/**
	 * Método que ejecuta el hilo principal del servidor en segundo plano, esperando
	 * conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * DONE: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
		 * otros peers que soliciten descargar ficheros
		 */
		
		/*
		 * DONE: (Boletín SocketsTCP) Al establecerse la conexión con un peer, la
		 * comunicación con dicho cliente se hace en el método
		 * serveFilesToClient(socket), al cual hay que pasarle el socket devuelto por
		 * accept
		 */
		
		/*
		 * DONE: (Boletín TCPConcurrente) Crear un hilo nuevo de la clase
		 * NFServerThread, que llevará a cabo la comunicación con el cliente que se
		 * acaba de conectar, mientras este hilo vuelve a quedar a la escucha de
		 * conexiones de nuevos clientes (para soportar múltiples clientes). Si este
		 * hilo es el que se encarga de atender al cliente conectado, no podremos tener
		 * más de un cliente conectado a este servidor.
		 * 
		 */
		
			try {
				while(true) {
					Socket socketCliente = serverSocket.accept();
					NFServerThread hiloComunicacionCliente = new NFServerThread(socketCliente);
					
					hiloComunicacionCliente.start();
				}
			} 
			catch (java.net.SocketException e) {
				// El socket se ha cerrado intencionadamente (normalmente al hacer quit)
				//no pongo nada porque NanoFiles ya pone un mensaje solito al hacer unregisterFileServer
			} 
			catch(IOException e) {
				e.printStackTrace();
			}
			
	}
	
	
	/*
	 * DONE: (Boletín SocketsTCP) Añadir métodos a esta clase para: 1) Arrancar el
	 * servidor en un hilo nuevo que se ejecutará en segundo plano 2) Detener el
	 * servidor (stopserver) 3) Obtener el puerto de escucha del servidor etc.
	 */
	
	public int getPort() {
		return serverSocket.getLocalPort();
	}
	
	public void startServer() {
		Thread serverThread = new Thread(this);
		//LO DEL DAEMON ES PARA QUE SE CIERRE EL HILO PRINCIPAL DE SERVIDOR AL CERRAR LA APLICACIÓN NANOFILES
		serverThread.setDaemon(true);
		//el start va a hacer que comience el run() automáticamente
		serverThread.start();
	}
	
	public void stopServer() {
		try {
			if(serverSocket != null && serverSocket.isBound()) {
				serverSocket.close();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método de clase que implementa el extremo del servidor del protocolo de
	 * transferencia de ficheros entre pares.
	 * 
	 * @param socket El socket para la comunicación con un cliente que desea
	 *               descargar ficheros.
	 */
	public static void serveFilesToClient(Socket socket) {
		/*
		 * DONE: (Boletín SocketsTCP) Crear dis/dos a partir del socket
		 */
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			/*
			 * DONE: (Boletín SocketsTCP) Mientras el cliente esté conectado, leer mensajes
			 * de socket, convertirlo a un objeto PeerMessage y luego actuar en función del
			 * tipo de mensaje recibido, enviando los correspondientes mensajes de
			 * respuesta.
			 */
			
			while (socket.isConnected()) {
				PeerMessage mensajeCliente = PeerMessage.readMessageFromInputStream(dis);
				
				switch(mensajeCliente.getOpcode()) {
				
				case PeerMessageOps.OPCODE_INVALID_CODE:{
					break;
				}
				
				case PeerMessageOps.OPCODE_PEERFILES: {
				    FileInfo[] ficheros = NanoFiles.db.getFiles();
				    
				    PeerMessage respuesta = PeerMessage.crearPeerFilesListMessage(ficheros);
				    respuesta.writeMessageToOutputStream(dos);
				    
				    System.out.println("NFServer: Enviada lista de " + ficheros.length + " ficheros al cliente.");
				    break;
				}
				/*
				 * DONE: (Boletín SocketsTCP) Para servir un fichero, hay que localizarlo a
				 * partir de su hash (o subcadena) en nuestra base de datos de ficheros
				 * compartidos. Los ficheros compartidos se pueden obtener con
				 * NanoFiles.db.getFiles(). Los métodos lookupHashSubstring y
				 * lookupFilenameSubstring de la clase FileInfo son útiles para buscar ficheros
				 * coincidentes con una subcadena dada del hash o del nombre del fichero. El
				 * método lookupFilePath() de FileDatabase devuelve la ruta al fichero a partir
				 * de su hash completo.
				 */
				
				case PeerMessageOps.OPCODE_PEERDL: {
					//casi igual al del directorio
					
					String hashPedido = mensajeCliente.getFileHash();
					FileInfo[] coincidencias = FileInfo.lookupHashSubstring(NanoFiles.db.getFiles(), hashPedido);
					
					if (coincidencias != null && coincidencias.length == 1) {
						PeerMessage response = PeerMessage.crearPeerDLOkMessage(coincidencias[0]);
						response.writeMessageToOutputStream(dos);
					} 
					
					else {
						//flipa con mi if-else inline hermano
						byte error;
						if(coincidencias == null) {
							System.out.println("algo fue mal en NFServer (desconocido), la lista de ficheros no ha sido inicializada?");
							error = (byte) 3;
						}
						else {
							error = (byte) (coincidencias.length == 0 ? 1 : 2);
						}
						
						
						PeerMessage response = PeerMessage.crearPeerDLError(error);
						response.writeMessageToOutputStream(dos);
					}
					
					break;
				}
				
				case PeerMessageOps.OPCODE_PEERDL_GET_CHUNK: {
					// El cliente pide un trozo del fichero
					String hash = mensajeCliente.getFileHash();
					long offset = mensajeCliente.getFileOffset();
					
					// Buscamos la ruta física en nuestro disco
					String filePath = NanoFiles.db.lookupFilePath(hash);
					FileInfo[] coincidencias = FileInfo.lookupHashSubstring(NanoFiles.db.getFiles(), hash);
					
					if(coincidencias != null && coincidencias.length != 1) {
						byte error = (byte) (coincidencias.length == 0 ? 1 : 2);
						
						
						PeerMessage response = PeerMessage.crearPeerDLError(error);
						response.writeMessageToOutputStream(dos);
						break;
						
					}
					else if(coincidencias == null) {
						
						System.out.println("algo fue mal en NFServer (desconocido), la lista de ficheros no ha sido inicializada?");
						byte error = (byte) 3;
						
						PeerMessage response = PeerMessage.crearPeerDLError(error);
						response.writeMessageToOutputStream(dos);
						break;
					}
					
					
					else if (filePath != null) {
						try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
							raf.seek(offset); // Saltamos a la posición que pide
							
							byte[] buffer = new byte[60000]; 
							int bytesLeidos = raf.read(buffer);
							
							if (bytesLeidos > 0) {
								byte[] dataEnviar = java.util.Arrays.copyOf(buffer, bytesLeidos);
								PeerMessage response = PeerMessage.crearPeerSendChunk(dataEnviar, offset);
								response.writeMessageToOutputStream(dos);
							}
						}
					}
					break;
				}
				
			//fin switch	
			}
				
		}//fin while
			
	//fin func
	}
		
		
		
	catch (java.io.EOFException e) {
		System.out.println("* NFServer: El cliente se ha desconectado correctamente.");
	} 
		
	catch(IOException e) {
		System.out.println("ha habido un IOException en NFServer al leer mensajes");
	}


	}




}
