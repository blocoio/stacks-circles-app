package io.bloco.circles.shared

fun String.encodeCrockford32() : String {
    return toByteArray(Charsets.UTF_8).encodeCrockford32()
}

fun ByteArray.encodeCrockford32(): String {
    var i = 0
    var index = 0
    var digit: Int
    var currByte: Int
    var nextByte: Int
    val base32 = StringBuffer((size + 7) * 8 / 5)
    val alphabet = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"

    while (i < size) {
        currByte = if (this[i] >= 0) this[i].toInt() else this[i] + 256

        if (index > 3) {
            nextByte = if (i + 1 < size) {
                if (this[i + 1] >= 0) this[i + 1].toInt() else this[i + 1] + 256
            } else {
                0
            }

            digit = currByte and (0xFF shr index)
            index = (index + 5) % 8
            digit = digit shl index
            digit = digit or (nextByte shr 8 - index)
            i++
        } else {
            digit = currByte shr 8 - (index + 5) and 0x1F
            index = (index + 5) % 8
            if (index == 0)
                i++
        }
        base32.append(alphabet[digit])
    }

    return base32.toString()
}

fun String.decodeCrockford32(): String {
    return String(decodeCrockford32ToByteArray(), Charsets.UTF_8)
}

fun String.decodeCrockford32ToByteArray(): ByteArray {
    return toByteArray(Charsets.UTF_8).decodeCrockford32ToByteArray()
}

fun ByteArray.decodeCrockford32ToByteArray(): ByteArray {
    if (size < 0) {
        return this
    }

    // control constants
    val MASK_8BITS = 0xff
    val BITS_PER_ENCODED_BYTE = 5
    val BYTES_PER_ENCODED_BLOCK = 8
    val PAD = '='.toByte()

    // Buffer
    val buffer = ByteArray((size + 7) * 8 / 5)
    var modulus = 0
    var bitWorkArea = 0L
    var pos = 0

    for ((inPos, _) in indices.withIndex()) {
        val b = this[inPos]
        if (b == PAD) {
            // We're done.
            break
        } else {
            if (b.isInCrockfordAlphabet()) {
                val result = b.decodeToCrockford32().toInt()
                modulus = (modulus + 1) % BYTES_PER_ENCODED_BLOCK
                bitWorkArea =
                    (bitWorkArea shl BITS_PER_ENCODED_BYTE) + result // collect decoded bytes
                if (modulus == 0) { // we can output the 5 bytes
                    buffer[pos++] = (bitWorkArea shr 32 and MASK_8BITS.toLong()).toByte()
                    buffer[pos++] = (bitWorkArea shr 24 and MASK_8BITS.toLong()).toByte()
                    buffer[pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                    buffer[pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                    buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                }
            }
        }
    }

    // This approach makes the '=' padding characters completely optional.
    if (modulus >= 2) { // if modulus < 2, nothing to do
        when (modulus) {
            2 -> buffer[pos++] = (bitWorkArea shr 2 and MASK_8BITS.toLong()).toByte()
            3 -> buffer[pos++] = (bitWorkArea shr 7 and MASK_8BITS.toLong()).toByte()
            4 -> {
                bitWorkArea = bitWorkArea shr 4 // drop 4 bits
                buffer[pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
            }
            5 -> {
                bitWorkArea = bitWorkArea shr 1
                buffer[pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
            }
            6 -> {
                bitWorkArea = bitWorkArea shr 6
                buffer[pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
            }
            7 -> {
                bitWorkArea = bitWorkArea shr 3
                buffer[pos++] = (bitWorkArea shr 24 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
            }
        }
    }

    val result = ByteArray(pos)
    System.arraycopy(buffer, 0, result, 0, pos)

    return result
}

fun Byte.isInCrockfordAlphabet(): Boolean {
    return decodeToCrockford32().toInt() != -1
}

fun Byte.decodeToCrockford32(): Byte {
    return when (toChar()) {
        '0', 'O', 'o' -> 0
        '1', 'I', 'i', 'L', 'l' -> 1
        '2' -> 2
        '3' -> 3
        '4' -> 4
        '5' -> 5
        '6' -> 6
        '7' -> 7
        '8' -> 8
        '9' -> 9
        'A', 'a' -> 10
        'B', 'b' -> 11
        'C', 'c' -> 12
        'D', 'd' -> 13
        'E', 'e' -> 14
        'F', 'f' -> 15
        'G', 'g' -> 16
        'H', 'h' -> 17
        'J', 'j' -> 18
        'K', 'k' -> 19
        'M', 'm' -> 20
        'N', 'n' -> 21
        'P', 'p' -> 22
        'Q', 'q' -> 23
        'R', 'r' -> 24
        'S', 's' -> 25
        'T', 't' -> 26
        'U', 'u', 'V', 'v' -> 27
        'W', 'w' -> 28
        'X', 'x' -> 29
        'Y', 'y' -> 30
        'Z', 'z' -> 31
        else -> -1
    }
}
