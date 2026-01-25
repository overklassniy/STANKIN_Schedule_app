package com.overklassniy.stankinschedule.journal.core.data.repository

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

private val Context.journalSecureStore by preferencesDataStore(
    name = "module_journal_secure_preference"
)

class JournalSecureRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : JournalSecureRepository {

    private var cachedCredentials: StudentCredentials? = null

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existing = keyStore.getKey(KEY_ALIAS, null)
        if (existing is SecretKey) return existing

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        generator.init(spec)
        return generator.generateKey()
    }

    private fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val out = ByteArray(1 + iv.size + cipherText.size)
        out[0] = iv.size.toByte()
        System.arraycopy(iv, 0, out, 1, iv.size)
        System.arraycopy(cipherText, 0, out, 1 + iv.size, cipherText.size)

        return Base64.encodeToString(out, Base64.NO_WRAP)
    }

    private fun decrypt(encoded: String): String {
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        if (bytes.isEmpty()) return ""

        val ivSize = bytes[0].toInt() and 0xFF
        if (ivSize <= 0 || 1 + ivSize >= bytes.size) {
            throw IllegalArgumentException("Invalid encrypted payload")
        }

        val iv = bytes.copyOfRange(1, 1 + ivSize)
        val cipherText = bytes.copyOfRange(1 + ivSize, bytes.size)

        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(128, iv)
        )
        return cipher.doFinal(cipherText).toString(Charsets.UTF_8)
    }

    private suspend fun clearSecureStore() {
        context.journalSecureStore.edit { it.clear() }
        cachedCredentials = null
    }


    @kotlin.jvm.Throws(StudentAuthorizedException::class)
    override suspend fun signIn(credentials: StudentCredentials) {
        try {
            context.journalSecureStore.edit { prefs ->
                prefs[LOGIN] = encrypt(credentials.login)
                prefs[PASSWORD] = encrypt(credentials.password)
            }
            cachedCredentials = credentials
        } catch (e: StudentAuthorizedException) {
            throw e
        } catch (e: Exception) {
            Log.d("JournalSecureRepositoryImpl", "signIn: clear secure store and retry", e)
            clearSecureStore()
            context.journalSecureStore.edit { prefs ->
                prefs[LOGIN] = encrypt(credentials.login)
                prefs[PASSWORD] = encrypt(credentials.password)
            }
            cachedCredentials = credentials
        }
    }

    @kotlin.jvm.Throws(StudentAuthorizedException::class)
    override suspend fun signOut() {
        try {
            clearSecureStore()
        } catch (e: Exception) {
            Log.d("JournalSecureRepositoryImpl", "signOut: ignored", e)
        }
    }

    @kotlin.jvm.Throws(StudentAuthorizedException::class)
    override suspend fun signCredentials(): StudentCredentials {
        val cache = cachedCredentials
        if (cache != null) return cache

        val prefs = context.journalSecureStore.data.first()
        val loginEnc = prefs[LOGIN]
        val passwordEnc = prefs[PASSWORD]

        if (loginEnc == null || passwordEnc == null) {
            throw StudentAuthorizedException("Credentials is null")
        }

        return try {
            val credentials = StudentCredentials(
                login = decrypt(loginEnc),
                password = decrypt(passwordEnc)
            )
            cachedCredentials = credentials
            credentials
        } catch (e: Exception) {
            Log.d("JournalSecureRepositoryImpl", "signCredentials: decrypt failed, clearing store", e)
            clearSecureStore()
            throw StudentAuthorizedException(e)
        }
    }

    companion object {
        private val LOGIN = stringPreferencesKey("login")
        private val PASSWORD = stringPreferencesKey("password")

        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "journal_secure_credentials_aes"
        private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
    }
}