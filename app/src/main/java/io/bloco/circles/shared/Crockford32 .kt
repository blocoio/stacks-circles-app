package io.bloco.circles.shared

import kotlin.experimental.or

const val ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
val base32Lookup = intArrayOf(
    0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
    0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
    0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
    0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
    0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
    0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
    0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
    0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
    0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
    0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
)

fun ByteArray.encodeCrockford32(): String {
    var i = 0
    var index = 0
    var digit: Int
    var currByte: Int
    var nextByte: Int
    val base32 = StringBuffer((size + 7) * 8 / 5)

    while (i < size) {
        currByte = if (this[i] >= 0) this[i].toInt() else this[i] + 256

        // Is the current digit going to span a byte boundary?
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
        base32.append(ALPHABET[digit])
    }

    return base32.toString()
}

fun String.decodeCockford32(): ByteArray {
    var i = 0
    var index = 0
    var lookup: Int
    var offset = 0
    var digit: Int
    val bytes = ByteArray(length * 5 / 8)

    while (i < length) {
        lookup = this[i] - '0'

        // Skip chars outside the lookup table
        if (lookup < 0 || lookup >= base32Lookup.size) {
            i++
            continue
        }

        digit = base32Lookup[lookup]

        // If this digit is not in the table, ignore it
        if (digit == 0xFF) {
            i++
            continue
        }

        if (index <= 3) {
            index = (index + 5) % 8
            if (index == 0) {
                bytes[offset] = bytes[offset] or digit.toByte()
                offset++
                if (offset >= bytes.size) break
            } else {
                bytes[offset] = bytes[offset] or (digit shl 8 - index).toByte()
            }
        } else {
            index = (index + 5) % 8
            bytes[offset] = bytes[offset] or digit.ushr(index).toByte()
            offset++

            if (offset >= bytes.size) break
            bytes[offset] = bytes[offset] or (digit shl 8 - index).toByte()
        }
        i++
    }
    return bytes
}