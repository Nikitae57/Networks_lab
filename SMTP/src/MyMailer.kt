import java.io.*
import java.net.Socket
import java.util.*

class MyMailer {
    private lateinit var socket: Socket
    private lateinit var bufferedReader: BufferedReader
    private lateinit var bufferedWriter: BufferedWriter

    @Throws(IOException::class)
    fun sendEmail() {

        val scanner = Scanner(System.`in`)

        print("SMTP server address: ")
        val smtpServerAddress = scanner.nextLine()

        print("Login: ")
        val login = scanner.nextLine()

        print("Password: ")
        val password = scanner.nextLine()

        print("From: ")
        val fromWho = scanner.nextLine()

        print("To: ")
        val toWho = scanner.nextLine()

        print("Subject: ")
        val subject = scanner.nextLine()

        print("Message: ")
        val msg = scanner.nextLine()

        socket = Socket(smtpServerAddress, 25)
        socket.soTimeout = 30000

        bufferedReader = socket.bufferedReader
        bufferedWriter = socket.bufferedWriter

        readMessage()
        sendMessage("HELO login")

        sendMessage("AUTH LOGIN")
        sendMessage(login.toBase64())
        sendMessage(password.toBase64())

        sendMessage("mail from: $fromWho")
        sendMessage("rcpt to: $toWho")

        sendMessage("data")
        sendMessage("From: $fromWho", false)
        sendMessage("To: $toWho", false)
        sendMessage("Subject: $subject", false)
        sendMessage(msg, false)
        sendMessage(".", false)
        sendMessage("quit")
    }

    @Throws(IOException::class)
    internal fun sendMessage(message: String, readBack: Boolean = true) {
        bufferedWriter.writeln(message)
        println(message)

        if (readBack) 
            readMessage()
    }

    @Throws(IOException::class)
    internal fun readMessage() {
        println(bufferedReader.readLine() + '\n')
    }
}

