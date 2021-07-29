package io.bloco.circles.domain

import com.nhaarman.mockitokotlin2.*
import io.bloco.circles.data.IdentityModel
import io.bloco.circles.data.interfaces.IdentityRepository
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.blockstack.android.sdk.extensions.toBtcAddress
import org.blockstack.android.sdk.extensions.toStxAddress
import org.json.JSONObject
import org.junit.Test
import org.kethereum.model.ECKeyPair
import org.kethereum.model.PrivateKey
import org.kethereum.model.PublicKey


class SignUpTest {

    private val stxRegisterAddress = "SPY80HCNDPWQ8DWH6SVCDBE7E3GCSE97ZGNKE7SD"
    private val firstIdentityKey = "16WuCzWVEyDphm1VAUuZXf3MzBYars5eCh"
    private val username = "pravicaspamisrealsorry"

    private val mockPrivateKey = PrivateKey(ByteArray(0))
    private val mockPublicKey = PublicKey(ByteArray(0))
    private val mockBtcKeys = ECKeyPair(mockPrivateKey, mockPublicKey)
    private val mockStxKeys = ECKeyPair(mockPrivateKey, mockPublicKey)

    private val mockIdentityKeys = mock<IdentityKeys>()
    private val mockIdentityRepository = mock<IdentityRepository>()
    private val mockGenerateIdentity = mock<GenerateIdentity>()
    private val mockUploadProfile = mock<UploadProfile>()
    private val mockRegistrarProfile = mock<RegistrarProfile>()
    private val mockUploadWallet = mock<UploadWallet>()


    @Test
    fun newAccountWithUsernameTest() : Unit = runBlocking {
        // Arrange
        val signUp = build()
        val identity = IdentityModel(JSONObject())
        mockkStatic(ECKeyPair::toBtcAddress)

        whenever(mockIdentityKeys.new()).thenReturn(mockBtcKeys)
        whenever(mockIdentityKeys.forStxAddresses()).thenReturn(mockStxKeys)
        whenever(mockGenerateIdentity.generate(stxRegisterAddress, username)).thenReturn(identity)

        every {
            ECKeyPair(mockPrivateKey, mockPublicKey).toBtcAddress()
        } returns firstIdentityKey

        every {
            ECKeyPair(mockPrivateKey, mockPublicKey).toStxAddress(true)
        } returns stxRegisterAddress


        // Act
        val result = signUp.newAccount(username)

        // Assert
        assert(result.isSuccess)
        verify(mockRegistrarProfile, times(1)).register(username, firstIdentityKey, stxRegisterAddress)
        verify(mockUploadProfile, times(1)).upload(any(), any())
        verify(mockUploadWallet, times(1)).upload(identity)
        verify(mockIdentityRepository, times(1)).set(any())
    }

    @Test
    fun newAccountWithoutUsernameTest() : Unit = runBlocking {
        // Arrange
        val signUp = build()
        val identity = IdentityModel(JSONObject())
        mockkStatic(ECKeyPair::toBtcAddress)

        whenever(mockIdentityKeys.new()).thenReturn(mockBtcKeys)
        whenever(mockIdentityKeys.forStxAddresses()).thenReturn(mockStxKeys)
        whenever(mockGenerateIdentity.generate(stxRegisterAddress, null)).thenReturn(identity)

        every {
            ECKeyPair(mockPrivateKey, mockPublicKey).toBtcAddress()
        } returns firstIdentityKey

        every {
            ECKeyPair(mockPrivateKey, mockPublicKey).toStxAddress(true)
        } returns stxRegisterAddress


        // Act
        val result = signUp.newAccount(null)

        // Assert
        assert(result.isSuccess)
        verify(mockRegistrarProfile, times(0)).register(username, firstIdentityKey, stxRegisterAddress)
        verify(mockUploadProfile, times(1)).upload(any(), any())
        verify(mockUploadWallet, times(1)).upload(identity)
        verify(mockIdentityRepository, times(1)).set(any())
    }

    fun build() = SignUp(
        mockIdentityKeys,
        mockIdentityRepository,
        mockGenerateIdentity,
        mockUploadProfile,
        mockRegistrarProfile,
        mockUploadWallet,
    )

}