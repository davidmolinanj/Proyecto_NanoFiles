package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {

	private byte opcode;
	private byte errorByte;
	
	//peerfiles
	//private String nickname;
		
	//Filelist
	
	/*private String fileName;
	private int fileNameLength;*/
	
	private String fileHash;
	private int fileHashLength;
	
	//private long fileSize;
	
	private FileInfo[] fileList;
	private FileInfo filePedido;
	
	//peerdl
	/*private String fileHash;
	private int fileHashLength;*/
	

	//Get chunk
	private long fileOffset;
	private byte[] data;
	
	
	
	
	
	
	/*
	 * DONE: (Boletín MensajesBinarios) Añadir atributos u otros constructores
	 * específicos para crear mensajes con otros campos, según sea necesario
	 * 
	 */

	
	//CONSTRUCTORS
	
	public PeerMessage(byte op) {
		opcode = op;
		fileList = new FileInfo[0];
		filePedido = new FileInfo();
	}
	
	
	//SPECIALIZED CONSTRUCTORS
	
	//petición lista ficheros
		public static PeerMessage crearPeerFilesListMessage(FileInfo[] ficheros) {
			PeerMessage mensaje = new PeerMessage(PeerMessageOps.OPCODE_PEERFILES_LIST);
			mensaje.setFileList(ficheros);

			return mensaje;
		}
	
	
	//petición download
	public static PeerMessage crearPeerDLMessage(String hash_substring) {
		PeerMessage mensaje = new PeerMessage(PeerMessageOps.OPCODE_PEERDL);
		mensaje.setFileHashLength(hash_substring.length());
		mensaje.setFileHash(hash_substring);
		
		return mensaje;
	}
	
	
	//primera respuesta download
	public static PeerMessage crearPeerDLOkMessage(FileInfo file) {
		PeerMessage mensaje = new PeerMessage(PeerMessageOps.OPCODE_PEERDL_OK);
		mensaje.setFilePedido(file);

		return mensaje;
	}
	
	//error respuesta download
	public static PeerMessage crearPeerDLError(byte error) {
		PeerMessage mensaje = new PeerMessage(PeerMessageOps.OPCODE_PEERDL_FAILED);
		mensaje.setErrorByte(error);

		return mensaje;
	}
	
	//pedir chunk	
	public static PeerMessage crearPeerGetChunk(String file_hash, long offset) {
		PeerMessage mensaje = new PeerMessage(PeerMessageOps.OPCODE_PEERDL_GET_CHUNK);
		mensaje.setFileHash(file_hash);
		mensaje.setFileOffset(offset);

		return mensaje;
	}
	
	//enviar chunk
	public static PeerMessage crearPeerSendChunk(byte[] rawData, long offset) {
		PeerMessage mensaje = new PeerMessage(PeerMessageOps.OPCODE_PEERDL_SEND_CHUNK);
		mensaje.setData(rawData);
		mensaje.setFileOffset(offset);

		return mensaje;
	}


	/*
	 * TODO: (Boletín MensajesBinarios) Crear métodos getter y setter para obtener
	 * los valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */

	//GETTERS AND SETTERS
	
		// Error
		public byte getErrorByte() {
			return errorByte;
		}

		public void setErrorByte(byte errorByte) {
			if (opcode != PeerMessageOps.OPCODE_PEERDL_FAILED) {
				throw new RuntimeException("PeerMessage: setErrorByte called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			this.errorByte = errorByte;
		}

		// FileHash
		public String getFileHash() {
			return fileHash;
		}

		public void setFileHash(String fileHash) {
			if (!(opcode == PeerMessageOps.OPCODE_PEERDL || opcode == PeerMessageOps.OPCODE_PEERDL_GET_CHUNK)) {
				throw new RuntimeException("PeerMessage: setFileHash called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			this.fileHash = fileHash;
			this.fileHashLength = (fileHash != null) ? fileHash.length() : 0;
		}

		public int getFileHashLength() {
			return fileHashLength;
		}

		public void setFileHashLength(int fileHashLength) {
			if (!(opcode == PeerMessageOps.OPCODE_PEERDL || opcode == PeerMessageOps.OPCODE_PEERDL_GET_CHUNK)) {
				throw new RuntimeException("PeerMessage: setFileHashLength called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			this.fileHashLength = fileHashLength;
		}

		// FileOffset
		public long getFileOffset() {
			return fileOffset;
		}

		public void setFileOffset(long fileOffset) {
			if (!(opcode == PeerMessageOps.OPCODE_PEERDL_GET_CHUNK || opcode == PeerMessageOps.OPCODE_PEERDL_SEND_CHUNK)) {
				throw new RuntimeException("PeerMessage: setFileOffset called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			this.fileOffset = fileOffset;
		}

		// RawData
		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			if (opcode != PeerMessageOps.OPCODE_PEERDL_SEND_CHUNK) {
				throw new RuntimeException("PeerMessage: setData called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			this.data = data;
		}

		// Operation (Opcode)
		public byte getOpcode() {
			return opcode;
		}
		
		public void setOpcode(byte opcode) {
			this.opcode = opcode;
		}

		// FileList
		public FileInfo[] getFileList() {
			return fileList;
		}

		public void setFileList(FileInfo[] fileList) {
			if (opcode != PeerMessageOps.OPCODE_PEERFILES_LIST) {
				throw new RuntimeException("PeerMessage: setFileList called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			this.fileList = fileList;
		}
		
		public void addToFileList(FileInfo file, int i) {
			if (opcode != PeerMessageOps.OPCODE_PEERFILES_LIST) {
				throw new RuntimeException("PeerMessage: addToFileList called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			fileList[i] = file;
		}

		// File Pedido (Para PEERDL_OK)
		public FileInfo getFilePedido() {
			return filePedido;
		}

		public void setFilePedido(FileInfo filePedido) {
			if (opcode != PeerMessageOps.OPCODE_PEERDL_OK) {
				throw new RuntimeException("PeerMessage: setFilePedido called for message of unexpected type (" 
						+ PeerMessageOps.opcodeToOperation(opcode) + ")");
			}
			this.filePedido = filePedido;
		}
		
		
		
		


	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: (Boletín MensajesBinarios) En función del tipo de mensaje, leer del
		 * socket a través del "dis" el resto de campos para ir extrayendo con los
		 * valores y establecer los atributos del un objeto PeerMessage que contendrá
		 * toda la información del mensaje, y que será devuelto como resultado. NOTA:
		 * Usar dis.readFully para leer un array de bytes, dis.readInt para leer un
		 * entero, etc.
		 */
		byte opcode = dis.readByte();
		PeerMessage message = new PeerMessage(opcode);
		
		String name = null;
		String hash = null;
		long size = 0;
		
		switch (opcode) {
		case PeerMessageOps.OPCODE_INVALID_CODE:
			break;
			
		case PeerMessageOps.OPCODE_PEERFILES:
			//el nickname no es parte del mensaje.
			//mensaje de petición simple (tipo Control)
			break;
			
		case PeerMessageOps.OPCODE_PEERFILES_LIST: {
			
			int nfiles = dis.readInt();
			
			FileInfo[] ficheros_en_server = new FileInfo[nfiles];
			
			for(int i = 0; i < nfiles; i++) {
				
				//fileName
				byte[] rawName = new byte[dis.readInt()];
				dis.readFully(rawName);
				name = new String(rawName);
				
				//fileHash
				byte[] rawHash = new byte[dis.readInt()];
				dis.readFully(rawHash);
				hash = new String(rawHash);
				
				//fileSize
				size = dis.readLong();
				
				//crear entrada en fileList
				FileInfo fichero = new FileInfo(hash, name, size, null);
				ficheros_en_server[i] = fichero;
				
			}
			
			message.setFileList(ficheros_en_server);
			
			name = null;
			hash = null;
			size = 0;
			
			break;
			
		}
			
		case PeerMessageOps.OPCODE_PEERDL:{
			//aquí envía sólo el hash que quiere enviar, y su tamaño
			
			int hashSize = dis.readInt();
			
			message.setFileHashLength(hashSize);
			
			byte[] rawHash = new byte[hashSize];
			dis.readFully(rawHash);
			hash = new String(rawHash);
			
			message.setFileHash(hash);
			
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_OK: {
			//aquí envía el filename, filehash y filesize correspondiente!!!
			
			//fileName
			byte[] rawName = new byte[dis.readInt()];
			dis.readFully(rawName);
			name = new String(rawName);
			
			//fileHash
			byte[] rawHash = new byte[dis.readInt()];
			dis.readFully(rawHash);
			hash = new String(rawHash);
			
			//fileSize
			size = dis.readLong();
			
			//crear entrada en fileList
			FileInfo fichero = new FileInfo(hash, name, size, null);
			message.setFilePedido(fichero);
			
			name = null;
			hash = null;
			size = 0;
			
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_FAILED:{
			message.setErrorByte(dis.readByte());
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_GET_CHUNK:{
			//leo el substring, y si es erróneo, creo un mensaje
			
			byte[] rawHash = new byte[dis.readInt()];
			dis.readFully(rawHash);
			hash = new String(rawHash);
			
			message.setFileHash(hash);
			
			//leo el offset desde el que empiesooo
			
			message.setFileOffset(dis.readLong());
			
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_SEND_CHUNK:{
			//esto va a enviar el tamaño del paquete y los datos!!
			size = dis.readLong();
			message.setFileOffset(size);
			
			int tamDatos = dis.readInt();
			byte[] datos = new byte[tamDatos];
		    dis.readFully(datos); 
		    
		    message.setData(datos);
		    
		    break;
			
		}
			


		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		
		
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO (Boletín MensajesBinarios): Escribir los bytes en los que se codifica el
		 * mensaje en el socket a través del "dos", teniendo en cuenta opcode del
		 * mensaje del que se trata y los campos relevantes en cada caso. NOTA: Usar
		 * dos.write para leer un array de bytes, dos.writeInt para escribir un entero,
		 * etc.
		 */

		dos.writeByte(opcode);
		
		switch (opcode) {
		case PeerMessageOps.OPCODE_INVALID_CODE:
			break;
			
		case PeerMessageOps.OPCODE_PEERFILES:
			//el nickname no es parte del mensaje.
			//mensaje de petición simple (tipo Control)
			break;
			
		case PeerMessageOps.OPCODE_PEERFILES_LIST: {
			//va a tener que escribir un número y luego un porraco de files
			
			dos.writeInt(fileList.length);
	
			for(int i = 0; i < fileList.length; i++) {
				FileInfo fichero = fileList[i];
				
				//fileName
				byte[] name = fichero.fileName.getBytes();
				dos.writeInt(name.length);
				dos.write(name);
				
				//fileHash
				byte[] hash = fichero.fileHash.getBytes();
				dos.writeInt(hash.length);
				dos.write(hash);
				
				//fileSize
				dos.writeLong(fichero.fileSize);
			}
			break;
			
		}
			
		case PeerMessageOps.OPCODE_PEERDL:{
			//aquí envía sólo el hash que quiere enviar, y su tamaño
			
			byte[] hashDLBytes = fileHash.getBytes();
			dos.writeInt(hashDLBytes.length);
			dos.write(hashDLBytes);
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_OK: {
			//aquí envía el filename, filehash y filesize correspondiente!!!
			
			//fileName
			byte[] name = filePedido.fileName.getBytes();
			dos.writeInt(name.length);
			dos.write(name);
			
			//fileHash
			byte[] hash = filePedido.fileHash.getBytes();
			dos.writeInt(hash.length);
			dos.write(hash);
			
			//fileSize
			dos.writeLong(filePedido.fileSize);
			
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_FAILED:{
			dos.writeByte(errorByte);
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_GET_CHUNK:{
			//leo el substring, y si es erróneo, creo un mensaje
			
			byte[] hashChunkBytes = fileHash.getBytes();
			dos.writeInt(hashChunkBytes.length);
			dos.write(hashChunkBytes);
			
			//offset desde el que pedimos
			dos.writeLong(fileOffset);
			break;
		}
			
		case PeerMessageOps.OPCODE_PEERDL_SEND_CHUNK:{
			//esto va a enviar el tamaño del paquete y los datos!!
			
			dos.writeLong(fileOffset); 
		    dos.writeInt(data.length); 
		    dos.write(data);          
		    break;
			
		}

		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}


}
