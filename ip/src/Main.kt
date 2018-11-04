import java.io.IOException
import java.lang.System.exit
import java.util.*


class Main {

    private val BITS_FOR_ADDRESS = 32

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                Main().go()
            } catch (ioex: IOException) {
                println(ioex.message)
                exit(1)
            }
        }
    }

    @Throws(IOException::class)
    fun go() {
        val scanner = Scanner(System.`in`)

        val networkClass = scanner.ask("Enter network class (A, B, C):")
        val mask = when (networkClass) {
            "a" -> 8
            "b" -> 16
            "c" -> 24
            else -> throw IOException("Invalid network class")
        }

        val subNetworksNumber = scanner.ask("Subnetworks number:").toInt()
        val maxHostsNumber = scanner.ask("Max hosts number:").toInt()

        var bitsForSubnetworks = 0
        while (1.shl(bitsForSubnetworks) < subNetworksNumber) {
            bitsForSubnetworks++
        }

        val totalBits = mask + bitsForSubnetworks
        val freeBits = BITS_FOR_ADDRESS - (totalBits)
        val maxPossibleHosts = 1.shl(freeBits) - 2

        if (maxPossibleHosts < maxHostsNumber) {
            println("Impossible to create such network")
            exit(1)
        }

        val unitOctets = totalBits / 8
        val finalOctetOnes = totalBits % 8
        val finalOctetStr = formatNumber(8, finalOctetOnes)

        val maskStr = ("255.".repeat(unitOctets)
                + "${Integer.parseInt(finalOctetStr, 2)}."
                + "0.".repeat(3 - unitOctets)).trimEnd('.')

        println("Mask: $maskStr")

        val possibleAddress = IntArray(4) {0}
        for (i in 0 until unitOctets) {
            possibleAddress[i] = when (networkClass) {
                "a" -> 127
                "b" -> 191
                "c" -> 192
                else -> throw IOException("Invalid network class")
            }
        }

        // maxPossibleHosts
        for (i in 0 until 10) {
            possibleAddress.increment()
            possibleAddress.print()
        }
    }

    private fun Scanner.ask(prompt: String): String {
        println(prompt)
        return nextLine().toLowerCase()
    }

    fun formatNumber(decimals: Int, numberOfOnes: Int): String {
        val sb = StringBuilder(decimals)
        sb.append("1".repeat(numberOfOnes))
        while (sb.length < 8) {
            sb.append("0")
        }
        return sb.toString()
    }

    fun IntArray.print() {
        val str = StringBuilder()
        this.forEach {
            str.append("$it.")
        }

        println(str.toString().removeSuffix("."))
    }
}

private fun IntArray.increment() {
    for (i in lastIndex downTo 1) {
        if (this[i] < 254) {
            this[i]++
            break
        } else {
            this[i] = 0
        }
    }
}
