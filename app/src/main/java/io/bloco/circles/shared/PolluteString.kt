package io.bloco.circles.shared


class PolluteString {

    companion object {

        const val INVALID_STRING = "INV4LID_STR1NG"

        fun pollute(length: Int): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return INVALID_STRING + (1..length-INVALID_STRING.length)
                .map { allowedChars.random() }
                .joinToString("")
        }

        fun isPolluted(string: String): Boolean =
             string.take(INVALID_STRING.length) == INVALID_STRING

    }
}
