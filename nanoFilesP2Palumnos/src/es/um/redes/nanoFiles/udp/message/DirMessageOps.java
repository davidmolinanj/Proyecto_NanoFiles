package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * DONE: (Boletín MensajesASCII) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con el
	 * directorio (valores posibles del campo "operation").
	 */
	
	
	//OPERACIONES CLIENTE
	public static final String OPERATION_PING = "ping";
	
	public static final String OPERATION_SERVE = "serve";
	
	public static final String OPERATION_QUIT = "quit";
	
	public static final String OPERATION_PEERS = "peers";
	
	public static final String OPERATION_DIRFILES = "dirfiles";
	
	public static final String OPERATION_DIRDL = "dirdl";
	
	
	//RESPUESTAS//
	
	//Familia 'ping'
	public static final String OPERATION_PING_OK = "pingOk";
	public static final String OPERATION_PING_DENIED = "pingDenied";

	//Familia 'serve' y 'quit'
	public static final String OPERATION_SERVE_OK = "serveOk";
	public static final String OPERATION_QUIT_OK = "quitOk";
	
	//Familia 'peers'
	public static final String OPERATION_PEERS_LIST = "peersList";
	
	//Familia 'dirfiles'
	public static final String OPERATION_DIRFILES_LIST = "dirfilesList";
	
	//Familia 'dirdl'
	public static final String OPERATION_DIRDL_OK = "dirdlOk";
	public static final String OPERATION_DIRDL_FAILED = "dirdlFailed";
	public static final String OPERATION_DIRDL_CLIENT_OK = "clientFileOk";
	public static final String OPERATION_DIRDL_SEND_OK = "nextPacket";

	//Error default
	public static final String OPERATION_INVALID = "invalid_operation";




}
