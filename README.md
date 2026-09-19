# NanoFiles - Sistema de Compartición de Archivos P2P

NanoFiles es un sistema híbrido de compartición y transferencia de ficheros Peer-to-Peer (P2P) desarrollado en Java[cite: 30]. Diseñado con una arquitectura modular, el sistema permite a los usuarios registrarse en un nodo centralizado, publicar sus archivos y conectarse directamente con otros usuarios para realizar descargas concurrentes[cite: 30].

## Características Principales

*   **Arquitectura Híbrida:** Combina un modelo cliente-servidor (UDP) para el descubrimiento de nodos y un modelo P2P puro (TCP) para la transferencia de datos[cite: 30].
*   **Protocolo UDP Confiable:** Implementa un mecanismo de "parada y espera" (stop-and-wait) con temporizadores y hasta 5 retransmisiones para tolerar posibles pérdidas de paquetes al comunicarse con el directorio[cite: 44, 50].
*   **Protocolos de Aplicación a Medida:** 
    *   El directorio utiliza un protocolo basado en texto ASCII estructurado en formato `campo:valor\n`[cite: 25, 46].
    *   La transferencia P2P utiliza un protocolo binario multiformato mucho más eficiente, estructurado mediante un byte de `Opcode`[cite: 27, 40].
*   **Servidor TCP Concurrente:** Cada peer puede convertirse en servidor ejecutando un hilo en segundo plano que escucha conexiones entrantes[cite: 29]. Utiliza un diseño multihilo (`NFServerThread`) para despachar múltiples descargas simultáneas sin bloquear la interfaz del usuario[cite: 29, 43].
*   **Descarga Paginada (Chunks):** Soporte para la transferencia de archivos grandes dividiéndolos en fragmentos mediante el uso de offsets, lo que optimiza la memoria y la fiabilidad de la descarga[cite: 27].
*   **Verificación de Integridad:** Comprobación criptográfica de los archivos descargados utilizando hashes SHA-1 para asegurar que los datos no han sufrido corrupción durante el tránsito[cite: 18, 26].
*   **Gestión de Colisiones:** Sistema automático de renombrado local que añade sufijos numéricos si el archivo descargado ya existe en la carpeta de destino[cite: 22].

## Estructura del Proyecto

*   **Directorio (`Directory.jar`):** Actúa como nodo central "stateless" en el puerto UDP 6868[cite: 30, 47]. Mantiene el censo en memoria de los peers registrados (IP y puerto TCP de escucha) y puede servir archivos pequeños desde su propia base local[cite: 30, 47].
*   **Peer (`NanoFiles.jar`):** Nodo interactivo con una interfaz de línea de comandos (Shell)[cite: 30]. Gestiona un autómata de estados finitos que valida la ejecución de comandos de red solo si el usuario ha establecido comunicación previa con el directorio[cite: 26, 35].

## Comandos Soportados (CLI)

*   `myfiles`: Muestra los ficheros alojados en la carpeta local compartida[cite: 37].
*   `ping`: Comprueba la disponibilidad del directorio y la compatibilidad del protocolo[cite: 37].
*   `dirfiles`: Obtiene la lista de ficheros que está sirviendo el directorio central[cite: 37].
*   `serve`: Arranca el servidor de ficheros local en segundo plano y lo registra en el directorio de forma automática[cite: 37].
*   `peers`: Consulta el censo actual de pares registrados en el directorio[cite: 37].
*   `peerfiles <nickname>`: Solicita vía TCP la lista de archivos disponibles en un peer específico[cite: 37].
*   `peerdl <nickname> <hash_substring>`: Inicia la descarga de un fichero desde un peer utilizando una subcadena de su hash SHA-1[cite: 37].
*   `dirdl <hash_substring>`: Descarga un archivo directamente desde el servidor de directorio[cite: 37].
*   `nick <nuevo_nombre>`: Cambia el apodo del usuario local antes de iniciar el servidor[cite: 37].
*   `quit`: Desregistra el servidor del directorio de forma limpia, detiene los hilos en segundo plano y cierra la aplicación[cite: 37].

## Ejecución

Para iniciar el servidor de directorio:
```bash
java -jar Directory.jar [-loss <probabilidad_perdida>] [-dir <carpeta_compartida>]
