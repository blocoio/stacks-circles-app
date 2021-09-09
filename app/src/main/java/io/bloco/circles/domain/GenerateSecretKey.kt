package io.bloco.circles.domain

import android.content.res.Resources
import org.kethereum.bip39.generateMnemonic
import io.bloco.circles.R
import io.bloco.circles.data.EncryptedPreferencesSecretKeyRepository
import javax.inject.Inject

class GenerateSecretKey
@Inject constructor(
    resources: Resources,
    private val secretKeyRepository: EncryptedPreferencesSecretKeyRepository
) {
    private val words: Array<String> by lazy {
        resources.getStringArray(R.array.bip_words)
    }

    //TODO: add test to check if string is 12 words
    suspend fun generate(): String = generateMnemonic(256, words.toList()).also {
            secretKeyRepository.set(it)
    }
}
