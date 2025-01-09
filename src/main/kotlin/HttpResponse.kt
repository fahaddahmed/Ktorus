import java.io.OutputStream

/**
 * Represents an HTTP response.
 */
data class HttpResponse(
    val status: HttpStatus,
    val headers: Map<String, String> = emptyMap(),
    val body: ByteArray = ByteArray(0)
) {
    /**
     * Writes the HTTP response to an OutputStream.
     * Constructs the response format by writing the status line, headers, 
     * and body to the output stream sequentially.
     */
    fun writeTo(output: OutputStream) {
        // Write status line
        val statusLine = "HTTP/1.1 ${status.code} ${status.message}\r\n"
        output.write(statusLine.toByteArray())

        // Write headers
        headers.forEach { (key, value) ->
            output.write("$key: $value\r\n".toByteArray())
        }

        // End headers section with an extra newline, then write the body
        output.write("\r\n".toByteArray())
        output.write(body)
    }

    companion object {
        /**
         * Factory method for a 200 OK response with optional body.
         * Adds Content-Type and Content-Length headers as required.
         */
        fun ok(body: String = "") = HttpResponse(
            status = HttpStatus.OK,
            headers = mapOf(
                "Content-Type" to "text/plain",
                "Content-Length" to body.toByteArray().size.toString()
            ),
            body = body.toByteArray()
        )

        /**
         * Factory method for a 201 Created response.
         */
        fun created() = HttpResponse(status = HttpStatus.CREATED)

        /**
         * Factory method for a 400 Bad Request response.
         */
        fun badRequest() = HttpResponse(status = HttpStatus.BAD_REQUEST)

        /**
         * Factory method for a 404 Not Found response.
         */
        fun notFound() = HttpResponse(status = HttpStatus.NOT_FOUND)

        /**
         * Factory method for a 405 Method Not Allowed response.
         */
        fun methodNotAllowed() = HttpResponse(status = HttpStatus.METHOD_NOT_ALLOWED)
    }
}
