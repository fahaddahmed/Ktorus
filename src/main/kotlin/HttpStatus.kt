/**
 * Enum representing HTTP status codes and their associated messages.
 * Each constant includes both the numeric code and a standard message.
 */
enum class HttpStatus(val code: Int, val message: String) {

    /** Successful HTTP response */
    OK(200, "OK"),

    /** Response indicating a resource was successfully created */
    CREATED(201, "Created"),

    /** Client error indicating a bad request */
    BAD_REQUEST(400, "Bad Request"),

    /** Client error indicating the requested resource was not found */
    NOT_FOUND(404, "Not Found"),

    /** Client error indicating the HTTP method is not allowed */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed")
}
