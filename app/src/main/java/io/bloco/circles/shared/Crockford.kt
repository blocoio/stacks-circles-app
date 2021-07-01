/**
 *
 * Provides Base32 encoding and decoding as defined by [RFC 4648](http://www.ietf.org/rfc/rfc4648.txt).
 * However it uses a custom alphabet first coined by Douglas Crockford. Only addition to the alphabet is that 'u' and
 * 'U' characters decode as if they were 'V' to improve mistakes by human input.
 *
 *
 *
 *
 * This class operates directly on byte streams, and not character streams.
 *
 *
 * @version $Id: Base32.java 1382498 2012-09-09 13:41:55Z sebb $
 * @see [RFC 4648](http://www.ietf.org/rfc/rfc4648.txt)
 *
 * @see [Douglas Crockford's Base32 Encoding](http://www.crockford.com/wrmg/base32.html)
 *
 */
class CrockfordBase32 {
    /**
     * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
     * `decodeSize = [.BYTES_PER_ENCODED_BLOCK] - 1 + lineSeparator.length;`
     */
    private val decodeSize: Int = BYTES_PER_ENCODED_BLOCK -1

    /**
     * Buffer for streaming.
     */
    private var buffer: ByteArray? = null

    /**
     * Position where next character should be written in the buffer.
     */
    private var pos = 0

    /**
     * Boolean flag to indicate the EOF has been reached. Once EOF has been reached, this object becomes useless,
     * and must be thrown away.
     */
    private var eof = false

    /**
     * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8 reads when decoding.
     * This variable helps track that.
     */
    private var modulus = 0

    /**
     * Place holder for the bytes we're dealing with for our based logic.
     * Bitwise operations store and extract the encoding or decoding from this variable.
     */
    private var bitWorkArea: Long = 0

    /**
     * Ensure that the buffer has room for `size` bytes
     *
     * @param size minimum spare space required
     */
    private fun ensureBufferSize() {
        if (buffer == null) {
            val defaultSize = 8192
            buffer = ByteArray(defaultSize)
            pos = 0
        } else if(buffer != null && buffer!!.size < pos + decodeSize){
            val bufferResizeFactor = 2
            val b = ByteArray(buffer!!.size * bufferResizeFactor)
            System.arraycopy(buffer!!, 0, b, 0, buffer!!.size)
            buffer = b
        }
    }

    /**
     * Decodes a String containing characters in the Base-N alphabet.
     *
     * @param pArray A String containing Base-N character data
     * @return a byte array containing binary data
     */
    fun decode(pArray: String): ByteArray? {
        return decode(pArray.toByteArray(Charsets.UTF_8))
    }

    /**
     * Decodes a byte[] containing characters in the Base-N alphabet.
     *
     * @param pArray A byte array containing Base-N character data
     * @return a byte array containing binary data
     */
    fun decode(pArray: ByteArray?): ByteArray? {
        buffer = null
        pos = 0
        modulus = 0
        eof = false
        if (pArray == null || pArray.size == 0) {
            return pArray
        }
        decode(pArray, 0, pArray.size)
        val result = ByteArray(pos)

        if (buffer != null) {
            val len = if (buffer != null) pos else 0
            System.arraycopy(buffer!!, 0, result, 0, len)
            buffer = null // so hasData() will return false, and this method can return -1
        }
        return result
    }

    /**
     *
     *
     * Decodes all of the provided data, starting at inPos, for inAvail bytes. Should be called at least twice: once
     * with the data to decode, and once with inAvail set to "-1" to alert decoder that EOF has been reached. The "-1"
     * call is not necessary when decoding, but it doesn't hurt, either.
     *
     *
     *
     * Ignores all non-Base32 characters. This is how chunked (e.g. 76 character) data is handled, since CR and LF are
     * silently ignored, but has implications for other bytes, too. This method subscribes to the garbage-in,
     * garbage-out philosophy: it will not check the provided data for validity.
     *
     *
     * @param in      byte[] array of ascii data to Base32 decode.
     * @param inPos   Position to start reading data from.
     * @param inAvail Amount of bytes available from input for encoding.
     *
     *
     * Output is written to [.buffer] as 8-bit octets, using [.pos] as the buffer position
     */
    fun decode(
        input: ByteArray,
        inPosition: Int,
        inAvail: Int
    ) { // package protected for access from I/O streams
        var inPos = inPosition
        if (eof) {
            return
        }
        if (inAvail < 0) {
            eof = true
        }
        for (i in 0 until inAvail) {
            val b = input[inPos++]
            if (b == PAD) {
                // We're done.
                eof = true
                break
            } else {
                ensureBufferSize()
                if (isInAlphabet(b)) {
                    val result = decode(b).toInt()
                    modulus = (modulus + 1) % BYTES_PER_ENCODED_BLOCK
                    bitWorkArea =
                        (bitWorkArea shl BITS_PER_ENCODED_BYTE) + result // collect decoded bytes
                    if (modulus == 0) { // we can output the 5 bytes
                        buffer!![pos++] = (bitWorkArea shr 32 and MASK_8BITS.toLong()).toByte()
                        buffer!![pos++] = (bitWorkArea shr 24 and MASK_8BITS.toLong()).toByte()
                        buffer!![pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                        buffer!![pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                        buffer!![pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                    }
                }
            }
        }

        // This approach makes the '=' padding characters completely optional.
        if (modulus >= 2) { // if modulus < 2, nothing to do
            ensureBufferSize()
            when (modulus) {
                2 -> buffer!![pos++] = (bitWorkArea shr 2 and MASK_8BITS.toLong()).toByte()
                3 -> buffer!![pos++] = (bitWorkArea shr 7 and MASK_8BITS.toLong()).toByte()
                4 -> {
                    bitWorkArea = bitWorkArea shr 4 // drop 4 bits
                    buffer!![pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                }
                5 -> {
                    bitWorkArea = bitWorkArea shr 1
                    buffer!![pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                }
                6 -> {
                    bitWorkArea = bitWorkArea shr 6
                    buffer!![pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                }
                7 -> {
                    bitWorkArea = bitWorkArea shr 3
                    buffer!![pos++] = (bitWorkArea shr 24 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                    buffer!![pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                }
            }
        }
    }

    companion object {
        /**
         * Mask used to extract 8 bits, used in decoding bytes
         */
        private const val MASK_8BITS = 0xff

        /**
         * BASE32 characters are 5 bits in length.
         * They are formed by taking a block of five octets to form a 40-bit string,
         * which is converted into eight BASE32 characters.
         */
        private const val BITS_PER_ENCODED_BYTE = 5
        private const val BYTES_PER_ENCODED_BLOCK = 8
        private const val PAD = '='.toByte()

        private fun decode(octet: Byte): Byte {
            return when (octet.toChar()) {
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

        /**
         * Returns whether or not the `octet` is in the Base32 alphabet.
         *
         * @param octet The value to test
         * @return `true` if the value is defined in the the Base32 alphabet `false` otherwise.
         */
        fun isInAlphabet(octet: Byte): Boolean {
            return decode(octet).toInt() != -1
        }
    }
}