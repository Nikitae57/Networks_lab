import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.*
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.concurrent.thread

class Main {
    lateinit var socket: SSLSocket
    lateinit var socketReader: Scanner
    lateinit var socketWriter: PrintWriter
    lateinit var consoleReader: Scanner

    fun start() {
        val sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        socket = sslSocketFactory.createSocket("pop.mail.ru", 995) as SSLSocket
        socket.startHandshake()
        socketReader = Scanner(socket.inputStream)
        socketWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.outputStream)))

        consoleReader = Scanner(System.`in`)

        thread {
            var msg = socketReader.nextLine()
            while (true) {
                println(msg)

                if (msg.contains("signing off") ||
                        msg.contains("ERR Authentication failed")) {

                    System.exit(0)
                }

                if (socketReader.hasNext()) {
                    msg = socketReader.nextLine()
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
    USER <SP> <name> <CRLF>

    PASS <SP> <passw> <CRLF>

    QUIT <CRLF>

    STAT <CRLF>
      +OK <n> <s>
          n - amount of emails, s - their size

    IST [<SP> <mes>] <CRLF>
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