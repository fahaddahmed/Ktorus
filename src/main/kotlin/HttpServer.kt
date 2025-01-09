import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.zip.GZIPOutputStream

/**
 * A simple HTTP server capable of handling multiple concurrent client connections.
 */
class HttpServer(private val port: Int, private val directory: String) {

    private val threadPool = Executors.newFixedThreadPool(10)

    /**
     * Starts the server and listens for incoming connections.
     */
    fun start() {
        println("Starting server on port $port")
        ServerSocket(port).use { serverSocket ->
            serverSocket.reuseAddress = true
            while (true) {
                val clientSocket = serverSocket.accept()
                println("Accepted new connection")
                threadPool.submit { handleClient(clientSocket) } // Handle each client in a separate thread
            }
        }
    }

    /**
     * Handles an individual client connection.
     * Parses request and generates appropriate response.
     */
    private fun handleClient(clientSocket: Socket) {
        clientSocket.use { socket ->
            val input = socket.getInputStream()
            val output = socket.getOutputStream()

            try {
                val request = HttpRequest.parse(input)
                val response = processRequest(request) // Process the request and generate response
                response.writeTo(output) // Send response to the client
            } catch (e: Exception) {
                println("Error handling client: ${e.message}")
                HttpResponse.badRequest().writeTo(output)
            }
        }
    }

    /**
     * Processes the HTTP request and generates an appropriate response.
     */
    private fun processRequest(request: HttpRequest): HttpResponse {
        return when (request.method) {
            HttpMethod.GET -> handleGet(request)
            HttpMethod.POST -> handlePost(request)
            else -> HttpResponse.methodNotAllowed() // Send 405 response for unsupported methods
        }
    }

    /**
     * Handles GET requests and routes them to the appropriate handler based on the request path.
     */
    private fun handleGet(request: HttpRequest): HttpResponse {
        return when {
            request.path == "/" -> HttpResponse.ok()
            request.path.startsWith("/echo/") -> handleEcho(request)
            request.path.startsWith("/files/") -> serveFile(request) // Serves files from the specified directory
            request.path == "/user-agent" -> {
                val userAgent = request.headers["User-Agent"] ?: "Unknown"
                HttpResponse.ok(body = userAgent)
            }
            else -> HttpResponse.notFound() // Sends 404 for unknown paths
        }
    }

    /**
     * Handles the /echo/ endpoint, optionally compressing the response with GZIP if requested.
     */
    private fun handleEcho(request: HttpRequest): HttpResponse {
        val message = request.path.removePrefix("/echo/")
        val acceptsGzip = request.headers["Accept-Encoding"]?.contains("gzip", ignoreCase = true) == true
        val bodyBytes = message.toByteArray()
        val body = if (acceptsGzip) compressData(bodyBytes) else bodyBytes
        val headers = mutableMapOf<String, String>().apply {
            put("Content-Type", "text/plain")
            put("Content-Length", body.size.toString())
            if (acceptsGzip) put("Content-Encoding", "gzip")
        }
        return HttpResponse(status = HttpStatus.OK, headers = headers, body = body)
    }

    /**
     * Serves a file from the specified directory if it exists, returning a 404 if not found.
     */
    private fun serveFile(request: HttpRequest): HttpResponse {
        val filename = request.path.removePrefix("/files/")
        val file = File(directory, filename)
        return if (file.exists() && file.isFile) {
            val body = file.readBytes()
            val headers = mapOf(
                "Content-Type" to "application/octet-stream", // Default MIME type for binary files
                "Content-Length" to body.size.toString()
            )
            HttpResponse(status = HttpStatus.OK, headers = headers, body = body)
        } else {
            HttpResponse.notFound()
        }
    }

    /**
     * Handles POST requests for creating or updating files in the server directory.
     * Writes the request body content to the specified filename.
     */
    private fun handlePost(request: HttpRequest): HttpResponse {
        return if (request.path.startsWith("/files/")) {
            val filename = request.path.removePrefix("/files/")
            val file = File(directory, filename)
            file.writeBytes(request.body) // Write content to file
            HttpResponse(status = HttpStatus.CREATED) // Return 201 Created status
        } else {
            HttpResponse.notFound()
        }
    }

    /**
     * Compresses data using GZIP, used primarily for the /echo endpoint if requested by the client.
     */
    private fun compressData(data: ByteArray): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use { it.write(data) }
        return byteArrayOutputStream.toByteArray()
    }
}
