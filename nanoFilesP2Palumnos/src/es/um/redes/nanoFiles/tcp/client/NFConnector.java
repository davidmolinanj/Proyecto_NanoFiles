package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.*;
import es.um.redes.nanoFiles.util.FileInfo;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	private DataInputStream dis;
	private DataOutputStream dos;




	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * DONE: (Boletín SocketsTCP) Se crea el socket a partir de la dirección del
		 * servidor (IP, puerto). La creación exitosa del socket significa que la
		 * conexión TCP ha sido establecida. AVANZADO EN CLASE??
		 */
		socket = new Socket(fserverAddr.getAddress(), fserverAddr.getPort());
		
		
		/*
		 * DONE: (Boletín SocketsTCP) Se crean los DataInputStream/DataOutputStream a
		 * partir de los streams de entrada/salida del socket creado. Se usarán para
		 * enviar (dos) y recibir (dis) datos del servidor. AVANZADO EN CLASE??
		 */
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());


	}

	public void test() {
		/*
		 * DONE: (Boletín SocketsTCP) Enviar entero cualquiera a través del socket y
		 * después recibir otro entero, comprobando que se trata del mismo valor.
		 */
		try {
			dos.writeInt(55);
			int numero = dis.readInt();
			if(numero==55)
				System.out.println("Es el mismo numero");
			else {
				System.out.println("ERROR! numero no igual");
			}		
			
			//tengo que enviar un mensaje PeerMessage de PeerList
			
			PeerMessage mensajePeticion = new PeerMessage(PeerMessageOps.OPCODE_PEERFILES);
			PeerMessage mensajeRespuesta = sendPeerMessageAndReceiveResponse(mensajePeticion);
			
			FileInfo[] files = null;
			files = mensajeRespuesta.getFileList();
			if(files != null) {
				System.out.println("LETS GOOOOO");
			}
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	public PeerMessage sendPeerMessageAndReceiveResponse(PeerMessage request) throws IOException {
		
		request.writeMessageToOutputStream(dos);
		PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
		
		return response;
	}



	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}