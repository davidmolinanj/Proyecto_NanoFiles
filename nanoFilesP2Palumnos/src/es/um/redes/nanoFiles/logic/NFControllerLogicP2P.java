package es.um.redes.nanoFiles.logic;

import java.net.InetSocketAddress;
import java.io.IOException;

import es.um.redes.nanoFiles.tcp.message.*;
import es.um.redes.nanoFiles.tcp.client.NFConnector;
import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.util.FileInfo;



import es.um.redes.nanoFiles.tcp.server.NFServer;

public class NFControllerLogicP2P {
	// Servidor TCP local para compartir ficheros con otros peers
	private NFServer fileServer = null;

	protected NFControllerLogicP2P() {
	}

	/**
	 * Método para ejecutar un servidor de ficheros en segundo plano. Debe arrancar
	 * el servidor en un nuevo hilo creado a tal efecto.
	 * 
	 * @return Verdadero si se ha arrancado en un nuevo hilo con el servidor de
	 *         ficheros, y está a la escucha en un puerto, falso en caso contrario.
	 * 
	 */
	protected boolean startFileServer() {
		boolean serverRunning = false;
		/*
		 * Comprobar que no existe ya un objeto NFServer previamente creado, en cuyo
		 * caso el servidor ya está en marcha.
		 */
		if (fileServer != null) {
			System.err.println("File server is already running");
		} else {
			
			/*
			 * DONE: (Boletín Servidor TCP concurrente) Arrancar servidor en segundo plano
			 * creando un nuevo hilo, comprobar que el servidor está escuchando en un puerto
			 * válido (>0), imprimir mensaje informando sobre el puerto de escucha, y
			 * devolver verdadero. Las excepciones que puedan lanzarse deben ser capturadas
			 * y tratadas en este método. Si se produce una excepción de entrada/salida
			 * (error del que no es posible recuperarse), se debe informar sin abortar el
			 * programa
			 * 
			 */
			try {
				fileServer = new NFServer();
				//voy a crear un hilo principal uwu
				
				/*Thread serverThread = new Thread(fileServer);
				serverThread.start();*/
				
				fileServer.startServer();
				
				serverRunning = true;
				
				
			} catch(IOException e){
				System.err.println("Es posible que ya exista un servidor a la escucha en este puerto/IP.");
			}
		}
		return serverRunning;

	}

	protected void testTCPServer() {
		assert (NanoFiles.testModeTCP);
		/*
		 * Comprobar que no existe ya un objeto NFServer previamente creado, en cuyo
		 * caso el servidor ya está en marcha.
		 */
		assert (fileServer == null);
		try {

			fileServer = new NFServer();
			/*
			 * (Boletín SocketsTCP) Inicialmente, se creará un NFServer y se ejecutará su
			 * método "test" (servidor minimalista en primer plano, que sólo puede atender a
			 * un cliente conectado). Posteriormente, se desactivará "testModeTCP" para
			 * implementar un servidor en segundo plano, que se ejecute en un hilo
			 * secundario para permitir que este hilo (principal) siga procesando comandos
			 * introducidos mediante el shell.
			 */
			fileServer.test();
			// Este código es inalcanzable: el método 'test' nunca retorna...
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("Cannot start the file server");
			fileServer = null;
		}
	}

	public void testTCPClient() {

		assert (NanoFiles.testModeTCP);
		/*
		 * (Boletín SocketsTCP) Inicialmente, se creará un NFConnector (cliente TCP)
		 * para conectarse a un servidor que esté escuchando en la misma máquina y un
		 * puerto fijo. Después, se ejecutará el método "test" para comprobar la
		 * comunicación mediante el socket TCP. Posteriormente, se desactivará
		 * "testModeTCP" para implementar la descarga de un fichero desde múltiples
		 * servidores.
		 */

		try {
			NFConnector nfConnector = new NFConnector(new InetSocketAddress(NFServer.PORT));
			nfConnector.test();
		} catch (IOException e) {
			// DONE? Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Método para listar los ficheros de un peer concreto vía TCP e imprimirlos por
	 * pantalla.
	 * 
	 * @param La dirección del peer cuyos ficheros se quiere listar
	 * @return Verdadero si se ha obtenido exitosamente el listado de fichero del
	 *         peer
	 */
	protected boolean listPeerFiles(InetSocketAddress peerAddr) {
		boolean success = false;
		
		try {
			NFConnector connector = new NFConnector(peerAddr);
			
			PeerMessage peticion = new PeerMessage(PeerMessageOps.OPCODE_PEERFILES);
			PeerMessage respuesta = connector.sendPeerMessageAndReceiveResponse(peticion);
			
			if (respuesta.getOpcode() == PeerMessageOps.OPCODE_PEERFILES_LIST) {
				FileInfo[] archivosPeer = respuesta.getFileList();
				
				System.out.println("*el peer está compartiendo " + archivosPeer.length + " ficheros:");
				FileInfo.printToSysout(archivosPeer); 
				
				success = true;
			} 
			
			else {
				System.err.println("*el peer ha devuelto una respuesta inesperada: " + PeerMessageOps.opcodeToOperation(respuesta.getOpcode()));
			}
			
		} catch (IOException e) {
			System.err.println("*error TCP al intentar obtener la lista de ficheros del peer: " + e.getMessage());
		}

		return success;
	}

	/**
	 * Descarga un fichero identificado por subcadena de hash desde uno o varios
	 * peers. Si se pasa "*" como nickname, usa el directorio para localizar los
	 * peers que tienen el hash.
	 */
	protected boolean downloadFromPeers(NFControllerLogicDir dirLogic, String targetPeerNickname,
			String targetHashSubstring) {
		
		// pedir al directorio la lista de peers y buscar al peer que se pide
		java.util.Map<String, InetSocketAddress> peers = dirLogic.fetchPeerList();
		InetSocketAddress peerAddr = peers.get(targetPeerNickname);
		
		if (peerAddr == null) {
			System.err.println("* No se ha encontrado al peer '" + targetPeerNickname + "' en el directorio.");
			return false;
		}

		// si se encuentra, creamos el array y llamamos al otro método para descargar
		//por ahora, este array siempre contendrá 1 servidor.
		InetSocketAddress[] serverAddressList = new InetSocketAddress[] { peerAddr };
		return downloadFileFromServers(serverAddressList, targetHashSubstring);
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param serverAddressList   La lista de direcciones de los servidores a los
	 *                            que se conectará
	 * @param targetHashSubstring Subcadena del hash del fichero a descargar
	 */
	
	
	protected boolean downloadFileFromServers(InetSocketAddress[] serverAddressList, String targetHashSubstring) {
		boolean downloaded = false;

		if (serverAddressList.length == 0) {
			System.err.println("* Cannot start download - No list of server addresses provided");
			return false;
		}
		
		try {
			NFConnector connector = new NFConnector(serverAddressList[0]);
			
			PeerMessage peticionInicial = PeerMessage.crearPeerDLMessage(targetHashSubstring);
			PeerMessage respuestaInicial = connector.sendPeerMessageAndReceiveResponse(peticionInicial);
			
			if (respuestaInicial.getOpcode() == PeerMessageOps.OPCODE_PEERDL_OK) {
				FileInfo info = respuestaInicial.getFilePedido();
				System.out.println("Empezando descarga de: " + info.fileName + " (" + info.fileSize + " bytes)");
				
				// decidimos dónde guardar el File (aquí será el nf-shared de la aplicación:
				String rutaGuardado = NanoFiles.sharedDirname + java.io.File.separator + info.fileName;
				java.nio.file.Path dest = es.um.redes.nanoFiles.util.FileNameUtil.chooseAvailableName(rutaGuardado);
				
				long bytesRecibidos = 0;
				
				try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest.toFile())) {
					
					while (bytesRecibidos < info.fileSize) {
						PeerMessage getChunkReq = PeerMessage.crearPeerGetChunk(info.fileHash, bytesRecibidos);
						PeerMessage chunkRes = connector.sendPeerMessageAndReceiveResponse(getChunkReq);
						
						if (chunkRes.getOpcode() == PeerMessageOps.OPCODE_PEERDL_SEND_CHUNK) {
							byte[] datos = chunkRes.getData();
							fos.write(datos);
							
							bytesRecibidos += datos.length;
							System.out.println("Progreso: " + bytesRecibidos + " / " + info.fileSize + " bytes.");
						} else {
							System.err.println("* Error crítico: El servidor falló enviando el offset " + bytesRecibidos);
							return false;
						}
					}
				}
				
				System.out.println("*descarga P2P TCP completada con éxito en " + toDisplayPath(dest));
				downloaded = true;
				
			} else {
				if(respuestaInicial.getErrorByte() == (byte) 1) {
					System.err.println("*El servidor rechazó la descarga. (error code: " + respuestaInicial.getErrorByte() + ")"
							+ '\n' + "rechazo causado por: fichero pedido inexistente (prueba otro hash).");
				}
				else if(respuestaInicial.getErrorByte() == (byte) 2) {
					System.err.println("*El servidor rechazó la descarga. (error code: " + respuestaInicial.getErrorByte() + ")"
							+ '\n' + "rechazo causado por: subcadena hash ambiguo (hay más de uno que coincide con este).");
				}
				else {
					System.err.println("*El servidor rechazó la descarga. (error code: " + respuestaInicial.getErrorByte() + ")"
							+ '\n' + "rechazo causado por: error de mensaje/programación (developer-side).");
				}
				
			}
			
		} catch (IOException e) {
			System.err.println("* Error de conexión TCP con el peer: " + e.getMessage());
		}

		return downloaded;
	}

	private String toDisplayPath(java.nio.file.Path path) {
		java.nio.file.Path abs = path.toAbsolutePath().normalize();
		java.nio.file.Path cwd = java.nio.file.Paths.get("").toAbsolutePath().normalize();
		if (abs.startsWith(cwd)) {
			return cwd.relativize(abs).toString();
		}
		return path.toString();
	}

	/**
	 * Método para obtener el puerto de escucha de nuestro servidor de ficheros
	 * 
	 * @return El puerto en el que escucha el servidor, o 0 en caso de error.
	 */
	protected int getServerPort() {
		/*
		 * DONE: Devolver el puerto de escucha de nuestro servidor de ficheros
		 */
		return fileServer.getPort();
	}

	/**
	 * Método para detener nuestro servidor de ficheros en segundo plano
	 * 
	 */
	protected void stopFileServer() {
		/*
		 *DONE: Enviar señal para detener nuestro servidor de ficheros en segundo plano
		 */
		if (fileServer != null) {
			fileServer.stopServer();
			fileServer = null; // ¡Añadimos esto!
		}
	}

	protected boolean serving() {
		

		return fileServer != null;

	}

}
