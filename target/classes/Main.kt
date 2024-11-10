import java.net.ServerSocket;

fun main() {
    println("Logs")
    val serverSocket = ServerSocket(4221).apply {
        reuseAddress = true // Ensures we don’t run into 'Address already in use' errors
    }
    // Accept a client connection
    val client = serverSocket.accept() // Wait for connection from client.
    println("Accepted new connection")

    // Accept a client connection
    val client = serverSocket.accept() // Wait for connection from client.
    println("Accepted new connection")

    // Handle the client's output & input
    val input = client.getInputStream()
    val output = client.getOutputStream()

    input.bufferedReader().use { reader ->
        val line = reader.readLine()
        val splitLine = line.split(' ')
        val path = if (splitLine.size > 1) splitLine[1] else "/"

        if (path == "/") {
            output.write("HTTP/1.1 200 OK\r\n\r\n".toByteArray())
        } else {
            output.write("HTTP/1.1 404 Not Found\r\n\r\n".toByteArray())
        }
        output.flush()
    }

    // Close the output and the client and server sockets
    output.close()
    client.close()
    serverSocket.close()
}

