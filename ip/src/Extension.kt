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

fun BitSet.from(value: Long, capacity: Int = 0): BitSet {
    val bits = BitSet()
    val binStr = Integer.toBinaryString(value.toInt())
    val binaryStr = "0".repeat(capacity - binStr.length) + binStr
    for (i in 0 until binaryStr.length) {
        if (binaryStr[i] == '1') {
            bits.set(i)
        }
    }

    return bits
}

fun BitSet.getToLong(fromIndex: Int, toIndex: Int): Long {
    val sum = (toIndex downTo fromIndex)
            .filter { get(it) }
            .map { 1.shl(toIndex - it).toLong() }
            .sum()

    return sum
}

fun BitSet.getOctets(): LongArray {
    return longArrayOf(
            getToLong(0, 7),
            getToLong(8, 15),
            getToLong(16, 23),
            getToLong(24, 31)
    )
}

fun Scanner.ask(prompt: String): String {
    println(prompt)
    return nextLine().toLowerCase()
}