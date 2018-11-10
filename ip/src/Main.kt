import java.io.IOException
import java.lang.System.exit
import java.util.*
import kotlin.collections.ArrayList

fun main(args: Array<String>) {
    Main().go()
}


class Main {

    @Throws(IOException::class)
    fun go() {
        val scanner = Scanner(System.`in`)

        val networkClass = scanner.ask("Enter network class (A, B, C):")
        val bitsForNetwork = when (networkClass) {
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
        val totalBits = bitsForNetwork + bitsForSubnetworks

        val freeBits = 32 - (totalBits)
        val maxPossibleHosts = 1.shl(freeBits) - 2

        if (maxPossibleHosts < maxHostsNumber || totalBits >= 32) {
            println("Impossible to create such network")
            exit(1)
        }

        val maskBitSet = BitSet(32)
        maskBitSet.set(0, totalBits)
        println("Mask:\n" +
                "${maskBitSet.toBinStr()}\n" +
                "${maskBitSet.toDecStr()}\n")

        val subnetworkAddresses = ArrayList<BitSet>()
        (0 until (1L.shl(bitsForSubnetworks))).mapTo(subnetworkAddresses) {
            BitSet().from(it, bitsForNetwork + bitsForSubnetworks)
        }

        for (i in 0 until subnetworkAddresses.size - 1) {
            val octets1 = subnetworkAddresses[i].getOctets()
            val octets2 = subnetworkAddresses[i + 1].getOctets()

            if (octets2[3] == 0L) {
                println("${octets1[0]}.${octets1[1]}.${octets1[2]}.${octets1[3] + 1} - " +
                        "${octets2[0]}.${octets2[1]}.${octets2[2]}.254")
            } else {
                println("${octets1[0]}.${octets1[1]}.${octets1[2]}.${octets1[3] + 1} - " +
                        "${octets2[0]}.${octets2[1]}.${octets2[2]}.${octets2[3] - 2}")
            }
        }
    }
}

