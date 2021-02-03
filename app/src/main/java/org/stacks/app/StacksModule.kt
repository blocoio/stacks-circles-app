package org.stacks.app

import android.content.ClipboardManager
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.DEFAULT_MASTER_KEY_ALIAS
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.blockstack.android.sdk.Blockstack
import org.blockstack.android.sdk.model.Hub
import org.stacks.app.data.AuthRequestsStore
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class StacksModule {

    @Provides
    @Singleton
    fun keyGenParameterSpec(): KeyGenParameterSpec {
        val builder = KeyGenParameterSpec.Builder(
            DEFAULT_MASTER_KEY_ALIAS,
            PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        )

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) {
            builder.setUserPresenceRequired(true)
        }

        builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)

        return builder.build()
    }

    @Provides
    @Singleton
    fun masterKey(
        @ApplicationContext appContext: Context,
        keyGenParameterSpec: KeyGenParameterSpec
    ): MasterKey =
        MasterKey.Builder(appContext)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .setUserAuthenticationRequired(true, AUTH_VALIDITY_SECONDS)
            .build()

    @Provides
    @Singleton
    fun flowSharedPreferences(
        @ApplicationContext appContext: Context,
        masterKey: MasterKey
    ): FlowSharedPreferences = FlowSharedPreferences(
        EncryptedSharedPreferences.create(
            appContext,
            "encrypted_preferences",
            masterKey,
            AES256_SIV,
            AES256_GCM
        )
    )

    @Provides
    @Singleton
    fun blockstack() =
        Blockstack()

    @Provides
    fun hub() =
        Hub()

    @Provides
    @Singleton
    fun authRequestStore() =
        AuthRequestsStore()

    @Provides
    @Singleton
    fun clipboardManager(@ApplicationContext context: Context) =
        ContextCompat.getSystemService(context, ClipboardManager::class.java) as ClipboardManager

    companion object {
        const val KEY_SIZE = 256
        const val AUTH_VALIDITY_SECONDS = 5
    }
}
