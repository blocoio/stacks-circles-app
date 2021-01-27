package org.stacks.app.domain

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.stacks.app.data.EncryptedPreferencesIdentityRepository
import org.stacks.app.data.EncryptedPreferencesSecretKeyRepository
import org.stacks.app.data.network.models.WalletConfig
import org.stacks.app.data.network.services.GaiaService

class LoginTest {

    val seed_phrase = "suggest pitch guide owner skin follow strategy point piano giraffe hole anchor"

    val mockGaiaService = mock<GaiaService>()
    val mockIdentitiesRepo = mock<EncryptedPreferencesIdentityRepository>()
    val mockSecretKeyRepo = mock<EncryptedPreferencesSecretKeyRepository>()

    @Test
    fun decrypt() { runBlocking {
        val login = Login(mockGaiaService, mockIdentitiesRepo, mockSecretKeyRepo)

        whenever(mockGaiaService.wallet(any<String>())).thenReturn(flowOf(WalletConfig(
"a4317a79a0669274c74b749df9a3636eb3485262312ce9e10b490d16fc9039559a9f36fa9cd4be5fd4be043566c67982d46b731b082545b9be2f0d8ed018218284ef55bf3e7dca38ac5b1131844526ceea8d345ca1f1ac7383a02ddacc094b5002f3dd765135222db09f26d35c7f930c1ecd5f7b56857b48589f76ccad2ac47ab195bdbc7a693dabe666c12d181e3b24d5f65a714bd367ecab11a07b1971ebe42add2fad1062763c05742464eec19c416ea1c52795d4daf9f34f012eed6ac357a0caab4a4ea61196325ac3789e18bb5bc9ddbd9e509008302ba6216d131ce8e4b9bbd0cedc2bfc87d29a21e3feb6025bc70669682a6d8e7a9f28156a6dae6a080c7353cb00da310a3acb486345edecd2",
            "0364dcaff5ae8c970c6cf05f5e28f02c6b49c6fa4127133bb7b0b21ca009a47006",
            "16a5174b005a271597df9c19f3c09206",
            "9cc84236cda88a98d32e48f2787e88f80410662b78a7ce208d386ecb9f41cbbe",
            true
        )))

        login.login(seed_phrase).first()
    }}
}