package org.stacks.app.domain

import android.content.res.Resources
import org.kethereum.bip39.generateMnemonic
import org.stacks.app.R
import org.stacks.app.data.EncryptedPreferencesSecretKeyRepository
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
    suspend fun generate(): String = generateMnemonic(wordList = words.toList()).also {
            secretKeyRepository.set(it)
    }
}
