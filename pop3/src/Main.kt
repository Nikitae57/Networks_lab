import java.io.*
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
                msg = socketReader.nextLine()
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
}