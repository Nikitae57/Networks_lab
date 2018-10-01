import java.io.*
import java.net.Socket
import java.util.*

object Starter {
    @JvmStatic
    fun main(args: Array<String>) {
        val m = MyMailer()
        try {
            m.sendEmail()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}

fun String.toBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())

fun BufferedWriter.writeln(msg: String) {
    this.write(msg)
    this.newLine()
    this.flush()
}

val Socket.bufferedReader: BufferedReader
    get() = BufferedReader(InputStreamReader(this.getInputStream()))

val Socket.bufferedWriter: BufferedWriter
    get() = BufferedWriter(OutputStreamWriter(this.getOutputStream()))
