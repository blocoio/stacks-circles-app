package org.stacks.app.shared

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class PolluteStringTest {

    private val testString = "{object:value}"

    @Test
    fun pollutedStringValidation() {
        val polluteString = PolluteString.pollute(testString.length)

        assertEquals(testString.length, polluteString.length)
        assertTrue(PolluteString.isPolluted(polluteString))
    }

}