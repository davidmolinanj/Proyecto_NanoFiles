package es.um.redes.nanoFiles.tcp.server;

import java.net.Socket;

public class NFServerThread extends Thread {
	private Socket socket;
	
	/*
	 * DONE?: Esta clase modela los hilos que son creados desde NFServer y cada uno
	 * de los cuales simplemente se encarga de invocar a
	 * NFServer.serveFilesToClient con el socket retornado por el método accept
	 * (un socket distinto para "conversar" con un cliente)
	 */
	

	public NFServerThread(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		NFServer.serveFilesToClient(socket);
		
		try {
			if (!socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
