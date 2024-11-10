import java.net.ServerSocket;

fun main() {
    println("Logs")
    var serverSocket = ServerSocket(4221)
    serverSocket.reuseAddress = true
    serverSocket.accept() // Wait for connection from client
    println("accepted new connection")
}
