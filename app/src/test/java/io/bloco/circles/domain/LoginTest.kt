package io.bloco.circles.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.bloco.circles.data.EncryptedPreferencesIdentityRepository
import io.bloco.circles.data.EncryptedPreferencesSecretKeyRepository
import io.bloco.circles.data.network.models.WalletConfig
import io.bloco.circles.data.network.services.GaiaService
import junit.framework.Assert
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class LoginTest {

    private val walletConfigAddress = "14tCpJ6hiY6YRZHwtkDPEPf1TWTJjFa7UP"
    private val seedPhrase = "spray forum chronic innocent exercise market ice pact foster twice glory account"

    private val walletIv = "f9574bf0f6cb57dd6b7dc96688a21390"
    private val walletEphemeralPK = "03ac944e5351ea85ccc589fd3b2f6ca469ee1500ed177562b6f13497040c94527a"
    private val walletCipherText = "dfc2d35f83a7a06ae2c5c1e5c3f9826a7f18b4d205b37a5fddb856eb7873fd7320d3ee248bd3b183376c7e402c52009459d7ababd76f44ab168e54fd9b472eca7b85323062596a12a60e4489ebe15c51bee5da29488cfa4216fea70d5b082ba3c3abeeba250e5c871b2ade7374e3a0455719a6fa246d67aac7ec6035cb8eed7988e3463ccc89b8b6f42493e377c718cbe8fe1b6536f608d75c0491ecd820afad4dccf14fe53fcd6e305453a08fe7a88474ab02c2ae5f9f78db06b388524e5cf1bb5fb2eaa68272904919996037a8dc8938b487b31f02943759a54041e16b8b5fa970eb50df4904fdbf28229e596b341899f7ef479acc431dd7b3b4309c7ce55ba32444cb2aebe9ebddfaae3bb49462eb3b9cb284d776e88cfcad027926bf16392fe31c93ce205984c1c350ae38682082d12ade1295eff60f1818c0fc9ef6b65e"
    private val walletMac = "6f3f2923490ff613f1db40f1a389f14745a046baa0451fe530ba44c631269158"
    private val walletWasString = true

    private val mockGaiaService = mock<GaiaService>()
    private val mockIdentitiesRepo = mock<EncryptedPreferencesIdentityRepository>()
    private val mockSecretKeyRepo = mock<EncryptedPreferencesSecretKeyRepository>()

    @Test
    fun decrypt() { runBlocking {
         val login = Login(mockGaiaService, mockIdentitiesRepo, mockSecretKeyRepo)

        val wallet = WalletConfig(
            walletCipherText,
            walletEphemeralPK,
            walletIv,
            walletMac,
            walletWasString
        )

        whenever(mockGaiaService.wallet(walletConfigAddress)).thenReturn(wallet)
        whenever(mockSecretKeyRepo.observe()).thenReturn(flowOf(seedPhrase))
        whenever(mockIdentitiesRepo.observe()).thenReturn(flowOf(emptyList()))

       val result = login.login(seedPhrase)
        Assert.assertTrue(result.isSuccess)
    }}

}

