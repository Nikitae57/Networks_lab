import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.*
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.concurrent.thread

class Main {

    private lateinit var socket: SSLSocket
    private lateinit var socketReader: Scanner
    private lateinit var socketWriter: PrintWriter
    private lateinit var consoleReader: Scanner

    private val yandexPop = "pop.yandex.ru"
    private val mailPop = "pop.mail.ru"

    fun start() {
        consoleReader = Scanner(System.`in`)

        println("Yandex or mail.ru?")
        var popServer = mailPop
        if (consoleReader.nextLine().toLowerCase().contains("yandex")) {
            popServer = yandexPop
        }

        val sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        socket = sslSocketFactory.createSocket(popServer, 995) as SSLSocket
        socket.startHandshake()

        socketReader = Scanner(socket.inputStream)
        socketWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.outputStream)))

        thread {
            var msg = socketReader.nextLine()
            var lowMsg = msg.toLowerCase()
            while (true) {
                println(msg)

                if (lowMsg.matches(Regex("(.*signing off.*)|(.*err.*auth.*)|(.*idle for too long.*)"))) {
                    System.exit(0)
                }

                if (socketReader.hasNext()) {
                    msg = socketReader.nextLine()
                    lowMsg = msg.toLowerCase()
                }
            }
        }

        while (true) {
            if (consoleReader.hasNext()) {
                socketWriter.println(consoleReader.nextLine())
                socketWriter.flush()

                if (socketWriter.checkError()) {
                    println("Error occurred")
                    socketWriter.close()
                    socketReader.close()

                    System.exit(1)
                }
            }
        }
    }

    companion object Object {
        @JvmStatic
        fun main(args: Array<String>) {
            val m = Main()
            m.start()
        }
    }

    /*
    pop.yandex.ru
    pop.mail.ru

    USER <SP> <name> <CRLF>
        name:
            mail.ru - username with @ and domain
            yandex.ru -

    PASS <SP> <passw> <CRLF>

    QUIT <CRLF>

    STAT <CRLF>
      +OK <n> <s>
          n - amount of emails, s - their size

    LIST [<SP> <mes>] <CRLF>
      -ERR no such message

      +OK scan listing follows
      <n1> <s1>
      <n2> <s2>...
          n[i] - number of message, s[i] - size of message (bytes)

    RETR <SP> <mes> <CRLF>
      +OK message follows <email text>
      -ERR no such message

    DELE <SP> <mes> <CRLF>
      +OK message deleted
      -ERR no such message

    NOOP <CRLF>
      Checks server status

    RSET <CRLF>
      Removes delete flag from every email

    TOP <SP> <mes> <SP> <n> <CRLF>
        Returns header and first n lines of email

    UIDL [<SP> <mes>] <CRLF>
        Returns uid of email
     */
}