import java.io.*
import java.util.*
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.concurrent.thread

/*
    smtp.mail.ru:465

    "mail from: $fromWho"
    "rcpt to: $toWho"
    "data"
    "From: $fromWho"
    "To: $toWho"
    "Subject: $subject"
    $message
    .
    quit
 */

class MyMailer {

    private lateinit var socket: SSLSocket
    private lateinit var socketReader: BufferedReader
    private lateinit var socketWriter: PrintWriter
    private lateinit var consoleReader: Scanner

    fun start() {

        consoleReader = Scanner(System.`in`)

        print("SMTP server address: ")
        val smtpServerAddress = consoleReader.nextLine()

        print("SMTP server port: ")
        val smtpServerPort = consoleReader.nextInt()
        consoleReader.nextLine()

        print("Login: ")
        val login = consoleReader.nextLine()

        val console = System.console()
        val password = String(console.readPassword("Password: "))

        val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
        socket = factory.createSocket(smtpServerAddress, smtpServerPort) as SSLSocket
        socket.startHandshake()

        socketReader = BufferedReader(InputStreamReader(socket.inputStream))
        socketWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.outputStream)))

        thread {
            var serverMsg = socketReader.readLine()

            while (serverMsg != null) {
                println(serverMsg)

                serverMsg = socketReader.readLine()
            }
        }

        sendMessage("HELO login")

        sendMessage("AUTH LOGIN")
        sendMessage(login.toBase64())
        sendMessage(password.toBase64())

        while (true) {
            if (consoleReader.hasNext()) {
                sendMessage(consoleReader.nextLine())
            }
        }
    }

    private fun sendMessage(msg: String) {
        socketWriter.println(msg)
        socketWriter.flush()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val mailer = MyMailer()
            mailer.start()
        }
    }

}

private fun String.toBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())

