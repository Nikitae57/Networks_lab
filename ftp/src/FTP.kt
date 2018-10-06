import java.io.*
import java.net.Socket
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.concurrent.thread


class FTP {

    private lateinit var controlSocket: Socket
    private lateinit var controlSocketReader: Scanner
    private lateinit var controlSocketWriter: PrintWriter

    private lateinit var dataSocket: Socket
    private lateinit var dataSocketReader: BufferedReader
    private lateinit var dataSocketWriter: DataOutputStream

    private lateinit var consoleReader: Scanner
    private lateinit var hostIp: String

    private var retred = false
    private var stored = false
    private var file: File? = null

    fun start() {
        consoleReader = Scanner(System.`in`)
        hostIp = consoleReader.ask("Host ip: ")

        controlSocket = Socket(hostIp, 21)
        controlSocketWriter = PrintWriter(controlSocket.getOutputStream().bufferedWriter())
        controlSocketReader = Scanner(controlSocket.getInputStream())

        thread {
            var serverInfoMsg: String
            while (true) {
                if (controlSocketReader.hasNext()) {
                    serverInfoMsg = controlSocketReader.nextLine()
                    println(serverInfoMsg)

                    when (serverInfoMsg.split(' ')[0]) {
                        "227" -> togglePassiveMode(serverInfoMsg)
                        //"226" -> if (!dataSocket.isClosed) getData()
                        "150" -> {
                            if (stored) completeFileUploading()
                            else if (!dataSocket.isClosed) getData()

                        }}
                }
            }
        }

        controlSocketWriter.send("USER anonymous")
        controlSocketWriter.send("PASS 1")

        loop@ while (true) {
            if (consoleReader.hasNext()) {
                val commandLine = consoleReader.nextLine()
                val splittedCommandLine = commandLine.split(' ')
                val command = splittedCommandLine[0].toLowerCase()

                if (command == "retr") {
                    file = File(splittedCommandLine[1])

                    if (file!!.exists()) {
                        val answer = consoleReader.ask("File already exists.\n" +
                                "1) overwrite\n" +
                                "2) do not overwrite\n" +
                                "0) abort"
                        )

                        when (answer) {
                            "1" -> file?.delete()
                            "2" -> file?.renameIfExists()
                            "0" -> continue@loop
                        }
                    }
                    file?.createNewFile()
                    retred = true
                }

                if (command == "stor") {
                    try {
                        file = File(splittedCommandLine[1])
                    } catch (ex: FileNotFoundException) {
                        println("Can't find file")
                        stored = false
                        file = null
                    }

                    stored = true
                }

                val togglePasv = retred
                        || command == "stor"
                        || command == "list"
                controlSocketWriter.send(commandLine, togglePasv)
            }
        }
    }

    private fun togglePassiveMode(pasvRespond: String) {
        val pattern = Pattern.compile("(\\d{1,3},){5}\\d{1,3}")
        val matcher = pattern.matcher(pasvRespond)

        if (!matcher.find()) {
            println("Cant toggle passive mode")
            return
        }

        val ints = matcher.group().split(',').map { it.toInt() }
        val dataSocketAddress = "${ints[0]}.${ints[1]}.${ints[2]}.${ints[3]}"
        val dataSocketPort = (ints[4] * 256) + ints[5]

        dataSocket = Socket(dataSocketAddress, dataSocketPort)
        dataSocketReader = BufferedReader(InputStreamReader(dataSocket.getInputStream()))
        dataSocketWriter = DataOutputStream(dataSocket.getOutputStream())
    }

    private fun getData() {
        if (retred) {
            while (!dataSocket.isClosed) {
                file?.appendBytes(dataSocket.getInputStream().readAllBytes())
                if (dataSocket.getInputStream().available() == 0) {
                    break
                }
            }

            file = null
            retred = false

            return
        }

        val fileContent = dataSocketReader.lines().collect(Collectors.joining("\n"))
        println("___DATA___")
        println(fileContent)
    }

    private fun completeFileUploading() {
        dataSocketWriter.write(file?.inputStream()?.readAllBytes())
        stored = false
        dataSocket.close()
    }

    private fun PrintWriter.send(str: String, passive: Boolean = false) {
        if (passive)
            println("PASV\r")

        println(str + '\r')
        flush()
    }

    private fun Scanner.ask(prompt: String): String {
        println(prompt)
        return nextLine()
    }

    private fun File.renameIfExists() {
        var newName: File? = null
        for (i in 0..1_000_000) {
            newName = File("$nameWithoutExtension {$i}.$extension")

            if (!newName.exists()) {
                renameTo(newName)
                return
            }
        }

        throw FileAlreadyExistsException(newName!!)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FTP().start()
        }
    }
}