import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    // Use /tmp as default directory if none
    val directory = if (args.isNotEmpty() && args[0] == "--directory") {
        args[1]
    } else {
        "/tmp"
    }

    // Print debugging logs
    println("Logs from program will appear here!")

    // Create server socket on port 4221
    val serverSocket = ServerSocket(4221).apply {
        reuseAddress = true
    }

    // Create a thread pool to handle concurrent connections
    val threadPool = Executors.newFixedThreadPool(10)

    // Continuously accept client connections
    while (true) {
        val client = serverSocket.accept()
        println("Accepted new connection")

        // Submit each client connection to the thread pool
        threadPool.submit { handleClient(client, directory) }
    }
}

fun handleClient(client: Socket, directory: String) {
    client.use {
        // Handle the client's input and output streams
        val input = client.getInputStream()
        val output = client.getOutputStream()

        input.bufferedReader().use { reader ->
            // Read the request line and headers
            val inputs = mutableListOf<String>()
            var line = reader.readLine()
            
            while (line != null && line.isNotEmpty()) {
                inputs.add(line)
                line = reader.readLine()
            }

            // Ensure we have a valid request line and extract the path
            if (inputs.isNotEmpty()) {
                val requestLine = inputs[0].split(" ")
                if (requestLine.size > 1) {
                    val path = requestLine[1]

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
                    } else if (path == "/user-agent") {
                        // Find the User-Agent header from the request
                        val userAgentHeader = inputs.find { it.startsWith("User-Agent:", ignoreCase = true) }
                        val userAgent = userAgentHeader?.split(":")?.get(1)?.trim() ?: "Unknown"

                        // Build and send the HTTP response
                        output.write("HTTP/1.1 200 OK\r\n".toByteArray())
                        output.write("Content-Type: text/plain\r\n".toByteArray())
                        output.write("Content-Length: ${userAgent.length}\r\n\r\n".toByteArray())
                        output.write(userAgent.toByteArray())
                    } else if (path.startsWith("/files/")) {
                        // Extract the filename and locate the file
                        val filename = path.removePrefix("/files/")
                        val file = File(directory, filename)

                        if (file.exists() && file.isFile) {
                            // File exists, prepare the response
                            val fileContent = file.readBytes()
                            val contentLength = fileContent.size

                            output.write("HTTP/1.1 200 OK\r\n".toByteArray())
                            output.write("Content-Type: application/octet-stream\r\n".toByteArray())
                            output.write("Content-Length: $contentLength\r\n\r\n".toByteArray())
                            output.write(fileContent)  // Write file content as response body
                        } else {
                            // File does not exist, respond with 404 Not Found
                            output.write("HTTP/1.1 404 Not Found\r\n\r\n".toByteArray())
                        }
                    } else {
                        // Respond with a 404 Not Found for any other paths
                        output.write("HTTP/1.1 404 Not Found\r\n\r\n".toByteArray())
                    }
                    output.flush()
                }
            }
        }

        // Close the output stream and client socket
        output.close()
    }
}
