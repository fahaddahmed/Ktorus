import java.io.InputStream

/**
 * Represents an HTTP request.
 */
data class HttpRequest(
    val method: HttpMethod, // HTTP method (GET, POST, etc.)
    val path: String, // Request path (e.g., "/user-agent")
    val headers: Map<String, String>, // Headers as a map for easy lookup
    val body: ByteArray 
) {
    companion object {
        /**
         * Parses an InputStream into an HttpRequest.
         */
        fun parse(input: InputStream): HttpRequest {
            val reader = input.bufferedReader()
            
            // Parse the request line (e.g., "GET /path HTTP/1.1")
            val requestLine = reader.readLine() ?: throw IllegalArgumentException("Empty request")
            val parts = requestLine.split(" ")
            if (parts.size < 2) throw IllegalArgumentException("Invalid request line")
            
            val method = HttpMethod.from(parts[0]) // HTTP method (e.g., GET)
            val path = parts[1]                    // Request path (e.g., /files/filename)
            val headers = mutableMapOf<String, String>()

            // Read headers until an empty line is reached
            var line: String?
            while (reader.readLine().also { line = it } != null && line!!.isNotEmpty()) {
                // Split header into key and value
                val (key, value) = line!!.split(":", limit = 2)
                headers[key.trim()] = value.trim()
            }

            // Determine content length and read body if present
            val contentLength = headers["Content-Length"]?.toIntOrNull() ?: 0
            val body = if (contentLength > 0) {
                // Read exact number of bytes specified by Content-Length
                val bodyChars = CharArray(contentLength)
                reader.read(bodyChars)
                String(bodyChars).toByteArray()  // Convert to byte array for HttpRequest body
            } else {
                ByteArray(0)  // Empty body if no content is specified
            }

            return HttpRequest(method, path, headers, body)
        }
    }
}