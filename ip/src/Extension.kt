import java.io.IOException
import java.util.*


fun BitSet.toBinStr(): String {
    val sb = StringBuilder()
    for (i in 0 until 32) {
        if (get(i)) {
            sb.append(1)
        } else {
            sb.append(0)
        }

        if ((i + 1) % 8 == 0 && i != 31) {
            sb.append('.')
        }
    }

    return sb.toString()
}

fun BitSet.toDecStr(): String {
    val octet1 = getToLong(0, 7)
    val octet2 = getToLong(8, 15)
    val octet3 = getToLong(16, 23)
    val octet4 = getToLong(24, 31)

    return "$octet1.$octet2.$octet3.$octet4"
}

fun BitSet.from(value: Long, shift: Int = 0): BitSet {
    val bits = BitSet()
    val binaryStr = "0".repeat(shift) + Integer.toBinaryString(value.toInt())
    println(binaryStr + " " + binaryStr.length)
    for (i in 0 until binaryStr.length) {
        if (binaryStr[i] == '1') {
            bits.set(i)
        }
    }

    return bits
}

class Extension {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val bs = BitSet().from(1023, 24)
        }
    }
}

fun BitSet.toStr(): String {
    val sb = StringBuilder()
    for (i in 0 until size()) {
        if (get(i)) {
            sb.append(1)
        } else {
            sb.append(0)
        }
    }

    return sb.toString()
}

fun BitSet.getToLong(fromIndex: Int, toIndex: Int): Long {
    val sum = (toIndex downTo fromIndex)
            .filter { get(it) }
            .map { 1.shl(toIndex - it).toLong() }
            .sum()

    return sum
}

fun BitSet.increment(): BitSet {
    val octets = longArrayOf(
            getToLong(0, 7),
            getToLong(8, 15),
            getToLong(16, 23),
            getToLong(24, 31)
    )

    for (i in 3 downTo 1) {
        if (octets[i] < 254) {
            octets[i]++
            break
        } else {
            octets[i] = 0
        }
    }

    set(0, 8, from(octets[0]))
    set(8, 16, from(octets[1]))
    set(16, 24, from(octets[2]))
    set(24, 32, from(octets[3]))

    return this
}

fun BitSet.set(fromIndex: Int, toIndex: Int, bitset: BitSet) {
    var index = 0
    for (i in fromIndex until toIndex) {
        if (bitset.get(index++)) {
            set(i)
        } else {
            set(i, false)
        }
    }
}

fun BitSet.shr(shiftDistance: Int): BitSet {

    for (i in (size() - 1) downTo 1) {
        if (i - shiftDistance < 0) { break }

        if (get(i - shiftDistance)) {
            set(i)
        } else {
            set(i, false)
        }
    }

    for (i in 0 until shiftDistance) {
        set(i, false)
    }

    return this
}

fun Scanner.ask(prompt: String): String {
    println(prompt)
    return nextLine().toLowerCase()
}