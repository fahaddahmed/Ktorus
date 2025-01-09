import java.net.ServerSocket

/**
 * Entry point of the application.
 * Parses command-line arguments and starts the HTTP server.
 */
fun main(args: Array<String>) {
    val directory = args.getOrNull(1).takeIf { args.firstOrNull() == "--directory" } ?: "/tmp"

    // Initialize and start the HTTP server on port 4221, using the specified or default directory.
    val server = HttpServer(port = 4221, directory = directory)
    server.start()
}
