package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessageTest {

	public static void main(String[] args) throws IOException {
		String nombreArchivo = "peermsg.bin";
		DataOutputStream fos = new DataOutputStream(new FileOutputStream(nombreArchivo));

		System.out.println("--- 📦 ESCRIBIENDO MENSAJES (DataOutputStream) ---");

		// 1. PEERFILES (Sin atributos adicionales)
		PeerMessage msgOut1 = new PeerMessage(PeerMessageOps.OPCODE_PEERFILES);
		msgOut1.writeMessageToOutputStream(fos);

		// 2. PEERFILES_LIST (Array de FileInfo)
		PeerMessage msgOut2 = new PeerMessage(PeerMessageOps.OPCODE_PEERFILES_LIST);
		FileInfo[] lista = new FileInfo[1];
		// NOTA: Revisa si el constructor de FileInfo de tu proyecto es (hash, nombre, tamaño, ruta) o (nombre, hash, tamaño, ruta)
		lista[0] = new FileInfo("hash123", "archivo_prueba.txt", 1024, null); 
		msgOut2.setFileList(lista);
		msgOut2.writeMessageToOutputStream(fos);

		// 3. PEERDL (Hash en texto)
		PeerMessage msgOut3 = PeerMessage.crearPeerDLMessage("hash_substring_test");
		msgOut3.writeMessageToOutputStream(fos);

		// 4. PEERDL_OK (FileInfo único)
		FileInfo pedido = new FileInfo("hash_completo_video", "video.mp4", 999999, null);
		PeerMessage msgOut4 = PeerMessage.crearPeerDLOkMessage(pedido);
		msgOut4.writeMessageToOutputStream(fos);

		// 5. PEERDL_FAILED (Byte de error)
		PeerMessage msgOut5 = PeerMessage.crearPeerDLError((byte) 2);
		msgOut5.writeMessageToOutputStream(fos);

		// 6. PEERDL_GET_CHUNK (Hash + Offset)
		PeerMessage msgOut6 = PeerMessage.crearPeerGetChunk("hash_chunk_test", 65536);
		msgOut6.writeMessageToOutputStream(fos);

		// 7. PEERDL_SEND_CHUNK (Offset + Datos crudos)
		byte[] datosSimulados = "Hola Mundo desde el Peer Server!".getBytes();
		PeerMessage msgOut7 = PeerMessage.crearPeerSendChunk(datosSimulados, 65536);
		msgOut7.writeMessageToOutputStream(fos);

		fos.close();
		System.out.println("Todos los mensajes empaquetados correctamente en disco.\\n");

		
		System.out.println("--- 🔍 LEYENDO Y COMPROBANDO MENSAJES (DataInputStream) ---");
		DataInputStream fis = new DataInputStream(new FileInputStream(nombreArchivo));

		// Leer y comprobar 1
		PeerMessage msgIn1 = PeerMessage.readMessageFromInputStream(fis);
		comprobar(msgIn1.getOpcode() == PeerMessageOps.OPCODE_PEERFILES, "PEERFILES");

		// Leer y comprobar 2
				PeerMessage msgIn2 = PeerMessage.readMessageFromInputStream(fis);
				boolean check2 = (msgIn2.getOpcode() == PeerMessageOps.OPCODE_PEERFILES_LIST) &&
								 (msgIn2.getFileList().length == 1) &&
								 (msgIn2.getFileList()[0].fileSize == 1024) &&
								 (msgIn2.getFileList()[0].fileName.equals("archivo_prueba.txt")) &&
								 (msgIn2.getFileList()[0].fileHash.equals("hash123"));
				comprobar(check2, "PEERFILES_LIST (Valores del array, nombre y hash preservados)");
		// Leer y comprobar 3
		PeerMessage msgIn3 = PeerMessage.readMessageFromInputStream(fis);
		boolean check3 = (msgIn3.getOpcode() == PeerMessageOps.OPCODE_PEERDL) &&
						 (msgIn3.getFileHash().equals("hash_substring_test"));
		comprobar(check3, "PEERDL (Hash string preservado)");

		// Leer y comprobar 4
		PeerMessage msgIn4 = PeerMessage.readMessageFromInputStream(fis);
		boolean check4 = (msgIn4.getOpcode() == PeerMessageOps.OPCODE_PEERDL_OK) &&
						 (msgIn4.getFilePedido().fileName.equals("video.mp4")) &&
						 (msgIn4.getFilePedido().fileSize == 999999);
		comprobar(check4, "PEERDL_OK (Objeto FileInfo restaurado)");

		// Leer y comprobar 5
		PeerMessage msgIn5 = PeerMessage.readMessageFromInputStream(fis);
		boolean check5 = (msgIn5.getOpcode() == PeerMessageOps.OPCODE_PEERDL_FAILED) &&
						 (msgIn5.getErrorByte() == 2);
		comprobar(check5, "PEERDL_FAILED (Byte de error conservado)");

		// Leer y comprobar 6
		PeerMessage msgIn6 = PeerMessage.readMessageFromInputStream(fis);
		boolean check6 = (msgIn6.getOpcode() == PeerMessageOps.OPCODE_PEERDL_GET_CHUNK) &&
						 (msgIn6.getFileHash().equals("hash_chunk_test")) &&
						 (msgIn6.getFileOffset() == 65536);
		comprobar(check6, "PEERDL_GET_CHUNK (Hash + Long offset)");

		// Leer y comprobar 7
		PeerMessage msgIn7 = PeerMessage.readMessageFromInputStream(fis);
		boolean check7 = (msgIn7.getOpcode() == PeerMessageOps.OPCODE_PEERDL_SEND_CHUNK) &&
						 (msgIn7.getFileOffset() == 65536) &&
						 (Arrays.equals(msgIn7.getData(), datosSimulados)); // Arrays.equals compara el contenido byte a byte
		comprobar(check7, "PEERDL_SEND_CHUNK (Datos binarios reensamblados correctamente)");

		fis.close();
		System.out.println("✨ ¡Todas las pruebas de serialización TCP han sido realizadas.");
	}

	// Método auxiliar para imprimir bonito
	private static void comprobar(boolean condicion, String nombreTest) {
		if (condicion) {
			System.out.println("✅ [OK] " + nombreTest);
		} else {
			System.err.println("❌ [FALLO] " + nombreTest);
		}
	}
}