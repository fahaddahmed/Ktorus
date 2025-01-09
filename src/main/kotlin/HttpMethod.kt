/**
 * Enum representing HTTP methods.
 */
enum class HttpMethod {
    GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH;

    companion object {
        fun from(value: String): HttpMethod {
            return values().find { it.name == value } ?: throw IllegalArgumentException("Unsupported HTTP method: $value")
        }
    }
}
