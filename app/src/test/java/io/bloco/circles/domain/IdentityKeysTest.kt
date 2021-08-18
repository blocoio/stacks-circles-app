package io.bloco.circles.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.bloco.circles.data.EncryptedPreferencesIdentityRepository
import io.bloco.circles.data.EncryptedPreferencesSecretKeyRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.blockstack.android.sdk.extensions.toBtcAddress
import org.blockstack.android.sdk.extensions.toHexPublicKey64
import org.blockstack.android.sdk.extensions.toStxAddress
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IdentityKeysTest {

    private val seedPhrase = "spray forum chronic innocent exercise market ice pact foster twice glory account"
    private val stxRegisterAddress = "SP0Y80HCNDPWQ8DWH6SVCDBE7E3GCSE97ZGNKE7SD"
    private val firstIdentityKey = "16WuCzWVEyDphm1VAUuZXf3MzBYars5eCh"
    private val firstIdentityHexKey = "0395b9d0cd584b9d7f00058184ac4dcdae18ddf6ef29a6b4deef413b162c86416e"

    private val mockIdentityRepository = mock<EncryptedPreferencesIdentityRepository>()
    private val mockSecretKeyRepository = mock<EncryptedPreferencesSecretKeyRepository>()

    @Before
    fun setUp() {
        whenever(mockIdentityRepository.observe()).thenReturn(flowOf(emptyList()))
        whenever(mockSecretKeyRepository.observe()).thenReturn(flowOf(seedPhrase))
    }


    @Test
    fun newTest() = runBlocking {
        // Arrange
        val identityKeys = build()

        // Act
        val result = identityKeys.new()

        // Assert
        assertEquals(firstIdentityKey,result.toBtcAddress())
        assertEquals(firstIdentityHexKey, result.toHexPublicKey64())
    }

    @Test
    fun forStxAddressesTest() = runBlocking {
        // Arrange
        val identityKeys = build()

        // Act
        val result = identityKeys.new()

        // Assert
        assertEquals(stxRegisterAddress, result.toStxAddress(true))
    }

    private fun build() = IdentityKeys(
        mockIdentityRepository,
        mockSecretKeyRepository
    )
}