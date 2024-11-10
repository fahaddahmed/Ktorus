import java.net.ServerSocket;

fun main() {
    println("Logs")
    val serverSocket = ServerSocket(4221).apply {
        reuseAddress = true // Ensures we don’t run into 'Address already in use' errors
    }
    // Accept a client connection
    val client = serverSocket.accept() // Wait for connection from client.
    println("Accepted new connection")

    // Handle the client's output
    val output = client.getOutputStream()
    output.write("HTTP/1.1 200 OK\r\n\r\n".toByteArray())
    output.flush()
    output.close()

    // Close the client and server sockets
    client.close()
    serverSocket.close()
}
