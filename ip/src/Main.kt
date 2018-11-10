import java.io.IOException
import java.lang.System.exit
import java.util.*


class Main {

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

        if (maxPossibleHosts < maxHostsNumber) {
            println("Impossible to create such network")
            exit(1)
        }

        val maskBitSet = BitSet(32)
        maskBitSet.set(0, totalBits)
        println("Mask:\n" +
                "${maskBitSet.toBinStr()}\n" +
                "${maskBitSet.toDecStr()}\n")

        val endIndex = bitsForNetwork + bitsForSubnetworks
        val subnetworkAddrBitset = BitSet()
        subnetworkAddrBitset.set(0, bitsForNetwork)

        for (i in 0 until (1L.shl(bitsForSubnetworks))) {
            var bitCount = 0
            while (1.shl(bitCount) < i) {
                bitCount++
            }

            val bitset = BitSet().from(i, bitsForNetwork + bitsForSubnetworks - bitCount - 1)
            println(bitset.toBinStr())
//            subnetworkAddrBitset.set(bitsForNetwork, endIndex, bitset.get(bitsForNetwork, endIndex))
//            println(subnetworkAddrBitset.toBinStr())
        }
    }
}

