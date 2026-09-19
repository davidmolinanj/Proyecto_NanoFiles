package es.um.redes.nanoFiles.udp.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.LinkedHashMap;


import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros alojados, servidores
	 * registrados, etc.
	 */
	/**
	 * Lista de ficheros alojados en el directorio.
	 */
	private FileInfo[] directoryFiles;
	/**
	 * Lista de servidores registrados (IP, puerto TCP).
	 */
	
	// HOLAAA HEMO CAMBIADO LA ESTRUCTURA DE reigsteredPeers PARA QUE
	//CADA USER SERVIDOR PUEDA TENER MÁS DE UN PUERTO ESCUCHANDO A GENTE
	
	
	private LinkedHashMap<String, InetSocketAddress> registeredPeers;

	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability, String directoryFilesPath) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * Cargar los ficheros del directorio compartido.
		 */
		File dir = new File(directoryFilesPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		directoryFiles = FileInfo.loadFilesFromFolder(directoryFilesPath);
		if (directoryFiles == null) {
			directoryFiles = new FileInfo[0];
		}
		
		
		System.out.println("* Directory loaded " + directoryFiles.length + " files from " + directoryFilesPath);
		/*
		 * DONE: (Boletín SocketsUDP) Inicializar el atributo socket: Crear un socket
		 * UDP ligado al puerto especificado por el argumento directoryPort en la
		 * máquina local,
		 */
		
		this.socket = new DatagramSocket(DIRECTORY_PORT);
		
		/*
		 * DONE?: (Boletín SocketsUDP) Inicializar atributos que mantienen el estado del
		 * servidor de directorio: peers registrados, etc.)
		 */
		registeredPeers = new LinkedHashMap<String, InetSocketAddress>();
		

		if (NanoFiles.testModeUDP) {
			if (socket == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public DatagramPacket receiveDatagram() throws IOException {
		DatagramPacket datagramReceivedFromClient = null;
		boolean datagramReceived = false;
		while (!datagramReceived) {
			/*
			 * DONE?: (Boletín SocketsUDP) Crear un búfer para recibir datagramas y un
			 * datagrama asociado al búfer (datagramReceivedFromClient)
			 */
			
			byte[] bufferRequest = new byte[DirMessage.PACKET_MAX_SIZE];
			datagramReceivedFromClient = new DatagramPacket(bufferRequest, bufferRequest.length);
			
			
			
			/*
			 * DONE?: (Boletín SocketsUDP) Recibimos a través del socket un datagrama
			 */
			
			socket.receive(datagramReceivedFromClient);
			System.out.println(datagramReceivedFromClient.getData());


			if (datagramReceivedFromClient == null) {
				System.err.println("[testMode] NFDirectoryServer.receiveDatagram: code not yet fully functional.\n"
						+ "Check that all TODOs have been correctly addressed!");
				System.exit(-1);
			} else {
				// Vemos si el mensaje debe ser ignorado (simulación de un canal no confiable)
				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println(
							"Directory ignored datagram from " + datagramReceivedFromClient.getSocketAddress());
				} else {
					datagramReceived = true;
				}
			}

		}

		return datagramReceivedFromClient;
	}

	public void runTest() throws IOException {

		System.out.println("[testMode] Directory starting...");

		System.out.println("[testMode] Attempting to receive 'ping' message...");
		DatagramPacket rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);

		System.out.println("[testMode] Attempting to receive 'ping&PROTOCOL_ID' message...");
		rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);
	}

	private void sendResponseTestMode(DatagramPacket pkt) throws IOException {
		/*
		 * DONE: (Boletín SocketsUDP) Construir un String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración.
		 */
		
		/*
		 * DONE: (Boletín SocketsUDP) Después, usar la cadena para comprobar que su
		 * valor es "ping"; en ese caso, enviar como respuesta un datagrama con la
		 * cadena "pingok". Si el mensaje recibido no es "ping", se informa del error y
		 * se envía "invalid" como respuesta.
		 */
		
		/*
		 * DONE: (Boletín Estructura-NanoFiles) Ampliar el código para que, en el caso
		 * de que la cadena recibida no sea exactamente "ping", comprobar si comienza
		 * por "ping&" (es del tipo "ping&PROTOCOL_ID", donde PROTOCOL_ID será el
		 * identificador del protocolo diseñado por el grupo de prácticas (ver
		 * NanoFiles.PROTOCOL_ID). Se debe extraer el "protocol_id" de la cadena
		 * recibida y comprobar que su valor coincide con el de NanoFiles.PROTOCOL_ID,
		 * en cuyo caso se responderá con "welcome" (en otro caso, "denied").
		 */
		
		
		String mensajeRecibido = new String(pkt.getData(), 0, pkt.getLength());
		
		byte[] respuesta = null;
		
		
		if(mensajeRecibido.equals("ping")) {
			System.out.println("Received 'ping', sending 'pingok'.");
			respuesta = "pingok".getBytes();
		}
		
		
		else if (mensajeRecibido.startsWith("ping&")) {
			
			String protocol = mensajeRecibido.substring(5);
			if(protocol.equals(NanoFiles.PROTOCOL_ID)) {
				System.out.println("Received a ping and correct protocol, sending 'welcome'.");
				respuesta = "welcome".getBytes();
			}
			else {
				System.out.println("Received an invalid protocol, sending 'denied'.");
				respuesta = "denied".getBytes();
			}
		}
		
		else {
			System.out.println("unknown message, sending 'invalid'.");
			respuesta = "invalid".getBytes();
		}
		
		DatagramPacket datagramaRespuesta = new DatagramPacket(respuesta, respuesta.length, pkt.getSocketAddress());
		socket.send(datagramaRespuesta);
		
		

		String messageFromClient = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("Data received: " + messageFromClient);
		System.out.println("Data sent: " + respuesta);

	}

	public void run() throws IOException {

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio
			DatagramPacket rcvDatagram = receiveDatagram();

			sendResponse(rcvDatagram);

		}
	}

	private void sendResponse(DatagramPacket pkt) throws IOException {
		/*
		 * DONE: (Boletín MensajesASCII) Construir String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración. Después, usar la cadena para construir un objeto
		 * DirMessage que contenga en sus atributos los valores del mensaje. A partir de
		 * este objeto, se podrá obtener los valores de los campos del mensaje mediante
		 * métodos "getter" para procesar el mensaje y consultar/modificar el estado del
		 * servidor.
		 */
		
		/*
		 * DONE: Una vez construido un objeto DirMessage con el contenido del datagrama
		 * recibido, obtener el tipo de operación solicitada por el mensaje y actuar en
		 * consecuencia, enviando uno u otro tipo de mensaje en respuesta.
		 */
		
		
		String cadena = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("Recibido: " + '\n' + cadena);
		
		
		DirMessage mensaje = DirMessage.fromString(cadena);
		
		String operation = mensaje.getOperation();

		/*
		 * DONE?: (Boletín MensajesASCII) Construir un objeto DirMessage (msgToSend) con
		 * la respuesta a enviar al cliente, en función del tipo de mensaje recibido,
		 * leyendo/modificando según sea necesario el "estado" guardado en el servidor
		 * de directorio (atributos files, etc.). Los atributos del objeto DirMessage
		 * contendrán los valores adecuados para los diferentes campos del mensaje a
		 * enviar como respuesta (operation, etc.)
		 */

		DirMessage msgToSend = null;

		InetAddress ip = null;
		int puerto = 0;
		String nickname = null;
		InetSocketAddress peer;
		
		
		switch (operation) {
		case DirMessageOps.OPERATION_PING: 
			
			boolean aceptado = false;
			
			if(mensaje.getProtocolId().equals(NanoFiles.PROTOCOL_ID)) {
				aceptado = true;
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_OK);
			}
			
			else {
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_DENIED);
			}
			
			System.out.println("Ping recibido desde " + pkt.getSocketAddress() + " " + 
			(aceptado ? "success" : "failure"));
			
			break;
			
			
			
		case DirMessageOps.OPERATION_SERVE:
			
			
			ip = pkt.getAddress();
			puerto = mensaje.getPort();
			nickname = mensaje.getNickname();
			
			System.out.println("Nuevo usuario servidor-fichero: " 
					+ nickname + " " + ip.toString() + " " + puerto);
			
			peer = new InetSocketAddress(ip, puerto);
			
			registeredPeers.put(nickname, peer);
			
			msgToSend = new DirMessage(DirMessageOps.OPERATION_SERVE_OK);
			msgToSend.setNickname(nickname); 
			msgToSend.setPort(puerto);
			
			break;
			
			
		case DirMessageOps.OPERATION_QUIT:
			
			ip = pkt.getAddress();
			puerto = mensaje.getPort();
			nickname = mensaje.getNickname();
			
			System.out.println("Usuario quiere parar de dar servicio en un puerto: " 
					+ nickname + " " + ip.toString() + " " + puerto);
			
			peer = new InetSocketAddress(ip, puerto);
			
			registeredPeers.remove(nickname);
			
			msgToSend = new DirMessage(DirMessageOps.OPERATION_QUIT_OK);
			
			break;
			
			
			
		case DirMessageOps.OPERATION_PEERS:
			
		//tengo que darle la lista de peers del servidor al Mensaje.	
			msgToSend = DirMessage.crearDirMessagePeersList(registeredPeers);
			
			break;
			
		case DirMessageOps.OPERATION_DIRFILES:
			
		//TENGO QUE DEVOLVERLE LA LISTA DE FICHEROS
			
			msgToSend = DirMessage.crearDirMessageDirFilesList(directoryFiles);
			
			break;
			
			
			
		case DirMessageOps.OPERATION_DIRDL:
			
		//TENGO QUE DEVOLVERLE EL FICHERO QUE PIDA
			
		//me da un substring owo
			
			String substring_buscar = mensaje.getHash_substring();
			FileInfo[] ficheros_encontrados = null;
			
			ficheros_encontrados = FileInfo.lookupHashSubstring(directoryFiles, substring_buscar);
			
			
			//existen 3 errores:
			/*
			 * 1 : no encuentra nada
			 * 2 : ambiguedad
			 * 3 : error de red?
			 * */
			
			if(ficheros_encontrados.length == 1) {
				//accept
				msgToSend = DirMessage.crearDirMessageDirDLReply(ficheros_encontrados[0]);
			}
			
			//refuse
			else if(ficheros_encontrados.length < 1) {
				System.out.println("Error en NFDirectoryServer: Responder a DIRDL: NO SE ENCUENTRA EL FICHERO");
				msgToSend = DirMessage.crearDirMessageDirDLReply(1);
			}
				
			else if(ficheros_encontrados.length > 1) {
				System.out.println("Error en NFDirectoryServer: Responder a DIRDL: AMBIGÜEDAD");
				msgToSend = DirMessage.crearDirMessageDirDLReply(2);
			}
			
			else {
				System.out.println("Error desconocido en NFDirectoryServer: Responder a DIRDL.");
				msgToSend = DirMessage.crearDirMessageDirDLReply(3);
			}
			
			break;
			
		case DirMessageOps.OPERATION_DIRDL_CLIENT_OK:
			String hash = mensaje.getHash_substring();
			int offsetPedido = mensaje.getOffset();
			
			// Buscamos el archivo en el directorio
			FileInfo[] ficheros = FileInfo.lookupHashSubstring(directoryFiles, hash);
			
			if (ficheros.length == 1) {
				File archivoFisico = new File(ficheros[0].filePath);
				
				try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(archivoFisico, "r")) {
					raf.seek(offsetPedido);
					
					byte[] bufferData = new byte[40000];
					int bytesLeidosReales = raf.read(bufferData);
					
					if (bytesLeidosReales > 0) {
						byte[] dataEnviar = java.util.Arrays.copyOf(bufferData, bytesLeidosReales);
						
						msgToSend = new DirMessage(DirMessageOps.OPERATION_DIRDL_SEND_OK);
						msgToSend.setOffset(offsetPedido);
						msgToSend.setFileData(dataEnviar);
					}
				} catch (Exception e) {
					System.err.println("Error leyendo el archivo físico: " + e.getMessage());
					msgToSend = DirMessage.crearDirMessageDirDLReply(3); // Error general
				}
			} else {
				msgToSend = DirMessage.crearDirMessageDirDLReply(1); // Fichero no encontrado
			}
			break;

		default:
			System.err.println("Unexpected message operation: \"" + operation + "\"");
			System.exit(-1);
		}

		/*
		 * DONE: (Boletín MensajesASCII) Convertir a String el objeto DirMessage
		 * (msgToSend) con el mensaje de respuesta a enviar, extraer los bytes en que se
		 * codifica el string y finalmente enviarlos en un datagrama
		 */
		
		if(msgToSend != null) {
			byte[] respuesta = msgToSend.toString().getBytes();
			DatagramPacket paquete_respuesta = new DatagramPacket(respuesta, 0, respuesta.length, pkt.getSocketAddress());
			
			socket.send(paquete_respuesta);
			System.out.println("msgToSend enviado desde NFDirectoryServer (Directorio, sendResponse)");
		}

	}

}
