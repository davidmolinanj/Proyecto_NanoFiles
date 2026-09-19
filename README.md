# NanoFiles - Sistema de Compartición de Archivos P2P

NanoFiles es un sistema híbrido de compartición y transferencia de ficheros Peer-to-Peer (P2P) desarrollado en Java. Diseñado con una arquitectura modular, el sistema permite a los usuarios registrarse en un nodo centralizado, publicar sus archivos y conectarse directamente con otros usuarios para realizar descargas concurrentes.

## Características Principales

*   **Arquitectura Híbrida:** Combina un modelo cliente-servidor (UDP) para el descubrimiento de nodos y un modelo P2P puro (TCP) para la transferencia de datos.
*   **Protocolo UDP Confiable:** Implementa un mecanismo de "parada y espera" (stop-and-wait) con temporizadores y hasta 5 retransmisiones para tolerar posibles pérdidas de paquetes al comunicarse con el directorio.
*   **Protocolos de Aplicación a Medida:** 
    *   El directorio utiliza un protocolo basado en texto ASCII estructurado en formato `campo:valor\n`.
    *   La transferencia P2P utiliza un protocolo binario multiformato mucho más eficiente, estructurado mediante un byte de `Opcode`.
*   **Servidor TCP Concurrente:** Cada peer puede convertirse en servidor ejecutando un hilo en segundo plano que escucha conexiones entrantes. Utiliza un diseño multihilo (`NFServerThread`) para despachar múltiples descargas simultáneas sin bloquear la interfaz del usuario.
*   **Descarga Paginada (Chunks):** Soporte para la transferencia de archivos grandes dividiéndolos en fragmentos mediante el uso de offsets, lo que optimiza la memoria y la fiabilidad de la descarga.
*   **Verificación de Integridad:** Comprobación criptográfica de los archivos descargados utilizando hashes SHA-1 para asegurar que los datos no han sufrido corrupción durante el tránsito.
*   **Gestión de Colisiones:** Sistema automático de renombrado local que añade sufijos numéricos si el archivo descargado ya existe en la carpeta de destino.

## Estructura del Proyecto

*   **Directorio (`Directory.jar`):** Actúa como nodo central "stateless" en el puerto UDP 6868. Mantiene el censo en memoria de los peers registrados (IP y puerto TCP de escucha) y puede servir archivos pequeños desde su propia base local.
*   **Peer (`NanoFiles.jar`):** Nodo interactivo con una interfaz de línea de comandos (Shell). Gestiona un autómata de estados finitos que valida la ejecución de comandos de red solo si el usuario ha establecido comunicación previa con el directorio.

## Comandos Soportados (CLI)

*   `myfiles`: Muestra los ficheros alojados en la carpeta local compartida.
*   `ping`: Comprueba la disponibilidad del directorio y la compatibilidad del protocolo.
*   `dirfiles`: Obtiene la lista de ficheros que está sirviendo el directorio central.
*   `serve`: Arranca el servidor de ficheros local en segundo plano y lo registra en el directorio de forma automática.
*   `peers`: Consulta el censo actual de pares registrados en el directorio.
*   `peerfiles <nickname>`: Solicita vía TCP la lista de archivos disponibles en un peer específico.
*   `peerdl <nickname> <hash_substring>`: Inicia la descarga de un fichero desde un peer utilizando una subcadena de su hash SHA-1.
*   `dirdl <hash_substring>`: Descarga un archivo directamente desde el servidor de directorio.
*   `nick <nuevo_nombre>`: Cambia el apodo del usuario local antes de iniciar el servidor.
*   `quit`: Desregistra el servidor del directorio de forma limpia, detiene los hilos en segundo plano y cierra la aplicación.

## Ejecución y Pruebas

Al tratarse de un repositorio exclusivamente de código fuente, para probar el sistema debes importarlo en un entorno de desarrollo o compilar previamente los ejecutables.

**Opción A: Desde Eclipse (Recomendado)**
1. Importa el proyecto desde `File > Import > General > Existing Projects into Workspace` seleccionando la carpeta `nanoFilesP2Palumnos`.
2. Haz clic derecho sobre `Directory.java` y selecciona `Run As > Java Application` para arrancar el servidor central.
3. Haz clic derecho sobre `NanoFiles.java` y selecciona `Run As > Java Application` para arrancar un peer. Puedes lanzar esta clase varias veces en paralelo para simular múltiples usuarios conectados a la red simultáneamente.

**Opción B: Mediante terminal (requiere exportar los .jar)**
Si exportas el proyecto desde tu IDE como "Runnable JAR file" (creando `Directory.jar` y `NanoFiles.jar`), abre distintas ventanas de tu terminal para simular los nodos:

*Terminal 1 (Directorio central):*
`java -jar Directory.jar`

*Terminal 2 (Peer 1 con su propia carpeta local):*
`java -jar NanoFiles.jar carpeta_peer1`

*Terminal 3 (Peer 2 con su propia carpeta local):*
`java -jar NanoFiles.jar carpeta_peer2`
