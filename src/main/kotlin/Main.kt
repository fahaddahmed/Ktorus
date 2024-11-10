import java.net.ServerSocket

fun main() {
    // Print debugging logs
    println("Logs from your program will appear here!")

    // Create server socket on port 4221
    val serverSocket = ServerSocket(4221).apply {
        reuseAddress = true
    }

    // Accept a client connection
    val client = serverSocket.accept()
    println("Accepted new connection")

    // Handle the client's input and output streams
    val input = client.getInputStream()
    val output = client.getOutputStream()

    input.bufferedReader().use { reader ->
        val line = reader.readLine()
        val splitLine = line.split(" ")
        
        // Ensure we have a valid request line and extract the path
        if (splitLine.size > 1) {
            val path = splitLine[1]

            if (path == "/") {
                // Root path, respond with a simple 200 OK response
                output.write("HTTP/1.1 200 OK\r\n\r\n".toByteArray())
            } else if (path.startsWith("/echo/")) {
                // Extract the string to echo from the URL
                val echoString = path.removePrefix("/echo/")
                val contentLength = echoString.length

                // Build and send the HTTP response
                output.write("HTTP/1.1 200 OK\r\n".toByteArray())
                output.write("Content-Type: text/plain\r\n".toByteArray())
                output.write("Content-Length: $contentLength\r\n\r\n".toByteArray())
                output.write(echoString.toByteArray())
            } else {
                // Respond with a 404 Not Found for any other paths
                output.write("HTTP/1.1 404 Not Found\r\n\r\n".toByteArray())
            }
            output.flush()
        }
    }

    // Close the output and the client and server sockets
    output.close()
    client.close()
    serverSocket.close()
}
