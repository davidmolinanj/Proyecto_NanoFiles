package es.um.redes.nanoFiles.tcp.message;

import java.util.Map;
import java.util.TreeMap;

public class PeerMessageOps {

	public static final byte OPCODE_INVALID_CODE = 0;
	
	public static final byte OPCODE_PEERFILES = 1;
	public static final byte OPCODE_PEERFILES_LIST = 17;
	
	public static final byte OPCODE_PEERDL = 2;
	public static final byte OPCODE_PEERDL_OK = 18;
	public static final byte OPCODE_PEERDL_FAILED = -18;
	public static final byte OPCODE_PEERDL_GET_CHUNK = 19;
	public static final byte OPCODE_PEERDL_SEND_CHUNK = 20;
	// PEERDL_FAILED sirve también para rechazar GET_CHUNK,
	//por si no existe el substring que pide.
	
	


	/*
	 * DONE: (Boletín MensajesBinarios) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con un par
	 * servidor de ficheros (valores posibles del campo "operation").
	 */

	/*
	 * DONE: (Boletín MensajesBinarios) Definir constantes con nuevos opcodes de
	 * mensajes definidos anteriormente, añadirlos al array "valid_opcodes" y añadir
	 * su representación textual a "valid_operations_str" EN EL MISMO ORDEN.
	 */
	private static final Byte[] _valid_opcodes = { OPCODE_INVALID_CODE,
			OPCODE_PEERFILES, OPCODE_PEERFILES_LIST,
			OPCODE_PEERDL, OPCODE_PEERDL_OK, OPCODE_PEERDL_FAILED,
			OPCODE_PEERDL_GET_CHUNK, OPCODE_PEERDL_SEND_CHUNK
	};
	private static final String[] _valid_operations_str = { "INVALID_OPCODE",
			"PEERFILES_PETITION", "PEERFILES_LIST",
			"PEERDL_PETITION", "PEERDL_OK", "PEERDL_FAILED",
			"PEERDL_CHUNK_GET", "PEERDL_CHUNK_SEND"
	};

	private static Map<String, Byte> _operation_to_opcode;
	private static Map<Byte, String> _opcode_to_operation;

	static {
		_operation_to_opcode = new TreeMap<>();
		_opcode_to_operation = new TreeMap<>();
		for (int i = 0; i < _valid_operations_str.length; ++i) {
			_operation_to_opcode.put(_valid_operations_str[i].toLowerCase(), _valid_opcodes[i]);
			_opcode_to_operation.put(_valid_opcodes[i], _valid_operations_str[i]);
		}
	}

	/**
	 * Transforma una cadena en el opcode correspondiente
	 */
	protected static byte operationToOpcode(String opStr) {
		return _operation_to_opcode.getOrDefault(opStr.toLowerCase(), OPCODE_INVALID_CODE);
	}

	/**
	 * Transforma un opcode en la cadena correspondiente
	 */
	public static String opcodeToOperation(byte opcode) {
		return _opcode_to_operation.getOrDefault(opcode, null);
	}
}
