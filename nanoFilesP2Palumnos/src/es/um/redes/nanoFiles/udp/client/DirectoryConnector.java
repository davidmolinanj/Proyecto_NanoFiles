package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;
import es.um.redes.nanoFiles.util.NickGenerator;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;
	/**
	 * Nombre/IP del host donde se ejecuta el directorio
	 */
	private String directoryHostname;
	/**
	 * Todos los peers que ha decidido guardar
	 */
	/**
	 * Puerto a la escucha
	 */
	private int registeredServerPort = -1;
	
	public static class DownloadedFile {
		public final String filename;
		public final long filesize;
		public final byte[] data;
		public final String filehash;

		public DownloadedFile(String filename, long fsize, byte[] data, String filehash) {
			this.filename = filename;
			this.filesize = fsize;
			this.data = data;
			this.filehash = filehash;
		}
	}

	public DirectoryConnector(String hostname) throws IOException {
		// Guardamos el string con el nombre/IP del host
		directoryHostname = hostname;
		/*
		 * DONE: (Boletín SocketsUDP) Convertir el string 'hostname' a InetAddress y
		 * guardar la dirección de socket (address:DIRECTORY_PORT) del directorio en el
		 * atributo directoryAddress, para poder enviar datagramas a dicho destino.
		 */

		this.directoryAddress = new InetSocketAddress(InetAddress.getByName(directoryHostname), DIRECTORY_PORT);

		/*
		 * DONE: (Boletín SocketsUDP) Crea el socket UDP en cualquier puerto para enviar
		 * datagramas al directorio
		 */
		this.socket = new DatagramSocket();

	}

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 */
	private byte[] sendAndReceiveDatagrams(byte[] requestData) {
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response[] = null;
		if (directoryAddress == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP server destination address is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"directoryAddress\"");
			System.exit(-1);

		}
		if (socket == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP socket is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"socket\"");
			System.exit(-1);
		}
		/*
		 * DONE: (Boletín SocketsUDP) Enviar datos en un datagrama al directorio y
		 * recibir una respuesta. El array devuelto debe contener únicamente los datos
		 * recibidos, *NO* el búfer de recepción al completo.
		 */
		
		DatagramPacket envio = new DatagramPacket(requestData, requestData.length, directoryAddress);
		
		DatagramPacket respuesta = new DatagramPacket(responseData, responseData.length);
		
			int intentos_actuales = 1;			
			while(intentos_actuales < MAX_NUMBER_OF_ATTEMPTS && (response == null)) {
				try {
					socket.send(envio);
					socket.setSoTimeout(TIMEOUT);
					socket.receive(respuesta);
					
					response = new byte[respuesta.getLength()];
					System.arraycopy(responseData, 0, response, 0, response.length);
					
				}
				catch(SocketTimeoutException sto){
					System.out.println("Ha pasado mucho tiempo desde que envié el último paquete. Intentos: " + intentos_actuales);
					intentos_actuales++;
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			if(intentos_actuales > MAX_NUMBER_OF_ATTEMPTS) {
				System.err.println("He probado esta mierda mucho");
			}
		
		/*
		 * DONE: (Boletín SocketsUDP) Una vez el envío y recepción asumiendo un canal
		 * confiable (sin pérdidas) esté terminado y probado, debe implementarse un
		 * mecanismo de retransmisión usando temporizador, en caso de que no se reciba
		 * respuesta en el plazo de TIMEOUT. En caso de salte el timeout, se debe volver
		 * a enviar el datagrama y tratar de recibir respuestas, reintentando como
		 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones.
		 */
		
		
		
		
		/*
		 * DONE: (Boletín SocketsUDP) Las excepciones que puedan lanzarse al
		 * leer/escribir en el socket deben ser capturadas y tratadas en este método. Si
		 * se produce una excepción de entrada/salida (error del que no es posible
		 * recuperarse), se debe informar y terminar el programa.
		 */
		/*
		 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
		 * SocketTimeoutException es más concreta que IOException.
		 */

		if (response != null && response.length == responseData.length) {
			System.err.println("Your response is as large as the datagram reception buffer!!\n"
					+ "You must extract from the buffer only the bytes that belong to the datagram!");
		}
		return response;
	}

	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 */
	public boolean testSendAndReceive() {
		boolean success = false;
		/*
		 * DONE: (Boletín SocketsUDP) Probar el correcto funcionamiento de
		 * sendAndReceiveDatagrams. Se debe enviar un datagrama con la cadena "ping" y
		 * comprobar que la respuesta recibida empieza por "pingok". En tal caso,
		 * devuelve verdadero, falso si la respuesta no contiene los datos esperados.
		 */
		String hola = new String("ping");
		byte[] bytesRespuesta = sendAndReceiveDatagrams(hola.getBytes());

		// va a devolver 'response' (que está vacío) si ha habido algun fallo

		String respuesta = new String(bytesRespuesta);

		if (respuesta.startsWith("pingok")) {
			success = true;
		}

		return success;
	}

	public String getDirectoryHostname() {
		return directoryHostname;
	}
	
	
	public DirMessage autoDirMessageSendAndReceive(DirMessage request) {
		String messageStr = request.toString();
		byte[] bytestosend = messageStr.getBytes();
		
		byte[] responseBytes = sendAndReceiveDatagrams(bytestosend);
		
		
		if (responseBytes == null) {
			return null;
		}
		
		String responseStr = new String(responseBytes);
		DirMessage response = DirMessage.fromString(responseStr);
		
		return response;
	}
	

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que
	 * usa un protocolo compatible. Este método no usa mensajes bien formados.
	 * 
	 * @return Verdadero si
	 */
	public boolean pingDirectoryRaw() {
		boolean success = false;
		/*
		 * DONE: (Boletín EstructuraNanoFiles) Basándose en el código de
		 * "testSendAndReceive", contactar con el directorio, enviándole nuestro
		 * PROTOCOL_ID (ver clase NanoFiles). Se deben usar mensajes "en crudo" (sin un
		 * formato bien definido) para la comunicación.
		 * 
		 * PASOS: 1.Crear el mensaje a enviar (String "ping&protocolId"). 2.Crear un
		 * datagrama con los bytes en que se codifica la cadena : 4.Enviar datagrama y
		 * recibir una respuesta (sendAndReceiveDatagrams). : 5. Comprobar si la cadena
		 * recibida en el datagrama de respuesta es "welcome", imprimir si éxito o
		 * fracaso. 6.Devolver éxito/fracaso de la operación.
		 */
		
		String p = new String("ping&" + NanoFiles.PROTOCOL_ID);
		byte[] pingProtocol = p.getBytes();
		
		byte[] respuestaProtocolo = sendAndReceiveDatagrams(pingProtocol);
		
		String hola = new String(respuestaProtocolo, 0, respuestaProtocolo.length);
		
		if(!(hola.equals("welcome"))) {
			System.out.println("uwu fracaso en la operation (hemo sido rechazado)");
			return success;
		}
			
		success = true;
		System.out.println("uwu tabien fuimo aseptado");
		return success;
	}

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que es
	 * compatible.
	 * 
	 * @return Verdadero si el directorio está operativo y es compatible
	 */
	public boolean pingDirectory() {
		boolean success = false;
		/*
		 * DONE: (Boletín MensajesASCII) Hacer ping al directorio 1.Crear el mensaje a
		 * enviar (objeto DirMessage) con atributos adecuados (operation, etc.) NOTA:
		 * Usar como operaciones las constantes definidas en la clase DirMessageOps :
		 * 2.Convertir el objeto DirMessage a enviar a un string (método toString)
		 * 3.Crear un datagrama con los bytes en que se codifica la cadena : 4.Enviar
		 * datagrama y recibir una respuesta (sendAndReceiveDatagrams). : 5.Convertir
		 * respuesta recibida en un objeto DirMessage (método DirMessage.fromString)
		 * 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */
		
		DirMessage pingMessage = DirMessage.crearDirMessagePing(NanoFiles.PROTOCOL_ID);
		DirMessage response = autoDirMessageSendAndReceive(pingMessage);
	
		
		if(response.getOperation().equals(DirMessageOps.OPERATION_PING_OK)){
			success = true;
		}
		
		else if(response.getOperation().equals(DirMessageOps.OPERATION_PING_DENIED)) {
			System.out.println("Protocolo inválido para la comunicación. " + response.getOperation());
		}
		
		else{
			System.err.println("Error: Campo 'operation' incorrecto / error indefinido. " 
			+ response.getOperation());
		}
			
		
		return success;
	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 */
	public boolean registerFileServer(String nick, int serverPort) {
		boolean success = false;

		
		// SE MODIFICAN LOS ATRIBUTOS CON EL MENSAJE DE VUELTA PARA ASEGURAR QUE
		//EL DIRECTORIO Y EL CLIENTE ESTÁN DICIENDO LO MISMO.
		
		DirMessage serveMessage = DirMessage.crearDirMessageServe(nick, serverPort);
		DirMessage response = autoDirMessageSendAndReceive(serveMessage);
		
		if(response != null && response.getOperation().equals(DirMessageOps.OPERATION_SERVE_OK)) {
			success = true;
			NanoFiles.peerNickname = response.getNickname(); 
	        registeredServerPort = response.getPort();
		}
		else {
			System.err.println("Algo ha pasado, no ha llegado el serveOk en DirectoryConnector.");
		}
		
		return success;
	}
	
	
	public boolean registerFileServer(int serverPort) {
		if(NanoFiles.peerNickname == null || NanoFiles.peerNickname.isBlank()) {
			return registerFileServer(NickGenerator.randomNickname(), serverPort);
		}
		return registerFileServer(NanoFiles.peerNickname, serverPort);
	}

	
	
	
	/**
	 * Método para obtener la lista de ficheros alojados en el directorio. Para cada
	 * fichero se debe obtener un objeto FileInfo con nombre, tamaño y hash.
	 * 
	 * @return Los ficheros disponibles en el directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 */
	 
	
	
	
	public FileInfo[] getFileList() {
		DirMessage dirfilesMessage = new DirMessage(DirMessageOps.OPERATION_DIRFILES);
		DirMessage response = autoDirMessageSendAndReceive(dirfilesMessage);
		
		FileInfo[] filelist = null;
		
		if(response != null && response.getOperation().equals(DirMessageOps.OPERATION_DIRFILES_LIST)) {
			filelist = response.getFiles();
			
			if (filelist.length == 0) {
				System.out.println("  (El directorio no tiene ficheros ahora mismo)");
			}
		} 
		
		else {
			System.err.println("Algo ha pasado, no ha llegado el dirfilesOk en DirectoryConnector.");
		}
		
		return filelist;
	}

	public LinkedHashMap<String, InetSocketAddress> getPeerList() {
		LinkedHashMap<String, InetSocketAddress> peers = new LinkedHashMap<String, InetSocketAddress>();
		
		DirMessage peersMessage = new DirMessage(DirMessageOps.OPERATION_PEERS);
		DirMessage response = autoDirMessageSendAndReceive(peersMessage);
		
		if(response != null && response.getOperation().equals(DirMessageOps.OPERATION_PEERS_LIST)) {
			peers = response.getServersConectados();
		}
		
		else {
			System.err.println("Algo ha pasado, no ha llegado el peersListOk en DirectoryConnector.");
		}
		
		
		return peers;
	}

	public LinkedHashMap<String, InetSocketAddress> searchFilesByHash(String hashSubstring) {
		LinkedHashMap<String, InetSocketAddress> results = new LinkedHashMap<String, InetSocketAddress>();

		
		
		return results;
	}

	public DownloadedFile downloadFileFromDirectory(String hashSubstring) {
		byte[] fileData = null;	
		String filename = null;
		long filesize = -1;
		String filehash = null;
		
		DirMessage dirDLMessage = DirMessage.crearDirMessageDirDL(hashSubstring);
		DirMessage response = autoDirMessageSendAndReceive(dirDLMessage);
		
		if(response != null && response.getOperation().equals(DirMessageOps.OPERATION_DIRDL_FAILED)) {
			//tengo que leer el tipo de error
			int error = response.getErrorType();
			switch(error) {
			case 1:
				System.out.println("No se ha encontrado un fichero con la subcadena " +
				hashSubstring);	
				break;
			case 2:
				System.out.println("Ambiguo: Hay más de un fichero con la subcadena " +
				hashSubstring);	
				break;
			case 3:
				System.err.println("Error de recepción de mensaje por parte del servidor");
				break;
			}
			return null;
		}
		else if(response != null && response.getOperation().equals(DirMessageOps.OPERATION_DIRDL_OK)){
			FileInfo file = response.getDownloadableFile();
			filename = file.fileName;
			filehash = file.fileHash;
			filesize = file.fileSize;
			
			//tengo que construir mensajes para recibir mierdas
			fileData = new byte[(int) filesize];
			int bytesRecibidos = 0;
			
			System.out.println("Recibí confirmación. Empezaré a descargar el fichero '" + filename + "'.");
			
			while (bytesRecibidos < filesize) {
				DirMessage dirDLClientOkMessage = DirMessage.crearDirMessageDirDLClientReply(filehash, bytesRecibidos);
				DirMessage dataResponse = autoDirMessageSendAndReceive(dirDLClientOkMessage);
				
				if (dataResponse != null && dataResponse.getOperation().equals(DirMessageOps.OPERATION_DIRDL_SEND_OK)) {
					byte[] dataRecibida = dataResponse.getFileData();
					
					System.arraycopy(dataRecibida, 0, fileData, dataResponse.getOffset(), dataRecibida.length);
					
					System.out.println("He recibido un paquete del fichero.");
					bytesRecibidos += dataRecibida.length;
					System.out.println("Progreso: " + bytesRecibidos + " / " + filesize + " bytes.");
				} 
				else {
					System.err.println("Error en descarga de fichero. Demasiados intentos fallidos en el offset " + bytesRecibidos);
					return null;
				}
			}
		}
		

		return new DownloadedFile(filename, filesize, fileData, filehash);
	}

	/**
	 * Método para darse de baja como servidor de ficheros.
	 * 
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y ha dado de baja sus ficheros.
	 */
	public boolean unregisterFileServer() {
		boolean success = false;
		
		String nickname = NanoFiles.peerNickname;
		
		if (nickname == null || this.registeredServerPort == -1) {
			System.err.println("No puedes darte de baja si no te has registrado primero.");
			return false;
		}

		DirMessage quitMessage = DirMessage.crearDirMessageQuit(nickname, this.registeredServerPort);
		DirMessage response = autoDirMessageSendAndReceive(quitMessage);

		if (response != null && response.getOperation().equals(DirMessageOps.OPERATION_QUIT_OK)) {
			success = true;
			this.registeredServerPort = -1;
		}
		
		return success;
	}
}
