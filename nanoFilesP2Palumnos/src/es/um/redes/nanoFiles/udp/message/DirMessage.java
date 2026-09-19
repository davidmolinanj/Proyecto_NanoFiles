package es.um.redes.nanoFiles.udp.message;

import java.net.InetAddress;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.net.UnknownHostException;


import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	
	/*
	 * DONE: (Boletín MensajesASCII) Definir de manera simbólica los nombres de
	 * todos los campos que pueden aparecer en los mensajes de este protocolo
	 * (formato campo:valor)
	 */

	//aqui van todos los fields
	private static final String FIELDNAME_OPERATION = "operation";
	
	
	private static final String FIELDNAME_PROTOCOL = "protocol";
	
	
	private static final String FIELDNAME_NICKNAME = "nickname";
	private static final String FIELDNAME_PORT = "port";
	private static final String FIELDNAME_IP = "ip";
	
	
	private static final String FIELDNAME_NUMBER_FILES = "number_files";
	private static final String FIELDNAME_FILE = "file";
	private static final String FIELDNAME_SIZE = "size";
	private static final String FIELDNAME_OFFSET = "offset";
	
	
	private static final String FIELDNAME_HASH_SUBSTRING = "hash_substring";
	private static final String FIELDNAME_DATA = "data";
	
	
	private static final String FIELDNAME_ERROR = "error";
	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	
	/*
	 * DONE?: (Boletín MensajesASCII) Crear un atributo correspondiente a cada uno de
	 * los campos de los diferentes mensajes de este protocolo.
	 */

	/**
	 * Identificador de protocolo usado, para comprobar compatibilidad del directorio.
	 */
	
	
	private String protocolId;
	
	private LinkedHashMap<String, InetSocketAddress> serversConectados;
	
	private String nickname;
	private int port;
	
	private int number_files;
	
	private FileInfo[] files;
	private FileInfo downloadableFile;
	
	private String hash_substring;
	
	//private String fileName;
	private byte[] fileData;
	private int offset = 0;
	
	private int errorType;
	
	
	
	
	public DirMessage(String op) {
		operation = op;
		
		serversConectados = new LinkedHashMap<String, InetSocketAddress>();
		files = new FileInfo[0];
	}

	/*
	 * DONE?: (Boletín MensajesASCII) Crear diferentes constructores adecuados para
	 * construir mensajes de diferentes tipos con sus correspondientes argumentos
	 * (campos del mensaje)
	 */
	
	public static DirMessage crearDirMessagePing(String protocolo) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_PING);
		mensaje.setProtocolID(protocolo);
		return mensaje;
	}
	
	public static DirMessage crearDirMessageServe(String nickname, int puerto) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_SERVE);
		mensaje.setNickname(nickname);
		mensaje.setPort(puerto);
		return mensaje;
	}
	
	public static DirMessage crearDirMessageQuit(String nickname, int puerto) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_QUIT);
		mensaje.setNickname(nickname);
		mensaje.setPort(puerto);
		return mensaje;
	}
	
	
	public static DirMessage crearDirMessageDirFilesList(FileInfo[] files) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_DIRFILES_LIST);
		mensaje.setFiles(files);
		mensaje.setNumber_files(files.length);
		return mensaje;
	}
	
	public static DirMessage crearDirMessageDirDL(String hash) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_DIRDL);
		mensaje.setHash_substring(hash);
		return mensaje;
	}
	
	public static DirMessage crearDirMessageDirDLReply(FileInfo downloadable) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_DIRDL_OK);
		mensaje.setDownloadableFile(downloadable);
		return mensaje;
	}
	
	public static DirMessage crearDirMessageDirDLReply(int error) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_DIRDL_FAILED);
		mensaje.setErrorType(error);
		return mensaje;
	}
	
	public static DirMessage crearDirMessageDirDLClientReply(String hash, int offset) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_DIRDL_CLIENT_OK);
		mensaje.setOffset(offset);
		mensaje.setHash_substring(hash);
		return mensaje;
	}

	public static DirMessage crearDirMessagePeersList(LinkedHashMap<String, InetSocketAddress> peers) {
		DirMessage mensaje = new DirMessage(DirMessageOps.OPERATION_PEERS_LIST);
		mensaje.setServersConectados(peers);
		return mensaje;
	}
	
	
	/*
	 * DONE?: (Boletín MensajesASCII) Crear métodos getter y setter para obtener los
	 * valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	
	
	public void setProtocolID(String protocolIdent) {
		
		if (!operation.equals(DirMessageOps.OPERATION_PING)) {
			throw new RuntimeException(
					"DirMessage: setProtocolId called for message of unexpected type (" + operation + ")");
		}
		
		protocolId = protocolIdent;
	}

	public String getProtocolId() {

		return protocolId;
	}
	
	public int getOffset() { 
		return offset; 
		}
	
	public void setOffset(int offset) {
		this.offset = offset; 
		}
	
	public LinkedHashMap<String, InetSocketAddress> getServersConectados() {
		return serversConectados;
	}

	public void setServersConectados(LinkedHashMap<String, InetSocketAddress> serversConectados) {
		if (!(operation.equals(DirMessageOps.OPERATION_SERVE) || 
				  operation.equals(DirMessageOps.OPERATION_QUIT) || 
				  operation.equals(DirMessageOps.OPERATION_PEERS_LIST) ||
				  operation.equals(DirMessageOps.OPERATION_SERVE_OK))) {
				throw new RuntimeException(
						"DirMessage: setServerToServersConectados called for message of unexpected type (" + operation + ")");
			}
		
		this.serversConectados = serversConectados;
	}
	
	
	
	public void addServerToServersConectados(String nickname, String ip, int puerto){
		if (!(operation.equals(DirMessageOps.OPERATION_SERVE) || 
			  operation.equals(DirMessageOps.OPERATION_QUIT) || 
			  operation.equals(DirMessageOps.OPERATION_PEERS_LIST))) {
			throw new RuntimeException(
					"DirMessage: addServerToServersConectados called for message of unexpected type (" + operation + ")");
		}
		
		InetAddress a = null;
		
		try {
            a = InetAddress.getByName(ip.trim());
        } catch (UnknownHostException e) {
            System.err.println("Error: Dirección IP inválida -> " + ip);
        }
		
		InetSocketAddress direccionServer = new InetSocketAddress(a, puerto);
		serversConectados.put(nickname, direccionServer);
	}
	
	
	public void setDownloadableFile(FileInfo file) {
		if (!(operation.equals(DirMessageOps.OPERATION_DIRDL_OK))) {
			throw new RuntimeException(
					"DirMessage: setDownloadableFile called for message of unexpected type (" + operation + ")");
		}
		this.downloadableFile = file;
	}
	
	public FileInfo getDownloadableFile() {
		return downloadableFile;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		if (!(operation.equals(DirMessageOps.OPERATION_SERVE) || operation.equals(DirMessageOps.OPERATION_QUIT)
				|| operation.equals(DirMessageOps.OPERATION_PEERS_LIST) || operation.equals(DirMessageOps.OPERATION_SERVE_OK))) {
			throw new RuntimeException(
					"DirMessage: setNickname called for message of unexpected type (" + operation + ")");
		}
		this.nickname = nickname;
	}
	

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if ( !( operation.equals(DirMessageOps.OPERATION_SERVE) || operation.equals(DirMessageOps.OPERATION_QUIT)
				|| operation.equals(DirMessageOps.OPERATION_PEERS_LIST) || operation.equals(DirMessageOps.OPERATION_SERVE_OK))) {
			throw new RuntimeException(
					"DirMessage: setPort called for message of unexpected type (" + operation + ")");
		}
		
		this.port = port;
	}
	
	
	

	public int getNumber_files() {
		return number_files;
	}

	public void setNumber_files(int number_files) {
		if (!(operation.equals(DirMessageOps.OPERATION_DIRFILES_LIST))) {
			throw new RuntimeException(
					"DirMessage: setNumber_files called for message of unexpected type (" + operation + ")");
		}
		
		this.number_files = number_files;
	}
	
	
	

	public FileInfo[] getFiles() {
		return files;
	}
	
	public void setFiles(FileInfo[] new_files){
		if (!(operation.equals(DirMessageOps.OPERATION_DIRFILES_LIST)) || operation.equals(DirMessageOps.OPERATION_DIRDL_OK)) {
			throw new RuntimeException(
					"DirMessage: setNumber_files called for message of unexpected type (" + operation + ")");
		}
		
		files = new_files;
		
	}

	
	
	public void addFiles(FileInfo f) {
		if (!(operation.equals(DirMessageOps.OPERATION_DIRFILES_LIST)) || operation.equals(DirMessageOps.OPERATION_DIRDL_OK)) {
			throw new RuntimeException(
					"DirMessage: addFiles called for message of unexpected type (" + operation + ")");
		}
		
		if (files == null) {
	        files = new FileInfo[] { f };
	        return;
	    }

	    // 2. Si ya tiene elementos, creamos una copia con un espacio extra
	    FileInfo[] nuevoArray = Arrays.copyOf(files, files.length + 1);

	    // 3. Metemos el nuevo archivo en la última posición
	    nuevoArray[nuevoArray.length - 1] = f;

	    // 4. Actualizamos la referencia de nuestro atributo al nuevo array
	    files = nuevoArray;
	}
	
	
	
	
	

	public String getHash_substring() {
		return hash_substring;
	}

	public void setHash_substring(String hash_substring) {
		
		if (!(operation.equals(DirMessageOps.OPERATION_DIRDL) || 
				operation.equals(DirMessageOps.OPERATION_DIRFILES_LIST)
				|| operation.equals(DirMessageOps.OPERATION_DIRDL_CLIENT_OK))) {
			throw new RuntimeException(
					"DirMessage: setHash_substring called for message of unexpected type (" + operation + ")");
		}
		
		this.hash_substring = hash_substring;
	}
	
	
	
	/*public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}*/
	
	
	

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		if (!(operation.equals(DirMessageOps.OPERATION_DIRDL) || 
			  operation.equals(DirMessageOps.OPERATION_DIRFILES_LIST) ||
			  operation.equals(DirMessageOps.OPERATION_DIRDL_SEND_OK))) {
			throw new RuntimeException(
					"DirMessage: setFileData called for message of unexpected type (" + operation + ")");
		}
		
		this.fileData = fileData;
	}
	
	
	
	

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		
		if (!operation.equals(DirMessageOps.OPERATION_DIRDL_FAILED)) {
			throw new RuntimeException(
					"DirMessage: setErrorType called for message of unexpected type (" + operation + ")");
		}
		
		this.errorType = errorType;
		
	}
	
	
	
	

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}

	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * DONE?: (Boletín MensajesASCII) Usar un bucle para parsear el mensaje línea a
		 * línea, extrayendo para cada línea el nombre del campo y el valor, usando el
		 * delimitador DELIMITER, y guardarlo en variables locales.
		 */
		
		DirMessage m = null;
		
		
		long fileSize = 0;
		String fileName = null;
	
		String ip = null;
		
		String[] lineas = message.split(END_LINE + "");
		
		for (String linea : lineas) { 
			
			if (linea.trim().isEmpty()) continue; // Salta líneas vacías
			int idx = linea.indexOf(DELIMITER);
			if (idx == -1) continue;
			
			String campo = linea.substring(0, idx).toLowerCase();
			String valor = linea.substring(idx + 1).trim(); 
			// DO SOMETHING WITH fieldName AND value
			
			switch (campo) {
				case FIELDNAME_OPERATION:{
					assert (m == null);
					m = new DirMessage(valor);
					break;
				}
									
				case FIELDNAME_PROTOCOL:{
					assert (m != null);
					m.setProtocolID(valor);
					break;
				}
					
				case FIELDNAME_NICKNAME:{
					assert (m != null);
					m.setNickname(valor);
					break;
				}
					
				case FIELDNAME_PORT:{
					assert (m != null);
					m.setPort(Integer.parseInt(valor));
					
					if(ip != null) {
						m.addServerToServersConectados(m.getNickname(), ip, m.getPort());
						
						ip = null;
						m.setPort(0);
						m.setNickname(null);
					}
					
					break;
				}
					
				case FIELDNAME_IP:{
					assert (m != null);
					ip = valor;
					break;
				}
					
				case FIELDNAME_NUMBER_FILES:{
					assert (m != null);
					m.setNumber_files(Integer.parseInt(valor));
					break;
				}
					
				case FIELDNAME_FILE:{
					assert (m != null);
					fileName = valor;
					break;
				}
					
				case FIELDNAME_SIZE:{
					assert (m != null);
					fileSize = Integer.parseInt(valor);
					break;
				}
					
				case FIELDNAME_HASH_SUBSTRING:{
					assert (m != null);
					
					if (m.getOperation().equals(DirMessageOps.OPERATION_DIRDL) || 
					    m.getOperation().equals(DirMessageOps.OPERATION_DIRDL_CLIENT_OK)) {
						m.setHash_substring(valor);

					} else if (m.getOperation().equals(DirMessageOps.OPERATION_DIRDL_OK)) {
						FileInfo downloadable = new FileInfo(valor, fileName, fileSize, null);
						m.setDownloadableFile(downloadable);

					} else if (m.getOperation().equals(DirMessageOps.OPERATION_DIRFILES_LIST)) {
						m.setHash_substring(valor); // Opcional
						FileInfo nuevo_fichero = new FileInfo(valor, fileName, fileSize, null);
						m.addFiles(nuevo_fichero);
						
						fileName = null;
						fileSize = 0;
					}
					break;
				}
					
				case FIELDNAME_OFFSET:{
					assert (m != null);
					m.setOffset(Integer.parseInt(valor));
					break;
				}
					
				case FIELDNAME_DATA:{
					assert (m != null);
					// decodificamos de base64 a bytes reales
					m.setFileData(java.util.Base64.getDecoder().decode(valor.trim()));
					break;
				}
					
				case FIELDNAME_ERROR:{
					assert (m != null);
					m.setErrorType(Integer.parseInt(valor));
					break;
				}
				
				default:
					System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + campo);
					System.err.println("Message was:\n" + message);
					System.exit(-1);
					
			}
		} 
		

		/*System.out.println("DirMessage read from socket:");
		System.out.println(message);*/
		
		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
		/*
		 * DONE: (Boletín MensajesASCII) En función de la operación del mensaje, crear
		 * una cadena la operación y concatenar el resto de campos necesarios usando los
		 * valores de los atributos del objeto.
		 */
		
		switch(operation) {
		
		//PING
		case DirMessageOps.OPERATION_PING:
			sb.append(FIELDNAME_PROTOCOL + DELIMITER + protocolId + END_LINE);
			break;
			
		case DirMessageOps.OPERATION_PING_OK:
			break;
			
		case DirMessageOps.OPERATION_PING_DENIED:
			break;
			
			
			
		//SERVE
		case DirMessageOps.OPERATION_SERVE:
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE);
			sb.append(FIELDNAME_PORT + DELIMITER + port + END_LINE);
			break;
			
		case DirMessageOps.OPERATION_SERVE_OK:
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE);
			sb.append(FIELDNAME_PORT + DELIMITER + port + END_LINE);
			break;
					
		
		//QUIT
		case DirMessageOps.OPERATION_QUIT:
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE);
			sb.append(FIELDNAME_PORT + DELIMITER + port + END_LINE);
			break;
			//quizá tengamos que sacarle de los swervidores aunque weno supongo que eso lo hace Directory solo
		
		case DirMessageOps.OPERATION_QUIT_OK:
			break;
			
			
		//PEERS
		case DirMessageOps.OPERATION_PEERS:
			break;
		
		case DirMessageOps.OPERATION_PEERS_LIST:
			for(String hostname : serversConectados.keySet()) {
				sb.append(FIELDNAME_NICKNAME + DELIMITER + hostname + END_LINE);
				
				InetSocketAddress address = serversConectados.get(hostname);
				
				sb.append(FIELDNAME_IP + DELIMITER + address.getAddress().getHostAddress() + END_LINE);
				sb.append(FIELDNAME_PORT + DELIMITER + address.getPort() + END_LINE);
			}
			break;
			
			
			//DIRFILES
		case DirMessageOps.OPERATION_DIRFILES:
			break;
			
		case DirMessageOps.OPERATION_DIRFILES_LIST:
			sb.append(FIELDNAME_NUMBER_FILES + DELIMITER + number_files + END_LINE);
			for(FileInfo cosa : files) {
				sb.append(FIELDNAME_FILE + DELIMITER + cosa.fileName + END_LINE);
				sb.append(FIELDNAME_SIZE + DELIMITER + cosa.fileSize + END_LINE);
				sb.append(FIELDNAME_HASH_SUBSTRING + DELIMITER + cosa.fileHash + END_LINE);
			}
			break;
			
			
			//DIRDL
		case DirMessageOps.OPERATION_DIRDL:
			sb.append(FIELDNAME_HASH_SUBSTRING + DELIMITER + hash_substring + END_LINE);
			break;
		
			//si no es ambiguo o no existe, da esto
		case DirMessageOps.OPERATION_DIRDL_OK:
			sb.append(FIELDNAME_FILE + DELIMITER + downloadableFile.fileName + END_LINE);
			sb.append(FIELDNAME_SIZE + DELIMITER + downloadableFile.fileSize + END_LINE);
			sb.append(FIELDNAME_HASH_SUBSTRING + DELIMITER + downloadableFile.fileHash + END_LINE);
			break;
			
			//otherwise, da esto:
		case DirMessageOps.OPERATION_DIRDL_FAILED:
			sb.append(FIELDNAME_ERROR + DELIMITER + errorType + END_LINE);
			break;
			
		case DirMessageOps.OPERATION_DIRDL_CLIENT_OK:
			sb.append(FIELDNAME_HASH_SUBSTRING + DELIMITER + hash_substring + END_LINE);
			sb.append(FIELDNAME_OFFSET + DELIMITER + offset + END_LINE);
			break;
			
		case DirMessageOps.OPERATION_DIRDL_SEND_OK:
			sb.append(FIELDNAME_OFFSET + DELIMITER + offset + END_LINE);
			String dataB64 = java.util.Base64.getEncoder().encodeToString(fileData);
			sb.append(FIELDNAME_DATA + DELIMITER + dataB64 + END_LINE);
			break;
			
		
			
		default:
			break;
		}
		

		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}

}
