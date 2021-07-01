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
        base32.append(alphabet[digit])
    }

    return base32.toString()
}
